package example.utils;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class Example_old {
    private String question;
    private String sideInfo;
    private String dbSchema;
    private String sql;
    /**
     * 无参构造器（lombok 提供的 @Data 已经包含）
     */
    public Example_old() {
    }
    /**
     * 带参构造器，方便直接传入其他字段，sideInfo 会自动填充当前日期
     */
    public Example_old(String question, String dbSchema, String sql) {
        this.question = question;
        this.dbSchema = dbSchema;
        this.sql = sql;
        this.sideInfo = generateCurrentDateSideInfo();
    }
    /**
     * 静态工具方法：生成形如 CurrentDate=[2025-11-21] 的字符串
     */
    public static String generateCurrentDateSideInfo() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = now.format(formatter);
        return "CurrentDate=[" + formattedDate + "], "+ "[Database does not support with statement]";
    }

    public void refreshSideInfo() {
        this.sideInfo = generateCurrentDateSideInfo();
    }
}
