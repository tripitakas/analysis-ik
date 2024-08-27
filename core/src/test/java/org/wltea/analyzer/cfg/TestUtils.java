package org.wltea.analyzer.cfg;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.TokenStream;

import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;

public class TestUtils {

    public static Configuration createFakeConfigurationSub() {
        FakeConfigurationSub configurationSub = new FakeConfigurationSub();
        Dictionary.initial(configurationSub);
        return configurationSub;
    }

    public static String[] convertCharArrayToHex(char[] charArray) {
        ArrayList<String> hexList = new ArrayList<>(charArray.length);
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if(ch==0) break;
            hexList.add(String.format("%04x", (int) ch));
        }
        return hexList.toArray(new String[0]);
    }

    public static String[] tokenize(Configuration configuration, String s)
    {
        ArrayList<String> tokens = new ArrayList<>();
        try (IKAnalyzer ikAnalyzer = new IKAnalyzer(configuration)) {
            TokenStream tokenStream = ikAnalyzer.tokenStream("text", s);
            tokenStream.reset();

            while(tokenStream.incrementToken())
            {
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
                int len = offsetAttribute.endOffset()-offsetAttribute.startOffset();
                char[] chars = new char[len];
                System.arraycopy(charTermAttribute.buffer(), 0, chars, 0, len);
                tokens.add(new String(chars));
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
        return  tokens.toArray(new String[0]);
    }

    /**
     * ES插件需要指向ES的配置目录，这里使用当前项目的config目录作为配置目录，避免依赖计算机上安装ES
     */
    static class FakeConfigurationSub extends Configuration
    {
        public FakeConfigurationSub() {

        }

        @Override
        public Path getConfDir() {
            return getConfigDir();
        }

        @Override
        public Path getConfigInPluginDir() {
            return getConfigDir();
        }

        @Override
        public Path getPath(String first, String... more) {
            return FileSystems.getDefault().getPath(first, more);
        }

        private static Path getConfigDir()
        {
            String projectRoot = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();
            return new File(projectRoot, "config").toPath();
        }
    }
}
