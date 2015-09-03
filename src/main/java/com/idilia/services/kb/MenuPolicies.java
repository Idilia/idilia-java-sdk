package com.idilia.services.kb;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuPolicies {

  public enum SenseCollapsing {
    none,   // do not collapse any senses
    equivs, // collapse the equivalent senses
  };
  
  public enum SenseFiltering {
    noExtRefs, // reject those without external references
    noDynamic, // reject those that are dynamic
  };
  
  public enum SkInfo {
    extRefs,
    schemaOrgT,
  };
  
  public SenseCollapsing senseCollapsing = null;
  public List<SenseFiltering> senseFiltering = new ArrayList<SenseFiltering>();
  public List<SkInfo> skInfo = new ArrayList<SkInfo>();
}
