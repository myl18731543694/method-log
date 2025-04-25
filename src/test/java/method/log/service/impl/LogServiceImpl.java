package method.log.service.impl;

import method.log.service.LogService;
import org.opentest4j.TestSkippedException;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Override
    public void error1(String phone, String password) {
        throw new UnsupportedOperationException("方法未实现，暂不支持调用");
    }

    @Override
    public String success(String phone) {
        return phone;
    }

    @Override
    public void ignoreError1() {
        throw new TestSkippedException();
    }

}
