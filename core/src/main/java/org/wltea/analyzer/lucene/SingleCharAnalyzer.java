/**
 * IK 中文分词  版本 5.0.1
 * IK Analyzer release 5.0.1
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 */
package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.util.IOUtils;
import org.wltea.analyzer.cfg.Configuration;

public final class SingleCharAnalyzer extends StopwordAnalyzerBase {
	@Override
	protected TokenStreamComponents createComponents(String s) {
		return null;
	}
/*
	public SingleCharAnalyzer(CharArraySet stopwords) {
		super(stopwords);
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		final Tokenizer source = new StandardTokenizer();
		// run the widthfilter first before bigramming, it sometimes combines characters.
		TokenStream result = new CJKWidthFilter(source);
		result = new LowerCaseFilter(result);
		result = new CJKBigramFilter(result);
		return new TokenStreamComponents(source, new StopFilter(result, stopwords));
	}

	@Override
	protected TokenStream normalize(String fieldName, TokenStream in) {
		TokenStream result = new CJKWidthFilter(in);
		result = new LowerCaseFilter(result);
		return result;
	}*/
}
