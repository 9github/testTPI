package com.peter.urlshorterner.service;

import com.peter.urlshorterner.entity.Url;
import com.peter.urlshorterner.repository.UrlRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

  private final UrlRepository repo;

  public UrlService(UrlRepository repo) {
    this.repo = repo;
  }

  public Url shortenUrl(String longUrl) {
    Url url = null;
    if (repo.existsByLongUrl(longUrl)) {
      throw new RuntimeException("Url already exists");
    } else {
      for (int length = 8; length <= 32; length++) {
        for (int i = 0; i < 100; i++) {
          String shortUrl = getShortUrl(longUrl, length);
          if (!repo.existsByShortUrl(shortUrl)) {
            url = Url.builder().longUrl(shortUrl).shortUrl(shortUrl).build();
            break;
          }
        }
        if (url != null) {
          break;
        }
      }
    }
    if (url == null) {
      throw new RuntimeException("No available option of short URL.");
    }
    return repo.save(url);
  }

  private String getShortUrl(String longUrl, int shortUrlLength) {
    // TODO: ensure not infinite loop
    if (longUrl.length() <= shortUrlLength) {
      return longUrl;
    }
    return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }

  public List<Url> getUrls() {
    return repo.findAll();
  }

  public Optional<Url> getUrl(String id) {
    return repo.findById(id);
  }
}
