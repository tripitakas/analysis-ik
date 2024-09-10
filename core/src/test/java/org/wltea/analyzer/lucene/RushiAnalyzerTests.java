package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Test;
import org.wltea.analyzer.TestUtils;
import org.wltea.analyzer.cfg.Configuration;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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


    @Test
    public void tokenizeCase2_max_word_then_checkTermInfo_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        List<Term> values = Arrays.asList(tokenizeAsTerms(cfg, "\uDB84\uDD2E中华人民共和国国歌"));
        //每一个元素都是唯一的
        assert  new HashSet<Term>(values).size()== values.size();
        //offset的值是递增的
        for (int i = 1; i < values.size(); i++) {
            assert values.get(i).getOffset() >= values.get(i-1).getOffset();
            //offset相同的则按照Length降序排列
            if(values.get(i).getOffset() == values.get(i-1).getOffset())
            {
                assert values.get(i).getLength() <= values.get(i-1).getLength();
            }
        }
    }

    // ElasticSearch 要求Term按照offset升序排列，如果offset相同则按照length降序排序
    @Test
    public void tokenize_longText_max_word_correctly() throws IOException {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String fileContent = "\uDB84\uDD2E中华人民共和国国歌国歌\n\uDB84\uDD2E中华人民共和国国歌国歌\r\n你好你好我好\uDB84\uDD2E\r\n阿弥陀佛";
        List<Term> values = Arrays.asList(tokenizeAsTerms(cfg, fileContent));
        //每一个元素都是唯一的
        assert  new HashSet<Term>(values).size()== values.size();
        //offset的值是递增的
        for (int i = 1; i < values.size(); i++) {
            assert values.get(i).getOffset() >= values.get(i-1).getOffset();
            //offset相同的则按照Length降序排列
            if(values.get(i).getOffset() == values.get(i-1).getOffset())
            {
                assert values.get(i).getLength() <= values.get(i-1).getLength();
            }
        }
    }


    static String[] tokenize(Configuration configuration, String s)
    {
        return Arrays.stream(tokenizeAsTerms(configuration, s))
                .map(Term::getTerm)
                .toArray(String[]::new);
    }

    static Term[] tokenizeAsTerms(Configuration configuration, String s)
    {
        ArrayList<Term> tokens = new ArrayList<>();
        try (RushiAnalyzer analyzer = new RushiAnalyzer(configuration)) {
            TokenStream tokenStream = analyzer.tokenStream("text", s);
            tokenStream.reset();
            while(tokenStream.incrementToken())
            {
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
                TypeAttribute typeAttribute = tokenStream.getAttribute(TypeAttribute.class);
                int len = offsetAttribute.endOffset()-offsetAttribute.startOffset();
                char[] chars = new char[len];
                System.arraycopy(charTermAttribute.buffer(), 0, chars, 0, len);

                Term term = new Term(new String(chars), offsetAttribute.startOffset(), len, typeAttribute.type());
                tokens.add(term);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
        return  tokens.toArray(new Term[0]);
    }

    static class Term
    {
        private String term;
        private int offset;
        private int length;
        private String type;

        public Term(String term, int offset, int length, String type) {
            this.term = term;
            this.offset = offset;
            this.length = length;
            this.type = type;
        }

        public String getTerm() {
            return term;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Term{" +
                    "term='" + term + '\'' +
                    ", offset=" + offset +
                    ", length=" + length +
                    ", type=" + type +
                    '}';
        }
    }
}
