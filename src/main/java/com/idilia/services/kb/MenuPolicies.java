package com.idilia.services.kb;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Policies for controlling the cards included in the menu and
 * the information provided with each card.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuPolicies {

  public enum SenseCollapsing {
    /** do not collapse any senses */
    none,
    
    /** collapse equivalent senses into their most probable one */
    equivs,
  };
  
  public enum SenseFiltering {
    /** do not include senses without external references */
    noExtRefs,
    
    /** do not include dynamic senses */
    noDynamic,
  };
  
  public enum SkInfo {
    /** include external references as an HTML data attribute */
    extRefs,
    
    /** include schema.org mapping as an HTML data attribute */
    schemaOrgT,
  };
  
  public SenseCollapsing senseCollapsing = null;
  public List<SenseFiltering> senseFiltering = new ArrayList<SenseFiltering>();
  public List<SkInfo> skInfo = new ArrayList<SkInfo>();
}
