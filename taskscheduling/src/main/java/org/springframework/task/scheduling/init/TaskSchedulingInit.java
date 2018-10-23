package org.springframework.task.scheduling.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import org.springframework.task.scheduling.actuator.MessageQueueConfig;
import org.springframework.task.scheduling.actuator.SpringTaskContext;
import org.springframework.task.scheduling.clock.Clock;
import org.springframework.task.scheduling.clock.service.ClockService;
import org.springframework.task.scheduling.release.service.ReleaseDevice;
import org.springframework.task.scheduling.storage.service.StorageService;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/4/4
 * Init
 */
@Component
public class TaskSchedulingInit implements ApplicationRunner{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private MessageQueueConfig messageQueueConfig;

    @Autowired
    private ClockService clockService;

    @Autowired
    private ReleaseDevice releaseDevice;

    @Override
    public void run(ApplicationArguments args) throws Exception{
        if(messageQueueConfig.getClock()){
            Clock clock = new Clock();
            clock.startDefaultClock(mongoOperations, releaseDevice);
            logger.info("计时器启动成功");
        }

        if(!"false".equals(messageQueueConfig.getUpdateTimeUrl())){
            clockService.updateTime();
            logger.info("计时器更新时间功能启动成功");
        }

        if(messageQueueConfig.getStorageServer()){
            StorageService storageService = SpringTaskContext.getBean(StorageService.class);
            logger.info("开始校验索引完整性");
            storageService.checkTaskIndex();
            logger.info("索引校验完成");
        }
    }
}
