package com.idilia.services.kb;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.idilia.services.base.IdiliaClientException;

/**
 * Request message to obtain a sense menu for a single word or a multi-word expression known
 * in the Language Graph.
 * <p>
 * When the text may contain multiple distinct words, a TaggingMenuRequest should be used.
 */
public class SenseMenuRequest extends MenuRequest {
  
  /**
   * Specify the text used to generate the sense menu. This text must correspond to a known
   * entry in the Language Graph. If the text is not found, an IdiliaClientException with status
   * "not found" is thrown when the request is processed.
   * @param text string with a word from the Language Graph.
   * @return updated SenseMenuRequest
   */
  public final SenseMenuRequest setText(String text) {
    this.text = text;
    return this;
  }

  /**
   * Specify which sense tile to select by default. Normally no sense are selected but the most
   * frequent senses are listed first. This ensures that the given sense is listed first and
   * appears selected.
   * @param s sensekey of the menu sense to automatically select.
   * @return updated SenseMenuRequest
   */
  public SenseMenuRequest setSelectedFsk(String s) {
    this.selectedFsk = s;
    return this;
  }
  
  @Override
  public SenseMenuRequest setFskInfos(String fskInfos) {
    super.setFskInfos(fskInfos);
    return this;
  }
  @Override
  public SenseMenuRequest setFilters(String filters) {
    super.setFilters(filters);
    return this;
  }
  @Override
  public SenseMenuRequest setCollapsing(String collapsing) {
    super.setCollapsing(collapsing);
    return this;
  }
  @Override
  public SenseMenuRequest setTemplate(String t) {
    super.setTemplate(t);
    return this;
  }
  
 @Override
  final public String requestPath() {
    return new String("/1/kb/sense_menu.json");
  }
  
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IdiliaClientException {
    if (text == null || text.isEmpty())
      throw new IdiliaClientException("No text provided.");
    parms.add(new BasicNameValuePair("text", text));
    if (selectedFsk != null)
      parms.add(new BasicNameValuePair("selectedFsk", selectedFsk));
    super.getHttpQueryParms(parms);
  }
  
  // Return the content to sign when creating the authentication information
  @Override
  final public byte[] toSign() throws IOException {
    return text.getBytes();
  }

  private String text;
  private String selectedFsk;
}
