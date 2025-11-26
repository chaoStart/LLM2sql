package example.pojo;

import example.pojo.Constants;
import lombok.Data;

import java.io.Serializable;

@Data
public class DetailTypeDefaultConfig implements Serializable {

    // default time to filter tag selection results
    private TimeDefaultConfig timeDefaultConfig = new TimeDefaultConfig();

    private long limit = Constants.DEFAULT_DETAIL_LIMIT;
}
