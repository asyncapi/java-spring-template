package com.asyncapi.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

public class ObjectWithKey2 implements LightMeasuredPayload.OneOfAnonymousSchema10ObjectWithKey2 {
  @Valid
  private String key2;

  @JsonProperty("key2")
  public String getKey2() { return this.key2; }
  public void setKey2(String key2) { this.key2 = key2; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectWithKey2 self = (ObjectWithKey2) o;
      return 
        Objects.equals(this.key2, self.key2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key2);
  }

  @Override
  public String toString() {
    return "class ObjectWithKey2 {\n" +   
      "    key2: " + toIndentedString(key2) + "\n" +
    "}";
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


public class ObjectWithKey2 implements LightMeasuredPayload.OneOfAnonymousSchema10ObjectWithKey2 {
    
    private @Valid String key2;
    

    

    
    @JsonProperty("key2")
    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectWithKey2 objectWithKey2 = (ObjectWithKey2) o;
        return 
            Objects.equals(this.key2, objectWithKey2.key2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key2);
    }

    @Override
    public String toString() {
        return "class ObjectWithKey2 {\n" +
        
                "    key2: " + toIndentedString(key2) + "\n" +
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