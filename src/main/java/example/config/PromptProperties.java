package example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openai.prompt")
public class PromptProperties {
    public String template;
    public String question;
    private String dataTable;
}
