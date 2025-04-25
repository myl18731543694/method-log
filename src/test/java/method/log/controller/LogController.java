package method.log.controller;

import method.log.controller.req.LogTestReq;
import method.log.service.LogService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/log")
public class LogController {

    @Resource
    private LogService logService;

    @PostMapping("/error1")
    public Map<String, String> error1(@RequestBody LogTestReq logTestReq) {
        logService.error1(logTestReq.getPhone(), logTestReq.getPassword());
        return Map.of("1", "ok");
    }

}
