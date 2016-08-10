/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file implements the base functionality for clients communicating
 *     with Idilia's server.
 */
package com.idilia.services.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientBase {

  /**
   * Number of simultaneous requests allowed by the embedded HttpClient.
   */
  public static int maxConnections = 200;
  
  protected ClientBase(IdiliaCredentials creds, URL serviceUrl) {
    this.credentials = creds;
    this.serviceUrl = serviceUrl;
  }

  /** Helper function to create the POST request for a single part request
   *
   * @param req base request to be POSTed
   * @return initialized HttpPost request
   */
  protected HttpPost createPost(RequestBase req) {
    String path = serviceUrl.toString();
    String resource = req.requestPath();
    path += resource;
    HttpPost httppost = new HttpPost(path);
    httppost.setEntity(req.getContent());
    return httppost;
  }
  
  /** Helper function to create the POST request for a multipart request
   *
   * @param req base request to be POSTed
   * @return initialized HttpPost request
   */
  protected HttpPost createMultipartPost(RequestBase req) throws IdiliaClientException {
    // Create the post request
    String path = this.serviceUrl.toString();
    String resource = req.requestPath();
    path += resource;
    HttpPost httpPost = new HttpPost(path);
    
    // Obtain the content to transmit. We have to convert to a ByteArrayEntity because
    // the server attempts to use getContent on the entity and that method is not
    // implemented for the multipartentity returned
    HttpEntity mpEntity = req.getContent();
    ByteArrayOutputStream outOs = new ByteArrayOutputStream();
    try {
      mpEntity.writeTo(outOs);
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }
    String ctStr = mpEntity.getContentType().getValue();
    ContentType ct = ContentType.create(ctStr);
    ByteArrayEntity txEntity = new ByteArrayEntity(outOs.toByteArray(), ct);
    httpPost.setEntity(txEntity);
    return httpPost;
  }

  /** Helper to add the information required to compute the signature into the context
   * @param ctxt HttpContext to be updated
   * @param resource string
   * @param toMD5 byte array for which a digest will be computed
   */

  protected void sign(HttpContext ctxt, String resource, byte[] toMD5) {

    String signTail = "-" + serviceUrl.getHost() + "-" + resource;

    if (toMD5.length > 0) {
      // Compute the MD5 of the content to sign
      try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] theDigest = md.digest(toMD5);
        byte[] base64md5 = Base64.encodeBase64(theDigest);
        String strDigest = new String(base64md5);
        signTail += "-";
        signTail += strDigest;
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
    }

    ctxt.setAttribute("idlSignData", new RequestSigner.SignatureData(signTail,
        serviceUrl, credentials));
  }


  /**
   * A connection keep alive strategy based on the value returned by the server
   */
  static protected ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
      // Honor 'keep-alive' header
      Header ra = response.getFirstHeader(HTTP.CONN_KEEP_ALIVE);
      if (ra != null && ra.getValue() != null) {
        try {
          // The header is formatted as: "timeout=15, max=100"
          int timPos = ra.getValue().indexOf("timeout=");
          int comPos = timPos == 0 ? ra.getValue().indexOf(',', timPos + 8) : 0;
          if (timPos == 0 && comPos != -1)
            return Long.parseLong(ra.getValue().substring(timPos + 8, comPos)) * 1000;
        } catch (NumberFormatException ignore) {
        }
      }

      // Could not get a value. Let the normal connection handling determine
      // reusability.
      return -1;
    }
  };

  protected ResponseBase decodeHttpResponse(HttpResponse httpResponse, RequestBase req) throws IdiliaClientException
  {
    // Recover the response. It can be a single part or multipart
    HttpEntity rxEntity = httpResponse.getEntity();

    if (rxEntity == null)
      throw new IdiliaClientException("Did not received a response from the server");
    
    Header ctHdr = rxEntity.getContentType();
    if (ctHdr == null)
      throw new IdiliaClientException("Unexpected no content type");
    
    String ct = ctHdr.getValue();
    ResponseBase resp;
    if (ct.startsWith("application/json"))
    {
      // Single part json message.
      try {
        JsonParser jp = jsonMapper_.getFactory().createParser(rxEntity.getContent());
        resp = jp.readValueAs(req.responseClass());
        if (resp.getStatus() != HttpStatus.SC_OK)
          throw new IdiliaClientException(resp);
        return resp;
      }
      catch (IOException e) {
        throw new IdiliaClientException(e);
      }
    } else {
      throw new IdiliaClientException("Unexpected content type: " + ct);
    }
  }
  
  final protected IdiliaCredentials credentials;
  final protected URL serviceUrl;
  final protected static String HMAC_SHA_ALGORITHM = "HmacSHA256";
  final protected static ObjectMapper jsonMapper_ = new ObjectMapper();
  final protected static URL defaultApiUrl;
  
  static {
    try {
      defaultApiUrl = new URL("http://api.idilia.com");
    } catch (MalformedURLException e) {
      throw new IdiliaClientException(e);
    }
  }
  
}
