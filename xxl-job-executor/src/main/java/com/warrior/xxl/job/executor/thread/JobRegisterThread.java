package com.warrior.xxl.job.executor.thread;

import com.alibaba.fastjson.JSON;
import com.warrior.xxl.job.executor.config.ApplicationContextHolder;
import com.warrior.xxl.job.executor.config.JobExecutorConfig;
import com.warrior.xxl.job.executor.util.RestTemplateClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author majun
 * @description 任务注册线程
 * @date 2020/5/22
 */
public class JobRegisterThread {
    private static Logger logger = LoggerFactory.getLogger(JobRegisterThread.class);

    private static JobRegisterThread instance = new JobRegisterThread();

    public static JobRegisterThread getInstance() {
        return instance;
    }

    private volatile boolean toStop = false;

    public void start(Set<String> jobHandlerValues) {

        if (CollectionUtils.isEmpty(jobHandlerValues)) {
            logger.warn(">>>>>>>>>>> xxl-job, job registry fail, executor handler is null.");
            return;
        }

        Thread jobHandlerRegisterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    //获取执行器appName
                    String appName = ApplicationContextHolder.getBean(JobExecutorConfig.class).getAppName();
                    String data = JSON.toJSONString(jobHandlerValues);
                    String adminService = "xxl-job-admin";
                    ReturnT<String> returnT = RestTemplateClient.jobRegister(appName, data);

                    //调用注册接口成功
                    if (returnT != null && returnT.getCode() == ReturnT.SUCCESS_CODE) {
                        toStop = true;
                        break;
                    }

                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
                        }
                    }
                }
            }
        });

        jobHandlerRegisterThread.setDaemon(true);
        jobHandlerRegisterThread.setName("xxl-job, executor jobHandlerRegisterThread");
        jobHandlerRegisterThread.start();
    }
}
