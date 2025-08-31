package com.petrokhovrashchuk.petprojects.utils;

import java.io.IOException;

public class Helper {

  private static final String ROOT_PATH = "/";

  public static String readClassPathFile(final String file) throws IOException {
    final byte[] bytes = Helper.class.getResourceAsStream(ROOT_PATH + file).readAllBytes();
    return new String(bytes);
  }

}
