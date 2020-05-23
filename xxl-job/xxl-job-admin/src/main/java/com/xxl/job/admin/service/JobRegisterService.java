package com.xxl.job.admin.service;

import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;

/***
 * 任务注册服务接口声明
 */
public interface JobRegisterService {

    /***
     * 执行器任务注册
     * @param appName  执行器appName
     * @param jobHandlerValues 注册任务名集合
     * @return
     */
    ReturnT<String> jobRegister(String appName, List<String> jobHandlerValues);
}
