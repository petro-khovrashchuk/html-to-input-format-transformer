package com.petrokhovrashchuk.petprojects.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertThrows;

import com.petrokhovrashchuk.petprojects.di.DaggerServicesComponent;
import com.petrokhovrashchuk.petprojects.exception.DocumentNotLoadedException;
import com.petrokhovrashchuk.petprojects.utils.Helper;
import com.petrokhovrashchuk.petprojects.utils.TestServer;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PageLoaderTest {

  private static final String LOCAL_FILE_NAME = "testIndex.html";
  private static final String PATH_DELIMITER = "/";
  private static final String INVALID = "INVALID";
  private static final TestServer testServer = new TestServer();

  private final PageLoader pageLoader = DaggerServicesComponent.create().buildPageLoader();

  @BeforeClass
  static void init() throws Exception {
    testServer.init();
  }

  @AfterClass
  static void tearDown() {
    testServer.tearDown();
  }

  @Test
  public void load_whenCalledWithValidUrl_shouldSuccessfullyLoadPage() throws Exception {
    // Arrange
    final URL url = testServer.getUri().toURL();

    // Act
    final String page = pageLoader.load(url);

    // Assert
    assertThat(normalize(page))
        .isInstanceOf(String.class)
        .isEqualTo(normalize(Helper.readClassPathFile(LOCAL_FILE_NAME)));
  }

  private String normalize(final String str) {
    return str.lines()
        .map(String::trim)
        .collect(Collectors.joining())
        .toLowerCase();
  }

  @Test
  public void load_whenCalledWithInvalidUrl_shouldThrowExpectedException() {
    // Arrange
    final URI invalidUri = testServer.getUri().resolve(PATH_DELIMITER + INVALID);
    final String expectedMessage = "Exception during connection";

    // Act & Assert
    assertThrows(expectedMessage, DocumentNotLoadedException.class,
        () -> pageLoader.load(invalidUri.toURL()));
  }

}