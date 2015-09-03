package com.idilia.services.base;

import java.security.InvalidKeyException;

import org.apache.log4j.Logger;

public class TestBase {

  protected static Logger logger_ = Logger.getRootLogger();

  // Return credentials valid for any tests
  protected static IdiliaCredentials getDefaultCreds() throws InvalidKeyException {
    return new IdiliaCredentials(
        Configuration.INSTANCE.getIdiliaAccessKey(),
        Configuration.INSTANCE.getIdiliaSecretKey());
  }
}
