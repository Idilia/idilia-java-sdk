/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file implements the base for all messaging requests.
 *     
 */

package com.idilia.services.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

/**
 * Base class for all request messages
 *
 */
public abstract class RequestBase {

  /**
   * Specifies a unique request ID to be echoed in the response.
   * 
   * Clients are responsible for making sure that this is unique if used. For
   * asynchronous requests, the client can use this ID to correlate with request
   * status messages.
   * 
   * @param requestId
   *          Request ID sent to the server.
   */
  public final void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  /**
   * Returns a unique request ID to be echoed in the response.
   * 
   * @return String Request ID sent to the server.
   */
  public final String getRequestId() {
    return this.requestId;
  }
  
  /**
   * Specifies the customer for this request. Enables custom sense inventory.
   * @param custId UUID for the customer
   */
  public final void setCustomerId(UUID custId) {
    this.customerId = custId;
  }
  
  /**
   * Returns the unique customer id of the request. Optional.
   * 
   * @return UUID assigned to the customer.
   */
  public final UUID getCustomerId() {
    return this.customerId;
  }

  /**
   * @return HttpEntity suitable for transmission in an HTTP request.
   */
  public HttpEntity getContent() {
    List<NameValuePair> parms = new ArrayList<NameValuePair>();
    getHttpQueryParms(parms);
    return new UrlEncodedFormEntity(parms, Consts.UTF_8);
  }

  // Encode the content as HTTP query parameters
  protected void getHttpQueryParms(List<NameValuePair> parms) {
    if (requestId != null && requestId.length() > 0)
      parms.add(new BasicNameValuePair("requestId", requestId));
    if (customerId != null)
      parms.add(new BasicNameValuePair(
          "customerId", "urn:uuid:" + customerId.toString()));
  }

  /**
   * Returns the content to use when signing the request for the purpose of
   * authentication. Must be provided by the subclass.
   * 
   * @return byte[] Bytes to sign
   * @throws IOException when the appropriate elements in the message content cannot be converted to a byte array for signature.
   */
  abstract public byte[] toSign() throws IOException;

  /**
   * @return String url for the request
   */
  abstract public String requestPath();
  
  /**
   * @return Class of derived ResponseBase class if implemented
   */
  public Class<? extends ResponseBase> responseClass() { return ResponseBase.class; }

  private String requestId;
  private UUID customerId;
}
