package example.DAO;

import lombok.Data;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;

/**
 * 数据库结构信息对象
 */
@Data
public class DatabaseSchemaInfo {

    // ==================== 固定不变的字段 ====================
    private final String databaseType = "MYSQL";
    private final String databaseVersion = "5.7";
    private final String partitionTimeField = "[数据日期 FORMAT 'yyyy-MM-dd']";
    private final String primaryKeyField = "用户名";

    // ==================== 可变字段（由构造器传入） ====================
    private final String table;                    // 表名
    private final String metrics;                  // 指标字段（如：<访问次数 COMMENT '...'>,...）
    private final String dimensions;               // 维度字段
    private final String values;                   // 值字段（目前可为空）

/*    // 构造器
    public DatabaseSchemaInfo(String table, String metrics, String dimensions, String values) {
        this.table = table;
        this.metrics = metrics != null ? metrics : "";
        this.dimensions = dimensions != null ? dimensions : "";
        this.values = values != null ? values : "";
    }*/

    // 关键：加上这个，让 Jackson 能从 JSON 构造对象
    @JsonCreator
    public DatabaseSchemaInfo(
            @JsonProperty("table") String table,
            @JsonProperty("metrics") String metrics,
            @JsonProperty("dimensions") String dimensions,
            @JsonProperty("values") String values) {
        this.table = table != null ? table : "";
        this.metrics = metrics != null ? metrics : "";
        this.dimensions = dimensions != null ? dimensions : "";
        this.values = values != null ? values : "";
    }

    // 自定义 toString，生成你期望的格式化字符串（用于最终输出或日志）
    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        sj.add("DatabaseType=[" + databaseType + "]");
        sj.add("DatabaseVersion=[" + databaseVersion + "]");
        sj.add("Table=[" + table + "]");
        sj.add("PartitionTimeField=[" + partitionTimeField + "]");
        sj.add("PrimaryKeyField=[" + primaryKeyField + "]");
        if (!metrics.isEmpty()) {
            sj.add("Metrics=[" + metrics + "]");
        }
        if (!dimensions.isEmpty()) {
            sj.add("Dimensions=[" + dimensions + "]");
        }
        if (!values.isEmpty()) {
            sj.add("Values=[" + values + "]");
        }
        return sj.toString();
    }
}

