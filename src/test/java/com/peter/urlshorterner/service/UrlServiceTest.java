package com.peter.urlshorterner.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.peter.urlshorterner.dto.UrlRequestDto;
import com.peter.urlshorterner.entity.Url;
import com.peter.urlshorterner.mapper.UrlMapper;
import com.peter.urlshorterner.mapper.UrlMapperImpl;
import com.peter.urlshorterner.repository.UrlRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class UrlServiceTest {

  @Autowired UrlRepository urlRepository;
  UrlMapper urlMapper = new UrlMapperImpl();

  private UrlService service;

  @BeforeAll
  void setUpService() {
    service = new UrlService(urlRepository, urlMapper);
  }

  @BeforeEach
  void setUp() {
    Url url = Url.builder().alias("alias").fullUrl("fullUrl").shortUrl("shortUrl").build();
    urlRepository.save(url);
  }

  @Test
  void shortenUrlWithProvidedAlias() {
    final String ALIAS = "alias2";
    UrlRequestDto request = UrlRequestDto.builder().fullUrl("fullUrl2").customAlias(ALIAS).build();
    var urlResponseDto = service.shortenUrl(request);
    assertEquals("https://alias2", urlResponseDto.getShortUrl());

    var url = urlRepository.findByAlias(ALIAS);
    assertEquals("alias2", url.getAlias());
    assertEquals("fullUrl2", url.getFullUrl());
    assertEquals("https://alias2", url.getShortUrl());
  }

  @Test
  void shortenUrlWithGeneratedAlias() {
    UrlRequestDto request = UrlRequestDto.builder().fullUrl("fullUrl2").build();
    var urlResponseDto = service.shortenUrl(request);
    assertTrue(Strings.isNotEmpty(urlResponseDto.getShortUrl()));

    Url url =
        urlRepository.findAll().stream()
            .filter(it -> it.getFullUrl().equals("fullUrl2"))
            .findFirst()
            .get();
    assertTrue(Strings.isNotEmpty(url.getAlias()));
    assertEquals(format("https://%s", url.getAlias()), url.getShortUrl());
    assertEquals("fullUrl2", url.getFullUrl());
  }

  @Test
  void returnNullIfNoFullUrlProvided() {
    UrlRequestDto request = UrlRequestDto.builder().build();
    assertNull(service.shortenUrl(request));
    UrlRequestDto request2 = UrlRequestDto.builder().fullUrl(" ").build();
    assertNull(service.shortenUrl(request2));
    assertEquals(1, urlRepository.count());
  }

  @Test
  void returnNullIfExistingFullUrlProvided() {
    UrlRequestDto request = UrlRequestDto.builder().fullUrl("fullUrl").build();
    assertNull(service.shortenUrl(request));
    assertEquals(1, urlRepository.count());
  }

  @Test
  void returnNullIfExistingAliasProvided() {
    UrlRequestDto request =
        UrlRequestDto.builder().fullUrl("fullUrl2").customAlias("alias").build();
    assertNull(service.shortenUrl(request));
    assertEquals(1, urlRepository.count());
  }

  @Test
  void getUrls() {
    var url = service.getUrls().getFirst();
    assertEquals("alias", url.getAlias());
    assertEquals("fullUrl", url.getFullUrl());
    assertEquals("shortUrl", url.getShortUrl());
  }

  @Test
  void getUrlByAlias() {
    var url = service.getUrlByAlias("alias");
    assertEquals("alias", url.getAlias());
    assertEquals("fullUrl", url.getFullUrl());
    assertEquals("shortUrl", url.getShortUrl());
  }

  @Test
  void deleteUrlByAlias() {
    assertTrue(service.deleteUrlByAlias("alias"));
    assertFalse(service.deleteUrlByAlias("alias"));
  }
}
