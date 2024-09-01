package org.wltea.analyzer.core;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.help.SurrogatePairHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;

public class RushiSegmenter {
    private final IKSegmenter ikSegmenter;
    private final Configuration configuration;
    //用栈来保存之前的词
    private final Queue<Lexeme> lexemeQueue = new LinkedList<Lexeme>();
    public RushiSegmenter(Reader input , Configuration configuration) {
        this.ikSegmenter = new IKSegmenter(input, configuration);
        this.configuration = configuration;
    }

    public synchronized void reset(Reader input) {
        ikSegmenter.reset(input);
        lexemeQueue.clear();
    }

    public synchronized Lexeme next()throws IOException {
        Lexeme next = ikSegmenter.next();
        if(next != null) {
            String lexemeText = next.getLexemeText();
            //把词拆分成字符（有可能是Surrogate Pair，一个Surrogate Pair看成一个逻辑字符）

            if(lexemeText.length()>1&&!SurrogatePairHelper.isSingleSurrogatePair(lexemeText))//如果原始词元就是单字或者一个SurrogatePair，就没必要再拆分了
            {
                String[] chars = SurrogatePairHelper.splitIntoChars(lexemeText);
                for (int i=0; i<chars.length; i++) {
                    String aChar = chars[i];
                    int offset = next.getBegin() + i;
                    int begin = next.getOffset()+i;
                    int length = aChar.length();
                    int lexemeType = next.getLexemeType();
                    Lexeme lexeme = new Lexeme(offset, begin, length, lexemeType);
                    lexeme.setLexemeText(aChar);
                    lexemeQueue.add(lexeme);
                }
            }
            return next;
        }
        //开始把之前的词进行拆分，进一步的补充
        if(lexemeQueue.isEmpty())
        {
            return null;
        }
        return lexemeQueue.poll();
    }
    public int getLastUselessCharNum() {
        return 0;
    }
}
