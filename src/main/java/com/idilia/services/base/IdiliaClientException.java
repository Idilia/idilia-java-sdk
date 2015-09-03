package com.idilia.services.base;

/**
 * Class to wrap all exceptions encountered when using the client API.
 * This extends RuntimeException to allow these exceptions to be thrown
 * within async callback processing.
 */
public class IdiliaClientException extends RuntimeException {

  public IdiliaClientException(String message) {
    super(message);
  }
  
  public IdiliaClientException(ResponseBase resp) {
    apiResponse = resp;
  }
  
  public IdiliaClientException(Throwable t) {
    super(t);
  }

  public IdiliaClientException(String message, Throwable t) {
    super(message, t);
  }
  
  /**
   * Return the error returned by the server. When this object is not null,
   * the properties getStatus() and getErrorMsg() return the details on
   * the error encountered.
   */
  public ResponseBase getApiResponse() {
    return this.apiResponse;
  }
  
  @Override
  public String getMessage() {
    if (this.apiResponse != null && 
        this.apiResponse.getErrorMsg() != null && 
        !this.apiResponse.getErrorMsg().isEmpty())
      return this.apiResponse.getErrorMsg();
    else
      return super.getMessage();
  }
  
 private ResponseBase apiResponse;
 private static final long serialVersionUID = -5786914648897122321L;
}
