package com.petrokhovrashchuk.petprojects.utils;

import static com.sun.net.httpserver.SimpleFileServer.OutputLevel;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TestServer {

  private static final InetSocketAddress LOOPBACK_ADDR = new InetSocketAddress(
      InetAddress.getLoopbackAddress(), 8080);
  private static final String ROOT_PATH = "/";
  private static final String SERVER_FILE_NAME = "index.html";
  private static final String LOCAL_FILE_NAME = "testIndex.html";
  private static final int ONE = 1;

  private HttpServer server;

  public void init() throws Exception {
    final Path root = createDirectoryHierarchy();
    server = SimpleFileServer.createFileServer(LOOPBACK_ADDR, root, OutputLevel.VERBOSE);
    server.start();
  }

  private Path createDirectoryHierarchy() throws IOException {
    final FileSystem fs = Jimfs.newFileSystem(createConfiguration());
    final Path root = fs.getPath(ROOT_PATH);
    final Path file = fs.getPath(SERVER_FILE_NAME);
    final Path serverFile = Files.createFile(root.resolve(file));

    Files.write(serverFile, readFile(ROOT_PATH + LOCAL_FILE_NAME), StandardOpenOption.WRITE);

    return root;
  }

  private static Configuration createConfiguration() {
    return Configuration.unix().toBuilder().setWorkingDirectory(ROOT_PATH).build();
  }

  private byte[] readFile(final String classPath) throws IOException {
    return this.getClass().getResourceAsStream(classPath).readAllBytes();
  }

  public void tearDown() {
    server.stop(ONE);
  }

  public URI getUri() {
    final String uriFormat = "http://%s:%d";
    final String uriStr = String.format(uriFormat, server.getAddress().getHostString(), server.getAddress().getPort());
    return URI.create(uriStr);
  }

}