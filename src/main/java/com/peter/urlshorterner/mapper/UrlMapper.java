package com.peter.urlshorterner.mapper;

import com.peter.urlshorterner.dto.UrlDto;
import com.peter.urlshorterner.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrlMapper {

  @Mapping(target = "id", ignore = true)
  Url toEntity(UrlDto urlDto);

  UrlDto toDto(Url url);
}
