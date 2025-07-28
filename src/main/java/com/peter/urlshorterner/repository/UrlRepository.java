package com.peter.urlshorterner.repository;

import com.peter.urlshorterner.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface UrlRepository extends JpaRepository<Url, String> {

  Url findByLongUrl(String longUrl);

  boolean existsByLongUrl(String longUrl);
  boolean existsByShortUrl(String shortUrl);
}
