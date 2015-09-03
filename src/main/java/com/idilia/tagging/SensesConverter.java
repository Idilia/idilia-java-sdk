package com.idilia.tagging;

import java.util.List;

public class SensesConverter {

  /**
   * Convert the Senses information into an HTML markup sequence.
   * This sequence is compatible with the input of the text/disambiguate API
   * and can be used to regenerate a tagging menu initialized with the same senses.
   * @param senses senses previously reported for the text sequence
   * @return HTML markup string.
   */
  static public String toHtml(List<Sense> senses) {
    
    StringBuilder sb = new StringBuilder(senses.size() * 64);
    for (Sense sense: senses) {
      if (sense.getFsk() != null) {
        sb.append("<span data-idl-fsk=\"").append(sense.getFsk().replace("\"", "&quot;")).append("\">");
        sb.append(sense.getText());
        sb.append("</span>");
      } else
        sb.append(sense.getText());
      if (sense.isSpcAft())
        sb.append(' ');
    }
    return sb.toString();
  }
  
  
  /**
   * Extract the surface text in the range given from the Senses information.
   * @param senses senses returned by the tagging menu
   * @param startOffset starting token offset
   * @param numToks number of tokens spanned
   * @return surface text covered by the tokens in the given range
   */
  static public String toText(List<Sense> senses, int startOffset, int numToks) {
    StringBuilder sb = new StringBuilder(numToks * 10);
    int endOffset = startOffset + numToks;
    for (Sense sense: senses)
      if (sense.getStart() + sense.getLen() > endOffset)
        break;
      else if (sense.getStart() >= startOffset) {
        sb.append(sense.getText());
        if (sense.isSpcAft())
          sb.append(' ');
      }
    
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ')
      sb.setLength(sb.length() - 1);
    return sb.toString();
  }
  
}
