package com.infinilabs.ik.elasticsearch;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class TestUtils {

    public static ConfigurationSub createFakeConfigurationSub() {
        return new FakeConfigurationSub();
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
    static class FakeConfigurationSub extends ConfigurationSub
    {
        public FakeConfigurationSub() {
            super(new Environment(getSettings(), getConfigDir()), getSettings());
        }

        @Override
        public Path getConfDir() {
            return getConfigDir();
        }

        @Override
        public Path getConfigInPluginDir() {
            return getConfigDir();
        }

        private static Path getConfigDir()
        {
            return new File(System.getProperty("user.dir"), "config").toPath();
        }

        private static Settings getSettings()
        {
            return Settings.builder()
                    .put("path.home",getConfigDir())
                    .build();
        }
    }
}
