package example.utils;

/*
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class PromptLoader {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode prompts;

    static {
        try {
            // 从 resources 目录加载
            prompts = mapper.readTree(new ClassPathResource("s2_prompt.json").getInputStream()
            );
        } catch (IOException e) {
            throw new RuntimeException("加载 different_prompt.json 失败", e);
        }
    }

    public static String get(String key) {
        JsonNode node = prompts.get(key);
        if (node == null) {
            throw new IllegalArgumentException("Prompt key 不存在: " + key);
        }
        return node.asText();
    }

    // 带参数替换的便捷方法（推荐）
    public static String format(String key, Object... args) {
        return String.format(get(key), args);
    }
}
*/
// PromptLoader.java（增强版，同时支持模板和示例）
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class PromptLoader {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode promptTemplates;
    // 获取所有 few-shot 示例
    @Getter
    private static List<Example> examples;

    static {
        try {
            // 加载提示词模板
            promptTemplates = mapper.readTree(new ClassPathResource("s2_prompt.json").getInputStream());

            // 加载 few-shot 示例
            examples = mapper.readValue(
                    new ClassPathResource("s2_example.json").getInputStream(),
                    new TypeReference<List<Example>>() {});
        } catch (IOException e) {
            throw new RuntimeException("加载 prompts/example.json 或 different_prompt.json 失败", e);
        }
    }

    // 获取原始模板
    public static String getTemplate(String key) {
        JsonNode node = promptTemplates.get(key);
        if (node == null) throw new IllegalArgumentException("Prompt key 不存在: " + key);
        return node.asText();
    }

    // 推荐：直接构建完整的 few-shot prompt
    public static String buildFewShotPrompt(String templateKey, Example newExample) {
        StringBuilder sb = new StringBuilder();

        // 1. 添加系统提示（你的 generate_sql_prompt）
        sb.append(getTemplate(templateKey)).append("\n\n");

        // 2. 添加 few-shot 示例（你可以控制取前几个）
        int fewShotCount = Math.min(8, examples.size()); // 比如最多用8个示例
        for (int i = 0; i < fewShotCount; i++) {
            Example ex = examples.get(i);
            sb.append("用户问题：").append(ex.getQuestion()).append("\n");
            sb.append("附加信息：").append(ex.getSideInfo()).append("\n");
            sb.append("数据库结构：").append(ex.getDbSchema()).append("\n");
            sb.append("SQL答案：").append(ex.getSql()).append("\n\n");
        }

        // 3. 添加当前真实问题
        sb.append("用户问题：").append(newExample.getQuestion()).append("\n");
        sb.append("附加信息：").append(newExample.getSideInfo()).append("\n");
        sb.append("数据库结构：").append(newExample.getDbSchema()).append("\n");
        sb.append("SQL答案：");

        return sb.toString();
    }
}