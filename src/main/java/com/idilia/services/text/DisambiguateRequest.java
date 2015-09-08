/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 * Description:
 *     This file implements the base class for issuing a request to Idilia's
 *     document server for sense analysis.
 *
 *     The class includes all the basic parameters. The text to process is
 *     recorded into a FormBodyPart for inclusion in a multipart request to
 *     the server.
 */

package com.idilia.services.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.RequestBase;

/**
 * Message for obtaining disambiguated documents.
 * 
 * The text to process is stored as an attachment with its own mime type
 * and character set.
 * 
 * If a @resultURI is provided, the result document is processed offline.
 * If not provided, the response is returned when the document is available.
 */

public class DisambiguateRequest extends RequestBase {

  /**
   * Specifies the format of the result document.
   * 
   * The recognized mime types are:
   * <ul>
   *  <li> application/x-tf:       A TextFeatures in binary format.
   *  <li> application/x-tf+xml:   A TextFeatures in XML format.
   *  <li> application/x-semdoc+xml: The semantic document format.
   * </ul>
   * <p>
   * Appending "+gz" to the mime will return a result in gzip format (RFC1951, RFC1952).
   * In addition to the reduced size, this format includes a CRC-32 that ensures
   * that the result was not corrupted during transmission.
   * Default is "application/x-semdoc+xml".
   *
   * 
   * @param resultMime Mime type for the result document.
  */

  public final void setResultMime(String resultMime) {
    this.resultMime = resultMime;
  }
  
  
  /**
   * Sets the type of recipe to use.
   * 
   * This parameter identifies the recipe that is used for processing the request.
   * Recipes can be tuned for real-time response, document type, etc. All recipes
   * specified here must be available on the server processing the request.
   * See https://www.idilia.com/developer/sense-analysis/concepts/sense-analysis-recipes
   * for possible values.
   * 
   * @param recipeKey value for the processing recipe to use
   */
  public final void setDisambiguationRecipe(String recipeKey) {
    this.disambiguationRecipe = recipeKey;
  }
  
  
  /** 
   * Specifies the URI for the result.
   * 
   * When this parameter is present, the server validates the request and
   * returns an immediate response to the request. When the request is valid,
   * the server queues the document for disambiguation and stores the result
   * at this URI when available. An application may poll the repository
   * or specify parameter @notificationURI to obtain an availability notification.
   * 
   * The given URI must be writable. If the name ends with "gz"
   * the output file is compressed in "gzip" format (RFC1951, RFC1952).
   * The name can be preceded with the following protocols:
   * <ul>
   * <li> those recognized by CURL: ftp:// file://
   * <li> s3://   -- for storing in the AWS network. The URI provided has the form "bucket/file_name" (e.g., s3://com-idilia-bucket/request-123.xml)
   * </ul>
   *     
   *  When using an AWS S3 bucket, the bucket must be writable by the account
   *  operating the servers. Please refer to our web site for our account number.
   *  
   *  @param resultURI uri for the storing the asynchronous result
   */
  public final void setResultURI(String resultURI) {
    this.resultURI = resultURI;
  }
  
  
  /** 
   * Specifies the URI for completion notification.
   * 
   * A signaling channel where the client can be informed when a request has
   * completed. The following are valid:
   *   - sqs://&lt;AWS queue&gt;
   *   
   *  When using an AWS S3QS queue, it must be writable by the account
   *  operating the servers. Please refer to our web site for our account number.
   *  
   *  The message received on the queue is identical to the message deposited
   *  at the @resultURI except that it does not contain the actual disambiguation
   *  result.
   *  
   *  @param notificationURI uri to notify
   *  
   */

