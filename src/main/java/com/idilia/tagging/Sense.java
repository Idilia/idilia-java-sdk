package com.idilia.tagging;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class to hold the information gathered when using the tagging menu
 * to select the meanings for the words in a text. Each sense selected
 * translated to one instance of this class.
 * 
 * The name of the properties directly match the properties generated
 * by the jquery_tagging_menu plugin when using method "sensesAsObjects".
 * This object is intended to be automatically populated from a JSON request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sense implements Comparable<Sense> {
  
  /** starting token offset in the text */ 
  private int start;
  
  /** number of tokens consumed */
  private int len;   
  
  /** surface word */
  private String text;
  
  /** selected sensekey */
  private String fsk;
  
  /** true when a space after in the text */
  private boolean spcAft;
  
  public Sense() {
    this.start = this.len = 0;
    this.spcAft = false;
  }
  
  public Sense(int start, int len, String text, String fsk) {
    this.start = start;
    this.len = len;
    this.text = text;
    this.fsk = fsk;
    this.spcAft = false;
  }
  
  /**
   * Return the starting token offset within the text.
   *
   * @return starting token offset of sense
   */
  public final int getStart() {
    return start;
  }
  
  public final void setStart(int start) {
    this.start = start;
  }
  
  /**
   * Return the number of tokens spanned by the sense.
   *
   * @return number of tokens spanned by the sense
   */
  public final int getLen() {
    return len;
  }
  public final void setLen(int len) {
    this.len = len;
  }
  
  /** 
   * @return text covered by the sense
   */
  public final String getText() {
    return text;
  }
  public final void setText(String text) {
    this.text = text;
  }
  
  
  /**
   * Return the sensekey assigned to this text. This is normally in the form
   * of "lemma/[NVJD]&lt;number&gt;". Special values for the part after the slash are:<ul>
   * <li>_UNK_: (unknown): An unknown sense. Triggered by selecting "other meaning" in the sense menu.
   * <li>_WC_:  (wildcard): Any sense. Triggered by selecting "any meaning" in the sense menu.
   * <li>_INA_: (inapplicable): Indicates that no meaning relevant for this word. E.g., punctuation, conjunctions and other closed-class words.
   * </ul>
   * 
   * May also be null when a word in the expression is disabled for tagging.
   * 
   * @return the selected sensekey or null when a disabled word.
   */
  public String getFsk() {
    return fsk;
  }
  public final void setFsk(String fsk) {
    this.fsk = fsk;
  }
  
  /**
   * Return true when the tagged expression has a space after the text of this meaning. This can
   * be used to reconstruct the original expression from a list of Sense.
   *
   * @return space after
   */
  public final boolean isSpcAft() {
    return spcAft;
  }
  public final void setSpcAft(boolean spcAft) {
    this.spcAft = spcAft;
  }
  
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Sense)) return false;
    Sense other = (Sense) o;
    return
        start == other.start &&
        len == other.len &&
        text.equals(other.text) &&
        Objects.equals(fsk, other.fsk) &&
        spcAft == other.spcAft &&
        true;
  }
  
  @Override
  public int hashCode() {
    return text.hashCode();
  }
  
  @Override
  public String toString() {
    return fsk != null ? fsk : text;
  }

  @Override
  public int compareTo(Sense other) {
    /* Return by increasing start and increasing length. Then compare string + fsk. */
    if (start != other.start)
      return start - other.start;
    if (len != other.len)
      return len - other.len;
    int c = text.compareTo(other.text);
    if (c != 0) return c;
    if (fsk != null && other.fsk != null)
      return fsk.compareTo(other.fsk);
    else if (fsk == null && other.fsk == null)
      return 0;
    else
      return fsk == null ? -1 : 1;
  }

}
