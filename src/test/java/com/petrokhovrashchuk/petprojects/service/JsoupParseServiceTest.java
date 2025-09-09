package com.petrokhovrashchuk.petprojects.service;

import static com.petrokhovrashchuk.petprojects.utils.Helper.readClassPathFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertThrows;

import com.petrokhovrashchuk.petprojects.di.DaggerServicesComponent;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.annotations.Test;

public class JsoupParseServiceTest {

  private static final String INPUT_FULL_FILE = "geeksForGeeks_full.html";
  private static final String INPUT_CATEGORIES_FILE = "geeksForGeeks_categories.html";

  private static final String INPUT_SIBLING_ELEMENTS_FILE_1 = "test_sibling_elements.html";

  private final JsoupParseService testService = DaggerServicesComponent.create()
                                                                       .getJsoupParseService();

  // getSiblingElementsByTag(Elements, String)
  @Test
  void getSiblingElementsByTag_whenCalledWithNullAsElements_shouldThrowIllegalArgumentException() {
    // Arrange
    final Elements input = null;
    final String tagName = "tag";

    // Act

    // Assert
    assertThat(input).isNull();
    assertThat(tagName).isNotNull();
    assertThrows(
        IllegalArgumentException.class,
        () -> testService.getSiblingElementsByTag(input, tagName));
  }

  @Test
  void getSiblingElementsByTag_whenCalledWithNullAsTagName_shouldThrowIllegalArgumentException() {
    // Arrange
    final Elements input = new Elements();
    final String tagName = null;

    // Act

    // Assert
    assertThat(input).isNotNull();
    assertThat(tagName).isNull();
    assertThrows(
        IllegalArgumentException.class,
        () -> testService.getSiblingElementsByTag(input, tagName));
  }

  @Test
  void getSiblingElementsByTag_whenCalledWithEmptyElements_shouldReturnEmptyCollection() {
    // Arrange
    final Elements input = new Elements();
    final String tagName = "h2";

    // Act
    List<Elements> actualResult = testService.getSiblingElementsByTag(input, tagName);

    // Assert
    assertThat(input)
        .isNotNull()
        .isEmpty();
    assertThat(tagName)
        .isNotNull();
    assertThat(actualResult)
        .isEmpty();
  }

  @Test
  void getSiblingElementsByTag_whenCalledWithEmptyTagName_shouldReturnEmptyCollection() {
    // Arrange
    final Elements input = new Elements(new Element("<h2>Hello, World</h2>"));
    final String tagName = "h2";

    // Act
    List<Elements> actualResult = testService.getSiblingElementsByTag(input, tagName);

    // Assert
    assertThat(input)
        .isNotNull()
        .isNotEmpty();
    assertThat(tagName)
        .isNotNull()
        .isNotEmpty();
    assertThat(actualResult)
        .isEmpty();
  }

  @Test
  void getSiblingElementsByTag_whenCalledWithValidElementsAndValidTagName_shouldReturnNotEmptyCollection() {
    // Arrange
    final Elements input = Jsoup.parse(readClassPathFile(INPUT_SIBLING_ELEMENTS_FILE_1))
                                .getAllElements();
    final String tagName = "h2";
    final List<Elements> expectedResult = List.of(
        new Elements(new Element("h2").text("Hello, World! #0")),
        new Elements(
            new Element("h2").text("Hello, World! #1"),
            new Element("h3").text("Title. #1.1"),
            new Element("p").text("It contains a heading and a paragraph. #1.1.1"),
            new Element("p").text("It contains a heading and a paragraph. #1.1.2")),
        new Elements(
            new Element("h2").text("Hello, World! #2"),
            new Element("p").text("It contains a heading and a paragraph. #2.1")),
        new Elements(new Element("h2").text("Hello, World! #3")),
        new Elements(new Element("h2").text("Hello, World! #4"))
    );

    // Act
    List<Elements> actualResult = testService.getSiblingElementsByTag(input, tagName);

    // Assert
    assertThat(input)
        .isNotNull()
        .isNotEmpty();
    assertThat(tagName)
        .isNotNull()
        .isNotEmpty();
    assertThat(actualResult.stream()
                           .flatMap(Elements::stream)
                           .map(Element::outerHtml)
                           .toList())
        .isNotEmpty()
        .containsExactlyElementsOf(expectedResult.stream()
                                                 .flatMap(Elements::stream)
                                                 .map(Element::outerHtml).
                                                 toList());
  }
}
