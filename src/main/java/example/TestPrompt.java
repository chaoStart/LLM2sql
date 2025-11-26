package example;

import example.utils.Example;
import example.utils.PromptLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class TestPrompt {
    static String sqlPrompt = PromptLoader.getTemplate("Generate_sql_prompt");
    public static void test1() {
        System.out.println("获取的提示词模板: " + sqlPrompt);
    }

    public static void test2()  {
        // 构造当前请求的 Example（不含 sql）
        String question = "近15天超音数访问次数汇总";
        String DbSchema = "Schema:DatabaseType=[MYSQL], DatabaseVersion=[5.7], Table=[超音数数据集], PartitionTimeField=[数据日期 FORMAT 'yyyy-MM-dd'], PrimaryKeyField=[用户名], Metrics=[<访问次数 COMMENT '一段时间内用户的访问次数'>], Dimensions=[<数据日期 FORMAT 'yyyy-MM-dd' COMMENT '数据日期'>], Values=[]";
        Example current = new Example(question, DbSchema, null);
//        current.setQuestion("近15天超音数访问次数汇总");
//        current.setSideInfo("CurrentDate=[2025-11-21],[Database does not support with statement]");
//        current.setDbSchema("Schema:DatabaseType=[MYSQL], DatabaseVersion=[5.7], Table=[超音数数据集], PartitionTimeField=[数据日期 FORMAT 'yyyy-MM-dd'], PrimaryKeyField=[用户名], Metrics=[<访问次数 COMMENT '一段时间内用户的访问次数'>], Dimensions=[<数据日期 FORMAT 'yyyy-MM-dd' COMMENT '数据日期'>], Values=[]");

        // 一行代码搞定完整的 few-shot prompt！
        String finalPrompt = PromptLoader.buildFewShotPrompt("Generate_sql_prompt", current);

        System.out.println(finalPrompt);


    }

    public static void main(String[] args) {
        test2();
    }

}