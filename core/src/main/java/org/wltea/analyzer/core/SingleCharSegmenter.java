package org.wltea.analyzer.core;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.help.SurrogatePairHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * 如是基于IKSegmenter改造，只是把CJKSegmenter、QuantifierSegmenter删除
 * todo: 提交PR，让IKSegmenter为非final类，并且把loadSegmenters搞成虚方法，这样我们这个类只要继承IKSegmenter即可
 * 考虑Surrogate Pair的单字符分词器，一个Surrogate Pair是一个Unicode字符，由两个16位的char组成。
 *
 */
public final class SingleCharSegmenter implements IRootSegmenter{
    //字符窜reader
    private Reader input;
    //分词器上下文
    private AnalyzeContext context;
    //分词处理器列表
    private List<ISegmenter> segmenters;
    //分词歧义裁决器
    private IKArbitrator arbitrator;
    private Configuration configuration;
    private boolean isFirstNext = true;
    private final LinkedList<LexemeWrapper> lexemeQueue = new LinkedList<LexemeWrapper>();

    /**
     * IK分词器构造函数
     * @param input
     */
    public SingleCharSegmenter(Reader input ,Configuration configuration){
        this.input = input;
        this.configuration = configuration;
        this.init();
        isFirstNext = true;
    }


    /**
     * 初始化
     */
    private void init(){
        //初始化分词上下文
        this.context = new AnalyzeContext(configuration);
        //加载子分词器
        this.segmenters = this.loadSegmenters();
        //加载歧义裁决器
        this.arbitrator = new IKArbitrator();
    }

    /**
     * 初始化词典，加载子分词器实现
     * @return List<ISegmenter>
     */
    private List<ISegmenter> loadSegmenters(){
        List<ISegmenter> segmenters = new ArrayList<ISegmenter>(4);
        //处理字母的子分词器
        segmenters.add(new LetterSegmenter());
        //处理由两个char组成的SurrogatePair
        segmenters.add(new SurrogatePairSegmenter());
        return segmenters;
    }

    public synchronized Lexeme next()throws IOException {
        if(isFirstNext)
        {
            //一次性分完，放到lexemeQueue中，再用next一个一个取出来
            isFirstNext = false;
            Set<LexemeWrapper> lexemeSet = new java.util.HashSet<LexemeWrapper>();
            Lexeme next;
            while((next = doNext())!=null)
            {
                lexemeSet.add(new LexemeWrapper(next));
                String lexemeText = next.getLexemeText();
                //把词拆分成字符（有可能是Surrogate Pair，一个Surrogate Pair看成一个逻辑字符）
                if(SurrogatePairHelper.isSingleLogicChar(lexemeText))//如果原始词元就是单字或者一个SurrogatePair，就没必要再拆分了
                {
                    String[] chars = SurrogatePairHelper.splitIntoChars(lexemeText);
                    for (int i=0; i<chars.length; i++) {
                        String aChar = chars[i];
                        int offset = next.getOffset();
                        int begin = next.getBegin()+i;
                        int length = aChar.length();
                        Lexeme lexeme = new Lexeme(offset, begin, length, Lexeme.TYPE_CNCHAR);
                        lexeme.setLexemeText(aChar);
                        lexemeSet.add(new LexemeWrapper(lexeme));
                    }
                }
            }
            //按照ES的要求：把lexemeSet中的内容按照BeginPosition升序排序，对于offset相同的项再按照EndPosition降序排列，然后放到lexemeQueue中
            lexemeQueue.clear();
            lexemeQueue.addAll(lexemeSet);
            lexemeQueue.sort(Comparator.comparingInt(LexemeWrapper::getBeginPosition).thenComparing(Comparator.comparingInt(LexemeWrapper::getEndPosition).reversed()));
        }

        return lexemeQueue.isEmpty()?null: lexemeQueue.poll().getLexeme();
    }

    /**
     * 分词，获取下一个词元
     * @return Lexeme 词元对象
     * @throws java.io.IOException
     */
    public synchronized Lexeme doNext()throws IOException{
        Lexeme l = null;
        while((l = context.getNextLexeme()) == null ){
            /*
             * 从reader中读取数据，填充buffer
             * 如果reader是分次读入buffer的，那么buffer要  进行移位处理
             * 移位处理上次读入的但未处理的数据
             */
            int available = context.fillBuffer(this.input);
            if(available <= 0){
                //reader已经读完
                context.reset();
                return null;

            }else{
                //初始化指针
                context.initCursor();
                do{
                    //遍历子分词器
                    for(ISegmenter segmenter : segmenters){
                        segmenter.analyze(context);
                    }
                    //字符缓冲区接近读完，需要读入新的字符
                    if(context.needRefillBuffer()){
                        break;
                    }
                    //向前移动指针
                }while(context.moveCursor());
                //重置子分词器，为下轮循环进行初始化
                for(ISegmenter segmenter : segmenters){
                    segmenter.reset();
                }
            }
            //对分词进行歧义处理
            this.arbitrator.process(context, configuration.isUseSmart());
            //将分词结果输出到结果集，并处理未切分的单个CJK字符
            context.outputToResult();
            //记录本次分词的缓冲区位移
            context.markBufferOffset();
        }
        return l;
    }

    /**
     * 重置分词器到初始状态
     * @param input
     */
    public synchronized void reset(Reader input) {
        this.input = input;
        context.reset();
        for(ISegmenter segmenter : segmenters){
            segmenter.reset();
        }
        isFirstNext = true;
    }

    /**
     * 返回末尾非CJK字符字符数目
     */
    public int getLastUselessCharNum() {
        return this.context.getLastUselessCharNum();
    }
}
