package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;
import org.wltea.analyzer.TestUtils;
import org.wltea.analyzer.cfg.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RushiAnalyzerTests {


    /**
     * 单char汉字和多个连续Surrogate Pair加词库中的词
     */
    @Test
    public void tokenizeCase1_smart_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub(true);
        List<String> values = Arrays.asList(tokenize(cfg, "菩\uDB84\uDD2E龟龙麟凤"));
        assert values.size() == 7;
        assert values.contains("菩");
        assert values.contains("\uDB84\uDD2E");
        assert values.contains("龟龙麟凤");
        assert values.contains("龟");
        assert values.contains("龙");
        assert values.contains("麟");
        assert values.contains("凤");
    }

    /**
     * 单char汉字和多个连续Surrogate Pair加词库中的词
     */
    @Test
    public void tokenizeCase2_smart_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub(true);
        List<String> values = Arrays.asList(tokenize(cfg, "\uDB84\uDD2E中华人民共和国国歌"));
        assert values.size() == 12;
        assert values.contains("\uDB84\uDD2E");
        assert values.contains("中华人民共和国");
        assert values.contains("中");
        assert values.contains("华");
        assert values.contains("人");
        assert values.contains("民");
        assert values.contains("共");
        assert values.contains("和");
        assert values.contains("国");
        assert values.contains("歌");
    }

    @Test
    public void tokenizeCase2_max_word_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        List<String> values = Arrays.asList(tokenize(cfg, "\uDB84\uDD2E中华人民共和国国歌"));
        assert values.size() >= 12;
        assert values.contains("\uDB84\uDD2E");
        assert values.contains("中华人民共和国");
        assert values.contains("中");
        assert values.contains("华");
        assert values.contains("人");
        assert values.contains("民");
        assert values.contains("共");
        assert values.contains("和");
        assert values.contains("国");
        assert values.contains("歌");
    }

    static String[] tokenize(Configuration configuration, String s)
    {
        ArrayList<String> tokens = new ArrayList<>();
        try (RushiAnalyzer analyzer = new RushiAnalyzer(configuration)) {
            TokenStream tokenStream = analyzer.tokenStream("text", s);
            tokenStream.reset();
            while(tokenStream.incrementToken())
            {
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
                int len = offsetAttribute.endOffset()-offsetAttribute.startOffset();
                char[] chars = new char[len];
                System.arraycopy(charTermAttribute.buffer(), 0, chars, 0, len);
                tokens.add(new String(chars));
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
        return  tokens.toArray(new String[0]);
    }
}
