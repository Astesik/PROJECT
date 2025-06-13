package com.example.ioproject.payload.response;

/**
 * Generic response payload used to return a simple message to the client.
 * <p>
 * Often used for success or error messages in API responses.
 * </p>
 */
public class MessageResponse {

  /**
   * The message content to be returned to the client.
   */
  private String message;

  /**
   * Constructs a new {@link MessageResponse} with the given message.
   *
   * @param message the message content
   */
  public MessageResponse(String message) {
    this.message = message;
  }

  /**
   * Gets the message content.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message content.
   *
   * @param message the new message
   */
  public void setMessage(String message) {
    this.message = message;
  }
}
