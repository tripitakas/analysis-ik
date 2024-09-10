package net.tripitakas.ik.elasticsearch;

import com.infinilabs.ik.elasticsearch.ConfigurationSub;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.lucene.RushiTokenizer;

public class RushiTokenizerFactory extends AbstractTokenizerFactory {
  private Configuration configuration;

  public RushiTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
      super(indexSettings, settings,name);
      configuration = new ConfigurationSub(env,settings);
  }


  public static RushiTokenizerFactory getRushiTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
      return new RushiTokenizerFactory(indexSettings,env, name, settings).setSmart(false);
  }

    public static RushiTokenizerFactory getRushiSmartTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new RushiTokenizerFactory(indexSettings,env, name, settings).setSmart(true);
    }

    public RushiTokenizerFactory setSmart(boolean smart){
        this.configuration.setUseSmart(smart);
        return this;
    }

  @Override
  public Tokenizer create() {
      return new RushiTokenizer(configuration);  }
}
