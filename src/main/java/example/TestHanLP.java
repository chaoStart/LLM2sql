package example;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.DynamicCustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.Term;
import org.testng.annotations.Test;
import java.util.List;

/**
 * 演示用户词典的动态增删
 *
 * @author hankcs
 */
public class TestHanLP {

    public static void test1() {
        System.out.println(CustomDictionary.add("大唐江苏", "nz 1024 n 1"));
        System.out.println("--------------------------------");
        String text = "大唐江苏日指标7月1日光伏电厂发电量是多少？";
        // 创建分词器并启用自定义词典优先
        DynamicCustomDictionary CreateDictionary = new DynamicCustomDictionary();
        CreateDictionary.add("大唐江苏公司", "nz 1024 n 1");
        CreateDictionary.add("光伏电厂的发电量", "nz 1024 n 1");
        CreateDictionary.add("光伏厂发电量", "nz 1024 n 1");
        CreateDictionary.add("光伏电厂发电量", "nz 1024 n 1");
        ViterbiSegment viterbi = new ViterbiSegment();
        viterbi.enableCustomDictionary(CreateDictionary);

        Segment segment = viterbi.enableCustomDictionary(true).enableCustomDictionaryForcing(true); // ✅ 强制优先使用自定义词典
        List<Term> termList = segment.seg(text);
        System.out.println(termList);
        System.out.println("--------------------------------");
    }

    public static void test2() {

        CustomDictionary.DEFAULT.load("D:\\JavaCache\\supernic\\src\\main\\resources\\CustomDictionary.txt");
        String text = "朵利亚和贝利亚的区别";
        System.out.println("--------------------------------");
        // AhoCorasickDoubleArrayTrie自动机扫描文本中出现的自定义词语
        final char[] charArray = text.toCharArray();
        CustomDictionary.parseText(charArray, new AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute>() {
            @Override
            public void hit(int begin, int end, CoreDictionary.Attribute value)
            {
                System.out.printf("[%d:%d]=%s %s\n", begin, end, new String(charArray, begin, end - begin), value);
            }
        });
        System.out.println("-----------------------------");
        // 自定义词典在所有分词器中都有效
        System.out.println(HanLP.segment(text));
    }
    public static void test3() {
        String text = "大唐江苏公司日指标7月1日光伏厂发电量是多少？";
        DynamicCustomDictionary CreateDictionary = new DynamicCustomDictionary();
        // 2. 然后直接覆盖 path 字段为你想要的实际路径
        CreateDictionary.add("大唐江苏公司", "nz 1024 n 1");
        CreateDictionary.add("光伏电厂的发电量", "nz 1024 n 1");
        CreateDictionary.add("光伏厂发电量", "nz 1024 n 1");
        CreateDictionary.add("光伏电厂发电量", "nz 1024 n 1");
        // 创建分词器并启用自定义词典优先
        Segment NewHanLP = HanLP.newSegment().enableCustomDictionary(CreateDictionary);
        Segment segment = NewHanLP.enableCustomDictionary(true).enableCustomDictionaryForcing(true); // ✅ 强制优先使用自定义词典
        List<Term> termList = segment.seg(text);
        System.out.println(termList);
    }

    public static void test4() {
        String text = "朵利亚、贝利亚、叙利亚之间是什么关系？";
//        CustomDictionary.DEFAULT.load("D:\\JavaCache\\emporxRag\\src\\main\\java\\com\\example\\CustomDictionary.txt");
//        DynamicCustomDictionary CreateDictionary = new DynamicCustomDictionary(path);
        String path = "D:\\JavaCache\\supernic\\src\\main\\resources\\CustomDictionary.txt";
        ViterbiSegment hh = new ViterbiSegment(path);
        Segment segment = hh.enableCustomDictionary(true).enableCustomDictionaryForcing(true); // ✅ 强制优先使用自定义词典
        List<Term> termList = segment.seg(text);
        System.out.println(termList);
    }

    public static void main(String[]  args) {
            test4();
    }
}