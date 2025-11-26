package example.DAO;

import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.DynamicCustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.Term;
import example.utils.Keymapper;

import java.sql.*;
import java.util.*;

public class SqlDictionary {
    // 数据库连接信息（请根据你的环境修改）
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dev-empoworx-rag?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    public static void main(String[] args) {
        // 1. 从数据库加载自定义词典
        Keymapper segmentresults = loadCustomWordsFromDatabase();
        Segment viterbi = segmentresults.getSegment();
        List<String> dataset_metric_name = segmentresults.getMetricNames();

        String text = "王者荣耀十周年庆典，有请宫本武藏李白和朵利亚出场";

        Segment segment = viterbi.enableCustomDictionary(true).enableCustomDictionaryForcing(true); // ✅ 强制优先使用自定义词典
        List<Term> termList = segment.seg(text);
        // ================== 关键：找出 termList 中完全出现在 dataset_metric_name 中的词 ==================
        List<Term> matchedTerms = new ArrayList<>();
        List<String> matchedWords = new ArrayList<>();   // 如果你只想要字符串，也可以只存这个

        // 方法1：最直观、推荐（把 dataset_metric_name 转成 Set，查找 O(1)）
        Set<String> metricSet = new HashSet<>(dataset_metric_name);  // 加速查找
        for (Term term : termList) {
            String word = term.word;  // Term 通常有 getWord() 方法
            if (metricSet.contains(word)) {
                matchedTerms.add(term);
                matchedWords.add(word);
            }
        }

        System.out.println("\n=== 在数据库指标词典中成功命中的词 ===");
        matchedTerms.forEach(term -> System.out.println(term.word + "  (offset=" + term.offset + ")"));
/*        System.out.println("命中的指标名称列表：");
        matchedWords.forEach(System.out::println);*/
        System.out.println("--------------------------------");

//        return matcthedTerms;
    }

    /**
     * 从数据库 superdictinonary.s2_metric 表中读取 name 字段，加入 HanLP 自定义词典
     */
    private static Keymapper loadCustomWordsFromDatabase() {
        // 创建分词器并启用自定义词典优先
        DynamicCustomDictionary CreateDictionary = new DynamicCustomDictionary();

        String sql = "SELECT name FROM s2_metric"; // 只读取 name 字段

        List<String> dataset_metric_name = new ArrayList<>();
        List<String> description = new ArrayList<>(Arrays.asList("数据库指标metric对于的描述"));
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String word = rs.getString("name");
                if (word != null && !word.trim().isEmpty()) {
                    // 简单添加：默认词性 nz，频次 1000（可根据需要扩展表结构存储词性和频次）
                    boolean added = CreateDictionary.add(word.trim());
                    dataset_metric_name.add(word.trim());
//                    System.out.println("Added word: " + word.trim() + " -> " + added);
                }
            }
            ViterbiSegment viterbi = new ViterbiSegment();
            viterbi.enableCustomDictionary(CreateDictionary);
//            return viterbi.enableCustomDictionary(CreateDictionary);
            return new Keymapper(viterbi, dataset_metric_name, description);
        } catch (SQLException e) {
            System.err.println("数据库读取失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}