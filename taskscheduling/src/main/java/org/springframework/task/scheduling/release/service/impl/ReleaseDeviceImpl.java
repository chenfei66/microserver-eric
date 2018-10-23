package org.springframework.task.scheduling.release.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.task.scheduling.actuator.MessageQueueConfig;
import org.springframework.task.scheduling.clock.service.impl.CurrentTime;
import org.springframework.task.scheduling.constant.Constant;
import org.springframework.task.scheduling.constant.InterfaceListConstant;
import org.springframework.task.scheduling.constant.TaskTypeEnum;
import org.springframework.task.scheduling.constant.TimeTypeEnum;
import org.springframework.task.scheduling.pojo.dto.MessageElement;
import org.springframework.task.scheduling.release.service.ReleaseDevice;
import org.springframework.task.scheduling.storage.pojo.model.TimedTask;
import org.springframework.task.scheduling.storage.service.impl.StorageServiceImpl;
import org.springframework.task.scheduling.utils.BaseUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/23
 * ReleaseDeviceImpl
 */
@Component
public class ReleaseDeviceImpl implements ReleaseDevice{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageQueueConfig messageQueueConfig;

    public void sendTimedTask(String serverName, String url, Map<String, Object> params, Long delaySecond){
        this.send(serverName, url, params, delaySecond, Constant.EXCHANGE, getTaskStorageRoutinKey());
    }

    private static String getRoutinKey(String serverName){
        return BaseUtils.StringUtilsSon.addMarkToString(serverName, "-");
    }

    public static String getTaskStorageRoutinKey(){
        return ReleaseDeviceImpl.getRoutinKey(Constant.TASK_STORAGE_SERVER_NAME);
    }

    public void sendTimedTaskFanout(String serverName, String url, Map<String, Object> params, Long delaySecond){
        this.send(serverName, url, params, delaySecond, Constant.EXCHANGE_FANOUT, "");
    }

    /***
     * 基础发布任务
     * @param serverName 执行任务的服务名称
     * @param url 地址
     * @param params 参数
     * @param delaySecond  延迟时间
     * @param exchange 交换机
     * @param routingKey 即将要发往的队列
     */
    private void send(String serverName, String url, Map<String, Object> params, Long delaySecond, String exchange, String routingKey){
        this.send(this.setGetMessageElement(serverName, url, params, delaySecond, exchange, routingKey));
    }

    @Override
    public void sendTimedTask(String serverName, String url, Object params, Long delaySecond){
        Map<String, Object> paramMap = new HashMap<>();
        BaseUtils.MapUtils.copyMap(paramMap, params);
        this.sendTimedTask(serverName, url, paramMap, delaySecond);
    }

    /***
     * 装载并获取消息元素
     * @param serverName 服务名称
     * @param url 地址
     * @param params 参数
     * @param delaySecond 延迟时间(单位:秒)
     * @return MessageElement
     */
    private MessageElement setGetMessageElement(String serverName, String url, Map<String, Object> params, Long delaySecond, String exchange, String routingKey){
        serverName = BaseUtils.StringUtilsSon.addMarkToString(serverName, "-");
        MessageElement messageElement = new MessageElement();
        Map<String, Object> messageParams = new HashMap<>();
        if(delaySecond == null){
            messageElement.setUrl(url);
            messageParams = params;
            messageElement.setRoutingKey(getRoutinKey(serverName), messageQueueConfig.getProfile());
        }else if(delaySecond <= Constant.MIN_DELAY){//如果延迟时间为null或0的话则直接执行此任务
            messageElement.setUrl(url);
            messageParams = params;
            messageElement.setRoutingKey(getRoutinKey(serverName), messageQueueConfig.getProfile());
        }else{
            TimedTask timedTask = new TimedTask();
            timedTask.setUrl(url);
            //此处需要加上当前时间
            logger.info("CurrentTime--->" + CurrentTime.getFormatTime());
            timedTask.setDelay(delaySecond + CurrentTime.getTime());
            timedTask.setExecutionTime(timedTask.getDelay());

            String formatExcutionTime = formatDelayTime(timedTask.getDelay());
            timedTask.setFormatExecutionTime(formatExcutionTime);
            timedTask.setIndex(StorageServiceImpl.getAddress(formatExcutionTime));
            timedTask.setMaxPointerAddress(StorageServiceImpl.getMaxPointerAddress(formatExcutionTime));
            timedTask.setExecutionTime(CurrentTime.getTime());
            //装载从clock(计时器)服务获取到的计时器当前时间
            timedTask.setClockCurrentFormatTime(CurrentTime.getFormatTime());
            timedTask.setClockCurrentTime(CurrentTime.getTime());

            Date date = new Date();
            timedTask.setUpdateTime(date);
            timedTask.setReleaseTime(date);

            timedTask.setParams(params);
            timedTask.setType(TaskTypeEnum.TIMED_TASK);
            timedTask.setServerName(serverName);

            messageElement.setRoutingKey(routingKey, messageQueueConfig.getProfile());
            messageElement.setUrl(InterfaceListConstant.STORAGE_SAVE_TIMED_TASK);
            BaseUtils.MapUtils.copyMap(messageParams, timedTask);
        }
        messageElement.setNumberOfFailures(0);
        messageElement.setParams(messageParams);
        messageElement.setExchange(exchange);
        return messageElement;
    }

    /***
     * 发布消息
     * @param messageElement 消息元素
     * @param rabbitTemplate rabbit操作对象
     */
    public static void send(MessageElement messageElement, RabbitTemplate rabbitTemplate){
        String exchange = messageElement.getExchange();
        String routingKey = messageElement.getRoutingKey();

        String uuid = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(uuid);
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                BaseUtils.Serialization.getBytesFromObject(messageElement),
                new MessagePostProcessor(){
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException{
                        //消息持久化
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    }
                }, correlationId);
    }

    private void send(MessageElement messageElement){
        logger.info("send--->" + JSONObject.toJSONString(messageElement));
        messageElement.setUrl(BaseUtils.StringUtilsSon.removeLastMark(messageElement.getUrl(), "/"));
        ReleaseDeviceImpl.send(messageElement, this.rabbitTemplate);
    }

    /***
     * 格式化延迟时间(将延迟秒数格式化成计时器标准时间)
     * @param paramSecond 将要延迟的秒数
     * @return
     */
    public static String formatDelayTime(Long paramSecond){
        Integer hours = TimeTypeEnum.HOURS.getDial().size();
        Integer minutes = TimeTypeEnum.MINUTES.getDial().size();
        Integer seconds = TimeTypeEnum.SECONDS.getDial().size();

        long resultMinutes = paramSecond / seconds;
        long resultHours = resultMinutes / minutes;

        return String.format(
                "%d.%d.%d.%d",
                resultHours / hours,
                resultHours % hours,
                resultMinutes % minutes,
                paramSecond % seconds
        );
    }
}
