package example;

import example.config.Config;
import example.config.PromptProperties;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {


    @Autowired
    private PromptProperties promptProps;

    @Autowired
    private Config config;
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    // 动态拼接 Prompt
     String prompt = promptProps.getTemplate() +
            "#Question: " + promptProps.getQuestion() + "\n" +
            "#Data:\n" + promptProps.getDataTable()+ "\n" +
            "#Answer:";

        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(config.getApi_key())
                .baseUrl(config.getUrl())
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(config.getModel_name())
                .build();

        try {
            ChatCompletion chatCompletion = client.chat().completions().create(params);
            System.out.println(chatCompletion);
            System.out.println("-----------------");
            String content = String.valueOf(chatCompletion.choices().get(0).message().content());
            System.out.println(content);

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
