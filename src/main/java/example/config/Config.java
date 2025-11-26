package example.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openai.config")
public class Config {
    public String api_key;
    public String model_name;
    private String temperature;
    private String url;
}
