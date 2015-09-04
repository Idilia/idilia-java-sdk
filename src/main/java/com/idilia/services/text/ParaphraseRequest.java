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
import java.util.List;
import java.util.Objects;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.RequestBase;

/**
 * Message for obtaining paraphrases.
 * 
 * If a @resultURI is provided, the result document is processed offline. If not
 * provided, the response is returned when the paraphrases are available.
 */

public class ParaphraseRequest extends RequestBase {

  /**
   * Sets the type of disambiguation recipe to use.
   * 
   * This parameter identifies the recipe that is used for processing the
   * request. Recipes can be tuned for real-time response, document type, etc.
   * All recipes specified here must be available on the server processing the
   * request. See
   * https://www.idilia.com/developer/sense-analysis/concepts/sense-
   * analysis-recipes for possible values.
   * 
   * @param recipeKey recipeKey
   */
  public final void setDisambiguationRecipe(String recipeKey) {
    this.disambiguationRecipe = recipeKey;
  }

  /**
   * Sets the maximum number of paraphrases to return.
   * 
   * When this parameter is not specified, the service's default of 10 is used.
   * 
   * @param maxCount
   *          Maximum number of paraphrases to return.
   */
  public final void setMaxCount(int maxCount) {
    this.maxCount = maxCount;
  }

  /**
   * Sets minimum weight of returned paraphrases.
   *
   * @param minW minimum weight
   */
  public final void setMinWeight(Double minW) {
    this.minWeight = minW;
  }

  /**
   * Specifies the URI for completion notification.
   * 
   * A signaling channel where the client can be informed when a request has
   * completed. The following are valid: - sqs://&lt;AWS queue&gt;
   * 
   * When using an AWS S3QS queue, it must be writable by the account operating
   * the servers. Please refer to our web site for our account number.
   * 
   * The message received on the queue is identical to the message deposited at
   * the @resultURI except that it does not contain the actual disambiguation
   * result.
   * 
   * @param notificationURI notificationURI
   * 
   */

  public final void setNotificationURI(String notificationURI) {
    this.notificationURI = notificationURI;
  }

  /**
   * Sets the type of paraphrasing recipe to use.
   * 
   * This parameter identifies the paraphrasing processing of the request.
   * Please refer to
   * http://www.idilia.com/developer/query-rewriting/concepts/query
   * -rewrite-recipes/ for available values.
   * 
   * @param recipeKey recipeKey
   */
  public final void setParaphrasingRecipe(String recipeKey) {
    this.paraphrasingRecipe = recipeKey;
  }

  public enum UserAction {
    PARAPHRASE, FREEZE, REMOVE
  }

  /**
   * Sets superfluous adjectives user option.
   * 
   * Please refer to
   * http://www.idilia.com/developer/query-rewriting/api/text-paraphrase/
   * 
   * @param action action
   */
  public final void setActionSuperfluousAdjectives(UserAction action) {
    this.actionSuperfluousAdjectives = getUserAction(action);
  }

  /**
   * Sets adjectives user option.
   * 
   * Please refer to
   * http://www.idilia.com/developer/query-rewriting/api/text-paraphrase/
   * 
   * @param action action
   */
  public final void setActionAdjectives(UserAction action) {
    this.actionAdjectives = getUserAction(action);
  }

  /**
   * Sets adverbs user option.
   * 
   * Please refer to
   * http://www.idilia.com/developer/query-rewriting/api/text-paraphrase/
   * 
   * @param action action
   */
  public final void setActionAdverbs(UserAction action) {
    this.actionAdverbs = getUserAction(action);
  }

  /**
   * Sets verbs user option.
   * 
   * Please refer to
   * http://www.idilia.com/developer/query-rewriting/api/text-paraphrase/
   * 
   * @param action action
   */
  public final void setActionVerbs(UserAction action) {
    this.actionVerbs = getUserAction(action);
  }

  /**
   * Sets nouns user option.
   * 
   * Please refer to
   * http://www.idilia.com/developer/query-rewriting/api/text-paraphrase/
   * 
   * @param action action
   */
  public final void setActionNouns(UserAction action) {
    this.actionNouns = getUserAction(action);
  }

  public static String getUserAction(UserAction action) {
    switch (action) {
    case PARAPHRASE:
      return "paraphrase";
    case FREEZE:
      return "freeze";
    case REMOVE:
      return "remove";
    }
    return null;
  }

  public static UserAction getUserAction(String action)
      throws IllegalStateException {

    if (action != null && action.equals("paraphrase"))
      return UserAction.PARAPHRASE;

    if (action != null && action.equals("freeze"))
      return UserAction.FREEZE;

    if (action != null && action.equals("remove"))
      return UserAction.REMOVE;

    throw new IdiliaClientException("null or unknown action");
  }

