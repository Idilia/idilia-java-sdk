package com.idilia.services.kb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.text.DisambiguatedDocument;

public class TaggingMenuRequest extends MenuRequest {

  @JsonIgnore
  public final ContentBody getTf() {
    return tf;
  }
  @JsonIgnore
  public final TaggingMenuRequest setTf(DisambiguatedDocument tf) throws IdiliaClientException {
    try {
      byte[] bytes = IOUtils.toByteArray(tf.getEncodedInputStream());
      // Work around the MultipartEntityBuilder inability for us to add a form
      // with Content-Encoding set. Therefore we ensure that we communicate 
      // the encoding through the Content-Type.
      String resultMime = tf.getResultMime();
      ContentType ct = null;
      if (tf.getEncoding() != null && tf.getEncoding().contentEquals("gzip")) {
        if (!resultMime.endsWith("+gz"))
          resultMime += "+gz";
        ct = ContentType.create(resultMime);
      } else
        ct = ContentType.create(resultMime, Consts.UTF_8);
      this.tf = new ByteArrayBody(bytes, ct, "tf");
      return this;
    } catch (IOException ioe) {
      throw new IdiliaClientException(ioe);
    }
  }
  
  public final int getTfStart() {
    return tfStart;
  }
  public final TaggingMenuRequest setTfStart(int tfStart) {
    this.tfStart = tfStart;
    return this;
  }
  public final int getTfEnd() {
    return tfEnd;
  }
  public final TaggingMenuRequest setTfEnd(int tfEnd) {
    this.tfEnd = tfEnd;
    return this;
  }
  
  @Override
  public TaggingMenuRequest setTemplate(String t) {
    super.setTemplate(t);
    return this;
  }
  
  @Override
  final public String requestPath() {
    return new String("/1/kb/tagging_menu.json");
  }
  
  @Override
  protected void getHttpQueryParms(List<NameValuePair> parms) throws IdiliaClientException {
    if (tf == null)
      throw new IdiliaClientException("TextFeatures attachment not provided.");
    if (tfStart != -1)
      parms.add(new BasicNameValuePair("tfStart", String.valueOf(tfStart)));
    if (tfEnd != -1)
      parms.add(new BasicNameValuePair("tfEnd", String.valueOf(tfEnd)));
    super.getHttpQueryParms(parms);
  }
  
  @Override
  public HttpEntity getContent() {
    
    // Add a part with as a form
    List<NameValuePair> parms = new ArrayList<NameValuePair>();
    getHttpQueryParms(parms);
    String parmsText = URLEncodedUtils.format(parms, Consts.UTF_8);
    
    MultipartEntityBuilder builder =  MultipartEntityBuilder.create()
        .addTextBody("parms", parmsText, ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8));
    
    // Add a part with the tf. This is where Content-Encoding did not work
    // as the method addPart(BodyPart) is not visible. 
    builder.addPart("tf", tf);
    return builder.build();
  }
  
  @Override
  final public byte[] toSign() throws IOException {
    int l = (int) tf.getContentLength();
    ByteArrayOutputStream signOs = new ByteArrayOutputStream(l);
    tf.writeTo(signOs);
    return signOs.toByteArray();
  }
  
  private ContentBody tf;       // textfeatures, when senseSource is "req"
  private int tfStart = -1, tfEnd = -1;    // fragment of TF to select
}
