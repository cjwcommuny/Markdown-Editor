package client;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

public class MarkdownParser {
    private static MutableDataHolder options = new MutableDataSet();
    private static Parser parser;
    private static HtmlRenderer renderer;

    static {
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public static String parseMarkdown(String text) {
        Node document = parser.parse(text);
        return renderer.render(document);
    }
}
