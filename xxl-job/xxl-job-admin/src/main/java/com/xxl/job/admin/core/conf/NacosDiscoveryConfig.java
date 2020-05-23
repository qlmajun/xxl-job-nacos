package com.xxl.job.admin.core.conf;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.google.common.collect.Sets;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author majun
 * @description nacos 服务监听
 * @date 2020/5/21
 */
@EnableScheduling
@Configuration
public class NacosDiscoveryConfig implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(NacosDiscoveryConfig.class);

    @Autowired
    private NamingService namingService;

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @Value("${spring.application.name}")
    private String applicationName;

    private Set<String> instanceSet;

    @Scheduled(cron = "${nacos.service.listener.cron}")
    public void serviceListener() throws NacosException {
        // 获取nacos还存活的serverName列表
        HashSet<String> serverSet = new HashSet<>(namingService.getServicesOfServer(1, 1000).getData());
        serverSet.remove(applicationName);
        // 移除掉已经添加过监听器的服务
        serverSet.removeAll(Sets.intersection(instanceSet, serverSet));
        if (!CollectionUtils.isEmpty(serverSet)) {
            addEventListener(serverSet);
            instanceSet.addAll(serverSet);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ListView<String> serverList = namingService.getServicesOfServer(1, 1000);
        HashSet<String> instanceSet = new HashSet<>(serverList.getData());
        instanceSet.remove(applicationName);
        addEventListener(instanceSet);
        this.instanceSet = instanceSet;
    }

    /****
     * 添加nacos服务监听
     * @param serverSet
     * @throws NacosException
     */
    private void addEventListener(HashSet<String> serverSet) throws NacosException {
        for (String serverName : serverSet) {
            namingService.subscribe(serverName, event -> {
                NamingEvent namingEvent = (NamingEvent) event;
                // 获取当前微服务下的实例列表
                List<Instance> instanceList = namingEvent.getInstances();
                if (CollectionUtils.isEmpty(instanceList)) {
                    // 服务下线处理
                    logger.info(">>>>>>>>>>> xxl-job nacos discovery service {} offline ", serverName);
                    handlerOfflineService(serverName);
                } else {
                    // 服务上线处理
                    logger.info(">>>>>>>>>>> xxl-job nacos discovery service {} online ", serverName);
                    handlerOnlineService(serverName);
                }
            });
        }
    }

    /****
     * 上线处理
     * @param serviceName
     */
    private void handlerOnlineService(String serviceName) {

        //获取服务名对应的执行器
        XxlJobGroup xxlJobGroup = xxlJobGroupDao.findByAppName(serviceName);

        if (xxlJobGroup == null) {
            //新增执行器
            xxlJobGroup = new XxlJobGroup();
            xxlJobGroup.setAppname(serviceName);
            xxlJobGroup.setTitle(serviceName);
            xxlJobGroup.setAddressList(serviceName);
            xxlJobGroup.setAddressType(0);
            xxlJobGroupDao.save(xxlJobGroup);
            return;
        }

        String addressList = xxlJobGroup.getAddressList();

        if (!serviceName.equals(addressList)) {
            xxlJobGroup.setAddressList(serviceName);
            xxlJobGroupDao.update(xxlJobGroup);
        }
    }

    /***
     * 下线处理
     * @param serviceName
     */
    private void handlerOfflineService(String serviceName) {
        // 下线处理
        XxlJobGroup xxlJobGroup = xxlJobGroupDao.findByAppName(serviceName);
        if (xxlJobGroup != null) {
            xxlJobGroup.setAddressList(null);
            xxlJobGroupDao.update(xxlJobGroup);
        }
    }
}
