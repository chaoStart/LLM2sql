package example.utils;

import lombok.Data;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import example.DAO.DatabaseSchemaInfo;
/**
 * 主实体类 Example
 */
@Data
public class Example {

    private String question;
    private String sideInfo;                    // CurrentDate=[2025-11-25]
    private DatabaseSchemaInfo dbSchema;        // 改为对象类型
    private String sql;

    // 私有构造器 + 静态工厂方法（推荐方式）
    public Example() {}

    public Example(String question, String dbSchema, Object o) {
    }

    // 静态工厂方法,这是一个创建 Example 实例的简洁、安全、现代的方式
    public static Example of(String question,
                             String table,
                             String metrics,
                             String dimensions,
                             String values,
                             String sql) {
        Example example = new Example();
        example.question = question;
        example.sql = sql;
        example.dbSchema = new DatabaseSchemaInfo(table, metrics, dimensions, values);
        example.sideInfo = generateCurrentDateSideInfo();
        return example;
    }

    // 便捷构造方法（如果你不想每次都传一堆参数）
    public static Example of(String question, String table, String sql) {
        return of(question, table, null, null, null, sql);
    }

    // 生成 CurrentDate=[yyyy-MM-dd]
    private static String generateCurrentDateSideInfo() {
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "CurrentDate=[" + formattedDate + "]"+",[Database does not support with statement]";
    }

    // ==================== 示例使用 ====================
    public static void main(String[] args) {
        Example example = Example.of(
                "统计最近7天每个用户的访问次数",
                "超音数数据集",
                "<访问次数 COMMENT '一段时间内用户的访问次数'>",
                "<数据日期 FORMAT 'yyyy-MM-dd' COMMENT '数据日期'>, <用户名 COMMENT '用户唯一标识'>",
                "",  // values 暂时为空
                "SELECT 用户名, SUM(访问次数) FROM 超音数数据集 WHERE 数据日期 >= '2025-11-18' GROUP BY 用户名"
        );

        System.out.println("sideInfo: " + example.getSideInfo());
        System.out.println("dbSchema: " + example.getDbSchema());
        System.out.println("question: " + example.getQuestion());
        System.out.println("sql: " + example.getSql());
    }
}