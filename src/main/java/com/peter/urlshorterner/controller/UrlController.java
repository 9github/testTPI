package com.peter.urlshorterner.controller;

import com.peter.urlshorterner.dto.UrlDto;
import com.peter.urlshorterner.dto.UrlRequestDto;
import com.peter.urlshorterner.entity.Url;
import com.peter.urlshorterner.service.UrlService;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class UrlController {

  private final UrlService service;

  @PostMapping("/shorten")
  public ResponseEntity<String> shortenUrl(@RequestBody UrlRequestDto request) {
    // exclude "shorten", "urls"
    UrlDto urlDto = service.shortenUrl(request);
    String response;
    if (urlDto != null) {
      response = urlDto.getShortUrl();
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/{alias}")
  public ResponseEntity<String> redirectToFullUrl(@PathVariable String alias) {
    Url url = service.getUrlByAlias(alias);
    if (url != null) {
      return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url.getFullUrl())).build();
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alias not found");
  }

  @DeleteMapping("/{alias}")
  public ResponseEntity<Void> deleteUrl(@PathVariable String alias) {
    boolean deleted = service.deleteUrlByAlias(alias);
    if (deleted) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/urls")
  public ResponseEntity<List<UrlDto>> getAllUrls() {
    List<UrlDto> urls = service.getUrls();
    return new ResponseEntity<>(urls, HttpStatus.OK);
  }
}
