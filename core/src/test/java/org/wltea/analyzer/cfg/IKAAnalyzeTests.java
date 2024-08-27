package org.wltea.analyzer.cfg;


import org.junit.Test;

public class IKAAnalyzeTests {

    /**
     * 单char汉字+一个Surrogate Pair
     */
    @Test
    public void tokenizeCase1_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = TestUtils.tokenize(cfg, "菩\uDB84\uDD2E");
        assert values.length == 2;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
    }

    /**
     * 单char汉字+一个Surrogate Pair+单char汉字
     */
    @Test
    public void tokenizeCase2_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = TestUtils.tokenize(cfg, "菩\uDB84\uDD2E凤");
        assert values.length == 3;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("凤");
    }

    /**
     * 单char汉字和多Surrogate Pair混合
     */
    @Test
    public void tokenizeCase3_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = TestUtils.tokenize(cfg, "菩\uDB84\uDD2E剃\uDB84\uDC97");
        assert values.length == 4;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("剃");
        assert values[3].equals("\uDB84\uDC97");
    }

    /**
     * 单char汉字和多个连续Surrogate Pair混合
     */
    @Test
    public void tokenizeCase4_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = TestUtils.tokenize(cfg, "菩\uDB84\uDD2E\uDB84\uDC97");
        assert values.length == 3;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("\uDB84\uDC97");
    }

    /**
     * 单char汉字和多个连续Surrogate Pair加词库中的词
     */
    @Test
    public void tokenizeCase5_correctly()
    {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        String[] values = TestUtils.tokenize(cfg, "菩\uDB84\uDD2E龟龙麟凤凤");
        assert values.length == 4;
        assert values[0].equals("菩");
        assert values[1].equals("\uDB84\uDD2E");
        assert values[2].equals("龟龙麟凤");
        assert values[3].equals("凤");
    }
}
