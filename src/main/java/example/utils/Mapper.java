package example.utils;

import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.Term;
import lombok.Data;

import java.util.List;
@Data
public class Mapper {
    private final List<Term> matchedTerms;   // 匹配的指标名称
    private final List<String> metricNames;
    public Mapper(List<Term> matchedTerms, List<String> metricNames) {
        this.matchedTerms = matchedTerms;
        this.metricNames = List.copyOf(metricNames); // 不可变防御性拷贝
    }

    public List<Term> getSegment() { return matchedTerms; }
    public List<String> getMetricNames() { return metricNames; }
}


