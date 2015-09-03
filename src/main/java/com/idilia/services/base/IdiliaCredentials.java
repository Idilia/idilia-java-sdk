/**
 * Copyright (c) 2012 Idilia Inc, All rights reserved.
 * Description:
 *     This file defines the credentials that are supplied when requesting
 *     services from the Idilia REST API.
 */

package com.idilia.services.base;

import java.security.InvalidKeyException;
import java.util.Objects;

final public class IdiliaCredentials {

  /**
   * 
   * @param ak
   *          : Valid access key
   * @param sk
   *          : Matching private key for encoding
   * @throws InvalidKeyException
   */
  public IdiliaCredentials(String ak, String sk) throws InvalidKeyException {
    accessKey = ak;
    secretKey = sk;

    if (accessKey.length() != 13 || secretKey.length() != 30 || !accessKey.startsWith("Idi"))
      throw new InvalidKeyException();
  }

  /**
   * 
   * @param k
   *          : Combination of access key and private key
   * @throws InvalidKeyException
   */
  public IdiliaCredentials(String k) throws InvalidKeyException {
    this(k.substring(0, 13), k.substring(13));
  }

  /**
   * @return The access key.
   */
  public final String getAccessKey() {
    return accessKey;
  }

  /**
   * @return The secret key.
   */
  public final String getSecretKey() {
    return secretKey;
  }

  /**
   * @return Combination of the access key and secret key
   */
  final public String asKey() {
    return accessKey + secretKey;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof IdiliaCredentials))
      return false;
    IdiliaCredentials other = (IdiliaCredentials) o;
    return accessKey.equals(other.accessKey)
        && secretKey.equals(other.secretKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessKey, secretKey);
  }
  
  @Override
  public String toString() {
    return accessKey;
  }

  final private String accessKey;
  final private String secretKey;
}
