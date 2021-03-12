package com.asyncapi.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

public class AnonymousSchema16 {
  @Valid
  private Integer myAppHeader;

  @JsonProperty("my-app-header")
  @Min(1)
  @Max(100)
  public Integer getMyAppHeader() { return this.myAppHeader; }
  public void setMyAppHeader(Integer myAppHeader) { this.myAppHeader = myAppHeader; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnonymousSchema16 self = (AnonymousSchema16) o;
      return 
        Objects.equals(this.myAppHeader, self.myAppHeader);
  }

  @Override
  public int hashCode() {
    return Objects.hash(myAppHeader);
  }

  @Override
  public String toString() {
    return "class AnonymousSchema16 {\n" +   
      "    myAppHeader: " + toIndentedString(myAppHeader) + "\n" +
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
// public class AnonymousSchema16 {
//     
//
//
//     private @Valid int myAppHeader;
//
//
//     

//     
//
//
//

//

//     
//     @JsonProperty("my-app-header")
//
//
//
//
//@Max(100)
//     public int getMyAppHeader() {
//         return myAppHeader;
//     }

//     public void setMyAppHeader(int myAppHeader) {
//         this.myAppHeader = myAppHeader;
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
//         AnonymousSchema16 anonymousSchema16 = (AnonymousSchema16) o;
//         return 
//             Objects.equals(this.myAppHeader, anonymousSchema16.myAppHeader);
//     }

//     @Override
//     public int hashCode() {
//         return Objects.hash(myAppHeader);
//     }

//     @Override
//     public String toString() {
//         return "class AnonymousSchema16 {\n" +
//         
//                 "    myAppHeader: " + toIndentedString(myAppHeader) + "\n" +
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