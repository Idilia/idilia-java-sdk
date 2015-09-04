package com.idilia.services.kb;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.idilia.services.base.AsyncClientBase;
import com.idilia.services.base.IdiliaClientException;
import com.idilia.services.base.IdiliaCredentials;

/**
 * Asynchronous Client For Kb Queries
 */
public class AsyncClient extends AsyncClientBase {

  /**
   * Constructs a client for requesting kb services provided at the default service URL.
   * <p>
   * This is a lightweight object. Allocated instances share an underlying HTTP client.
   * Multithread safe.
   * <p>
   * @param creds Idilia API credentials
   */
  public AsyncClient(IdiliaCredentials creds) {
    this(creds, defaultApiUrl);
  }

  /**
   * Constructs a client for requesting kb services provided at the given service URL.
   * <p>
   * This is a lightweight object. Allocated instances share an underlying HTTP client.
   * Multithread safe.
   * <p>
   * @param creds Idilia API credentials
   * @param url   URL to reach the API. Normally http://api.idilia.com
   */
  public AsyncClient(IdiliaCredentials creds, URL url) {
    super(creds, url);
  }


  /**
   * Sends a query request to the kb server.
   * 
   * Asynchronously sends an HTTP request to the kb server and signals the returned
   * future when the result is available. 
   * 
   * @param req Request message. One concrete implementation of {@link QueryRequest}
   * @param tpRef type of the class of object to recover from the JSON response. Can be Object.class
   *              or a user defined class.
   * @return a CompletableFuture set when the response is available
   * @throws IdiliaClientException wrapping the actual exception encountered
   */
  public <T> CompletableFuture<QueryResponse> queryAsync(QueryRequest req, Class<T> tpRef) throws IdiliaClientException {

    final HttpPost httppost = createPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }

    final CompletableFuture<QueryResponse> future = new CompletableFuture<>();
    getClient().execute(httppost, ctxt, new QueryCB<T>(tpRef, future));
    return future;
  }


  /** The callback for decoding a kb/query response. Adds a constructor parameter for the recovery type */
  private class QueryCB<T> extends HttpCallback<QueryResponse> {

    final private Class<T> tpRef;

    public QueryCB(Class<T> tpRef, CompletableFuture<QueryResponse> future) { 
      super(future);
      this.tpRef = tpRef;
    }

    public QueryResponse completedHdlr(HttpResponse httpResponse) throws IdiliaClientException, JsonParseException, UnsupportedOperationException, IOException {

      QueryResponse resp = QueryCodec.decode(jsonMapper_, tpRef, httpResponse.getEntity());
      if (resp.getStatus() != HttpURLConnection.HTTP_OK)
        throw new IdiliaClientException(resp);
      return resp;
    }
  }
  


  /**
   * Sends a sense menu request to a kb server.
   * 
   * Asynchronously sends an HTTP request to a kb server and signals the returned
   * future when the result is available. 
   * 
   * @param req Request message. One concrete implementation of {@link SenseMenuRequest}
   * @return a CompletableFuture set when the response is available
   * @throws IdiliaClientException wrapping the actual exception encountered
   */
  public CompletableFuture<SenseMenuResponse> senseMenuAsync(SenseMenuRequest req) throws IdiliaClientException {

    final HttpPost httppost = createPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }

    final CompletableFuture<SenseMenuResponse> future = new CompletableFuture<>();
    getClient().execute(httppost, ctxt, new HttpCallback<SenseMenuResponse>(future) {
      @Override
      public SenseMenuResponse completedHdlr(HttpResponse result) throws IdiliaClientException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
        String ct = result.getEntity().getContentType().getValue();
        if (!ct.startsWith("application/json")) {
          throw new IdiliaClientException("Unexpected content type: " + ct);
        } else {
          SenseMenuResponse resp = jsonMapper_.readValue(result.getEntity().getContent(), SenseMenuResponse.class);
          if (resp.getStatus() != HttpURLConnection.HTTP_OK)
            throw new IdiliaClientException(resp);
          return resp;
        }
      }
    });

    return future;
  }

  
  /**
   * Sends a tagging menu request to a kb server.
   * 
   * Asynchronously sends an HTTP request to a kb server and signals the returned
   * future when the result is available. 
   * 
   * @param req Request message. One concrete implementation of {@link TaggingMenuRequest}
   * @return a CompletableFuture set when the response is available
   * @throws IdiliaClientException wrapping the actual exception encountered
   */
  public CompletableFuture<TaggingMenuResponse> taggingMenuAsync(TaggingMenuRequest req) throws IdiliaClientException {

    final HttpPost httpPost = createMultipartPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException ioe) {
      throw new IdiliaClientException(ioe);
    }

    final CompletableFuture<TaggingMenuResponse> future = new CompletableFuture<>();
    getClient().execute(httpPost, ctxt, new HttpCallback<TaggingMenuResponse>(future) {
      @Override
      public TaggingMenuResponse completedHdlr(HttpResponse result) throws IdiliaClientException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
        String ct = result.getEntity().getContentType().getValue();
        if (!ct.startsWith("application/json")) {
          throw new IdiliaClientException("Unexpected content type: " + ct);
        } else {
          TaggingMenuResponse resp = jsonMapper_.readValue(result.getEntity().getContent(), TaggingMenuResponse.class);
          if (resp.getStatus() != HttpURLConnection.HTTP_OK)
            throw new IdiliaClientException(resp);
          return resp;
        }
      }
    });

    return future;
  }

  
  /**
   * Sends a sense card request to a kb server.
   * 
   * Asynchronously sends an HTTP request to a kb server and signals the returned
   * future when the result is available. 
   * 
   * @param req Request message. One concrete implementation of SenseCardJsonRequest
   * @return a CompletableFuture set when the response is available
   * @throws IdiliaClientException wrapping the actual exception encountered
   */
  public CompletableFuture<SenseCardResponse> senseCardAsync(SenseCardRequest req) throws IdiliaClientException {

    final HttpPost httppost = createPost(req);
    final HttpClientContext ctxt = HttpClientContext.create();
    try {
      sign(ctxt, req.requestPath(), req.toSign());
    } catch (IOException e) {
      throw new IdiliaClientException(e);
    }

    final CompletableFuture<SenseCardResponse> future = new CompletableFuture<>();
    getClient().execute(httppost, ctxt, new HttpCallback<SenseCardResponse>(future) {
      @Override
      public SenseCardResponse completedHdlr(HttpResponse result) throws IdiliaClientException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
        String ct = result.getEntity().getContentType().getValue();
        if (!ct.startsWith("application/json")) {
          throw new IdiliaClientException("Unexpected content type: " + ct);
        } else {
          SenseCardResponse resp = jsonMapper_.readValue(result.getEntity().getContent(), SenseCardResponse.class);
          if (resp.getStatus() != HttpURLConnection.HTTP_OK)
            throw new IdiliaClientException(resp);
          return resp;
        }
      }
    });

    return future;
  }
}
