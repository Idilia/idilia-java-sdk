package com.idilia.services.kb;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.idilia.services.base.Configuration;
import com.idilia.services.base.TestBase;
import com.idilia.services.kb.objects.NeInfo;

public class ClientTest extends TestBase {

  protected URL getSvcUrl() {
    return Configuration.INSTANCE.getKbApiUrl();
  }
  
  @SuppressWarnings("unchecked")
  public void testProcess() {
    // Perform a REST request to the kb server
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
      QueryRequest req = defaultRequest();
      req.setRequestId("testing-services-kb-" + (new Random()).nextInt(1000));

      QueryResponse response = client.query(req);
      Assert.assertTrue(response.getErrorMsg() == null);
      Assert.assertTrue(response.getResult().size() == 1);
      LinkedHashMap<String,Object> mtlRes = (LinkedHashMap<String,Object>) response.getResult().get(0);
      Assert.assertTrue(mtlRes.get("fs") != null);
      Assert.assertTrue(mtlRes.get("definition") != null);
    } finally {
    }
  }
  
  @Test
  public void testObjectMapping() {
    
    try (Client client = new Client(getDefaultCreds(), getSvcUrl())) {
    
      QueryRequest req = new QueryRequest();
      ArrayList<Object> qrys = new ArrayList<Object>();
      LinkedHashMap<String, Object> qry1 = new LinkedHashMap<String,Object>(), qry2 = new LinkedHashMap<String,Object>();
      qry1.put("fs", "Montreal/N1");
      qry1.put("definition", null);
      qry1.put("neInfos", new ArrayList<NeInfo>());
      qrys.add(qry1);
      qry2.put("fs", "Quebec/N1");
      qry2.put("definition", null);
      qrys.add(qry2);
      req.setQuery(qrys);
  
      QueryResponse response = client.query(req, QueryResult.class);
      Assert.assertEquals(response.getResult().size(), 2);
      Assert.assertEquals(((QueryResult) response.getResult().get(0)).getFs(), "Montreal/N1");
      Assert.assertEquals(((QueryResult) response.getResult().get(1)).getFs(), "Quebec/N1");
      for (Object rObj: response.getResult()) {
        QueryResult r = (QueryResult) rObj;
        Assert.assertTrue(!r.getDefinition().isEmpty());
      }
    } finally {
    }
  }
  
  static QueryRequest defaultRequest() {
    QueryRequest req = new QueryRequest();
    ArrayList<Object> qrys = new ArrayList<Object>();
    LinkedHashMap<String, Object> qry = new LinkedHashMap<String,Object>();
    qry.put("fs", "Montreal/N1");
    qry.put("definition", null);
    qrys.add(qry);
    req.setQuery(qrys);
    return req;
  }  
}
