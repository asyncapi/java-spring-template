package com.asyncapi.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

public class DimLightPayload {
  @Valid
  private Integer percentage;
  @Valid
  private java.time.OffsetDateTime sentAt;
  @Valid
  private List<String> arrayProp;

  /**
   * Percentage to which the light should be dimmed to.
   */
  @JsonProperty("percentage")
  @Min(0)
  @Max(99)
  public Integer getPercentage() { return this.percentage; }
  public void setPercentage(Integer percentage) { this.percentage = percentage; }

  /**
   * Date and time when the message was sent.
   */
  @JsonProperty("sentAt")
  @Pattern(regexp="test-test")
  public java.time.OffsetDateTime getSentAt() { return this.sentAt; }
  public void setSentAt(java.time.OffsetDateTime sentAt) { this.sentAt = sentAt; }

  @JsonProperty("arrayProp")
  @Size(max=3)
  public List<String> getArrayProp() { return this.arrayProp; }
  public void setArrayProp(List<String> arrayProp) { this.arrayProp = arrayProp; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DimLightPayload self = (DimLightPayload) o;
      return 
        Objects.equals(this.percentage, self.percentage) &&
        Objects.equals(this.sentAt, self.sentAt) &&
        Objects.equals(this.arrayProp, self.arrayProp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(percentage, sentAt, arrayProp);
  }

  @Override
  public String toString() {
    return "class DimLightPayload {\n" +   
      "    percentage: " + toIndentedString(percentage) + "\n" +
      "    sentAt: " + toIndentedString(sentAt) + "\n" +
      "    arrayProp: " + toIndentedString(arrayProp) + "\n" +
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

// 
// public class DimLightPayload {
//     
//
//
//     private @Valid int percentage;
//
//
//     
//
//
//     private @Valid java.time.OffsetDateTime sentAt;
//
//
//     
//
//
//     private @Valid List<String> arrayPropList;
//
//
//     

//     
//
//
//

//

//     /**
//      * Percentage to which the light should be dimmed to.
//      */
//     @JsonProperty("percentage")
//
//
//
//
//@Max(101)
//     public int getPercentage() {
//         return percentage;
//     }

//     public void setPercentage(int percentage) {
//         this.percentage = percentage;
//     }
//     
//
//
//

//

//     /**
//      * Date and time when the message was sent.
//      */
//     @JsonProperty("sentAt")
//
//
//@Pattern(regexp="test-test")
//
//
//     public java.time.OffsetDateTime getSentAt() {
//         return sentAt;
//     }

//     public void setSentAt(java.time.OffsetDateTime sentAt) {
//         this.sentAt = sentAt;
//     }
//     
//
//
//

//
//
//

//     
//     @JsonProperty("arrayProp")
//
//@Size(max = 3)
//
//
//
//     public List<String> getArrayProp() {
//         return arrayPropList;
//     }

//     public void setArrayProp(List<String> arrayPropList) {
//         this.arrayPropList = arrayPropList;
//     }
//     
//     @Override
//     public boolean equals(Object o) {
//         if (this == o) {
//             return true;
//         }
//         if (o == null || getClass() != o.getClass()) {
//             return false;
//         }
//         DimLightPayload dimLightPayload = (DimLightPayload) o;
//         return 
//             Objects.equals(this.percentage, dimLightPayload.percentage) &&
//             Objects.equals(this.sentAt, dimLightPayload.sentAt) &&
//             Objects.equals(this.arrayPropList, dimLightPayload.arrayPropList);
//     }

//     @Override
//     public int hashCode() {
//         return Objects.hash(percentage, sentAt, arrayPropList);
//     }

//     @Override
//     public String toString() {
//         return "class DimLightPayload {\n" +
//         
//                 "    percentage: " + toIndentedString(percentage) + "\n" +
//                 "    sentAt: " + toIndentedString(sentAt) + "\n" +
//                 "    arrayPropList: " + toIndentedString(arrayPropList) + "\n" +
//                 "}";
//     }

//     /**
//      * Convert the given object to string with each line indented by 4 spaces (except the first line).
//      */
//     private String toIndentedString(Object o) {
//         if (o == null) {
//            return "null";
//         }
//         return o.toString().replace("\n", "\n    ");
//     }
// }