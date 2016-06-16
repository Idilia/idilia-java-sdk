package com.idilia.services.base;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.protocol.HttpContext;

/**
 * Interceptors to install within the client to request gzip compression
 * and decompress  gzipped responses.
 */
class GzipInterceptors {

  /** Request Interceptor */
  static class GzipRequestInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
      if (!request.containsHeader("Accept-Encoding")) {
        request.addHeader("Accept-Encoding", "gzip");
      }
    }
  }

  /** Response Interceptor */
  static class GzipResponseInterceptor implements HttpResponseInterceptor {

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        Header ceheader = entity.getContentEncoding();
        if (ceheader != null) {
          HeaderElement[] codecs = ceheader.getElements();
          for (int i = 0; i < codecs.length; i++) {
            if (codecs[i].getName().equalsIgnoreCase("gzip")) {
              response.setEntity(
                  new GzipDecompressingEntity(response.getEntity()));
              return;
            }
          }
        }
      }
    }
  }

}
