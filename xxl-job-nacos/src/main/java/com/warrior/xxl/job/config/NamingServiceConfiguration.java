package com.warrior.xxl.job.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

public class NamingServiceConfiguration {

    private static Logger logger = LoggerFactory.getLogger(NamingServiceConfiguration.class);

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Bean
    public NamingService namingService() {
        Properties properties = new Properties();
        properties.put("serverAddr", nacosDiscoveryProperties.getServerAddr());
        properties.put("namespace", nacosDiscoveryProperties.getNamespace());
        try {
            return NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            logger.error("get NamingService error，detail：{}", e.getErrMsg());
        }
        return null;
    }
}
