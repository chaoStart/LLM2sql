/*
package example.headless.chat.parser.rule;

import com.google.common.collect.Lists;
import example.pojo.SchemaElementMatch;
import example.pojo.SchemaMapInfo;
import example.headless.chat.ChatQueryContext;
import example.headless.parser.SemanticParser;
import example.headless.query.SemanticQuery;
import example.headless.query.rule.RuleSemanticQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

*/
/**
 * RuleSqlParser resolves a specific SemanticQuery according to co-appearance of certain schema
 * element types.
 *//*

@Slf4j
public class RuleSqlParser implements SemanticParser {

    private static final List<SemanticParser> auxiliaryParsers = Arrays.asList(new TimeRangeParser(), new AggregateTypeParser());

    @Override
    public void parse(ChatQueryContext chatQueryContext) {
        if (!chatQueryContext.getCandidateQueries().isEmpty()) {
            return;
        }
        SchemaMapInfo mapInfo = chatQueryContext.getMapInfo();
        List<SemanticQuery> candidateQueries = Lists.newArrayList();
        // iterate all schemaElementMatches to resolve query mode
        for (Long dataSetId : mapInfo.getMatchedDataSetInfos()) {
            List<SchemaElementMatch> elementMatches = mapInfo.getMatchedElements(dataSetId);
            List<RuleSemanticQuery> queries = RuleSemanticQuery.resolve(dataSetId, elementMatches, chatQueryContext);
            candidateQueries.addAll(queries);
        }
        chatQueryContext.setCandidateQueries(candidateQueries);
        // 辅助获取具体时间范围(TimeRangeParser用于匹配时间)
        //(AggregateTypeParser根据关键字匹配提取用户查询中指定的聚合类型 max,min,average,sum,top...)
        auxiliaryParsers.forEach(p -> p.parse(chatQueryContext));
        // 根据数据集字段 ---> 生成语义Sql
        candidateQueries.forEach(query ->
                query.buildS2Sql(chatQueryContext.getDataSetSchema(query.getParseInfo().getDataSetId())));
    }
}
*/
