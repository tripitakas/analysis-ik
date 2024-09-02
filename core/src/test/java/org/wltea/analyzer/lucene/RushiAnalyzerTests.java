package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Test;
import org.wltea.analyzer.TestUtils;
import org.wltea.analyzer.cfg.Configuration;

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
        assertEveryElementUnique(values);
    }

    //每一个元素都是唯一的
    static void assertEveryElementUnique(List<Term> terms)
    {
        assert  new HashSet<Term>(terms).size()==terms.size();
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
