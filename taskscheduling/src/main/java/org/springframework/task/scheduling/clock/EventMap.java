package org.springframework.task.scheduling.clock;

import lombok.Data;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/14
 * EventMap
 */
@Data
public class EventMap{

    private Object object;

    private String methodName;

    public EventMap(Object object, String methodName){
        this.object = object;
        this.methodName = methodName;
    }
}
