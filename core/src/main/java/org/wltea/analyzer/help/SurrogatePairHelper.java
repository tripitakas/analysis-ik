package org.wltea.analyzer.help;

import java.util.ArrayList;

public class SurrogatePairHelper {
    /**
     * 把有可能包含Surrogate Pair的字符串拆分成字符数组，因为一个Surrogate Pair是一个Unicode字符，返回值每个元素有可能由两个16位的char组成。因此返回值用string数组，而不是char数组
     * @param s
     * @return
     */
    public static String[] splitIntoChars(String s) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isHighSurrogate(c) && i + 1 < s.length()) {
                char c2 = s.charAt(i + 1);
                if (Character.isLowSurrogate(c2)) {
                    result.add(s.substring(i, i + 2));
                    i++;
                    continue;
                }
            }
            result.add(String.valueOf(c));
        }
        return result.toArray(new String[0]);
    }

    public static boolean isSingleSurrogatePair(String s) {
        if (s.length() != 2) {
            return false;
        }
        return Character.isHighSurrogate(s.charAt(0)) && Character.isLowSurrogate(s.charAt(1));
    }
}
