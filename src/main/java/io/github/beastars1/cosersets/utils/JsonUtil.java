package io.github.beastars1.cosersets.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {
  private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

  public static ObjectMapper objectMapper = new ObjectMapper();
  static {
    objectMapper.findAndRegisterModules();
  }

  public static String toString(Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj.getClass() == String.class) {
      return (String) obj;
    }
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      logger.error("json序列化出错：" + obj, e);
      throw new RuntimeException();
    }
  }

  public static JsonNode toTree(String json) {
    try {
      return objectMapper.readTree(json);
    } catch (JsonProcessingException e) {
      logger.error("json解析出错：" + json, e);
      throw new RuntimeException();
    }
  }

  public static <T> T toBean(String json, Class<T> tClass) {
    try {
      return objectMapper.readValue(json, tClass);
    } catch (IOException e) {
      logger.error("json解析出错：" + json, e);
      throw new RuntimeException();
    }
  }

  public static <E> List<E> toList(String json, Class<E> eClass) {
    try {
      return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, eClass));
    } catch (IOException e) {
      logger.error("json解析出错：" + json, e);
      throw new RuntimeException();
    }
  }

  public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
    try {
      return objectMapper.readValue(json, objectMapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
    } catch (IOException e) {
      logger.error("json解析出错：" + json, e);
      throw new RuntimeException();
    }
  }

  public static <T> T nativeRead(String json, TypeReference<T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (IOException e) {
      logger.error("json解析出错：" + json, e);
      throw new RuntimeException();
    }
  }
}
