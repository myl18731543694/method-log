package method.log.controller.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LogTestReq {

    private String phone;

    private String password;

}
