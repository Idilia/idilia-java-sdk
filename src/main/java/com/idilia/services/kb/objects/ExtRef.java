package com.idilia.services.kb.objects;

import java.util.Objects;

/*
 * Definition of root element "extRef".
 */
public class ExtRef {
  private String dm;
  private String ref;

  /**
   * The domain for the external reference. E.g., wikipedia
   * @return string value for the domain of the external reference
   */
  public String getDm() {
    return dm;
  }
  
  public void setDm(String dm) {
    this.dm = dm;
  }
  
  /**
   * The unique reference value within the domain.
   * @return string value for the unique reference within the domain
   */
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
