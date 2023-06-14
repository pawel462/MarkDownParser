package com.github.arena.challenges.weakmdparser;

import java.util.Optional;

public class MarkdownParser {

  private static final String REGEX_LI = "(<li>).*";
  private static final String REGEX_H = "(<h).*";
  private static final String REGEX_P = "(<p>).*";
  private static final String UL1 = "</ul>";
  private static final String P = "<p>";
  private static final String P1 = "</p>";
  private static final String LI = "<li>";
  private static final String LI1 = "</li>";
  private static final String LOOKING_FOR = "__(.+)__";
  private static final String LOOKING_FOR1 = "_(.+)_";
  private static final String STRONG_1_STRONG = "<strong>$1</strong>";
  private static final String EM_1_EM = "<em>$1</em>";
  private static final String UL = "<ul>";
  private static final char HASH = '#';
  private static final String STAR = "*";
  public static final String H = "<h";
  public static final String H1 = "</h";


  public String parse(String markdown) {
    String[] lines = markdown.split("\n");
    StringBuilder result = new StringBuilder();
    boolean activeList = false;
    return getParserString(lines, result, activeList);
  }

  private String getParserString(String[] lines, StringBuilder result, boolean activeList) {
    activeList = parserString(lines, result, activeList);
    StringBuilder stringBuilder = activeList ? result.append(UL1) : result;
    return stringBuilder.toString();
  }

  private Optional<String> isListItem(String markdown) {
    if (markdown.startsWith(STAR)) {
      String skipAsterisk = markdown.substring(2);
      String listItemString = parseSomeSymbols(skipAsterisk);
      return Optional.of(LI +
              listItemString +
              LI1);
    }
    return Optional.empty();
  }

  private Optional<String> isParagraph(String markdown) {
    return Optional.of(P +
            parseSomeSymbols(markdown) +
            P1);
  }

  private String parseSomeSymbols(String markdown) {
    String workingOn = markdown.replaceAll(LOOKING_FOR, STRONG_1_STRONG);
    return workingOn.replaceAll(LOOKING_FOR1, EM_1_EM);
  }

  private Optional<String> isHeader(String markdown) {
    int count = 0;
    for (int i = 0; i < markdown.length() && markdown.charAt(i) == HASH; i++) {
      count++;
    }
    return count == 0 ? Optional.empty() :
            Optional.of(H + count + ">" +
                    markdown.substring(count + 1) +
                    H1 + count + ">");
  }

  private boolean parserString(String[] lines, StringBuilder result, boolean activeList) {
    for (String line : lines) {
      Optional<String> theLine = isHeader(line);
      theLine = theLine.isPresent() ? theLine : isListItem(line);
      theLine = theLine.isPresent() ? theLine : isParagraph(line);
      activeList = theLine.isPresent() &&
              isActiveList(result, activeList, theLine.get());
    }
    return activeList;
  }

  private static boolean isActiveList(StringBuilder result, boolean activeList, String theLine) {
    if (isRegexMatches(activeList, theLine)) {
      activeList = true;
      result.append(UL)
              .append(theLine);
    } else if (!theLine.matches(REGEX_LI) && activeList) {
      activeList = false;
      result.append(UL1)
              .append(theLine);
    } else {
      result.append(theLine);
    }
    return activeList;
  }

  private static boolean isRegexMatches(boolean activeList, String theLine) {
    return theLine.matches(REGEX_LI) &&
            !theLine.matches(REGEX_H) &&
            !theLine.matches(REGEX_P) &&
            !activeList;
  }
}