  public final void setNotificationURI(String notificationURI) {
    this.notificationURI = notificationURI;
  }
  
  
  /**
   * Sets the maximum number of tokens to process in the input document.
   * 
   * The input document is tokenized and truncated to the next nearest paragraph
   * boundary with this number of tokens.
   * 
   * @param maxTokens maximum tokens to process
   */
  public final void setMaxTokens(int maxTokens) {
    this.maxTokens = maxTokens;
  }
  
  
  /**
   * Provides the document to process. 
   * 
   * The document is provided as an instance of FormBodyPart that already
   * includes the text and its mime. The mime must have been set to a valid
   * mime + character set.
   * 
   * @param attachment document to process
   */
  public final void setAttachment(FormBodyPart attachment) {
    this.attachments.add(attachment);
  }
  
  /**
   * Provides the document to process as a String
   * 
   * Convenience method that allows setting the text.
   * "mime" is the mime expected by the server (e.g., text/query).
   * @param text Text to process
   * @param mime Mime type as described in @{link sourceMime}
   * @param chSet Character set for the text encoding
   */
  public final void setText(String text, String mime, Charset chSet) {
    FormBodyPartBuilder bld = FormBodyPartBuilder.create(
        "text" + this.attachments.size(),
        new StringBody(text, ContentType.create(mime, chSet)));
    this.attachments.add(bld.build());
  }
  
  /**
   * Specifies a timeout by which the response must be returned.
   * 
   * Timeout in units of hundreds of milliseconds for returning a result
   * (i.e., a value of 10 returns after 1 second). If the scheduling delay + 
   * computation time exceeds this limit, the request aborts immediately and 
   * returns HTTP 504. Useful for real-time applications.
   * 
   * @param timeout hundreds of seconds
   */
  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  
  /**
   * Encodes the request as a multipart message for sending to the server.
   * 
   * @return MultipartEntity suitable for transmission in an HTTP request.
   */
  @Override
  public HttpEntity getContent() {
    
    // Add a part with as a form
    List<NameValuePair> parms = new ArrayList<NameValuePair>();
    getHttpQueryParms(parms);
    String parmsText = URLEncodedUtils.format(parms, Consts.UTF_8);
    
    MultipartEntityBuilder builder =  MultipartEntityBuilder.create()
        .addTextBody("parms", parmsText, ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8));
    
    for (FormBodyPart part: attachments)
      builder.addPart(part.getName(), part.getBody());
     
     return builder.build();
  }
  
  
  final public String requestPath() {
    return new String("/1/text/disambiguate.mpjson");
  }
  
  
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IdiliaClientException {
    
    if (this.attachments.isEmpty())
      throw new IdiliaClientException("No text specified");
    
    // Add base parameters
    super.getHttpQueryParms(parms);
    
    if (resultMime != null && resultMime.length() > 0)
      parms.add(new BasicNameValuePair("resultMime", resultMime));
    if (disambiguationRecipe != null && disambiguationRecipe.length() > 0)
      parms.add(new BasicNameValuePair("disambiguationRecipe", disambiguationRecipe));
    if (resultURI != null && resultURI.length() > 0)
      parms.add(new BasicNameValuePair("resultURI", resultURI));
    if (notificationURI != null && notificationURI.length() > 0)
      parms.add(new BasicNameValuePair("notificationURI", notificationURI));
    if (timeout != 0)
      parms.add(new BasicNameValuePair("timeout", timeout.toString()));
    parms.add(new BasicNameValuePair("maxTokens", Integer.toString(maxTokens)));
  }
  
  final public byte[] toSign() throws IOException {
    int totalBodyLen = 0;
    for (FormBodyPart attachment: attachments) {
      long l = attachment.getBody().getContentLength();
      if (l != -1)
        totalBodyLen += l;
    }
    
    ByteArrayOutputStream signOs = new ByteArrayOutputStream(totalBodyLen);
    for (FormBodyPart attachment: attachments)
      attachment.getBody().writeTo(signOs);
    return signOs.toByteArray();
  }
  
  private String resultMime = "application/x-semdoc+xml+gz";
  private String disambiguationRecipe;
  private String resultURI;
  private String notificationURI;
  private int maxTokens = 1000;
  private Integer timeout = 0;
  private ArrayList<FormBodyPart> attachments = new ArrayList<FormBodyPart>(1);
}
