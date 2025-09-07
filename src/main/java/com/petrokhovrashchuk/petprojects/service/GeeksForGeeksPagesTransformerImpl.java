package com.petrokhovrashchuk.petprojects.service;

import jakarta.inject.Inject;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

@AllArgsConstructor(onConstructor_ = {@Inject})
public class GeeksForGeeksPagesTransformerImpl implements Transformer {

  /* query for h3 elements with id attribute that has value which starts with a number 1 followed
                      by '-' symbol followed by any combination of letters or '-' symbols */
  private static final String QUESTION_CCS_QUERY = "h3[id~=^[0-9]+\\-[\\-a-z]+$]";
  private static final String SPAN = "span";
  private static final String IMG = "img";
  private static final String H3 = "h3";
  private static final String P = "p";
  private static final String SRCSET = "srcset";
  private static final String SRC = "src";
  private static final String COMA = ",";
  private static final String SPACE = " ";
  private static final String SHADOWROOTMODE = "shadowrootmode";
  private static final String OPEN = "open";
  private static final String ID = "id";
  private static final String GFG_TAB_GENERATED_0 = "gfg-tab-generated-0";
  private static final String TEMPLATE = "template";
  private static final String GFG_TAB = "gfg-tab";
  private static final String CODE = "code";
  private static final String DIV = "div";
  private static final String CLASS = "class";
  private static final String HIGHLIGHT_MONOKAI = "highlight monokai";

  @Override
  public String transform(final String html) {
    final Document document = Jsoup.parse(html);
    final Element firstQuestion = document.expectFirst(QUESTION_CCS_QUERY);
    final Map<Element, Element> questionToAnswer = filterQuestionsWithAnswers(firstQuestion);
    final Map<Element, Element> cleanQuestionToAnswer = cleanUpElements(questionToAnswer);

    return firstQuestion.parent().html();
  }

  private Map<Element, Element> filterQuestionsWithAnswers(final Element firstQuestion) {
    final LinkedHashMap<Element, Element> questionsToAnswers = new LinkedHashMap<>();
    for (var iterator = firstQuestion.parent().children().iterator(); iterator.hasNext();) {
      final Element element = iterator.next();
      if(element.is(QUESTION_CCS_QUERY)) {
        final Element div = new Element(DIV);
        questionsToAnswers.put(element, div);
      } else if (element.elementSiblingIndex() < firstQuestion.elementSiblingIndex()) {
        element.remove();
      } else {
        final Element answer = questionsToAnswers.lastEntry().getValue();
        answer.appendChild(element.clone());
        element.replaceWith(answer);
      }
    }
    return questionsToAnswers;
  }

  private Map<Element, Element> cleanUpElements(final Map<Element, Element> questionToAnswer) {
    final Map<Element, Element> cleanQuestionToAnswer = Map.copyOf(questionToAnswer);
    cleanUpQuestions(cleanQuestionToAnswer);
    cleanUpAnswers(questionToAnswer);
    return cleanQuestionToAnswer;
  }

  private void cleanUpQuestions(final Map<Element, Element> questionToAnswer) {
    for (Element element : questionToAnswer.keySet()) {
      swapHeadElementWithParagraph(element);
      cleanUpSpanWithoutAttributes(questionToAnswer.keySet(), element);
    }
  }

  private void cleanUpAnswers(final Map<Element, Element> questionToAnswer) {
    for (Element element : questionToAnswer.values()) {
      cleanUpSpanWithoutAttributes(questionToAnswer.values(), element);
      cleanUpImage(element);
      cleanUpCodeBlocks(element);
    }
  }

  private void cleanUpSpanWithoutAttributes(final Collection<Element> elements, final Element element) {
    element.forEachNode(node -> {
      if (!elements.contains(node) && node.nameIs(SPAN) && node.attributesSize() == 0) {
        node.unwrap();
      }
    });
  }

  private void swapHeadElementWithParagraph(final Element element) {
    if (element.nameIs(H3)) {
      element.tagName(P);
    }
  }

  private void cleanUpImage(final Element element) {
    element.forEachNode(node -> {
      if (isNodeEqual(node, IMG, SRCSET)) {
        setSourceLargestImageLink(node);
        node.removeAttr(SRCSET);
      }
    });

  }

  private void setSourceLargestImageLink(final Node imageNode) {
    final String srcsetValue = imageNode.attr(SRCSET);
    final String link = getLinkForLargestImage(srcsetValue);
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

  private void cleanUpCodeBlocks(final Element element) {
    element.forEachNode(node -> {
      if (isCodeNodeForRemoval(node)) {
        node.remove();
      }
    });
    final Element code = element.selectFirst(CODE);
    if(code == null) {
      return;
    }
    final Node child = code.nextElementSibling();
    if (isNodeEqual(child, DIV, CLASS, HIGHLIGHT_MONOKAI)) {
      child.forEachNode(n -> {
        final String value = n.nodeValue();
        if (value.contains(SPACE.repeat(4))) {
          final String indent = value.replace(SPACE.repeat(4), SPACE.repeat(2));
          ((TextNode) n).text(indent);
        }
      });
      child.forEachNode(Node::unwrap);
    }
  }

  private boolean isCodeNodeForRemoval(final Node node) {
    return isNodeEqual(node, TEMPLATE, SHADOWROOTMODE, OPEN)
        || isNodeEqual(node, GFG_TAB, ID, GFG_TAB_GENERATED_0);
  }

  private boolean isNodeEqual(final Node node,
      final String nodeName,
      final String attributeName) {
    return isNodeEqual(node, nodeName, attributeName, null);
  }

  private boolean isNodeEqual(
      final Node node,
      final String nodeName,
      final String attributeName,
      final String attributeValue) {
    if(node == null) {
      return false;
    }
    if (!node.nameIs(nodeName)) {
      return false;
    }
    if (attributeName != null && !node.hasAttr(attributeName)) {
      return false;
    }
    return attributeName == null ||
        attributeValue == null ||
        attributeValue.equals(node.attr(attributeName));
  }

}
