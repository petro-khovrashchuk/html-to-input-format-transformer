package com.petrokhovrashchuk.petprojects.service;

import com.petrokhovrashchuk.petprojects.exception.DocumentNotLoadedException;
import jakarta.inject.Inject;
import java.net.URL;
import lombok.AllArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

@AllArgsConstructor(onConstructor_ = {@Inject})
public class PageLoaderImpl implements PageLoader {

  private final Connection connection;

  @Override
  public String load(final URL url) {
    final Document doc;

    try {
      doc = connection.url(url).get();
      return doc.toString();
    } catch (Exception e) {
      throw new DocumentNotLoadedException("Exception during connection", e);
    }
  }

}
