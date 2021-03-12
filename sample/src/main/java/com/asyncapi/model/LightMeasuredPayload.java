package com.asyncapi.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

public class LightMeasuredPayload {
  @Valid
  private Integer lumens;
  @Valid
  private java.time.OffsetDateTime sentAt;
  public class AllOfAnonymousSchema6ObjectWithKey2 {
    @Valid
    private AnonymousSchema6 anonymousSchema6;
    @Valid
    private ObjectWithKey2 objectWithKey2;

    @JsonProperty("anonymousSchema6")
    public AnonymousSchema6 getAnonymousSchema6() { return this.anonymousSchema6; }
    public void setAnonymousSchema6(AnonymousSchema6 anonymousSchema6) { this.anonymousSchema6 = anonymousSchema6; }

    @JsonProperty("objectWithKey2")
    public ObjectWithKey2 getObjectWithKey2() { return this.objectWithKey2; }
    public void setObjectWithKey2(ObjectWithKey2 objectWithKey2) { this.objectWithKey2 = objectWithKey2; }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      AllOfAnonymousSchema6ObjectWithKey2 self = (AllOfAnonymousSchema6ObjectWithKey2) o;
        return 
          Objects.equals(this.anonymousSchema6, self.anonymousSchema6) &&
          Objects.equals(this.objectWithKey2, self.objectWithKey2);
    }

    @Override
    public int hashCode() {
      return Objects.hash(anonymousSchema6, objectWithKey2);
    }

    @Override
    public String toString() {
      return "class AllOfAnonymousSchema6ObjectWithKey2 {\n" +   
        "    anonymousSchema6: " + toIndentedString(anonymousSchema6) + "\n" +
        "    objectWithKey2: " + toIndentedString(objectWithKey2) + "\n" +
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
  @Valid
  private AllOfAnonymousSchema6ObjectWithKey2 allOfProp;
  public interface OneOfAnonymousSchema10ObjectWithKey2 {}
  @Valid
  private OneOfAnonymousSchema10ObjectWithKey2 oneOfProp;
  @Valid
  private Object nestedOneOf;

  /**
   * Light intensity measured in lumens.
   */
  @JsonProperty("lumens")
  @Min(0)
  public Integer getLumens() { return this.lumens; }
  public void setLumens(Integer lumens) { this.lumens = lumens; }

  /**
   * Date and time when the message was sent.
   */
  @JsonProperty("sentAt")
  @Pattern(regexp="test-test")
  public java.time.OffsetDateTime getSentAt() { return this.sentAt; }
  public void setSentAt(java.time.OffsetDateTime sentAt) { this.sentAt = sentAt; }

  @JsonProperty("allOfProp")
  public AllOfAnonymousSchema6ObjectWithKey2 getAllOfProp() { return this.allOfProp; }
  public void setAllOfProp(AllOfAnonymousSchema6ObjectWithKey2 allOfProp) { this.allOfProp = allOfProp; }

  @JsonProperty("oneOfProp")
  public OneOfAnonymousSchema10ObjectWithKey2 getOneOfProp() { return this.oneOfProp; }
  public void setOneOfProp(OneOfAnonymousSchema10ObjectWithKey2 oneOfProp) { this.oneOfProp = oneOfProp; }

  @JsonProperty("nestedOneOf")
  public Object getNestedOneOf() { return this.nestedOneOf; }
  public void setNestedOneOf(Object nestedOneOf) { this.nestedOneOf = nestedOneOf; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LightMeasuredPayload self = (LightMeasuredPayload) o;
      return 
        Objects.equals(this.lumens, self.lumens) &&
        Objects.equals(this.sentAt, self.sentAt) &&
        Objects.equals(this.allOfProp, self.allOfProp) &&
        Objects.equals(this.oneOfProp, self.oneOfProp) &&
        Objects.equals(this.nestedOneOf, self.nestedOneOf);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lumens, sentAt, allOfProp, oneOfProp, nestedOneOf);
  }

