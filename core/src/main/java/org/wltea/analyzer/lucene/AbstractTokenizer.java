package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.core.IRootSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;

public abstract class AbstractTokenizer extends Tokenizer {
    //IK分词器实现
    private final IRootSegmenter segmenter;

    //词元文本属性
    private final CharTermAttribute termAtt;
    //词元位移属性
    private final OffsetAttribute offsetAtt;
    //词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
    private final TypeAttribute typeAtt;
    //记录最后一个词元的结束位置
    private int endPosition;

    private int skippedPositions;

    private PositionIncrementAttribute posIncrAtt;

    protected final Configuration configuration;
    /**
     * Lucene 4.0 Tokenizer适配器类构造函数
     */
    public AbstractTokenizer(Configuration configuration){
        super();
        this.configuration = configuration;
        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);

        this.segmenter = createSegmenter();
    }

    protected abstract IRootSegmenter createSegmenter();

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.TokenStream#incrementToken()
     */
    @Override
    public boolean incrementToken() throws IOException {
        //清除所有的词元属性
        clearAttributes();
        skippedPositions = 0;

        Lexeme nextLexeme = segmenter.next();
        if(nextLexeme != null){
            posIncrAtt.setPositionIncrement(skippedPositions +1 );

            //将Lexeme转成Attributes
            //设置词元文本
            termAtt.append(nextLexeme.getLexemeText());
            //设置词元长度
            termAtt.setLength(nextLexeme.getLength());
            //设置词元位移
            offsetAtt.setOffset(correctOffset(nextLexeme.getBeginPosition()), correctOffset(nextLexeme.getEndPosition()));

            //记录分词的最后位置
            endPosition = nextLexeme.getEndPosition();
            //记录词元分类
            typeAtt.setType(nextLexeme.getLexemeTypeString());
            //返会true告知还有下个词元
            return true;
        }
        //返会false告知词元输出完毕
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.lucene.analysis.Tokenizer#reset(java.io.Reader)
     */
    @Override
    public void reset() throws IOException {
        super.reset();
        segmenter.reset(input);
        skippedPositions = 0;
        endPosition = 0;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        // set final offset
        int finalOffset = correctOffset(this.endPosition+ segmenter.getLastUselessCharNum());
        offsetAtt.setOffset(finalOffset, finalOffset);
        posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
    }
}
