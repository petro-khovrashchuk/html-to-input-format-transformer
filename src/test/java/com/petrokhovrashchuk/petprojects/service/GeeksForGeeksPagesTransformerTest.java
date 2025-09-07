package com.petrokhovrashchuk.petprojects.service;

import static com.petrokhovrashchuk.petprojects.utils.Helper.readClassPathFile;
import static org.assertj.core.api.Assertions.assertThat;

import com.petrokhovrashchuk.petprojects.di.DaggerServicesComponent;
import java.util.stream.Collectors;
import org.testng.annotations.Test;

public class GeeksForGeeksPagesTransformerTest {

  private static final String INPUT_FILE = "geeksForGeeks.html";
  private static final String CODE_INPUT_FILE = "inputCodePageGeeksForGeeks.html";
  private static final String CODE_OUTPUT_FILE = "outputCodePageGeeksForGeeks.html";
  private final Transformer transformer = DaggerServicesComponent.create()
    .buildGeeksFroGeeksPagesTransformer();

  @Test(priority = 1)
  void transform_whenCalledWithValidPage_shouldReturnTransformedPage() {
    // Arrange
    final String input = readClassPathFile(INPUT_FILE);

    // Act
    final String actualResult = transformer.transform(input);

    // Assert
    assertThat(actualResult)
        .isNotEmpty()
        .isNotEqualTo(input);
  }

  @Test
  void transform_whenCalledWithValidPage_shouldReturnTransformedPageWithoutChildSpanNodesThatHasNoAttributes() {
    // Arrange
    final String input = readClassPathFile(INPUT_FILE);

    // Act
    final String actualResult = transformer.transform(input);

    // Assert
    assertThat(actualResult)
        .isNotEmpty()
        .containsOnlyOnce("<span>")
        .containsOnlyOnce("</span>");

  }

  @Test
  void transform_whenCalledWithValidPage_shouldReturnTransformedPageWithParagraphTagsForQuestions() {
    // Arrange
    final String input = readClassPathFile(INPUT_FILE);

    // Act
    final String actual = transformer.transform(input);

    // Assert
    assertThat(actual)
        .isNotEmpty()
        .doesNotContain("<h3 id=")
        .contains("<p id=");
  }

  @Test
  void transform_whenCalledWithValidPage_shouldReturnTransformedPageWithHighestResolutionForImages() {
    // Arrange
    final String input = readClassPathFile(INPUT_FILE);
    final String[] smallResolutionImages = {
        "https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768-100.png 100w",
        "https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768-200.png 200w",
        "https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768-300.png 300w",
        "https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768-660.png 660w"};

    // Act
    final String actual = transformer.transform(input);

    // Assert
    assertThat(actual)
        .isNotEmpty()
        .doesNotContain(smallResolutionImages)
        .contains(
            "src=\"https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768.png\"");
  }

  @Test
  void transform_whenCalledWithPageWithCode_shouldReturnTransformedPageWithCode() {
    // Arrange
    final String input = readClassPathFile(CODE_INPUT_FILE);

    // Act
    final String actual = transformer.transform(input);

    // Assert
    assertThat(actual)
        .containsOnlyOnce("<div>")
        .containsOnlyOnce("</div>");
  }

  @Test
  void transform_whenCalledWithValidPageWithCode_shouldReturnTransformedPageWithCode() {
    // Arrange
    final String input = readClassPathFile(CODE_INPUT_FILE);
    final String expected = readClassPathFile(CODE_OUTPUT_FILE);

    // Act
    final String actual = transformer.transform(input);

    // Assert
    assertThat(trimLines(actual)).isEqualTo(trimLines(expected));
  }

  private static String trimLines(final String str) {
    return str.lines().map(String::trim).collect(Collectors.joining());
  }

}
