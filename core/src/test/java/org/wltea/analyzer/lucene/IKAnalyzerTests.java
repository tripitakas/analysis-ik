package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.TestUtils;

import java.util.ArrayList;

public class IKAnalyzerTests {

    /**
     * 单char汉字+一个Surrogate Pair
     */
    @Test
    public void tokenizeCase1_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = tokenize(cfg, "菩\uDB84\uDD2E");
        assert values.length == 2;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
    }

    /**
     * 单char汉字+一个Surrogate Pair+单char汉字
     */
    @Test
    public void tokenizeCase2_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = tokenize(cfg, "菩\uDB84\uDD2E凤");
        assert values.length == 3;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("凤");
    }

    /**
     * 单char汉字和多Surrogate Pair混合
     */
    @Test
    public void tokenizeCase3_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = tokenize(cfg, "菩\uDB84\uDD2E剃\uDB84\uDC97");
        assert values.length == 4;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("剃");
        assert values[3].equals("\uDB84\uDC97");
    }

    /**
     * 单char汉字和多个连续Surrogate Pair混合
     */
    @Test
    public void tokenizeCase4_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = tokenize(cfg, "菩\uDB84\uDD2E\uDB84\uDC97");
        assert values.length == 3;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("\uDB84\uDC97");
    }

    /**
     * 单char汉字和多个连续Surrogate Pair加词库中的词
     */
    @Test
    public void tokenizeCase5_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = tokenize(cfg, "菩\uDB84\uDD2E龟龙麟凤凤");
        assert values.length == 4;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("龟龙麟凤");
        assert values[3].equals("凤");
    }

    static String[] tokenize(Configuration configuration, String s)
    {
        ArrayList<String> tokens = new ArrayList<>();
        try (IKAnalyzer ikAnalyzer = new IKAnalyzer(configuration)) {
            TokenStream tokenStream = ikAnalyzer.tokenStream("text", s);
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
