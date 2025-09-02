package com.petrokhovrashchuk.petprojects.service;

import static com.petrokhovrashchuk.petprojects.utils.Helper.readClassPathFile;
import static org.assertj.core.api.Assertions.assertThat;

import com.petrokhovrashchuk.petprojects.di.DaggerServicesComponent;
import org.testng.annotations.Test;

public class GeeksForGeeksPagesTransformerTest {

  public static final String INPUT_FILE = "geeksForGeeks.html";
  private final Transformer transformer = DaggerServicesComponent.create()
    .buildGeeksFroGeeksPagesTransformer();

  @Test(priority = 1)
  void transform_whenCalledWithValidPage_shouldReturnTransformedPage() {
    // Arrange
    final String input = readClassPathFile(INPUT_FILE);
    final String expectedResult = "<p>This is a basic HTML document. It contains a heading and a paragraph.</p>";

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
    final String actualResult = transformer.transform(input);

    // Assert
    assertThat(actualResult)
        .isNotEmpty()
        .doesNotContain("<h3 id=")
        .contains("<p id=");
  }

  @Test
  void transform_whenCalledWithValidPage_shouldReturnTransformedPageWithHighestResolutionForImages() {
    // Arrange
    final String input = readClassPathFile(INPUT_FILE);

    // Act
    final String actualResult = transformer.transform(input);

    // Assert
    assertThat(actualResult)
        .isNotEmpty()
        .doesNotContain(
            "https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768-100.png 100w",
            "https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768-200.png 200w",
            "https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768-300.png 300w",
            "https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768-660.png 660w")
        .contains(
            "src=\"https://media.geeksforgeeks.org/wp-content/uploads/20240401182630/Features-of-Java-768.png\"");
  }

}
