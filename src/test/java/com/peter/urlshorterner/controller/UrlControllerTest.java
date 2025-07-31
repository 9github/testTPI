package com.peter.urlshorterner.controller;

import static com.peter.urlshorterner.util.TestUtil.getBodyAsListOf;
import static com.peter.urlshorterner.util.TestUtil.getBodyAsObjectOf;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.urlshorterner.dto.UrlDto;
import com.peter.urlshorterner.dto.UrlRequestDto;
import com.peter.urlshorterner.dto.UrlResponseDto;
import com.peter.urlshorterner.entity.Url;
import com.peter.urlshorterner.repository.UrlRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@ExtendWith({SnapshotExtension.class})
public class UrlControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UrlRepository urlRepository;
  @Autowired private ObjectMapper objectMapper;
  private Expect expect;

  @BeforeEach
  void setUp() {
    Url url = Url.builder().alias("alias").fullUrl("fullUrl").shortUrl("shortUrl").build();
    urlRepository.save(url);
  }

  @AfterEach
  void tearDown() {
    urlRepository.deleteAll();
  }

  final UrlDto urlDto =
      UrlDto.builder().alias("alias").fullUrl("fullUrl").shortUrl("shortUrl").build();

  @Test
  void shortenUrlWithProvidedAlias() throws Exception {
    UrlRequestDto requestDto =
        UrlRequestDto.builder().fullUrl("https://fullUrl2").customAlias("alias2").build();
    String request = objectMapper.writeValueAsString(requestDto);

    var body =
        getBodyAsObjectOf(
            UrlDto.class,
            mockMvc
                .perform(post("/shorten")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString());

    expect.serializer("json").toMatchSnapshot(body);
  }

  @Test
  void shortenUrlWithGeneratedAlias() throws Exception {
    UrlRequestDto requestDto =
        UrlRequestDto.builder().fullUrl("https://fullUrl2").build();
    String request = objectMapper.writeValueAsString(requestDto);

    var body =
        getBodyAsObjectOf(
            UrlResponseDto.class,
            mockMvc
                .perform(post("/shorten")
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString());


    assertTrue(body.getShortUrl().startsWith("https://"));
  }

  @Test
  void getUrls() throws Exception {
    var body =
        getBodyAsListOf(
            UrlDto.class,
            mockMvc
                .perform(get("/urls").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString());

    expect.serializer("json").toMatchSnapshot(body);
  }

  @Test
  void getUrlByAlias() throws Exception {
    mockMvc
        .perform(get("/alias"))
        .andExpect(status().is(302))
        .andExpect(header().string("location", "fullUrl"))
        .andReturn();
  }

  @Test
  void getUrlByAliasNoUrl() throws Exception {
    mockMvc
        .perform(get("/alias2"))
        .andExpect(status().is(404))
        .andReturn();
  }

  @Test
  void deleteUrlByAlias() throws Exception {
    final String ALIAS = "alias";
    mockMvc
        .perform(delete("/" + ALIAS))
        .andExpect(status().is(204))
        .andReturn();

    assertFalse(urlRepository.existsByAlias(ALIAS));
  }

  @Test
  void deleteUrlByAliasNoUrl() throws Exception {
    final String ALIAS = "alias2";
    mockMvc
        .perform(delete("/" + ALIAS))
        .andExpect(status().is(404))
        .andReturn();
  }
}
