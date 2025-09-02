package com.petrokhovrashchuk.petprojects.service;

import jakarta.inject.Inject;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

@AllArgsConstructor(onConstructor_ = {@Inject})
public class GeeksForGeeksPagesTransformerImpl implements Transformer {

  /* query for h3 elements with id attribute that has value which starts with a number 1 followed
  by '-' symbol followed by any combination of letters or '-' symbols */
  private static final String FIRST_QUESTION_CCS_QUERY = "h3[id~=^1\\-[\\-a-z]+$]";
  private static final String SPAN = "span";
  private static final String IMG = "img";
  private static final String H3 = "h3";
  private static final String P = "p";
  private static final String SRCSET = "srcset";
  private static final String SRC = "src";
  public static final String COMA = ",";
  private static final String SPACE = " ";

  @Override
  public String transform(final String html) {
    final Document document = Jsoup.parse(html);
    final Element firstQuestion = document.expectFirst(FIRST_QUESTION_CCS_QUERY);
    Elements questionsWithAnswers = filterQuestionsWithAnswers(firstQuestion);
    questionsWithAnswers = cleanUpElements(questionsWithAnswers);

    return questionsWithAnswers.outerHtml();
  }

  private Elements filterQuestionsWithAnswers(final Element firstQuestion) {
    final Elements questionsWithAnswers = new Elements();
    for (var element = firstQuestion; element != null; element = element.nextElementSibling()) {
      questionsWithAnswers.add(element);
    }
    return questionsWithAnswers;
  }

  private Elements cleanUpElements(final Elements questionsWithAnswers) {
    final Node parent = questionsWithAnswers.first().parentNode();
    for (Element element : questionsWithAnswers) {
      changeHeadElementWithParagraph(element);
      cleanUpSpanWithoutAttributes(questionsWithAnswers, element);
      cleanUpImage(element);
//      cleanUpCodeBlocks();
    }
    return questionsWithAnswers;
  }

  private void cleanUpSpanWithoutAttributes(final Elements questionsWithAnswers,
      final Element element) {
    element.forEachNode(node -> {
      if (!questionsWithAnswers.contains(node) && node.nameIs(SPAN) && node.attributesSize() == 0) {
        node.unwrap();
      }
    });
  }

  private void changeHeadElementWithParagraph(final Element element) {
    if(element.nameIs(H3)) {
      element.tagName(P);
    }
  }

  private void cleanUpImage(Element element) {
    element.forEachNode(node -> {
      if (node.nameIs(IMG) && node.hasAttr(SRCSET)) {
        setSourceLargestImageLink(node);
        node.removeAttr(SRCSET);
      }
    });

  }

  private void setSourceLargestImageLink(final Node imageNode) {
        final String srcset = imageNode.attr(SRCSET);
        final String link = getLinkForLargestImage(srcset);
        imageNode.attr(SRC, link);
  }

  private String getLinkForLargestImage(final String srcset) {
    return Arrays.stream(srcset.split(COMA))
        .map(this::convertSrcIntoWidthToLinkEntry)
        .max(Entry.comparingByKey())
        .get()
        .getValue();
  }

  private SimpleEntry<String, String> convertSrcIntoWidthToLinkEntry(final String src) {
    final String[] linkAndWidth = src.split(SPACE);
    return new SimpleEntry<>(linkAndWidth[1], linkAndWidth[0]);
  }

}
