/*
 * Definition of root element "extRef"
 */
package com.idilia.services.kb.objects;

import java.util.Objects;

public class ExtRef {
  private String dm;   // the domain
  private String ref;  // the unique identifier within the domain

  public String getDm() {
    return dm;
  }
  public void setDm(String dm) {
    this.dm = dm;
  }
  public String getRef() {
    return ref;
  }
  public void setRef(String ref) {
    this.ref = ref;
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ExtRef))
      return false;
    ExtRef other = (ExtRef) o;
    return dm.equals(other.dm) && ref.equals(other.ref);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(dm, ref);
  }
}
