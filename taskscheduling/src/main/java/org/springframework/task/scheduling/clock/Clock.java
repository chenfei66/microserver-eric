package org.springframework.task.scheduling.clock;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.task.scheduling.clock.pojo.model.CurrentDateDO;
import org.springframework.task.scheduling.constant.TimeTypeEnum;
import org.springframework.task.scheduling.release.service.ReleaseDevice;
import org.springframework.task.scheduling.utils.BaseUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/13
 * Clock
 */
public class Clock{
    /***
     * 最小指针刻度单位
     */
    private Pointer pointer;

    /***
     * 配置计时器
     * @param pointers 指针数组
     * @param mongoOperations mongo操作对象
     * @return
     */
    private Clock setClock(Pointer[] pointers, MongoOperations mongoOperations, ReleaseDevice releaseDevice){
        CurrentTime currentTime = new CurrentTime(pointers, mongoOperations, releaseDevice);
        int trueNumber = 0;
        Pointer observerPointer = null;
        for(Pointer pointer : pointers){
            pointer.setCurrentTime(currentTime);
            if(pointer.isMinimumScaleBool()){
                this.pointer = pointer;
                trueNumber = trueNumber + 1;
            }
            if(trueNumber > 1){
                try{
                    throw new Exception("pointers中Pointer的minimumScaleBool属性为true的数量不能超过1一个");
                }catch(Exception e){
                    e.printStackTrace();
                    break;
                }
            }
            if(!pointer.getPointerType().isMaximumScaleBool()){//判断是否为最大刻度,如果是的话则不进行指针跳动事件委托
                TimeTypeEnum timeTypeEnum = pointer.getPointerType();
                Clock.addBeatEvent(timeTypeEnum, observerPointer);//添加指针跳动事物委托
            }
            observerPointer = pointer;
        }
        CurrentDateDO currentDateDO = mongoOperations.findById(CurrentDateDO.FAULT_PROTECTION, CurrentDateDO.class);
        Date startDate = currentDateDO.getData().getCurrentTime();
        this.init(startDate, pointers, currentDateDO.getData());
        return this;
    }

    private void start(){
        pointer.run();
    }

    /***
     * 使用默认钟表配置, 后期会增加配置功能
     */
    private Pointer[] useDefaultConfiguration(){
        TimeTypeEnum[] timeTypeEnums = TimeTypeEnum.values();
        List<Pointer> pointerList = new ArrayList<>();
        for(int i = 0; i < timeTypeEnums.length; i++){
            TimeTypeEnum timeTypeEnum = timeTypeEnums[i];
            Pointer pointer = new Pointer(timeTypeEnum);
            pointerList.add(pointer);
        }
        Pointer[] pointers = pointerList.toArray(new Pointer[pointerList.size()]);
        return pointers;
    }

    /***
     * 添加指针跳动事物委托
     * @param timeTypeEnum timeTypeEnum
     * @param pointer pointer
     */
    private static void addBeatEvent(TimeTypeEnum timeTypeEnum, Pointer pointer){
        Clock.addEvent(timeTypeEnum, new EventMap(pointer, "beat"));
    }

    /***
     * 添加事物委托
     * @param timeTypeEnum timeTypeEnum
     * @param eventMaps eventMaps
     */
    private static void addEvent(TimeTypeEnum timeTypeEnum, EventMap... eventMaps){
        for(EventMap eventMap : eventMaps){
            timeTypeEnum.addEvent(eventMap.getObject(), eventMap.getMethodName());
        }
    }

    /***
     * 启动默认配置的计时器
     * @param mongoOperations mongoOperations
     */
    public void startDefaultClock(MongoOperations mongoOperations, ReleaseDevice releaseDevice){
        new Clock().setClock(this.useDefaultConfiguration(), mongoOperations, releaseDevice).start();
    }

    /***
     * 启动时,将上次关闭时间和这次启动的时间进行累加(按最小单位算)
     * @param startTime startTime 开始时间
     * @param pointers pointers 指针数组
     * @param data data
     */
    public void init(Date startTime, Pointer[] pointers, CurrentDateDO.Data data){
        //将各个指针指向上次关闭时的位置
        for(Pointer pointer : pointers){
            Map<String, Integer> priorToAddressMap = data.getAddress();
            Integer address = priorToAddressMap.get(pointer.getPointerType().name());
            pointer.toAddress(address);
            if(pointer.isMinimumScaleBool()){//判断是否为最小刻度指针,如果是的话则将赋值到跳动次数上
                pointer.setMinPointerBeatNumber(data.getMinPointerBeatNumber());
            }
        }

        long difference = BaseUtils.DateUtils.calLastedTime(startTime);
        for(int i = 0; i < difference; i++){
            this.pointer.beat();
        }
    }

    public static void main(String[] args){
    }
}