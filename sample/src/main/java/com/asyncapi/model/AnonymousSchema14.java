package com.asyncapi.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

public class AnonymousSchema_14 {
  @Valid
  private String key;

  @JsonProperty("key")
  public String getKey() { return this.key; }
  public void setKey(String key) { this.key = key; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnonymousSchema_14 self = (AnonymousSchema_14) o;
      return 
        Objects.equals(this.key, self.key);
  }
    
  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return "class AnonymousSchema_14 {\n" +   
      "    key: " + toIndentedString(key) + "\n" +
      "}";
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


public class AnonymousSchema14 {
    
    private @Valid String key;
    

    

    
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnonymousSchema14 anonymousSchema14 = (AnonymousSchema14) o;
        return 
            Objects.equals(this.key, anonymousSchema14.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "class AnonymousSchema14 {\n" +
        
                "    key: " + toIndentedString(key) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
           return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}