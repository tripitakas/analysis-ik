package org.wltea.analyzer.core;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.help.SurrogatePairHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class RushiSegmenter {
    private final IKSegmenter ikSegmenter;
    private final Queue<Lexeme> lexemeQueue = new LinkedList<Lexeme>();//用栈来保存供分单字的的词
    private final Set<Lexeme> lexemeSetReturned = new java.util.HashSet<Lexeme>();//记录IK分词已经返回给调用者Lexeme集合，用于排重
    public RushiSegmenter(Reader input , Configuration configuration) {
        this.ikSegmenter = new IKSegmenter(input, configuration);
    }

    public synchronized void reset(Reader input) {
        ikSegmenter.reset(input);
        lexemeQueue.clear();
        lexemeSetReturned.clear();
    }

    public synchronized Lexeme next()throws IOException {
        Lexeme next = ikSegmenter.next();
        if(next != null) {
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
                    lexemeQueue.add(lexeme);
                }
            }
            lexemeSetReturned.add(next);
            return next;
        }
        //开始把之前的词进行拆分，进一步的补充
        return pollUniqueLexeme(lexemeQueue, lexemeSetReturned);
    }

    //从栈中取出一个唯一的(之前没有返回过的)词元
    private static Lexeme pollUniqueLexeme(Queue<Lexeme> lexemeQueue, Set<Lexeme> lexemeSetReturned) {
        Lexeme lexeme = lexemeQueue.poll();
        while(lexeme!=null)
        {
            if(!lexemeSetReturned.contains(lexeme))
            {
                lexemeSetReturned.add(lexeme);
                return lexeme;
            }
            lexeme = lexemeQueue.poll();
        }
        return null;
    }

    public int getLastUselessCharNum() {
        return 0;
    }
}
