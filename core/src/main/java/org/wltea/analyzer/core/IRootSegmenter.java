package org.wltea.analyzer.core;

import java.io.IOException;
import java.io.Reader;

public interface IRootSegmenter {
    void reset(Reader input);
    Lexeme next() throws IOException;
    int getLastUselessCharNum();
}
