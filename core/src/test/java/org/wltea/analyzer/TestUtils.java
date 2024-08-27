package org.wltea.analyzer;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.TokenStream;

import org.wltea.analyzer.cfg.Configuration;
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
