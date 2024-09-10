package org.wltea.analyzer.lucene;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.core.IRootSegmenter;
import org.wltea.analyzer.core.SingleCharSegmenter;

/**
 * 基于IKTokenizer改造，只是把IKTokenizer中的IKSegmenter替换成了SingleCharSegmenter
 */
public final class SingleCharTokenizer extends AbstractTokenizer {

    /**
	 * Lucene 4.0 Tokenizer适配器类构造函数
     */
	public SingleCharTokenizer(Configuration configuration){
	    super(configuration);
	}

	@Override
	protected IRootSegmenter createSegmenter() {
		return new SingleCharSegmenter(input,configuration);
	}
}
