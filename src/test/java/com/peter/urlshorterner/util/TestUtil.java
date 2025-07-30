package com.peter.urlshorterner.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  // use single objects
  /**
   * Deserializes a JSON string into a single object of the specified class.
   *
   * @param <T> The type of object to return.
   * @param clazz The Class object representing the type T.
   * @param body The JSON string to deserialize.
   * @return An object of type T deserialized from the body string.
   * @throws JsonProcessingException If there's an error during JSON deserialization.
   */
  public static <T> T getBodyAsObjectOf(Class<T> clazz, String body) throws JsonProcessingException {
    return objectMapper.readValue(body, clazz);
  }

  // use for lists
  public static <T> List<T> getBodyAsListOf(Class<T> clazz, String body)
      throws JsonProcessingException {
    return objectMapper.readValue(
        body, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
  }

  /**
   * Deserializes a JSON string into a collection (like List, Set, etc.) of objects of the specified
   * generic type. This method should be used when the expected JSON body represents an array of
   * objects.
   *
   * @param <T> The generic type of the collection (e.g., List<MyObject>).
   * @param typeReference A TypeReference capturing the full generic type information. Example: new
   *     TypeReference<List<MyObject>>() {}
   * @param body The JSON string to deserialize.
   * @return A collection of type T deserialized from the body string.
   * @throws JsonProcessingException If there's an error during JSON deserialization.
   */
  public static <T> T getBody(TypeReference<T> typeReference, String body)
      throws JsonProcessingException {
    return objectMapper.readValue(body, typeReference);
  }


}
