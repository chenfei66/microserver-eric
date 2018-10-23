package org.springframework.task.scheduling.actuator;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.task.scheduling.constant.Constant;
import org.springframework.task.scheduling.constant.InterfaceListConstant;
import org.springframework.task.scheduling.pojo.dto.MessageElement;
import org.springframework.task.scheduling.pojo.dto.ServerResponse;
import org.springframework.task.scheduling.release.service.ReleaseDevice;
import org.springframework.task.scheduling.release.service.impl.ReleaseDeviceImpl;
import org.springframework.task.scheduling.storage.pojo.model.ErrorTask;
import org.springframework.task.scheduling.storage.pojo.model.FailureTask;
import org.springframework.task.scheduling.utils.BaseUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/3
 * 根据服务名称注入一个队列,并绑定到交换机上
 */
@Data
@Configuration
public class MessageQueueConfig{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ConnectionFactory connectionFactory;

    /***
     * 地址
     */
    @Value("${message-queue.hostName}")
    private String hostName;

    /***
     * 端口
     */
    @Value("${message-queue.port}")
    private int port;

    /***
     * 用户名
     */
    @Value("${message-queue.userName}")
    private String userName;

    /***
     * 密码
     */
    @Value("${message-queue.password}")
    private String password;

    @Value("${message-queue.virtualHost}")
    private String virtualHost;

    @Value("${message-queue.publisherConfirms}")
    private boolean publisherConfirms;

    @Value("${spring.application.name}")
    private String springApplicationName;

    /***
     * 运行环境,如果不为空的话则发布时添加到
     */
    @Value("${message-queue.profile}")
    private String profile;

    /***
     * 失败次数上限(超出该上限后,该任务会进入失败队列)
     */
    @Value("${message-queue.numberOfFailures}")
    private Integer numberOfFailures;

    /***
     * 是否是计时器服务
     */
    @Value("${message-queue.clock}")
    private Boolean clock;

    /***
     * 更新时间
     */
    @Value("${message-queue.updateTimeUrl}")
    private String updateTimeUrl;

    /***
     * 判断是否为存储服务
     */
    @Value("${message-queue.storageServer}")
    private Boolean storageServer;

    /***
     * 消息
     */
    private static final String MESSAGES = "messages";

    /***
     * 默认交换机名称
     */
    private static final String DEFAULT_EXCHANGE = "defaultExchange";

