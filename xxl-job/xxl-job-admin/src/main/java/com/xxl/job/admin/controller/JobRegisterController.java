package com.xxl.job.admin.controller;

import com.alibaba.fastjson.JSON;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.service.JobRegisterService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author majun
 * @description 任务注册controller
 * @date 2020/5/22
 */
@Controller
public class JobRegisterController {

    @Autowired
    private JobRegisterService jobRegisterService;

    /**
     * 执行器任务注册
     *
     * @param appName 执行器appName
     * @param data    注册任务数据
     * @return
     */
    @PostMapping("/job/register")
    @ResponseBody
    @PermissionLimit(limit=false)
    public ReturnT<String> jobRegister(@RequestParam("appName") String appName, @RequestParam String data) {
        List<String> jobHandlerValues = JSON.parseArray(data, String.class);
        ReturnT<String> returnT = jobRegisterService.jobRegister(appName, jobHandlerValues);
        return returnT;
    }
}
