package org.springframework.task.scheduling.clock.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.task.scheduling.actuator.MessageQueueConfig;
import org.springframework.task.scheduling.clock.pojo.model.CurrentDateDO;
import org.springframework.task.scheduling.clock.service.ClockService;
import org.springframework.task.scheduling.constant.InterfaceListConstant;
import org.springframework.task.scheduling.pojo.dto.ServerResponse;
import org.springframework.task.scheduling.release.service.impl.ReleaseDeviceImpl;
import org.springframework.task.scheduling.utils.BaseUtils;
import org.springframework.task.scheduling.utils.HttpClientUtil;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/28
 * ClockServiceImpl
 */
@Service
public class ClockServiceImpl implements ClockService{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageQueueConfig messageQueueConfig;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private RestTemplate restTemplate;

    public void updateTime(){
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        while(true){
            try{
                singleThreadExecutor.submit(new Thread(){
                    @Override
                    public void run(){
                        ResponseEntity<ServerResponse> responseResponseEntity = restTemplate.getForEntity(
                                messageQueueConfig.getUpdateTimeUrl() + InterfaceListConstant.CLOCK_GET_TIME,
                                ServerResponse.class
                        );

                        ServerResponse serverResponse = null;
                        try{
                            serverResponse = responseResponseEntity.getBody();
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                        if(serverResponse != null){
                            CurrentDateDO currentDateDO = JSONObject.parseObject(JSONObject.toJSONString(serverResponse.getData()), CurrentDateDO.class);
                            long minPointerBeatNumber = currentDateDO.getData().getMinPointerBeatNumber();
                            CurrentTime.set(minPointerBeatNumber, ReleaseDeviceImpl.formatDelayTime(minPointerBeatNumber));
                        }
                    }
                });
                Thread.sleep(1000);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public CurrentDateDO getTime(){
        return mongoOperations.findById(CurrentDateDO.FAULT_PROTECTION, CurrentDateDO.class);
    }

    public static void main(String[] args){
        System.out.println(ReleaseDeviceImpl.formatDelayTime(782266L));
    }
}
