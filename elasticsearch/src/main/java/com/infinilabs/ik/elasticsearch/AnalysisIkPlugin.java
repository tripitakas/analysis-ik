package com.infinilabs.ik.elasticsearch;

import net.tripitakas.ik.elasticsearch.SingleCharAnalyzerProvider;
import net.tripitakas.ik.elasticsearch.SingleCharTokenizerFactory;
import net.tripitakas.ik.elasticsearch.RushiAnalyzerProvider;
import net.tripitakas.ik.elasticsearch.RushiTokenizerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;


public class AnalysisIkPlugin extends Plugin implements AnalysisPlugin {

	public static String PLUGIN_NAME = "analysis-ik";

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();


        extra.put("ik_smart", IkTokenizerFactory::getIkSmartTokenizerFactory);
        extra.put("ik_max_word", IkTokenizerFactory::getIkTokenizerFactory);
        extra.put("rs_char", SingleCharTokenizerFactory::getSingleCharTokenizerFactory);
        extra.put("rs_smart", RushiTokenizerFactory::getRushiSmartTokenizerFactory);
        extra.put("rs_max_word", RushiTokenizerFactory::getRushiTokenizerFactory);
        return extra;
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> extra = new HashMap<>();

        extra.put("ik_smart", IkAnalyzerProvider::getIkSmartAnalyzerProvider);
        extra.put("ik_max_word", IkAnalyzerProvider::getIkAnalyzerProvider);
        extra.put("rs_char", SingleCharAnalyzerProvider::getSingleCharAnalyzerProvider);
        extra.put("rs_smart", RushiAnalyzerProvider::getRushiSmartAnalyzerProvider);
        extra.put("rs_max_word", RushiAnalyzerProvider::getRushiAnalyzerProvider);
        return extra;
    }

}
