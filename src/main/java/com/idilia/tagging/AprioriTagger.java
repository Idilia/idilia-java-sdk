package com.idilia.tagging;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that allows tagging selected words in a text using
 * seed senses. For each one of these senses, we attempt to find
 * their surface into the text to tag. When found, we annotate the
 * text with an HTML markup recording the sensekey.
 * <p>
 * The markup inserted is in the form:
 *   {@code <span data-idl-fsk="dog/N1">dogs</span>}
 * <p>
 * Instances of this class are created using the {@link AprioriTaggerBuilder}
 */
public class AprioriTagger {

  final private Pattern re;
  final private TreeMap<String, String> surfToSks;
  final private boolean ci;
  
  /**
   * Constructor. Record the senses given. If a duplicate surface is found,
   * the first sense key encountered has priority over the others.
   * This object is created using an {@link AprioriTaggerBuilder}.
   * 
   * @param senses senses (surface-sense pairs)
   * @param caseInsensitive whether to treat as case sensitive
   * @param acceptPlurals whether to match plurals
   */
  AprioriTagger(Iterable<Sense> senses, boolean caseInsensitive, boolean acceptPlurals) {
    this.ci = caseInsensitive;
    
    /*
     * Create the surface map with a comparator to ensure that the surface map
     * is ordered to have the longest entries first. This is an attempt to match
     * compounds prior to single words. Note that this strategy is not
     * sufficient when we have competing compounds. But that's an acceptable
     * compromise.
     */
    this.surfToSks = new TreeMap<>((arg0, arg1) -> {
        int c = arg1.length() - arg0.length();
        if (c != 0) return c;
        return arg0.compareTo(arg1);
      });
    
    /* Record the resulting sense for each the surfaces */
    for (Sense sns: senses) {
      if (sns.getFsk() == null)
        continue;
      String surf = ci ? sns.getText().toLowerCase() : sns.getText();
      /* Bit of a dirty hook here to prevent disrupting the existing
       * <span data-idl-fsk="ina"></span> that are present in the input.
       * We just don't allow tagging any word that is either of span or ina.
       * The others (data, idl, fsk) don't matter because we don't retag when
       * hyphen bounded.
       */
      if (surf.contentEquals("span") || surf.contentEquals("ina"))
        continue;
      surfToSks.putIfAbsent(surf, sns.getFsk());
    }
    
    if (surfToSks.isEmpty()) {
      this.re = null;
      return;
    }
    
    HashMap<String, String> plurals = null;
    if (acceptPlurals)
      plurals = new HashMap<String, String>();
    
    /* Construct a regular expression that we can use for mapping
     * between the surfaces to the senses.
     * The REs use word boundary excluding the hyphen. */
    StringBuilder sb = new StringBuilder(surfToSks.size() * 16);
    sb.append("(?<![\\w-])(");
    for (Map.Entry<String,String> surfFsk: surfToSks.entrySet()) {
      
      String surf = surfFsk.getKey();
      sb.append(surf).append('|');
      
      /* Add possible plural form when a common word noun. */
      if (acceptPlurals) {
        Matcher m = skRe.matcher(surfFsk.getValue());
        if (m.find() && m.group(2).charAt(0) == 'N' && Character.isLowerCase(m.group(1).charAt(0))) {
          String lemma = m.group(1);
          if (surf.equals(lemma)) {
            /* Very simple pluralization rule here. Lots of exceptions not handled. */
            char lastCh = lemma.charAt(lemma.length() - 1);
            String plural;
            if (lastCh != 's')
              plural = surf + "s";
            else
              plural = surf + "es";
            
            if (!surfToSks.containsKey(plural) && 
                plurals.putIfAbsent(plural, surfFsk.getValue()) == null)
              sb.append(plural).append('|');
          }
        }
      }
    }
    
    sb.setLength(sb.length() - 1);
    sb.append(")(?![\\w-])");
    
    if (plurals != null)
      surfToSks.putAll(plurals);
    
    re = Pattern.compile(sb.toString(), ci ? Pattern.CASE_INSENSITIVE : 0);
  }
  
  
  /**
   * Tag the given expression. 
   * <p>
   * Where a word in the text corresponds to one key in surfToSks, we
   * wrap the word with an HTML span with a data element containing
   * the sensekey. Word matching is case sensitive.
   * @param expr text expression to tag. This expression may already contain sense
   *   constraints (i.e., span elements with attribute data-idl-fsk).
   * @return an HTML annotated string with sense tags where possible.
   */
  public String tag(String expr) {
    if (re == null)
      return expr;
    
    /* Search the given string for all matches in our pattern and replace
     * with the text to set the sense in the tagging menu. */
    StringBuilder resSb = new StringBuilder(expr.length() * 2);
    Matcher matcher = re.matcher(expr);
    int lastExprIdx = 0;
    while (matcher.find()) {
      /* Add text up to match */
      if (matcher.start() > lastExprIdx)
        resSb.append(expr, lastExprIdx, matcher.start());
      
      /* Recover the surface and sensekey corresponding to the match */
      String surf = matcher.group();
      String fsk = surfToSks.get(surf);
      if (fsk == null && ci)
        fsk = surfToSks.get(surf.toLowerCase());
      
      /* Insert the markup */
      if (fsk != null) {
        resSb.append("<span data-idl-fsk=\"").append(fsk);
        resSb.append("\">");
        resSb.append(matcher.group(0));
        resSb.append("</span>");
        lastExprIdx = matcher.end();
      }
    }
    resSb.append(expr, lastExprIdx, expr.length());
    return resSb.toString();
  }
  
  static private final Pattern skRe = Pattern.compile("^([^\\s]+)\\/([NJVD])(\\d{1,4}|_WC_|_UNK_|_INA_)$");
}
