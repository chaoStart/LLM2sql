package example.pojo;

import example.pojo.enums.DatePeriodEnum;
import example.pojo.enums.TimeMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeDefaultConfig implements Serializable {

    /** default time span unit */
    private Integer unit = 1;

    private DatePeriodEnum period = DatePeriodEnum.DAY;

    private TimeMode timeMode = TimeMode.LAST;
}