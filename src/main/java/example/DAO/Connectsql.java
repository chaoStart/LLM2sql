package example.DAO;

import com.hankcs.hanlp.dictionary.DynamicCustomDictionary;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.Term;
import example.utils.Keymapper;

import java.sql.*;
import java.util.*;

public class Connectsql {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/supersonic?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";
    // 缓存 Keymapper 实例，避免每次调用都查数据库（可根据需求调整）
    private static volatile Keymapper cachedKeymapper = null;

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            // 获取当前数据库名称
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT DATABASE()")) {
                if (rs.next()) {
                    System.out.println("当前数据库: " + rs.getString(1));
                }
            }

            // 获取所有表
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                System.out.println("\n数据库中的表:");
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("- " + tableName);

                    // 获取表的列信息
                    try (ResultSet columns = metaData.getColumns(null, null, tableName, "%")) {
                        while (columns.next()) {
                            String columnName = columns.getString("COLUMN_NAME");
                            String dataType = columns.getString("TYPE_NAME");
                            System.out.println("  └─ " + columnName + " (" + dataType + ")");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对外提供的核心方法：输入文本，返回其中命中的数据库指标词（Term 列表）
     */
    public static List<Term> extractMetricTerms(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 懒加载 + 简单单例（非严格线程安全，生产环境可加锁或使用更健壮的初始化方式）
        if (cachedKeymapper == null) {
            cachedKeymapper = loadCustomWordsFromDatabase();
            if (cachedKeymapper == null) {
                System.err.println("警告：自定义词典加载失败，将使用空词典进行匹配");
                return Collections.emptyList();
            }
        }

        ViterbiSegment segment = cachedKeymapper.getSegment();
        Set<String> metricSet = new HashSet<>(cachedKeymapper.getMetricNames());

        // 强制启用自定义词典（确保优先识别）
        segment.enableCustomDictionary(true);
        segment.enableCustomDictionaryForcing(true);

        List<Term> termList = segment.seg(text);
        List<Term> matchedTerms = new ArrayList<>();

        for (Term term : termList) {
            if (metricSet.contains(term.word)) {
                matchedTerms.add(term);
            }
        }

        return matchedTerms;
    }

    /**
     * 从数据库加载自定义词典
     */
    private static Keymapper loadCustomWordsFromDatabase() {
        DynamicCustomDictionary customDict = new DynamicCustomDictionary();
        List<String> metricNames = new ArrayList<>();

        String sql = "SELECT name FROM s2_metric";
        List<String> description = new ArrayList<>(Arrays.asList("数据库指标metric对于的描述"));
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String word = rs.getString("name");
                if (word != null && !word.trim().isEmpty()) {
                    word = word.trim();
                    customDict.add(word); // HanLP 的 add 通常返回 boolean，但这里我们不依赖其结果
                    metricNames.add(word);
                }
            }

            ViterbiSegment viterbi = new ViterbiSegment();
            viterbi.enableCustomDictionary(customDict);
            return new Keymapper(viterbi, metricNames, description);

        } catch (SQLException e) {
            System.err.println("数据库读取失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 保留 main 用于测试
/*    public static void main(String[] args) {
        String text = "大唐江苏逆袭你的欲梦，迎娶黄雨萌，吃上漂亮饭";
        List<Term> matchedTerms = extractMetricTerms(text);

        System.out.println("\n=== 在数据库指标词典中成功命中的词 ===");
        if (matchedTerms.isEmpty()) {
            System.out.println("（无匹配项）");
        } else {
            matchedTerms.forEach(term ->
                    System.out.println(term.word + "  (offset=" + term.offset + ")")
            );
        }
        System.out.println("--------------------------------");
    }*/
}