    private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);

    /***
     * 生成队列
     * @return
     */
    @Bean(name = MESSAGES)
    public Queue queue(){
        return new Queue(this.springApplicationName, true, false, false, null);
    }

    /***
     * 创建直连交换机
     * @return DirectExchange
     */
    @Bean
    public DirectExchange defaultExchange(){
        return new DirectExchange(Constant.EXCHANGE, true, false);
    }

    /***
     * 创建广播交换机
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(Constant.EXCHANGE_FANOUT, true, false);
    }

    @Bean
    public Binding bindingFanoutExchangeMessages(){
        Queue queue = this.queue();
        return BindingBuilder.bind(queue).to(this.fanoutExchange());
    }

    /***
     * 将队列和交换机绑定,并指定Routing key
     * @return
     */
    @Bean
    public Binding bindingExchangeMessages(){
        Queue queue = this.queue();
        return BindingBuilder.bind(queue).to(this.defaultExchange()).with(queue.getName());
    }

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(hostName, port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(publisherConfirms); // 必须要设置
        return connectionFactory;
    }

    /***
     * 直连
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer taskContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(queue());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式, 手工确认
        ChannelAwareMessageListenerSon channelAwareMessageListenerSon = new ChannelAwareMessageListenerSon();
        channelAwareMessageListenerSon.setProfile(getProfile());
        container.setMessageListener(channelAwareMessageListenerSon);
        return container;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause){
                logger.info(" 回调id:" + correlationData);
                if(ack){
                    logger.info("消息成功消费");
                }else{
                    logger.info("消息消费失败:" + cause + "\n重新发送");
                }
            }
        });
        return template;
    }

    /***
     * 监听
     */
    class ChannelAwareMessageListenerSon implements ChannelAwareMessageListener{
        String profile;

        public void setProfile(String profile){
            this.profile = profile;
        }

        public void onMessage(Message message, Channel channel){
            newFixedThreadPool.submit(new Runnable(){
                @Override
                public void run(){
                    asyncOnMessage(message, channel);
                }
            });
        }

        private void asyncOnMessage(Message message, Channel channel){
            MessageElement messageElement = null;
            String result;
            try{
                byte[] body = message.getBody();
                messageElement = (MessageElement) BaseUtils.Serialization.getObjectFromBytes(body);
                logger.info("接收消息:" + JSONObject.toJSONString(messageElement));
                result = MessageQueueConfig.urlTransferMethod(messageElement);
                logger.info("执行结果:" + result);
                ServerResponse serverResponse = JSONObject.parseObject(result, ServerResponse.class);
                if(serverResponse.getCode() == ServerResponse.SUCCESS){//判断是否出现业务异常,如果发生业务异常,则立即重复执行五次,如果全部失败,则进入存储服务的失败队列
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//执行成功
                }else{
                    String errorMsg;
                    //boolean requeue;
                    if(messageElement.getNumberOfFailures() > Constant.NUMBER_OF_FAILURES){//超出后则不执行
                        errorMsg = String.format(
                                "任务失败次数超出%d次上限,进入失败任务回收队列,任务详情---> %s",
                                Constant.NUMBER_OF_FAILURES,
                                JSONObject.toJSONString(messageElement)
                        );
                        MessageElement failureMessageElement = new MessageElement();
                        FailureTask failureTask = new FailureTask();
                        failureTask.setMessageElement(messageElement);
                        failureTask.setServerResponse(serverResponse);

                        Map<String, Object> failureTaskParams = new HashMap<>();
                        BaseUtils.MapUtils.copyMap(failureTaskParams, failureTask);

                        failureMessageElement.setParams(failureTaskParams);
                        failureMessageElement.setNumberOfFailures(0);
                        failureMessageElement.setUrl(InterfaceListConstant.STORAGE_SAVE_FAILURE_TASK);
                        failureMessageElement.setExchange(Constant.EXCHANGE);
                        failureMessageElement.setRoutingKey(ReleaseDeviceImpl.getTaskStorageRoutinKey(), this.profile);
                        ReleaseDeviceImpl.send(messageElement, rabbitTemplate());
                    }else{
                        errorMsg = String.format(
                                "任务执行失败,等待重复执行,当前已执行次数%d,任务详情---> %s",
                                messageElement.getNumberOfFailures(),
                                JSONObject.toJSONString(messageElement)
                        );
                        messageElement.setNumberOfFailures(messageElement.getNumberOfFailures() + 1);
                        ReleaseDeviceImpl.send(messageElement, rabbitTemplate());
                    }
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    logger.info(errorMsg);
                }
            }catch(Exception ex){//当发生任何异常时,判定为此任务发生系统异常,不进行重复执行,并记录到error队列中
                logger.error("此任务异常,执行失败:", ex);
                ex.printStackTrace();
                try{
                    //发布到任务存储服务将失败任务存起来
                    MessageElement errorTaskMessageElement = new MessageElement();
                    errorTaskMessageElement.setExchange(Constant.EXCHANGE);
                    errorTaskMessageElement.setUrl(InterfaceListConstant.STORAGE_SAVE_ERROR_TASK);
                    errorTaskMessageElement.setRoutingKey(ReleaseDeviceImpl.getTaskStorageRoutinKey(), this.profile);
                    errorTaskMessageElement.setNumberOfFailures(0);
                    Map<String, Object> paramMap = new HashMap<>();
                    ErrorTask errorTask = new ErrorTask();
                    errorTask.setMessageElement(messageElement);
                    errorTask.setMessage(message);
                    BaseUtils.MapUtils.copyMap(paramMap, errorTask);
                    errorTaskMessageElement.setParams(paramMap);

                    ReleaseDeviceImpl.send(errorTaskMessageElement, rabbitTemplate());
                    try{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }catch(Exception ex2){
                    ex2.printStackTrace();
                    try{
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
//                //装载消息持久配置
//                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
//                //生成消息配置模型对象(这里主要需要确认是持久的)
//                AMQP.BasicProperties convertedMessageProperties = new DefaultMessagePropertiesConverter().fromMessageProperties(message.getMessageProperties(), "UTF-8");
//                //将任务发布到任务存储服务的失败队列
//                try{
//                    MessageElement errorTaskMessageElement = new MessageElement();
//                    errorTaskMessageElement.setUrl("/storage/save/error/task/");
//                    //装载参数
//                    ErrorTask errorTask = new ErrorTask();
//                    errorTask.setResult(ex.getMessage());
//                    errorTask.setType(Constant.ErrorTaskType.EXCEPTION_TASK);
//                    try{
//                        errorTask.setContent(JSONObject.toJSONString(message));
//                    }catch(Exception jsonException){
//                        errorTask.setContent("message错误无法翻译");
//                    }
//                    Map<String, Object> messageParams = new HashMap<>();
//                    BaseUtils.MapUtils.copyMap(messageParams, errorTask);
//                    errorTaskMessageElement.setParams(messageParams);
//                    channel.basicPublish(
//                            Constant.EXCHANGE,
//                            Constant.TASK_STORAGE_SERVER_NAME,
//                            convertedMessageProperties,
//                            BaseUtils.Serialization.getBytesFromObject(errorTaskMessageElement)
//                    );

    }


    /***
     * 通过异步调用DTO中url的属性获取对应controller method中方法的名称，
     * 然后从spring上下文中获取该controller实例对象,通过methodData中method方法名获取方法，
     * 最后通过反射调用
     * @param messageElement messageElement
     * @return ServerResponse
     */
    private static String urlTransferMethod(MessageElement messageElement){
        try{
            String url = BaseUtils.StringUtilsSon.removeLastMark(messageElement.getUrl(), "/");
            MethodData methodData = UrlForMathod.getMethodDataToUrl(url);
            Class c;
            c = Class.forName(methodData.getClassName());
            Object result = null;
            Object controllerObj = SpringTaskContext.getBean(c);
            String methodName = methodData.getMethod();
            String[] paramClassStrs = methodData.getParamClass();
            String dtoClass;
            if(paramClassStrs.length != 0){
                dtoClass = paramClassStrs[0];
                Object dto;
                try{
                    dto = BaseUtils.Reflection.mapToPojo(messageElement.getParams(), Class.forName(dtoClass));
                    result = c.getMethod(methodName, dto.getClass()).invoke(controllerObj, dto);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                try{
                    result = c.getMethod(methodName).invoke(controllerObj);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return JSONObject.toJSONString(result);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
