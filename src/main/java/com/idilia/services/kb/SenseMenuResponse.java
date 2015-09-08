package com.idilia.services.kb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idilia.services.base.ResponseBase;

/**
 * Response class from the KB Server API for a SenseMenuRequest
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SenseMenuResponse extends ResponseBase {

  public String menu;
  
  /**
   * Return the HTML that defines the menu. This HTML is expected by plugin jquery_sense_menu.js.
   * @return HTML string for the menu
   */
  public final String getMenu() {
    return menu;
  }
  
}
