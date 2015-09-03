package com.idilia.services.kb;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.idilia.services.base.RequestBase;

public class SenseCardRequest extends RequestBase {
  
  public enum Special { 
    other,
    any,    // the any sense card
    filler, // card for a filler word or punctuation
  };
  
  public SenseCardRequest() {}
  public SenseCardRequest(String fsk) { this.fsk = fsk; }
  
  /**
   * Construct a sense request for a special card.
   * @param sp Type of special card requested
   * @param word Optional argument. When provided becomes that card title and value in data-fsk
   */
  public SenseCardRequest(Special sp, String word) {
    this.special = sp.toString();
    this.word = word;
  }
  
  //
  // Accessors
  //
  public final String getSenseKey() {
    return fsk;
  }
  
  public final SenseCardRequest setSenseKey(String fsk) {
    this.fsk = fsk;
    return this;
  }
  
  public final SenseCardRequest setTemplate(String t) {
    this.tmplt = t;
    return this;
  }
  
  // Encode the content as HTTP query parameters
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IllegalStateException {
    
    // Add base parameters
    super.getHttpQueryParms(parms);
    if (fsk != null)
      parms.add(new BasicNameValuePair("fsk", fsk));
    if (tmplt != null)
      parms.add(new BasicNameValuePair("template", tmplt));
    if (special != null)
      parms.add(new BasicNameValuePair("special", special));
    if (word != null)
      parms.add(new BasicNameValuePair("word", word));
  }
  
  
  // Return the content to sign when creating the authentication information
  @Override
  final public byte[] toSign() throws IOException {
    if (fsk != null)
      return fsk.getBytes();
    else
      return special.getBytes();
  }
  
  /**
   * Returns the request path for the appropriate REST method on the server.
   * @return request path.
   */
  @Override
  public String requestPath() {
    return new String("/1/kb/sense_card.json");
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(fsk, word);
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SenseCardRequest)) return false;
    SenseCardRequest other = (SenseCardRequest) o;
    return Objects.equals(fsk, other.fsk) &&
        Objects.equals(tmplt, other.tmplt) &&
        Objects.equals(special, other.special) &&
        Objects.equals(word, other.word) &&
        true;
  }
  
  @Override
  public String toString() {
    if (fsk == null)
      return String.format("[SCR]: special: %s, word: %s", special, (word == null ? "" : word).toString());
    else
      return String.format("[SCR]: fsk: %s", fsk);
  }
  // Data elements
  private String fsk;                    // the sensekey
  private String tmplt = "image_v2";     // the template
  private String special;                // special card indicator
  private String word;                   // word used in a special card
}
