package com.idilia.services.kb;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;

public class AsyncClientTest extends TestBase {
  
  protected URL getSvcUrl() {
    return Configuration.INSTANCE.getKbApiUrl();
  }
  
  @Test
  public void testObjectMapping() throws InterruptedException {
    // Perform a REST request to the documentation server
    try (AsyncClient client = new AsyncClient(getDefaultCreds(), getSvcUrl())) {
      final QueryRequest req = new QueryRequest(KbQuery.build("Montreal/N1"));
      QueryResponse<KbQuery> response = client.queryAsync(req, KbQuery.class).join();
      Assert.assertEquals(1, response.getResult().size());
      Assert.assertFalse(response.getResult().get(0).definition.isEmpty());
    } finally {
    }
  }
}
