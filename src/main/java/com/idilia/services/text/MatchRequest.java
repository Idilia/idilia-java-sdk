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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.RequestBase;
import com.idilia.services.base.ResponseBase;

/**
 * Message for performing semantic matching.
 * 
 * If a @resultURI is provided, the result document is processed offline.
 * If not provided, the response is returned when the results are available.
 */

public class MatchRequest extends RequestBase {

  @JsonInclude(Include.NON_NULL)
  public static class Keywords {
    public ArrayList<String> positive;
    public ArrayList<String> negative;
  }
  
  public static class ClientMatchRequest {
    public String requestId;
    public String fsk;
    public String synonyms;
    public String text;
    public Keywords keywords;
  }

  public static class Equivalencies {
    public boolean synonyms;
  }
  
  public static class Parms {
    public Parms() {
      equivalencies = new Equivalencies();
    }
    public Equivalencies equivalencies;
  }
  
  public static class Filter {
    public Filter() {}
    public String fsk;
    @JsonInclude(Include.NON_NULL)
    public Keywords keywords;
    @JsonInclude(Include.NON_NULL)
    public Parms parms;
  }
  
  /**
   * Sets the type of disambiguation recipe to use.
   * 
   * This parameter identifies the recipe that is used for processing the request.
   * Recipes can be tuned for real-time response, document type, etc. All recipes
   * specified here must be available on the server processing the request.
   * See https://www.idilia.com/developer/sense-analysis/concepts/sense-analysis-recipes
   * for possible values.
   * 
   * @param recipeKey recipe label for sense analysis
   */
  public final void setDisambiguationRecipe(String recipeKey) {
    this.disambiguationRecipe = recipeKey;
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
   *  @param notificationURI uri to notify on completion
   *  
   */
  
  public final void setNotificationURI(String notificationURI) {
    this.notificationURI = notificationURI;
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
   *  @param resultURI uri to store asynchronous result
   */
  public final void setResultURI(String resultURI) {
    this.resultURI = resultURI;
  }


  /**
   * Provides the query to process as a String + Mime + Charset
   * 
   * Convenience method that allows setting the text.
   * "mime" is the mime expected by the server (e.g., text/query).
   * @param text Text to process
   * @param mime Mime type as described in @{link sourceMime}
   * @param chSet Character set for the text encoding
   */
  public final void setText(String text, String mime, Charset chSet) {
    this.text = text;
    this.textMime = mime + ";charset=" + chSet.name();
  }
  

  /**
   * 
   * Sets filter parameter
   * 
   * @param filterSpec filter spec
   */
  public final void setFilter(String filterSpec)  {
      filters = filterSpec;
  }
  
  public final void setFilter(ArrayList<Filter> pfilters) throws IdiliaClientException {
    try {
      filters = jsonMapper.writeValueAsString(pfilters);
    } catch (JsonProcessingException e) {
      throw new IdiliaClientException(e);
    }
  }

  /**
   * Specifies a timeout by which the response must be returned.
   * 
   * Timeout in units of hundreds of milliseconds for returning a result
   * (i.e., a value of 10 returns after 1 second). If the scheduling delay + 
   * computation time exceeds this limit, the request aborts immediately and 
   * returns HTTP 504. Useful for real-time applications.
   * 
   * @param timeout units of hundreds of milliseconds for returning a result
   */
  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }
  
  /**
   * Request to include the disambiguated result with the response and its format.
   * 
   * The recognized mime types are:
   * <ul>
   *  <li> application/x+tf:       A TextFeatures in binary format.
   *  <li> application/x+tf+xml:   A TextFeatures in XML format.
   *  <li> application/x+semdoc+xml: The semantic document format.
   * </ul>
   * <p>
   * Appending "+gz" to the mime will return a result in gzip format (RFC1951, RFC1952).
   * In addition to the reduced size, this format includes a CRC-32 that ensures
   * that the result was not corrupted during transmission.
   * Default is "application/x+semdoc+xml".
   *
   * 
   * @param wsdMime Mime type for the disambiguated query.
  */
  
  public final void setWsdMime(String wsdMime) {
    this.wsdMime = wsdMime;
  }


  @Override
  final public String requestPath() {
    return "/1/text/match.json";
  }
  
  @Override
  public Class<? extends ResponseBase> responseClass() { return MatchResponse.class; }
    
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IllegalStateException {
    
    if (this.text == null || this.text.length() == 0)
      throw new IllegalStateException("No text specified");
    
    // Add base parameters
    super.getHttpQueryParms(parms);
    
    parms.add(new BasicNameValuePair("text", text));
    parms.add(new BasicNameValuePair("textMime", textMime));
    
    if (wsdMime != null && wsdMime.length() > 0)
      parms.add(new BasicNameValuePair("wsdMime", wsdMime));
    if (disambiguationRecipe != null && disambiguationRecipe.length() > 0)
      parms.add(new BasicNameValuePair("disambiguationRecipe", disambiguationRecipe));

    if (resultURI != null && resultURI.length() > 0)
      parms.add(new BasicNameValuePair("resultURI", resultURI));
    if (notificationURI != null && notificationURI.length() > 0)
      parms.add(new BasicNameValuePair("notificationURI", notificationURI));
    if (timeout != 0)
      parms.add(new BasicNameValuePair("timeout", timeout.toString()));
    if (maxCount != null)
      parms.add(new BasicNameValuePair("maxCount", maxCount.toString()));
    if (!filters.isEmpty())
      parms.add(new BasicNameValuePair("filters", filters));
  }
  
  @Override
  final public byte[] toSign() throws IOException {
    return text.getBytes();
  }
  
  private String text;
  private String textMime;
  private String filters;
  private String wsdMime;
  private String disambiguationRecipe;
  private String resultURI;
  private String notificationURI;
  private Integer timeout = 0;
  private Integer maxCount;
  private static final ObjectMapper jsonMapper = new ObjectMapper();
}
