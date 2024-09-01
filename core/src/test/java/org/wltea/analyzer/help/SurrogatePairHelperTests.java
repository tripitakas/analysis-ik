package org.wltea.analyzer.help;

import org.junit.Test;

public class SurrogatePairHelperTests {
    @Test
    public void splitIntoChars_correctly() {
        SurrogatePairHelper helper = new SurrogatePairHelper();
        String[] values = helper.splitIntoChars("菩\uD84C\uDD2E龟龙");
        assert values.length == 4;
        assert values[0].equals("菩");
        assert values[1].equals("\uD84C\uDD2E");
        assert values[2].equals("龟");
        assert values[3].equals("龙");
    }
}
