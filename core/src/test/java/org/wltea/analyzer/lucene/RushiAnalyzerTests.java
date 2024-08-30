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
    public void tokenizeCase1_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String[] values = tokenize(cfg, "菩\uDB84\uDD2E龟龙麟凤凤");
        assert values.length == 5;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("龟龙麟凤");
        assert values[3].equals("凤");
        assert values[4].equals("哈哈");
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
