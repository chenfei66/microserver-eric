package org.springframework.task.scheduling.constant;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/22
 * 任务类型枚举
 */
public enum TaskTypeEnum{
    TIMED_TASK("timedTask", "延时任务"),
    CYCLIC_TASK("cyclicTask", "循环任务");

    /***
     * 类型key值
     */
    private String type;

    /***
     * 介绍
     */
    private String introduction;

    TaskTypeEnum(String type, String introduction){
        this.type = type;
        this.introduction = introduction;
    }

    public String getIntroduction(){
        return introduction;
    }

    public String getType(){
        return type;
    }
}
