package example.utils;

import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;

import java.util.List;
import lombok.Data;
@Data
public class Keymapper {
    private final ViterbiSegment segment;          // 已经开启自定义词典的分词器
    private final List<String> metricNames;        // 从数据库加载的指标名称列表
    private final List<String> description;        // 从数据库加载的指标名称列表
    public Keymapper(ViterbiSegment segment, List<String> metricNames, List<String> description) {
        this.segment = segment;
        this.metricNames = List.copyOf(metricNames); // 不可变防御性拷贝
        this.description = List.copyOf(description); // 不可变防御性拷贝
    }

    public ViterbiSegment getSegment() { return segment; }
    public List<String> getMetricNames() { return metricNames; }
    public List<String> getDescription() { return description; }
}
