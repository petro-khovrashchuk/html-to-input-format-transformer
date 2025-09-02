package com.petrokhovrashchuk.petprojects.utils;

public class Helper {

  private static final String ROOT_PATH = "/";

  public static String readClassPathFile(final String file) {
    try (var is = Helper.class.getResourceAsStream(ROOT_PATH + file)) {
      final byte[] bytes = is.readAllBytes();
      return new String(bytes);
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not read file");
    }
  }

}
