package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;
import org.wltea.analyzer.TestUtils;
import org.wltea.analyzer.cfg.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class SingleCharAnalyzerTests {
    @Test
    public void tokenizeCase1_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String[] values = tokenize(cfg, "菩\uDB84\uDD2E龟龙麟凤");
        assert values.length == 6;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("龟");
        assert values[3].equals("龙");
        assert values[4].equals("麟");
        assert values[5].equals("凤");
    }

    @Test
    public void temp()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String allText = readAllText("d:/JS1671_025.txt");
        long countInRaw = allText.chars().filter(x -> x == '一').count();
        String[] values = tokenize(cfg, allText);
        long count = Arrays.stream(values).filter(x -> x.equals("一")).count();
        assert countInRaw == count;
    }

    //读取一个文本文件的全部内容到一个字符串
    public static String readAllText(String path)
    {
        try {
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 含有中英文标点
     */
    @Test
    public void tokenizeCase2_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String[] values = tokenize(cfg, "菩！\uDB84\uDD2E，龟:龙。麟,凤");
        assert values.length == 6;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("龟");
        assert values[3].equals("龙");
        assert values[4].equals("麟");
        assert values[5].equals("凤");
    }

    static String[] tokenize(Configuration configuration, String s)
    {
        ArrayList<String> tokens = new ArrayList<>();
        try (SingleCharAnalyzer analyzer = new SingleCharAnalyzer(configuration)) {
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
