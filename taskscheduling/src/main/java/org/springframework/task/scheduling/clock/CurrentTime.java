package org.springframework.task.scheduling.clock;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.task.scheduling.clock.pojo.dto.ClockUpdateTimeDTO;
import org.springframework.task.scheduling.constant.Constant;
import org.springframework.task.scheduling.constant.InterfaceListConstant;
import org.springframework.task.scheduling.constant.TimeTypeEnum;
import org.springframework.task.scheduling.release.service.ReleaseDevice;
import org.springframework.task.scheduling.release.service.impl.ReleaseDeviceImpl;
import org.springframework.task.scheduling.storage.service.impl.StorageServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/13
 * CurrentTime 表的报时类
 */
public class CurrentTime{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Pointer[] pointers;

    private FaultProtection faultProtection;

    private ReleaseDevice releaseDevice;

    private MongoOperations mongoOperations;

    /***
     * 异步有序线程,保证输出的时间顺序不出错。使用异步的方式主要是因为保证时间精确性,因为这里的操作比较多
     */
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    /***
     * showTime开关[true:开][false:关]
     */
    public boolean showTimeSwitch = true;

    public void setShowTimeSwitch(boolean showTimeSwitch){
        this.showTimeSwitch = showTimeSwitch;
    }

    public boolean isShowTimeSwitch(){
        return this.showTimeSwitch;
    }

    public CurrentTime(Pointer[] pointers, MongoOperations mongoOperations, ReleaseDevice releaseDevice){
        this.pointers = pointers;
        if(mongoOperations != null){
            this.faultProtection = new FaultProtection(mongoOperations);
        }
        this.releaseDevice = releaseDevice;
        this.mongoOperations = mongoOperations;
    }

    public CurrentTime(Pointer[] pointers){
        this.pointers = pointers;
    }

    public FaultProtection getFaultProtection(){
        return this.faultProtection;
    }

    /***
     * 输出当前时间字符串
     */
    public void showTime(){
        String log = "当前时间-->";
        Map<String, Integer> address = new HashMap<>();
        String formatTimeStr = "";
        for(int i = 0; i < pointers.length; i++){
            Pointer pointer = pointers[i];
            String temp;
            if(i == (pointers.length - 1)){
                temp = "%s%s";
                formatTimeStr = formatTimeStr + pointer.getPointerAddress();
            }else{
                temp = "%s%s:";
                formatTimeStr = formatTimeStr + pointer.getPointerAddress() + ".";
            }
            log = log + String.format(temp, pointer.getPointerAddress(), pointer.getPointerType().getName());
            address.put(pointer.getPointerType().name(), pointer.getPointerAddress());
        }
        Long day = address.get(TimeTypeEnum.DAYS.name()).longValue();
        day = day * 24 * 60 * 60;

        Long hours = address.get(TimeTypeEnum.HOURS.name()).longValue();
        hours = hours * 60 * 60;

        Long minutes = address.get(TimeTypeEnum.MINUTES.name()).longValue();
        minutes = minutes * 60;

        Long seconds = address.get(TimeTypeEnum.SECONDS.name()).longValue();

        Long minPointerNumber = day + hours + minutes + seconds;
        if(this.showTimeSwitch){
            this.singleThreadExecutor.submit(new SingThread(log, formatTimeStr, minPointerNumber, address));
        }
    }

    class SingThread implements Runnable{

        private FaultProtection faultProtection;

        private String log;
        private String formatTimeStr;
        private Long minPointerNumber;
        private Map<String, Integer> address;

        public SingThread(String log, String formatTimeStr, Long minPointerNumber, Map<String,Integer> address){
            this.faultProtection = new FaultProtection(mongoOperations);
            this.log = log;
            this.formatTimeStr = formatTimeStr;
            this.minPointerNumber = minPointerNumber;
            this.address = address;
        }

        @Override
        public void run(){
            try{
                if(this.faultProtection != null){
                    logger.info(log);
                    this.faultProtection.updateTime(log, address, minPointerNumber);

                    ClockUpdateTimeDTO clockUpdateTimeDTO = new ClockUpdateTimeDTO();
                    clockUpdateTimeDTO.setTime(minPointerNumber);
                    clockUpdateTimeDTO.setFormatDate(formatTimeStr);
                    clockUpdateTimeDTO.setMaxPointerAddress(StorageServiceImpl.getMaxPointerAddress(formatTimeStr));
                    clockUpdateTimeDTO.setIndexAddress(StorageServiceImpl.getAddress(formatTimeStr));
                    //向任务存储服务推送当前计时器时间,
                    releaseDevice.sendTimedTask(
                            Constant.TASK_STORAGE_SERVER_NAME,
                            InterfaceListConstant.STORAGE_PUSH_EXPIRED_TASK,
                            clockUpdateTimeDTO,
                            null
                    );
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}