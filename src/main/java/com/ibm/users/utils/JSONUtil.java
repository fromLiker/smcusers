package com.ibm.users.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.IOException;

public class JSONUtil {

  public static final ObjectMapper om = new ObjectMapper();

  public static <T> T deserialize(String content, Class<T> type) {
    try {
      return JSONUtil.om.readValue(content, type);
    } catch (IOException e) {
      return null;
    }
  }

  public static <T> T deserialize(String content, String name, Class<T> type) {
    try {
      return JSONUtil.om.readValue(JSONUtil.getByFullName(content, name), type);
    } catch (IOException e) {
      return null;
    }
  }

  public static String getByFullName(String content, String fullName) {
    String[] t = fullName.split("\\.");
    try {
      JsonNode j = JSONUtil.om.readTree(content);
      for (String s : t) {
        j = j.path(s);
      }
      return j.asText();
    } catch (IOException e) {
      return "";
    }
  }

  public static JsonNode toJsonNode(Object o) {
    try {
      return JSONUtil.om.readTree(JSONUtil.om.writeValueAsBytes(o));
    } catch (IOException e) {
      e.printStackTrace();
      return JsonNodeFactory.instance.nullNode();
    }
  }

  public static JsonNode toJsonNode(String s) {
    try {
      return JSONUtil.om.readTree(s);
    } catch (IOException e) {
      e.printStackTrace();
      return JsonNodeFactory.instance.nullNode();
    }
  }

  public static String toString(Object o) {
    try {
      return JSONUtil.om.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "";
    }
  }

}
