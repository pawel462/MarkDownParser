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


    public String parse(String markdown) {
        String[] lines = markdown.split("\n");
        StringBuilder result = new StringBuilder();
        boolean activeList = false;
        return getParserString(lines, result, activeList);
    }

    private Optional<String> isListItem(String markdown) {
        if (markdown.startsWith("*")) {
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
        for (int i = 0; i < markdown.length() && markdown.charAt(i) == '#'; i++) {
            count++;
        }
        return count == 0 ? Optional.empty() :
                Optional.of("<h" + count + ">" +
                markdown.substring(count + 1) +
                "</h" + count + ">");
    }

    private String getParserString(String[] lines, StringBuilder result, boolean activeList) {
        activeList = parserString(lines, result, activeList);
        StringBuilder stringBuilder = activeList ? result.append(UL1) : result;
        return stringBuilder.toString();
    }

    private boolean parserString(String[] lines, StringBuilder result, boolean activeList) {
        for (String line : lines) {
            Optional<String> theLine = isHeader(line);
            theLine = theLine.isEmpty() ? isListItem(line) : theLine;
            theLine = theLine.isEmpty() ? isParagraph(line) : theLine;
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
