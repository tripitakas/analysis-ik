package net.tripitakas.ik.elasticsearch;

import com.infinilabs.ik.elasticsearch.ConfigurationSub;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.lucene.SingleCharAnalyzer;

public class SingleCharAnalyzerProvider extends AbstractIndexAnalyzerProvider<SingleCharAnalyzer> {
    private final SingleCharAnalyzer analyzer;

    public SingleCharAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(name, settings);

        Configuration configuration = new ConfigurationSub(env,settings);

        analyzer=new SingleCharAnalyzer(configuration);
    }

    public static SingleCharAnalyzerProvider getSingleCharAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new SingleCharAnalyzerProvider(indexSettings,env,name,settings);
    }


    @Override public SingleCharAnalyzer get() {
        return this.analyzer;
    }
}
