package net.tripitakas.ik.elasticsearch;

import com.infinilabs.ik.elasticsearch.ConfigurationSub;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SingleCharAnalyzerProvider extends AbstractIndexAnalyzerProvider<IKAnalyzer> {
    private final IKAnalyzer analyzer;

    public SingleCharAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(name, settings);

        Configuration configuration = new ConfigurationSub(env,settings);

        analyzer=new IKAnalyzer(configuration);
    }

    public static SingleCharAnalyzerProvider getSingleCharAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new SingleCharAnalyzerProvider(indexSettings,env,name,settings);
    }


    @Override public IKAnalyzer get() {
        return this.analyzer;
    }
}
