package org.wltea.analyzer.core;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.help.SurrogatePairHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;

/**
 * 在IKAnalyzer的基础上，增加了对单字分词的支持
 */
public class RushiSegmenter implements IRootSegmenter {
    private final IKSegmenter ikSegmenter;
    private boolean isFirstNext = true;
    private final LinkedList<Lexeme> lexemeQueue = new LinkedList<Lexeme>();
    public RushiSegmenter(Reader input , Configuration configuration) {
        this.ikSegmenter = new IKSegmenter(input, configuration);
        isFirstNext = true;
    }

    public synchronized void reset(Reader input) {
        ikSegmenter.reset(input);
        lexemeQueue.clear();
        isFirstNext = true;
    }

    public synchronized Lexeme next()throws IOException {
        if(isFirstNext)
        {
            //一次性分完，放到lexemeQueue中，再用next一个一个取出来
            isFirstNext = false;
            Set<Lexeme> lexemeSet = new java.util.HashSet<Lexeme>();
            Lexeme next;
            while((next = ikSegmenter.next())!=null)
            {
                lexemeSet.add(next);
                String lexemeText = next.getLexemeText();
                //把词拆分成字符（有可能是Surrogate Pair，一个Surrogate Pair看成一个逻辑字符）
                if(!SurrogatePairHelper.isSingleLogicChar(lexemeText))//如果原始词元就是单字或者一个SurrogatePair，就没必要再拆分了
                {
                    String[] chars = SurrogatePairHelper.splitIntoChars(lexemeText);
                    for (int i=0; i<chars.length; i++) {
                        String aChar = chars[i];
                        int offset = next.getOffset();
                        int begin = next.getBegin()+i;
                        int length = aChar.length();
                        Lexeme lexeme = new Lexeme(offset, begin, length, Lexeme.TYPE_CNCHAR);
                        lexeme.setLexemeText(aChar);
                        lexemeSet.add(lexeme);
                    }
                }
            }
            //按照ES的要求：把lexemeSet中的内容按照BeginPosition升序排序，对于offset相同的项再按照EndPosition降序排列，然后放到lexemeQueue中
            lexemeQueue.clear();
            lexemeQueue.addAll(lexemeSet);
            lexemeQueue.sort(Comparator.comparingInt(Lexeme::getBeginPosition).thenComparing(Comparator.comparingInt(Lexeme::getEndPosition).reversed()));
        }
        return lexemeQueue.poll();
    }

    public int getLastUselessCharNum() {
        return 0;
    }
}
