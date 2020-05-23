package com.warrior.xxl.job.executor.util;

import com.warrior.xxl.job.executor.config.ApplicationContextHolder;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author majun
 * @description 执行器回调调度中心接口
 * @date 2020/5/23
 */
public class RestTemplateClient {

    private static final String HTTP = "http://";

    /***
     * 调度中心nacos注册服务名
     */
    private static final String ADMIN_SERVICE = "xxl-job-admin";

    /***
     * 执行器回调接口
     */
    private final static String CALLBACK_API = "/api/callback";

    /***
     * 任务注册接口
     */
    private final static String JOB_REGISTER_API = "/job/register";

    /***
     * 执行器回调admin操作
     * @param callbackParamList 回调参数集合
     * @return
     */
    public static ReturnT<String> executorCallBack(List<HandleCallbackParam> callbackParamList) {
        String url = HTTP + ADMIN_SERVICE + CALLBACK_API;
        HttpHeaders headers = new HttpHeaders(); // http请求头
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8); // 请求头设置属性
        HttpEntity<List<HandleCallbackParam>> requestEntity = new HttpEntity<>(callbackParamList, headers);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
    }

    /***
     * 执行器任务注册
     * @param appName 执行器appName
     * @param data 注册的任务数据
     * @return
     */
    public static ReturnT<String> jobRegister(String appName, String data) {
        String url = HTTP + ADMIN_SERVICE + JOB_REGISTER_API;
        MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("appName", appName);
        requestEntity.add("data", data);
        RestTemplate restTemplate = getRestTemplateNacosJob();
        ReturnT<String> returnT = restTemplate.postForObject(url, requestEntity, ReturnT.class);
        return returnT;
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
