package com.idilia.services.kb;

import com.idilia.services.base.ResponseBase;

/**
 * Response class from the KB Server API for a TaggingMenuRequest
 *
 */
public class TaggingMenuResponse extends ResponseBase {
  public String text;
  public String menu;
  
  /**
   * Return the HTML that defines the menu. This HTML is expected by plugin jquery_tagging_menu.js.
   * It contains a sense menu for each word in the source text.
   * @return HTML string for the tagging menu
   */
  public final String getMenu() {
    return menu;
  }
  
  /**
   * Return the HTML for the text of the menu. This HTML is expected by plugin jquery_tagging_menu.js.
   * @return HTML string for the text of the menu
   */
  public final String getText() {
    return text;
  }

}
