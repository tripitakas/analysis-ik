package org.wltea.analyzer.lucene;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.core.IRootSegmenter;
import org.wltea.analyzer.core.RushiSegmenter;

public final class RushiTokenizer extends AbstractTokenizer {
	public RushiTokenizer(Configuration configuration){
		super(configuration);
	}

	@Override
	protected IRootSegmenter createSegmenter() {
		return new RushiSegmenter(input,this.configuration);
	}
}
