package com.petrokhovrashchuk.petprojects.service;

import jakarta.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@AllArgsConstructor(onConstructor_ = {@Inject})
public class JsoupParseService {

  List<Elements> getSiblingElementsByTag(Elements elements, String tagName) {
    if (elements == null || tagName == null) {
      throw new IllegalArgumentException();
    }

    List<Elements> result = new LinkedList<>();
    Elements currentElements = null;
    for (Element element : elements) {
      if (element.tagName().equalsIgnoreCase(tagName)) {
        if (currentElements != null) {
          result.add(currentElements);
        }
        currentElements = new Elements();
      }
      if (currentElements != null) {
        currentElements.add(element);
      }
    }
    if (currentElements != null && !currentElements.isEmpty()) {
      result.add(currentElements);
    }

    return result;
  }
}
