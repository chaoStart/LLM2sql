package example.headless.demo;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.hankcs.hanlp.seg.common.Term;
import example.DAO.DatabaseSchemaInfo;
import example.DAO.MatchKeyword;
import example.config.Config;
import example.utils.Example;
import example.utils.PromptLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LLM2sql {

    static Config config;
    static {
        config = new Config();
        config.setApi_key(" ");
        config.setUrl("http://langchain4j.dev/demo/openai/v1/");
        config.setModel_name("gpt-4o-mini");
    }
    static MatchKeyword matchKeyword;
//    static String query = "近15天大数据分析部访问次数汇总";
    static String query = "昨天大数据分析部访问次数汇总";

    public static Map<String, String> get_keyword(String text){
        // 1、匹配查询问题中的指标（专有名称）
        Map<String, String> keyword_result = matchKeyword.matchword(text);
        System.out.println("-----关键词-------");
        System.out.println(keyword_result);
        List<String> metrics = formatMetricMap(keyword_result);
        metrics.add(" AGGREGATE 'SUM'");
        // 2、补全变量补充Prompt提示模板中的4个占位符中的内容
        Example current_chat = Example.of(
                query,
                "数字中心平台",
                String.join("", metrics),
                "<数据日期>",  //示例：Dimensions=[<部门>,<数据日期>]
                "<部门='大数据分析部'>",  // values 暂时为空,为值字段; 示例：Values[<用户='jackjchen'>,<用户='robinlee'>]
                ""
        );
        // 3、提示词补充 + few_shot示例 + 当前问题 + 数据库中表字段信息
        String finalPrompt = PromptLoader.buildFewShotPrompt("Generate_sql_prompt", current_chat);
        System.out.println("-----模拟最终提示词-------");
        System.out.println(finalPrompt);

        // 4、调用大模型生成语义SQL语句
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(config.getApi_key())
                .baseUrl(config.getUrl())
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(finalPrompt)
                .model(config.getModel_name())
                .build();

        try {
            ChatCompletion chatCompletion = client.chat().completions().create(params);
            System.out.println("--------调用LLM生成的语义SQL---------");
            String content = String.valueOf(chatCompletion.choices().get(0).message().content());
            System.out.println(content);

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return keyword_result;
    }

    public static List<String> formatMetricMap(Map<String, String> metricMap) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : metricMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue() : ""; // 防止 value 为 null
            String formatted = "<" + key + " COMMENT '" + value + "'>";
            result.add(formatted);
        }
        return result;
    }

    public  static void main(String[] args){
        get_keyword(query);
    }
}

