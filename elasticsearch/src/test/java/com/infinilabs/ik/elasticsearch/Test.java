package com.infinilabs.ik.elasticsearch;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        //todo: 完善更多单元测试
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        try (IKAnalyzer ikAnalyzer = new IKAnalyzer(cfg)) {
            org.apache.lucene.analysis.TokenStream tokenStream = ikAnalyzer.tokenStream("text","又見菩\uDB84\uDD2E，處林放光，濟地獄苦，令入佛\uDB84\uDC01。又見佛子\uD83D\uDE00\uD83D\uDE43龟龙麟凤剃\uDB84\uDC97鬚髪。或見菩\uDB84\uDCA7做张做势牛哈");
            tokenStream.reset();

            while(tokenStream.incrementToken())
            {
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
                int len = offsetAttribute.endOffset()-offsetAttribute.startOffset();
                char[] chars = new char[len];
                System.arraycopy(charTermAttribute.buffer(), 0, chars, 0, len);
                TypeAttribute typeAttribute = tokenStream.getAttribute(TypeAttribute.class);
                System.out.println(charTermAttribute.toString()+"-"+typeAttribute.type()+"-"+String.join(",",TestUtils.convertCharArrayToHex(chars)));
            }
        }
    }
}