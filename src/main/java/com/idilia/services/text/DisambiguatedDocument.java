/**
 * Copyright (c) 2011 Idilia Inc, All rights reserved.
 */

package com.idilia.services.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

/**
 * Class to hold on disambiguated document
 */
public class DisambiguatedDocument {

  private String resultMime;
  private String encoding;
  private InputStream is;
  private byte[] bytes;

  
  DisambiguatedDocument(String m, String encoding, InputStream is) {
    this.resultMime = m;
    this.encoding = encoding;
    this.is = is;
   }
  
  /**
   * Returns the resultMime. Matches the same field in the request. E.g., application/x-semdoc+xml
   * @return the result mime of the document
   */
  
  public String getResultMime() { return this.resultMime; }
  
  /**
   * Returns the encoding of the document. E.g., gzip.
   * @return the encoding of the document
   */
  public String getEncoding() { return this.encoding; }
  
  /**
   * Return the content of the result as a stream.
   * @return an input stream with the decoded (if applicable) content of the disambiguated document
   * @throws IOException when the input stream cannot be extracted
   */
  public InputStream getInputStream() throws IOException {
    InputStream i = this.getEncodedInputStream();
    return encoding != null ? new GZIPInputStream(i) : i;

  }
  
  /**
   * Return the content of the result as a the raw stream which is possibly encoded.
   * @return an inputstream with the raw document returned by the text server.
   * @throws IOException when the input stream cannot be extracted
   */
  public InputStream getEncodedInputStream() throws IOException {
    if (bytes == null)
      bytes = IOUtils.toByteArray(is);
    return new ByteArrayInputStream(this.bytes);
  }
}
