package com.peter.urlshorterner.service;

import static java.lang.String.format;

import com.peter.urlshorterner.dto.UrlDto;
import com.peter.urlshorterner.dto.UrlRequestDto;
import com.peter.urlshorterner.dto.UrlResponseDto;
import com.peter.urlshorterner.entity.Url;
import com.peter.urlshorterner.mapper.UrlMapper;
import com.peter.urlshorterner.repository.UrlRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UrlService {

  private final UrlRepository repo;
  private final UrlMapper mapper;

  public UrlResponseDto shortenUrl(UrlRequestDto request) {
    if (request == null || request.getFullUrl() == null || request.getFullUrl().isBlank()) {

      return null;
    }

    if (repo.existsByFullUrl(request.getFullUrl())) {

      return null;
    }
    // use provided alias
    String alias;
    if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
      alias = request.getCustomAlias();
      if (repo.existsByAlias(alias)) {

        return null;
      }

      return createAndSaveUrl(request, alias);
    }
    // create alias
    alias = createAlias();

    return createAndSaveUrl(request, alias);
  }

  private String createAlias() {
    String alias;
    for (int length = 8; length <= 32; length++) {
      for (int i = 0; i < 100; i++) {
        alias = UUID.randomUUID().toString().replace("-", "").substring(0, length);
        if (!repo.existsByAlias(alias)) {

          return alias;
        }
      }
    }
    throw new RuntimeException("No available option of short URL.");
  }

  private UrlResponseDto createAndSaveUrl(UrlRequestDto request, String alias) {
    Url url =
        repo.save(
            Url.builder()
                .alias(alias)
                .fullUrl(request.getFullUrl())
                .shortUrl(format("https://%s", alias))
                .build());

    return mapper.toUrlResponseDto(url);
  }

  public List<UrlDto> getUrls() {
    return repo.findAll().stream().map(mapper::toDto).toList();
  }

  public Url getUrlByAlias(String alias) {
    return repo.findByAlias(alias);
  }

  public boolean deleteUrlByAlias(String alias) {
    return repo.deleteByAlias(alias) > 0;
  }
}
