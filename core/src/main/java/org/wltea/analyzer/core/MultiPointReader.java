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
        System.arraycopy(tempBuffer, 0, buffer, 0, len);
        //如果最后一个字符是高代理字符，那么再读取一个低代理字符
        if (Character.isHighSurrogate(tempBuffer[len - 1])) {
            int nextChar = input.read();
            if(nextChar==-1)
            {
                return len;
            }
            char lowSurrogateChar = (char) nextChar;
            buffer[len] = lowSurrogateChar;
            len++;
        }
        return len;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        if(len<0)
        {
            throw new IllegalArgumentException("len must be >= 0");
        }
        else if(len==0)
        {
            return 0;
        }
        else if(len==1)
        {
            int c = input.read();
            if(c==-1)
            {
                return -1;
            }
            cbuf[off] = (char)c;
            return 1;
        }
        // Read len-1 characters into a temporary buffer
        char[] tempBuffer = new char[len - 1];
        int charsRead = input.read(tempBuffer);

        // If no characters were read, return -1 (end of stream)
        if (charsRead == -1) {
            return -1;
        }

        // Copy the characters from the temporary buffer into the target buffer
        System.arraycopy(tempBuffer, 0, cbuf, off-1, charsRead);

        // Check if the last character read is a high surrogate
        if (Character.isHighSurrogate(tempBuffer[charsRead - 1])) {
            // Read one more character and add it to the buffer if possible
            int nextChar = input.read();
            if(nextChar==-1)
            {
                return charsRead;
            }
            else {
                cbuf[off + charsRead] = (char) nextChar;
                return charsRead + 1; // Return the total number of characters read
            }
        }

        // Return the number of characters read if no extra character was needed
        return charsRead;
    }
}
