package com.idilia.services.base;

import org.apache.log4j.Logger;

public class TestBase {

  protected static Logger logger_ = Logger.getRootLogger();

  // Return credentials valid for any tests
  protected static IdiliaCredentials getDefaultCreds() throws IdiliaClientException {
    return new IdiliaCredentials(
        Configuration.INSTANCE.getIdiliaAccessKey(),
        Configuration.INSTANCE.getIdiliaSecretKey());
  }
}
