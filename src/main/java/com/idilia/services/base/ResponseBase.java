/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.base;

import com.idilia.services.kb.QueryRequest;

/**
 * Response from the KnowledgeBase server.
 *
 */
public class ResponseBase {

  /**
   * Returns the request id from the {@link QueryRequest} echoed back by the
   * server
   */
  public String getRequestId() {
    return requestId;
  }

  /**
   * Returns the status code for the request.
   * <p>
   * The status code is a numeric value that corresponds to the HTTP status
   * codes. E.g., "200" is the return code for "success".
   */
  public final int getStatus() {
    return status;
  }

  /**
   * Returns a descriptive string for the error encountered when the status is
   * not "200".
   */
  public final String getErrorMsg() {
    return errorMsg;
  }

  /**
   * Set the request Id from the server's response. Normally not used by
   * application code.
   */
  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  /**
   * Set the status from the server's response. Normally not used by application
   * code.
   */
  public final void setStatus(int status) {
    this.status = status;
  }

  /**
   * Set the error message from the server's response. Normally not used by
   * application code.
   */
  public final void setErrorMsg(String errMsg) {
    this.errorMsg = errMsg;
  }

  /**
   * Creates an empty object. Normally not used by application code.
   */
  public ResponseBase() {
  }

  /**
   * Creates an object with an error condition. Normally not used by application
   * code.
   */
  public ResponseBase(int status, String errorMsg) {
    setStatus(status);
    setErrorMsg(errorMsg);
  }

  private String requestId;
  private int status = 0;
  private String errorMsg;
}
