package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.util.RestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * job registry instance
 * @author xuxueli 2016-10-02 19:10:24
 */
public class JobRegistryMonitorHelper {
	private static Logger logger = LoggerFactory.getLogger(JobRegistryMonitorHelper.class);

	private static JobRegistryMonitorHelper instance = new JobRegistryMonitorHelper();
	public static JobRegistryMonitorHelper getInstance(){
		return instance;
	}

    private static ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(), 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(200), (Runnable r) -> new Thread(r, "notice_thread_pool")
    );

    private Thread registryThread;
	private volatile boolean toStop = false;
	public void start(){
		registryThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!toStop) {
					try {
						// auto registry group
						List<XxlJobGroup> groupList = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().findByAddressType(0);

                        groupList.forEach(xxlJobGroup -> {
                            // 一个是admin一个是executor执行器类型
                            XxlJobAdminConfig.getAdminConfig().xxlJobService().registryByDiscovery(xxlJobGroup, RegistryConfig.RegistType.EXECUTOR.name());
                        });

						if (groupList!=null && !groupList.isEmpty()) {
							// remove dead address (admin/executor)
							List<Integer> ids = XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().findDead(RegistryConfig.DEAD_TIMEOUT, new Date());
							if (ids!=null && ids.size()>0) {
								XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().removeDead(ids);
							}

							// fresh online address (admin/executor)
//							HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
//							List<XxlJobRegistry> list = XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
//							if (list != null) {
//								for (XxlJobRegistry item: list) {
//									if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
//										String appname = item.getRegistryKey();
//										List<String> registryList = appAddressMap.get(appname);
//										if (registryList == null) {
//											registryList = new ArrayList<String>();
//										}
//
//										if (!registryList.contains(item.getRegistryValue())) {
//											registryList.add(item.getRegistryValue());
//										}
//										appAddressMap.put(appname, registryList);
//									}
//								}
//							}

							// fresh group address
//							for (XxlJobGroup group: groupList) {
//								List<String> registryList = appAddressMap.get(group.getAppname());
//								String addressListStr = null;
//								if (registryList!=null && !registryList.isEmpty()) {
//									Collections.sort(registryList);
//									addressListStr = "";
//									for (String item:registryList) {
//										addressListStr += item + ",";
//									}
//									addressListStr = addressListStr.substring(0, addressListStr.length()-1);
//								}
//								group.setAddressList(addressListStr);
//								XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().update(group);
//							}

                            List<XxlJobGroup> noticeList = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().findByAddressType(0);
                            noticeList.forEach(xxlJobGroup -> {
                                //TODO 重构 By majun
                                String appName = xxlJobGroup.getAppname();
                                executorService.submit(() -> {
                                    try {
                                        RestTemplateClient.getPost("http://" + appName + RestTemplateClient.adminList + "?adminName=" + XxlJobAdminConfig.getAdminConfig().getName(), null);
                                    } catch (Exception e) {
                                        logger.error("notice app error : {}, url address : {}", e.toString(), appName);
                                    }
                                });
                            });
						}
					} catch (Exception e) {
						if (!toStop) {
							logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
						}
					}
					try {
						TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
					} catch (InterruptedException e) {
						if (!toStop) {
							logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
						}
					}
				}
				logger.info(">>>>>>>>>>> xxl-job, job registry monitor thread stop");
			}
		});
		registryThread.setDaemon(true);
		registryThread.setName("xxl-job, admin JobRegistryMonitorHelper");
		registryThread.start();
	}

	public void toStop(){
		toStop = true;
		// interrupt and wait
		registryThread.interrupt();
		try {
			registryThread.join();
		} catch (InterruptedException e) {
			logger.error("=====>>>>>"+e.getMessage(), e);
		}
	}

}
