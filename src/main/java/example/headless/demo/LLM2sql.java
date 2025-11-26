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

import java.util.List;


public class LLM2sql {

//    @Autowired
//    public static Config config;
    static Config config;

    static {
        config = new Config();
        config.setApi_key(" ");
        config.setUrl("http://langchain4j.dev/demo/openai/v1/");
        config.setModel_name("gpt-4o-mini");
    }
    static MatchKeyword matchKeyword;
    static String query = "近15天大数据分析部访问次数汇总";

    public static List<Term> get_keyword(String text){
        // 匹配Mysql数据库中的关键词
        List<Term> keyword_result = matchKeyword.matchword(text);
        System.out.println("-----关键词-------");
        System.out.println(keyword_result);

        String metrics_name = keyword_result.get(0).word + "COMMENT" + "description";
        // 构造当前请求的 Example（不含 sql）
//        Example current = new Example();
//        current.setQuestion("近15天超音数访问次数汇总");
//        current.setSideInfo("CurrentDate=[2025-11-25],[Database does not support with statement]");
//        String table_name = "超音数数据集";
//        String metrics =  "<访问次数 COMMENT '一段时间内用户的访问次数'>";
//        String dimensions = "<数据日期 FORMAT 'yyyy-MM-dd' COMMENT '数据日期'>, <用户名 COMMENT '用户唯一标识'>";
//        String values = "";
//        DatabaseSchemaInfo dbSchema = new DatabaseSchemaInfo(table_name, metrics, dimensions, values);
//        current.setDbSchema(dbSchema);

        Example current_chat = Example.of(
                "近15天超音数访问次数汇总",
                "超音数数据集",
                "<访问次数 COMMENT '一段时间内用户的访问次数'>",
                "<数据日期 FORMAT 'yyyy-MM-dd' COMMENT '数据日期'>",
                "",  // values 暂时为空,为值字段; 示例：Values[<用户='jackjchen'>,<用户='robinlee'>]
                ""
        );

        String finalPrompt = PromptLoader.buildFewShotPrompt("Generate_sql_prompt", current_chat);

        System.out.println("-----模拟最终提示词-------");
        System.out.println(finalPrompt);
        System.out.println("-----调用大模型生成SQL语句-------");
/*        // 或者从配置文件/环境变量读取
        Config config = new Config();
        config.setApi_key(" ");
        config.setUrl("http://langchain4j.dev/demo/openai/v1/");
        config.setModel_name("gpt-4o-mini");*/
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
            System.out.println(chatCompletion);
            System.out.println("--------下面是返回的语义SQL---------");
            String content = String.valueOf(chatCompletion.choices().get(0).message().content());
            System.out.println(content);

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return keyword_result;
    }

    public  static void main(String[] args){
        get_keyword(query);
    }
}

