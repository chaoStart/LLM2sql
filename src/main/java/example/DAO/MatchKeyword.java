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

public class MatchKeyword {
    // 数据库连接信息（请根据你的环境修改）
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dev-empoworx-rag?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    public static List<Term> matchword(String text) {
        // 1. 从数据库加载自定义词典
        Keymapper segmentresults = loadCustomWordsFromDatabase();
        Segment viterbi = segmentresults.getSegment();
        List<String> dataset_metric_name = segmentresults.getMetricNames();
        List<String> dataset_metric_description = segmentresults.getDescription();

        // 合并两个列表为一个Map
        Map<String, String> metricMap = combineMetrics(dataset_metric_name, dataset_metric_description);

        Segment segment = viterbi.enableCustomDictionary(true).enableCustomDictionaryForcing(true); // ✅ 强制优先使用自定义词典
        List<Term> termList = segment.seg(text);
        // ================== 关键：找出 termList 中完全出现在 dataset_metric_name 中的词 ==================
        List<Term> matchedTerms = new ArrayList<>();
        List<String> matchedWords = new ArrayList<>();   // 如果你只想要字符串，也可以只存这个

        // 方法1：最直观、推荐（把 dataset_metric_name 转成 Set，查找 O(1)）
        Set<String> metricSet = new HashSet<>(dataset_metric_name);  // 加速查找
        for (Term term : termList) {
            String word = term.word;
            if (metricSet.contains(word)) {
                matchedTerms.add(term);
                matchedWords.add(word);
            }
        }
        System.out.println("\n=== 在数据库指标词典中成功命中的词 ===");
        matchedTerms.forEach(term -> System.out.println(term.word + "  (offset=" + term.offset + ")"));
        return matchedTerms;
    }

    public static Map<String, String> combineMetrics(List<String> metricName, List<String> metricDescription) {
        if (metricName.size() != metricDescription.size()) {
            throw new IllegalArgumentException("两个列表长度必须相同");
        }

        Map<String, String> result = new LinkedHashMap<>(); // 保持插入顺序
        for (int i = 0; i < metricName.size(); i++) {
            result.put(metricName.get(i), metricDescription.get(i));
        }
        return result;
    }
    /**
     * 从数据库 superdictinonary.s2_metric 表中读取 name 字段，加入 HanLP 自定义词典
     */
    private static Keymapper loadCustomWordsFromDatabase() {
        // 创建分词器并启用自定义词典优先
        DynamicCustomDictionary CreateDictionary = new DynamicCustomDictionary();

        String sql = "SELECT name FROM s2_metric"; // 只读取 name 字段

        List<String> dataset_metric_name = new ArrayList<>();
        List<String> dataset_metric_description = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String word = rs.getString("name");
                String description = rs.getString("description");
                if (word != null && !word.trim().isEmpty()) {
                    // 简单添加：默认词性 nz，频次 1000（可根据需要扩展表结构存储词性和频次）
                    boolean added = CreateDictionary.add(word.trim());
                    dataset_metric_name.add(word.trim());
                    dataset_metric_description.add(description.trim());
//                    System.out.println("Added word: " + word.trim() + " -> " + added);
                }
            }
            ViterbiSegment viterbi = new ViterbiSegment();
            viterbi.enableCustomDictionary(CreateDictionary);
//            return viterbi.enableCustomDictionary(CreateDictionary);
            return new Keymapper(viterbi, dataset_metric_name, dataset_metric_description);
        } catch (SQLException e) {
            System.err.println("数据库读取失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}