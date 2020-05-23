package com.warrior.xxl.job.executor.endpoint;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author majun
 * @description executor EndPoint
 * @date 2020/5/15
 */
@RestController
public class ExecutorBizEndPoint {

    private static ExecutorBiz executorBiz = new ExecutorBizImpl();

    /**
     * Task execution
     * @param triggerParam execution param
     * @return success | fail
     */
    @PostMapping(value = "/v1/executor/api/run", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReturnT<String> run(@RequestBody TriggerParam triggerParam) {
        return executorBiz.run(triggerParam);
    }

    /**
     * 执行器心跳操作
     *
     * @return success | fail
     */
    @PostMapping(value = "/v1/executor/api/beat")
    public ReturnT<String> beat() {
        return executorBiz.beat();
    }

    /**
     * 执行器心跳空闲操作
     *
     * @param idleBeatParam execution param
     * @return success | fail
     */
    @PostMapping(value = "/v1/executor/api/idleBeat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReturnT<String> idleBeat(@RequestBody IdleBeatParam idleBeatParam) {
        return executorBiz.idleBeat(idleBeatParam);
    }

    /**
     * 执行器kill操作
     *
     * @param killParam execution param
     * @return success | fail
     */
    @PostMapping(value = "/v1/executor/api/kill", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReturnT<String> kill(@RequestBody KillParam killParam) {
        return executorBiz.kill(killParam);
    }

    /**
     * 获取执行器执行日志
     *
     * @param logParam execution param
     * @return success | fail
     */
    @PostMapping(value = "/v1/executor/api/log", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReturnT<LogResult> log(@RequestBody LogParam logParam) {
        return executorBiz.log(logParam);
    }

}
