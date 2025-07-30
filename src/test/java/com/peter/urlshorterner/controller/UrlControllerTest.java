package com.peter.urlshorterner.controller;

import static com.peter.urlshorterner.util.TestUtil.getBodyAsListOf;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.peter.urlshorterner.dto.UrlDto;
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
  @Autowired UrlRepository urlRepository;
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
        .perform(get("/alias").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(302))
        .andExpect(header().string("location", "fullUrl"))
        .andReturn();
  }

  @Test
  void deleteUrlByAlias() throws Exception {
    final String ALIAS = "alias";
    mockMvc
        .perform(delete("/" + ALIAS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(204))
        .andReturn();

    assertFalse(urlRepository.existsByAlias(ALIAS));
  }
}
