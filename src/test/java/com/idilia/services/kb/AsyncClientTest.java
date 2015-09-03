package com.idilia.services.kb;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;

public class AsyncClientTest extends TestBase {
  
  protected URL getSvcUrl() {
    return Configuration.INSTANCE.getKbApiUrl();
  }
  
  @Test
  public void testObjectMapping() {
    // Perform a REST request to the documentation server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), getSvcUrl())) {

      final AtomicBoolean failed = new AtomicBoolean(false);
      final int numReqs = 10;
      final CountDownLatch counter = new CountDownLatch(numReqs);
      for (int i = 0; i < numReqs; ++i) {
        final QueryRequest req = defaultRequest();
        req.setRequestId("testing-services-kb-"
            + (new Random()).nextInt(1000));
        CompletableFuture<QueryResponse> future = client.queryAsync(req, QueryResult.class);
        future.handle((response, ex) -> {
          try {
            if (ex != null) {
              logger_.error("encountered exception", ex);
              failed.set(true);
            }
            else if (response.getStatus() == 200) {
              for (Object rObj : response.getResult()) {
                QueryResult r = (QueryResult) rObj;
                failed.set(failed.get() || r.getDefinition().isEmpty());
              }
            } else {
              logger_.error("Failed request: " + response.getStatus() + " " + response.getErrorMsg());
              failed.set(true);
            }
          } catch (Exception e) {
            logger_.error("Caught exception", e);
            Assert.assertTrue(false);
          } finally {
            counter.countDown();
          }
          return response;
        });
      }

      counter.await();
      Assert.assertFalse(failed.get());
    } catch (IOException e) {
      logger_.log(Level.INFO, "Got exception when trying to contact kb server", e);
    } catch (Exception e) {
      logger_.log(Level.INFO, "Got unexpected exception when running kb test", e);
    }
  }

  
  static QueryRequest defaultRequest() throws JsonGenerationException,
      JsonMappingException, IOException {
    QueryRequest req = new QueryRequest();
    ArrayList<Object> qrys = new ArrayList<Object>();
    LinkedHashMap<String, Object> qry = new LinkedHashMap<String, Object>();
    qry.put("fs", "Montreal/N1");
    qry.put("definition", null);
    qrys.add(qry);
    req.setQuery(qrys);
    return req;
  }
}