  @Override
  public String toString() {
    return "class LightMeasuredPayload {\n" +   
      "    lumens: " + toIndentedString(lumens) + "\n" +
      "    sentAt: " + toIndentedString(sentAt) + "\n" +
      "    allOfProp: " + toIndentedString(allOfProp) + "\n" +
      "    oneOfProp: " + toIndentedString(oneOfProp) + "\n" +
      "    nestedOneOf: " + toIndentedString(nestedOneOf) + "\n" +
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


public class LightMeasuredPayload {
    
    private @Valid int lumens;
    
    private @Valid java.time.OffsetDateTime sentAt;
    
    public class AllOfAnonymousSchema6ObjectWithKey2 {
        private @Valid AnonymousSchema6 anonymousSchema6;

        public AnonymousSchema6 getAnonymousSchema6() {
            return anonymousSchema6;
        }

        public void setAnonymousSchema6(AnonymousSchema6 anonymousSchema6) {
            this.anonymousSchema6 = anonymousSchema6;
        }
        private @Valid ObjectWithKey2 objectWithKey2;

        public ObjectWithKey2 getObjectWithKey2() {
            return objectWithKey2;
        }

        public void setObjectWithKey2(ObjectWithKey2 objectWithKey2) {
            this.objectWithKey2 = objectWithKey2;
        }
    }

    private @Valid AllOfAnonymousSchema6ObjectWithKey2 allOfProp;
    
    public interface OneOfAnonymousSchema10ObjectWithKey2 {

    }
    private @Valid OneOfAnonymousSchema10ObjectWithKey2 oneOfProp;
    
    private @Valid Object nestedOneOf;
    

    

    /**
     * Light intensity measured in lumens.
     */
    @JsonProperty("lumens")
    public int getLumens() {
        return lumens;
    }

    public void setLumens(int lumens) {
        this.lumens = lumens;
    }
    

    /**
     * Date and time when the message was sent.
     */
    @JsonProperty("sentAt")@Pattern(regexp="test-test")
    public java.time.OffsetDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(java.time.OffsetDateTime sentAt) {
        this.sentAt = sentAt;
    }
    

    
    @JsonProperty("allOfProp")
    public AllOfAnonymousSchema6ObjectWithKey2 getAllOfProp() {
        return allOfProp;
    }

    public void setAllOfProp(AllOfAnonymousSchema6ObjectWithKey2 allOfProp) {
        this.allOfProp = allOfProp;
    }
    

    
    @JsonProperty("oneOfProp")
    public OneOfAnonymousSchema10ObjectWithKey2 getOneOfProp() {
        return oneOfProp;
    }

    public void setOneOfProp(OneOfAnonymousSchema10ObjectWithKey2 oneOfProp) {
        this.oneOfProp = oneOfProp;
    }
    

    
    @JsonProperty("nestedOneOf")
    public Object getNestedOneOf() {
        return nestedOneOf;
    }

    public void setNestedOneOf(Object nestedOneOf) {
        this.nestedOneOf = nestedOneOf;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LightMeasuredPayload lightMeasuredPayload = (LightMeasuredPayload) o;
        return 
            Objects.equals(this.lumens, lightMeasuredPayload.lumens) &&
            Objects.equals(this.sentAt, lightMeasuredPayload.sentAt) &&
            Objects.equals(this.allOfProp, lightMeasuredPayload.allOfProp) &&
            Objects.equals(this.oneOfProp, lightMeasuredPayload.oneOfProp) &&
            Objects.equals(this.nestedOneOf, lightMeasuredPayload.nestedOneOf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lumens, sentAt, allOfProp, oneOfProp, nestedOneOf);
    }

    @Override
    public String toString() {
        return "class LightMeasuredPayload {\n" +
        
                "    lumens: " + toIndentedString(lumens) + "\n" +
                "    sentAt: " + toIndentedString(sentAt) + "\n" +
                "    allOfProp: " + toIndentedString(allOfProp) + "\n" +
                "    oneOfProp: " + toIndentedString(oneOfProp) + "\n" +
                "    nestedOneOf: " + toIndentedString(nestedOneOf) + "\n" +
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