package org.wltea.analyzer.core;

/**
 * AnalyzeContext中每一组都是4096个字符，这样每一组的最后一个字符和下一组的第一个字符重复，但是他们的begin和offset虽然不同，但是他们的begin+offset是相同的。
 * 想要了解这个bug，请看SingleCharAnalyzerTests的dumpTokenize方法的生成的csv文件。
 * 由于HashSet不支持自定义equals和hashCode生成器，所以我们需要把Lexeme包装一下，然后重写equals和hashCode方法
 */
public final class LexemeWrapper{
    private final Lexeme lexeme;

    public LexemeWrapper(Lexeme lexeme) {
        this.lexeme = lexeme;
    }

    public Lexeme getLexeme() {
        return lexeme;
    }

    public boolean equals(Object o){
        if(o == null){
            return false;
        }

        if(this == o){
            return true;
        }

        if(o instanceof LexemeWrapper){
            LexemeWrapper other = (LexemeWrapper)o;
            if(this.getLexeme().getOffset()+this.getLexeme().getBegin() == other.getLexeme().getOffset()+ other.getLexeme().getBegin()
                    && this.getLexeme().getLength() == other.getLexeme().getLength()){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public int hashCode(){
        return this.getLexeme().getOffset()+this.getLexeme().getBegin() + this.getLexeme().getLength();
    }

    public int getBeginPosition(){
        return getLexeme().getBeginPosition();
    }

    public int getEndPosition(){
        return getLexeme().getEndPosition();
    }
}