  public final void setTransformationSynonymy(String value) {
    this.transformationSynonymy = value;
  }

  public final void setTransformationAssociation(String value) {
    this.transformationAssociation = value;
  }

  public final void setTransformationSpecialization(String value) {
    this.transformationSpecialization = value;
  }

  public final void setTransformationGeneralization(String value) {
    this.transformationGeneralization = value;
  }

  public final void setTransformationSyntax(String value) {
    this.transformationSyntax = value;
  }

  public final void setFilterFrequency(String value) {
    this.filterFrequency = value;
  }

  /**
   * Specifies the URI for the result.
   * 
   * When this parameter is present, the server validates the request and
   * returns an immediate response to the request. When the request is valid,
   * the server queues the document for disambiguation and stores the result at
   * this URI when available. An application may poll the repository or specify
   * parameter @notificationURI to obtain an availability notification.
   * 
   * The given URI must be writable. If the name ends with "gz" the output file
   * is compressed in "gzip" format (RFC1951, RFC1952). The name can be preceded
   * with the following protocols: <ul><li>those recognized by CURL: ftp:// file://
   * <li>s3:// -- for storing in the AWS network. The URI provided has the form
   * "bucket/file_name" (e.g., s3://com-idilia-bucket/request-123.xml)</ul>
   * 
   * When using an AWS S3 bucket, the bucket must be writable by the account
   * operating the servers. Please refer to our web site for our account number.
   * 
   * @param resultURI result destination
   */
  public final void setResultURI(String resultURI) {
    this.resultURI = resultURI;
  }

  /**
   * Provides the query to process.
   * 
   * @param text query to process
   */
  public final void setText(String text) {
    this.text = text;
  }

  /**
   * Provides the query to process as a String + Mime + Charset
   * 
   * Convenience method that allows setting the text. "mime" is the mime
   * expected by the server (e.g., text/query).
   * 
   * @param text
   *          Text to process
   * @param mime
   *          Mime type as described in @{link sourceMime}
   * @param chSet
   *          Character set for the text encoding
   */
  public final void setText(String text, String mime, Charset chSet) {
    setText(text);
    setTextMime(mime, chSet);
  }

  /**
   * Provides the character set of @text
   * 
   * @param mime
   * @param chSet
   */
  public final void setTextMime(String mime, Charset chSet) {
    this.textMime = mime;
    if (chSet != null)
      this.textMime += ";charset=" + chSet.name();
  }

  /**
   * Specifies a timeout by which the response must be returned.
   * 
   * Timeout in units of hundreds of milliseconds for returning a result (i.e.,
   * a value of 10 returns after 1 second). If the scheduling delay +
   * computation time exceeds this limit, the request aborts immediately and
   * returns HTTP 504. Useful for real-time applications.
   * 
   * @param timeout
   *          Timeout in tenths of second.
   */
  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  /**
   * Request to include the disambiguated result with the response and its
   * format.
   * 
   * The recognized mime types are: <ul><li>application/x+tf: A TextFeatures in
   * binary format. <li>application/x+tf+xml: A TextFeatures in XML format. <li>
   * application/x+semdoc+xml: The semantic document format.</ul>
   * <p>
   * Appending "+gz" to the mime will return a result in gzip format (RFC1951,
   * RFC1952). In addition to the reduced size, this format includes a CRC-32
   * that ensures that the result was not corrupted during transmission. Default
   * is "application/x+semdoc+xml".
   *
   * 
   * @param wsdMime
   *          Mime type for the disambiguated query.
   */

  public final void setWsdMime(String wsdMime) {
    this.wsdMime = wsdMime;
  }

  // Return the name of the REST path used when accessing the server
  /**
   * Returns the request path for the appropriate REST method on the server.
   * 
   * @return request path.
   */
  @Override
  final public String requestPath() {
    return new String("/1/text/paraphrase.mpjson");
  }

