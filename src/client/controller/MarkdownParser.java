package client.controller;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {
    private static MutableDataHolder options = new MutableDataSet();
    private static Parser parser;
    private static HtmlRenderer renderer;

    private static Pattern[] headingPatterns = {
            Pattern.compile("(?m)^#(?!#)(.*)"),
            Pattern.compile("(?m)^#{2}(?!#)(.*)"),
            Pattern.compile("(?m)^#{3}(?!#)(.*)"),
            Pattern.compile("(?m)^#{4}(?!#)(.*)"),
            Pattern.compile("(?m)^#{5}(?!#)(.*)"),
            Pattern.compile("(?m)^#{6}(?!#)(.*)"),
    };

    static {
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public static String parseMarkdown(String text) {
        Node document = parser.parse(text);
        return renderer.render(document);
    }

    public static List<Heading> getHeadingOfMarkdown(String text) {
        List<Heading> headingList = new LinkedList<>();
        for (int i = 0; i < headingPatterns.length; ++i) {
            matchHeading(headingList, headingPatterns[i], text, i);
        }
        // ascending order
        headingList.sort(Comparator.comparingInt((Heading h)-> h.getIndex()));
        return headingList;
    }

    private static void matchHeading(List<Heading> list, Pattern pattern, String text, int rank) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int index = matcher.start();
            String content = matcher.group(1);
            list.add(new Heading(content, index, rank));
        }
    }

    public static class Heading {
        private String text;
        private int index;
        private int rank;

        public Heading(String text, int index, int rank) {
            this.text = text;
            this.index = index;
            this.rank = rank;
        }

        public String getText() {
            return text;
        }

        public int getIndex() {
            return index;
        }

        public int getRank() {
            return rank;
        }
    }
}
