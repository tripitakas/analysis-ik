package net.tripitakas.ik.elasticsearch;

import com.infinilabs.ik.elasticsearch.ConfigurationSub;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.lucene.RushiAnalyzer;

public class RushiAnalyzerProvider extends AbstractIndexAnalyzerProvider<RushiAnalyzer> {
    private final RushiAnalyzer analyzer;

    public RushiAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings,boolean useSmart) {
        super(name, settings);

        Configuration configuration = new ConfigurationSub(env,settings).setUseSmart(useSmart);

        analyzer=new RushiAnalyzer(configuration);
    }

    public static RushiAnalyzerProvider getRushiAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new RushiAnalyzerProvider(indexSettings,env,name,settings, false);
    }

    public static RushiAnalyzerProvider getRushiSmartAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new RushiAnalyzerProvider(indexSettings,env,name,settings, true);
    }


    @Override public RushiAnalyzer get() {
        return this.analyzer;
    }
}
