package org.wltea.analyzer.core;

import org.wltea.analyzer.cfg.Configuration;

import java.io.IOException;
import java.io.Reader;

public class BigBangSegmenter {
    private IKSegmenter ikSegmenter;

    public BigBangSegmenter(Reader input , Configuration configuration) {
        this.ikSegmenter = new IKSegmenter(input, configuration);
    }

    public synchronized void reset(Reader input) {
        ikSegmenter.reset(input);
    }

    private boolean done = false;
    public synchronized Lexeme next()throws IOException {
        Lexeme next = ikSegmenter.next();
        if(next != null) {
            return next;
        }
        if(done==false)
        {
            Lexeme newLexeme = new Lexeme(0,0, 2, Lexeme.TYPE_CNCHAR);
            newLexeme.setLexemeText("哈哈");
            done = true;
            return newLexeme;
        }
        return null;
    }
    public int getLastUselessCharNum() {
        return this.ikSegmenter.getLastUselessCharNum();
    }
}
