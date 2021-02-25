package com.asyncapi.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

/**
 * Examples: {"command":"on","sentAt":"lol"}, {"command":"off","sentAt":"lol"}
 */
public class TurnOnOffPayload {
  public enum CommandEnum {
    ON("on"), OFF("off");

    private String value;

    CommandEnum(String value) {
      this.value = value;
    }
    
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CommandEnum fromValue(String value) {
      for (CommandEnum e : CommandEnum.values()) {
        if (e.value.equals(value)) {
          return e;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }
  @Valid
  private CommandEnum command;
  @Valid
  private java.time.OffsetDateTime sentAt;

  /**
   * Whether to turn on or off the light.
   */
  @JsonProperty("command")
  public CommandEnum getCommand() { return this.command; }
  public void setCommand(CommandEnum command) { this.command = command; }

  /**
   * Date and time when the message was sent.
   */
  @JsonProperty("sentAt")
  @Pattern(regexp="test-test")
  public java.time.OffsetDateTime getSentAt() { return this.sentAt; }
  public void setSentAt(java.time.OffsetDateTime sentAt) { this.sentAt = sentAt; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TurnOnOffPayload self = (TurnOnOffPayload) o;
      return 
        Objects.equals(this.command, self.command) &&
        Objects.equals(this.sentAt, self.sentAt);
  }
        
  @Override
  public int hashCode() {
    return Objects.hash(command, sentAt);
  }

  @Override
  public String toString() {
    return "class TurnOnOffPayload {\n" +   
      "    command: " + toIndentedString(command) + "\n" +
      "    sentAt: " + toIndentedString(sentAt) + "\n" +
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

/**
 * Examples: {"command":"on","sentAt":"lol"}, {"command":"off","sentAt":"lol"}
 */
public class TurnOnOffPayload {
    
    public enum CommandEnum {
            
        ON(String.valueOf("on")),
            
        OFF(String.valueOf("off"));
            
        private String value;

        CommandEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static CommandEnum fromValue(String value) {
            for ( CommandEnum b :  CommandEnum.values()) {
                if (Objects.equals(b.value, value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    private @Valid CommandEnum command;
    
    private @Valid java.time.OffsetDateTime sentAt;
    

    

    /**
     * Whether to turn on or off the light.
     */
    @JsonProperty("command")
    public CommandEnum getCommand() {
        return command;
    }

    public void setCommand(CommandEnum command) {
        this.command = command;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TurnOnOffPayload turnOnOffPayload = (TurnOnOffPayload) o;
        return 
            Objects.equals(this.command, turnOnOffPayload.command) &&
            Objects.equals(this.sentAt, turnOnOffPayload.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, sentAt);
    }

    @Override
    public String toString() {
        return "class TurnOnOffPayload {\n" +
        
                "    command: " + toIndentedString(command) + "\n" +
                "    sentAt: " + toIndentedString(sentAt) + "\n" +
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