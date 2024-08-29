package net.tripitakas.ik.elasticsearch;

import com.infinilabs.ik.elasticsearch.ConfigurationSub;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.lucene.SingleCharTokenizer;

public class SingleCharTokenizerFactory extends AbstractTokenizerFactory {
  private Configuration configuration;

  public SingleCharTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
      super(indexSettings, settings,name);
      configuration = new ConfigurationSub(env,settings);
  }


  public static SingleCharTokenizerFactory getRushiCharTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
      return new SingleCharTokenizerFactory(indexSettings,env, name, settings);
  }

  @Override
  public Tokenizer create() {
      return new SingleCharTokenizer(configuration);  }
}
