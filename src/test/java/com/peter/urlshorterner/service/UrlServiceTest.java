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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
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
    UrlRequestDto request =
        UrlRequestDto.builder().fullUrl("fullUrl2").customAlias("alias2").build();
    var urlDto = service.shortenUrl(request);
    assertEquals("alias2", urlDto.getAlias());
    assertEquals("fullUrl2", urlDto.getFullUrl());
    assertEquals("https://alias2", urlDto.getShortUrl());
    assertTrue(urlRepository.existsByAlias("alias2"));
  }

  @Test
  void shortenUrlWithGeneratedAlias() {
    UrlRequestDto request = UrlRequestDto.builder().fullUrl("fullUrl2").build();
    var urlDto = service.shortenUrl(request);
    assertEquals(8, urlDto.getAlias().length());
    assertEquals("fullUrl2", urlDto.getFullUrl());
    assertEquals(format("https://%s", urlDto.getAlias()), urlDto.getShortUrl());
    assertTrue(urlRepository.existsByAlias(urlDto.getAlias()));
  }

  @Test
  void returnNullIfNoFullUrlProvided() {
    UrlRequestDto request = UrlRequestDto.builder().build();
    assertNull(service.shortenUrl(request));
    UrlRequestDto request2 = UrlRequestDto.builder().fullUrl(" ").build();
    assertNull(service.shortenUrl(request2));
  }

  @Test
  void returnNullIfExistingFullUrlProvided() {
    UrlRequestDto request = UrlRequestDto.builder().fullUrl("fullUrl").build();
    assertNull(service.shortenUrl(request));
  }

  @Test
  void returnNullIfExistingAliasProvided() {
    UrlRequestDto request =
        UrlRequestDto.builder().fullUrl("fullUrl2").customAlias("alias").build();
    assertNull(service.shortenUrl(request));
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
