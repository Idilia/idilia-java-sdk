/*
 * Definition for elements returned by link queries
 */

package com.idilia.services.kb.objects;

public class Link {
  private String src;  // source node nid
  private String dest; // destination node id
  private String type; // type of edge
  private String dir;  // direction of edge
  
  public Link() {
  }
  
  public String getSrc() {
    return src;
  }
  public void setSrc(String src) {
    this.src = src;
  }
  public String getDest() {
    return dest;
  }
  public void setDest(String dest) {
    this.dest = dest;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getDir() {
    return dir;
  }
  public void setDir(String dir) {
    this.dir = dir;
  }
  
}
