package com.warrior.xxl.job.util;

import com.warrior.xxl.job.config.ApplicationContextHolder;
import com.xxl.job.core.biz.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author majun
 * @description 调度中心调度执行器相关接口
 * @date 2020/5/15
 */
public class RestTemplateClient<T> {

    private static final String HTTP = "http://";

    /***
     * 执行器执行接口
     */
    private final static String EXECUTOR_API_RUN = "/v1/executor/api/run";

    /**
     * 执行器心跳接口
     */
    private final static String EXECUTOR_API_BEAT = "/v1/executor/api/beat";

    /****
     * 执行器空闲心跳接口
     */
    private final static String EXECUTOR_API_IDLE_BEAT = "/v1/executor/api/idleBeat";

    /***
     * 执行器kill操作接口
     */
    private final static String EXECUTOR_API_KILL = "/v1/executor/api/kill";

    /**
     * 执行器日志查看接口
     */
    private final static String EXECUTOR_API_LOG = "/v1/executor/api/log";


    /***
     * 执行器kill操作
     * @param executorService nacos上注册的执行器服务Id
     * @param killParam kill参数
     * @return
     */
    public static ReturnT<String> executorKill(String executorService, KillParam killParam) {
        String url = HTTP + executorService + EXECUTOR_API_KILL;
        HttpHeaders headers = new HttpHeaders(); // http请求头
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // 请求头设置属性
        HttpEntity<KillParam> requestEntity = new HttpEntity<>(killParam, headers);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
    }

    /****
     * 执行器心跳空闲操作
     * @param executorService nacos上注册的执行器服务Id
     * @param idleBeatParam 空闲心跳参数
     * @return
     */
    public static ReturnT<String> executorIdleBeat(String executorService, IdleBeatParam idleBeatParam) {
        String url = HTTP + executorService + EXECUTOR_API_IDLE_BEAT;
        HttpHeaders headers = new HttpHeaders(); // http请求头
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // 请求头设置属性
        HttpEntity<IdleBeatParam> requestEntity = new HttpEntity<>(idleBeatParam, headers);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
    }

    /***
     * 执行器心跳操作
     * @param executorService nacos上注册的执行器服务Id
     * @return
     */
    public static ReturnT<String> executorBeat(String executorService) {
        String url = HTTP + executorService + EXECUTOR_API_BEAT;
        HttpHeaders headers = new HttpHeaders(); // http请求头
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // 请求头设置属性
        HttpEntity requestEntity = new HttpEntity(headers);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
    }

    /***
     * 执行器执行操作
     * @param executorService nacos上注册的执行器服务Id
     * @param triggerParam 触发参数
     * @return
     */
    public static <T> ReturnT<String> runExecutor(String executorService, TriggerParam triggerParam) {
        String url = HTTP + executorService + EXECUTOR_API_RUN;
        HttpHeaders headers = new HttpHeaders(); // http请求头
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // 请求头设置属性
        HttpEntity<TriggerParam> requestEntity = new HttpEntity<>(triggerParam, headers);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
    }

    /***
     * 获取执行器执行日志
     * @param executorService nacos上注册的执行器服务Id
     * @param logParam 日志参数
     * @return
     */
    public static <T> ReturnT<LogResult> logDetailCat(String executorService, LogParam logParam) {
        String url = HTTP + executorService + EXECUTOR_API_LOG;
        HttpHeaders headers = new HttpHeaders(); // http请求头
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // 请求头设置属性
        HttpEntity<LogParam> requestEntity = new HttpEntity<>(logParam, headers);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ParameterizedTypeReference<ReturnT<LogResult>> responseBodyType = new ParameterizedTypeReference<ReturnT<LogResult>>() {
        };
        ResponseEntity<ReturnT<LogResult>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseBodyType);
        return responseEntity.getBody();
    }

    /**
     * 必须使用
     * sel com.bosssoft.nontax3.saas.xxl.job.config.JobScheduleConfig
     *
     * @return
     */
    private static RestTemplate getRestTemplateNacosJob() {
        RestTemplate restTemplate = (RestTemplate) ApplicationContextHolder.getBean("restTemplateNacosJob");
        return restTemplate;
    }


}
