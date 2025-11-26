package example.pojo;

import example.pojo.Constants;
import example.pojo.enums.DatePeriodEnum;
import example.pojo.enums.TimeMode;
import lombok.Data;

import java.io.Serializable;

@Data
public class AggregateTypeDefaultConfig implements Serializable {

    private TimeDefaultConfig timeDefaultConfig = new TimeDefaultConfig(7, DatePeriodEnum.DAY, TimeMode.RECENT);

    private long limit = Constants.DEFAULT_METRIC_LIMIT;
}
