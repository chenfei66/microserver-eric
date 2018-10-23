package org.springframework.task.scheduling.pojo.dto;

import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.task.scheduling.constant.Constant;
import org.springframework.task.scheduling.release.service.impl.ReleaseDeviceImpl;
import org.springframework.task.scheduling.utils.BaseUtils;

import java.io.Serializable;
import java.util.Map;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/23
 * MessageElement
 */
@Data
public class MessageElement implements Serializable{
    /***
     * 交换机
     */
    private String exchange;

    /***
     * routingKey
     */
    private String routingKey;

    /***
     * 调用接口
     */
    private String url;

    /***
     * 参数
     */
    private Map<String, Object> params;

    /***
     * 失败次数,超出失败次数上限后会进入失败队列
     */
    private int numberOfFailures;

    public void setRoutingKey(String routingKey, String profile){
        if(!BaseUtils.isBlank(profile)){
            profile = "-" + profile;
        }
        this.routingKey = routingKey + profile;
    }
}
