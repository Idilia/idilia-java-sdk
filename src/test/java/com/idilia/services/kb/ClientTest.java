package com.idilia.services.kb;
import java.net.URL;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;

public class ClientTest extends TestBase {

  protected URL getSvcUrl() {
    return Configuration.INSTANCE.getKbApiUrl();
  }
  
  @Test
  public void testObjectMapping() {
    
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
    
      QueryRequest req = new QueryRequest(
          Arrays.asList(
              KbQuery.build("Montreal/N1"), KbQuery.build("Quebec/N1")));
      QueryResponse<KbQuery> response = client.query(req, KbQuery.class);
      
      Assert.assertEquals(response.getResult().size(), 2);
      Assert.assertEquals((response.getResult().get(0)).fs, "Montreal/N1");
      Assert.assertEquals((response.getResult().get(1)).fs, "Quebec/N1");
      for (Object rObj: response.getResult()) {
        KbQuery r = (KbQuery) rObj;
        Assert.assertTrue(!r.definition.isEmpty());
      }
    } finally {
    }
  }
}