  // Encode the content as HTTP query parameters
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms)
      throws IdiliaClientException {

    if (this.text == null || this.text.length() == 0)
      throw new IdiliaClientException("No text specified");

    // Add base parameters
    super.getHttpQueryParms(parms);

    parms.add(new BasicNameValuePair("text", text));
    parms.add(new BasicNameValuePair("textMime", textMime));

    if (wsdMime != null && wsdMime.length() > 0)
      parms.add(new BasicNameValuePair("wsdMime", wsdMime));
    if (disambiguationRecipe != null && disambiguationRecipe.length() > 0)
      parms.add(new BasicNameValuePair("disambiguationRecipe",
          disambiguationRecipe));
    if (paraphrasingRecipe != null && paraphrasingRecipe.length() > 0)
      parms
          .add(new BasicNameValuePair("paraphrasingRecipe", paraphrasingRecipe));

    // user options
    if (actionSuperfluousAdjectives != null)
      parms.add(new BasicNameValuePair("superfluousAdjectives",
          this.actionSuperfluousAdjectives));
    if (actionAdjectives != null)
      parms.add(new BasicNameValuePair("adjectives", this.actionAdjectives));
    if (actionAdverbs != null)
      parms.add(new BasicNameValuePair("adverbs", this.actionAdverbs));
    if (actionVerbs != null)
      parms.add(new BasicNameValuePair("verbs", this.actionVerbs));
    if (actionNouns != null)
      parms.add(new BasicNameValuePair("nouns", this.actionNouns));

    if (resultURI != null && resultURI.length() > 0)
      parms.add(new BasicNameValuePair("resultURI", resultURI));
    if (notificationURI != null && notificationURI.length() > 0)
      parms.add(new BasicNameValuePair("notificationURI", notificationURI));
    if (timeout != 0)
      parms.add(new BasicNameValuePair("timeout", timeout.toString()));
    if (maxCount != null)
      parms.add(new BasicNameValuePair("maxCount", maxCount.toString()));
    if (minWeight != null)
      parms.add(new BasicNameValuePair("minWeight", minWeight.toString()));

    // transformation parameters
    String transformations = "";
    if (transformationSynonymy != null)
      transformations += transformationSynonymy + ",";
    if (transformationAssociation != null)
      transformations += transformationAssociation + ",";
    if (transformationSpecialization != null)
      transformations += transformationSpecialization + ",";
    if (transformationGeneralization != null)
      transformations += transformationGeneralization + ",";
    if (transformationSyntax != null)
      transformations += transformationSyntax + ",";
    if (!transformations.isEmpty())
      parms.add(new BasicNameValuePair("transformations", transformations));

    if (filterFrequency != null)
      parms.add(new BasicNameValuePair("filters", filterFrequency));
  }

  // Return the content to sign when creating the authentication information
  @Override
  final public byte[] toSign() throws IOException {
    return text.getBytes();
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, textMime, wsdMime, disambiguationRecipe,
        paraphrasingRecipe, resultURI, notificationURI, timeout, maxCount,
        minWeight, actionSuperfluousAdjectives, actionAdjectives,
        actionAdverbs, actionVerbs, actionNouns, transformationSynonymy,
        transformationAssociation, transformationSpecialization,
        transformationGeneralization, transformationSyntax, filterFrequency);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof ParaphraseRequest))
      return false;
    ParaphraseRequest other = (ParaphraseRequest) o;
    return Objects.equals(text, other.text)
        && Objects.equals(textMime, other.textMime)
        && Objects.equals(wsdMime, other.wsdMime)
        && Objects.equals(disambiguationRecipe, other.disambiguationRecipe)
        && Objects.equals(paraphrasingRecipe, other.paraphrasingRecipe)
        && Objects.equals(resultURI, other.resultURI)
        && Objects.equals(notificationURI, other.notificationURI)
        && Objects.equals(timeout, other.timeout)
        && Objects.equals(maxCount, other.maxCount)
        && Objects.equals(minWeight, other.minWeight)
        && Objects.equals(actionSuperfluousAdjectives,
            other.actionSuperfluousAdjectives)
        && Objects.equals(actionAdjectives, other.actionAdjectives)
        && Objects.equals(actionAdverbs, other.actionAdverbs)
        && Objects.equals(actionVerbs, other.actionVerbs)
        && Objects.equals(actionNouns, other.actionNouns)
        && Objects.equals(transformationSynonymy, other.transformationSynonymy)
        && Objects.equals(transformationAssociation,
            other.transformationAssociation)
        && Objects.equals(transformationSpecialization,
            other.transformationSpecialization)
        && Objects.equals(transformationGeneralization,
            other.transformationGeneralization)
        && Objects.equals(transformationSyntax, other.transformationSyntax)
        && Objects.equals(filterFrequency, other.filterFrequency);
  }

  private String text;
  private String textMime = "text/query; charset=utf8";
  private String wsdMime;
  private String disambiguationRecipe;
  private String paraphrasingRecipe;
  private String resultURI;
  private String notificationURI;
  private Integer timeout = 0;
  private Integer maxCount;
  private Double minWeight;
  private String actionSuperfluousAdjectives;
  private String actionAdjectives;
  private String actionAdverbs;
  private String actionVerbs;
  private String actionNouns;
  private String transformationSynonymy;
  private String transformationAssociation;
  private String transformationSpecialization;
  private String transformationGeneralization;
  private String transformationSyntax;
  private String filterFrequency;
}
