package example.pojo.request;

import example.pojo.SemanticParseInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParseReq {
    private String queryText;
    private Integer chatId;
    private Integer agentId;
    private Long dataSetId;
    private User user;
    private QueryFilters queryFilters;
    private boolean saveAnswer = true;
    //    private boolean disableLLM = false; // 原来的代码，保留
    private boolean disableLLM = true;
    private Long queryId;
    private SemanticParseInfo selectedParse;
}
