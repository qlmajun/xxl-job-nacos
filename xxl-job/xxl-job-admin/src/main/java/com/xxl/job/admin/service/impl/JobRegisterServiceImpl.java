package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.JobRegisterService;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author majun
 * @description 任务注册服务接口实现
 * @date 2020/5/22
 */
@Service
public class JobRegisterServiceImpl implements JobRegisterService {

    private static Logger logger = LoggerFactory.getLogger(JobRegisterServiceImpl.class);

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @Resource
    private XxlJobInfoDao xxlJobInfoDao;

    @Override
    public ReturnT<String> jobRegister(String appName, List<String> jobHandlerValues) {

        //根据执行器appName获取执行器信息
        XxlJobGroup xxlJobGroup = xxlJobGroupDao.findByAppName(appName);

        if (xxlJobGroup == null) {
            logger.debug(">>>>>>>>>>> xxl-job, job registry fail group：{} non-existent ", appName);
            return ReturnT.FAIL;
        }

        int jobGroup = xxlJobGroup.getId();

        //根据执行器Id获取对应的任务
        List<XxlJobInfo> xxlJobInfoList = xxlJobInfoDao.getJobsByGroup(jobGroup);

        if (CollectionUtils.isEmpty(xxlJobInfoList)) {
            //添加注册任务
            saveJob(jobGroup, appName, jobHandlerValues);
            return ReturnT.SUCCESS;
        }

        //获取已经存在的执行器handler
        List<String> executorHandlerList = getExecutorHandler(xxlJobInfoList);

        //添加去除已存在新增的注解任务
        Set<String> newAddSet = new HashSet<>();
        newAddSet.addAll(jobHandlerValues);
        newAddSet.removeAll(executorHandlerList);
        saveJob(jobGroup, appName, newAddSet);

        //删除之前存在,但执行器重新更新后去除的注解任务
        Set<String> oldDeleteSet = new HashSet<>();
        oldDeleteSet.addAll(executorHandlerList);
        oldDeleteSet.removeAll(jobHandlerValues);
        deleteJob(jobGroup, oldDeleteSet);

        return ReturnT.SUCCESS;
    }

    /***
     * 保存任务到 DB中
     * @param jobGroup 执行器主键Id
     * @param appName 执行器appName
     * @param jobHandlerValues 任务注解名称
     */
    private void saveJob(int jobGroup, String appName, Collection<String> jobHandlerValues) {
        XxlJobInfo xxlJobInfo = null;
        for (String jobHandlerName : jobHandlerValues) {
            xxlJobInfo = new XxlJobInfo();
            xxlJobInfo.setJobGroup(jobGroup);
            //设置默认CRON表达式为空字符串
            xxlJobInfo.setJobCron("");
            xxlJobInfo.setJobDesc(jobHandlerName);
            xxlJobInfo.setAuthor(appName);
            //设置路由策略：轮询
            xxlJobInfo.setExecutorRouteStrategy("ROUND");
            xxlJobInfo.setExecutorHandler(jobHandlerName);
            //设置阻塞处理策略：单机串行（默认）
            xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
            //设置GLUE类型
            xxlJobInfo.setGlueType("BEAN");
            //设置调度状态为停止状态
            xxlJobInfo.setTriggerStatus(0);
            Date currentDate = new Date();
            xxlJobInfo.setAddTime(currentDate);
            xxlJobInfo.setUpdateTime(currentDate);
            //设置GLUE更新时间
            xxlJobInfo.setGlueUpdatetime(currentDate);

            //保存到DB
            xxlJobInfoDao.save(xxlJobInfo);
        }
    }

    /***
     * 删除任务
     * @param jobGroup 执行器主键Id
     * @param deleteJobHandlers 需要删除的任务类别
     */
    private void deleteJob(int jobGroup, Set<String> deleteJobHandlers) {

        if (CollectionUtils.isEmpty(deleteJobHandlers)) {
            return;
        }
        xxlJobInfoDao.deleteByExecutorHandlers(jobGroup, deleteJobHandlers);
    }

    /***
     * 获取执行器任务handler
     * @param xxlJobInfoList 任务列表
     * @return
     */
    private List<String> getExecutorHandler(List<XxlJobInfo> xxlJobInfoList) {
        List<String> executorHandlerList = new ArrayList<>(xxlJobInfoList.size());
        for (XxlJobInfo xxlJobInfo : xxlJobInfoList) {
            executorHandlerList.add(xxlJobInfo.getExecutorHandler());
        }
        return executorHandlerList;
    }
}
