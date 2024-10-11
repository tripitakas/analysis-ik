package org.wltea.analyzer.core;

import java.io.IOException;
import java.io.Reader;

public class MultiPointReader {
    private Reader input;
    public MultiPointReader(Reader input) {
        this.input = input;
    }


    public int read(char[] buffer) throws IOException {
        char[] tempBuffer = new char[buffer.length-1];
        int len = input.read(tempBuffer);
        if (len == -1) {
            return -1;
        }
        if (Character.isHighSurrogate(tempBuffer[len - 1])) {
            char lowSurrogateChar = (char) input.read();
            System.arraycopy(tempBuffer, 0, buffer, 0, len);
            buffer[len] = lowSurrogateChar;
            len++;
        }
        else {
            System.arraycopy(tempBuffer, 0, buffer, 0, len);
        }
        return len;
    }
}
