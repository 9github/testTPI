package com.peter.urlshorterner.repository;

import com.peter.urlshorterner.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UrlRepository extends JpaRepository<Url, String> {

  Url findByAlias(String alias);
  boolean existsByFullUrl(String fullUrl);
  boolean existsByAlias(String alias);
  long deleteByAlias(String alias);
}
