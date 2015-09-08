package com.idilia.services.kb;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.RequestBase;

/**
 * Request message for a sense card
 */
public class SenseCardRequest extends RequestBase {
  
  public SenseCardRequest() {}
  
  /**
   * Construct a request for the given sensekey.
   * <p>
   * @param fsk sense key of the card. This is a sense from the Knowledge Base
   * (e.g., dog/N1) or one of the special meaning forms:
   * <ul>
   * <li> word/_UNK_: A card with "other" meaning
   * <li> word/_INA_: A card with "not a meaning" (e.g., closed class words)
   * <li> word/_WC_: A card with "any" meaning (i.e., wildcard)
   * </ul>
   */
  public SenseCardRequest(String fsk) { 
    this.fsk = fsk;
  }
  
  /**
   * Set the template for the card. Defaults to "image_v2". If the name of the template
   * starts with "menu_" (e.g., menu_image_v2), the card returned is in the same
   * format as the cards in a sense menu. Otherwise the card use the large card format.
   * @param t template to use when generating the card
   * @return updated request object
   */
  public final SenseCardRequest setTemplate(String t) {
    this.tmplt = t;
    return this;
  }
  
  /**
   * The number of tokens spanned in the text is an attribute of a card in a tagging menu.
   * This parameter can be set to obtain a card suitable for insertion into a menu. Normally
   * used with a template starting with "menu_".
   * @param len value to set as the data-len attribute in the generated card
   * @return updated request object
   */
  public final SenseCardRequest setLength(Integer len) {
    this.len = len;
    return this;
  }
  
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IdiliaClientException {
    
    if (tmplt == null)
      throw new IdiliaClientException("Parameter template must be set");
    
    // Add base parameters
    super.getHttpQueryParms(parms);
    parms.add(new BasicNameValuePair("fsk", fsk));
    parms.add(new BasicNameValuePair("template", tmplt));
    if (len != null)
      parms.add(new BasicNameValuePair("len", len.toString()));
  }
  
  
  @Override
  final public byte[] toSign() throws IOException {
    return fsk.getBytes();
  }
  
  @Override
  public String requestPath() {
    return new String("/1/kb/sense_card.json");
  }
  
  @Override
  public int hashCode() {
    return fsk.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SenseCardRequest)) return false;
    SenseCardRequest other = (SenseCardRequest) o;
    return Objects.equals(fsk, other.fsk) &&
        Objects.equals(tmplt, other.tmplt) &&
        true;
  }
  
  @Override
  public String toString() {
    return String.format("[SCR]: fsk: %s", fsk);
  }
  // Data elements
  private String fsk;
  private String tmplt;
  private Integer len;
}
