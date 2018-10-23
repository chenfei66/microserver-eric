package org.springframework.task.scheduling.constant;

import org.springframework.task.scheduling.clock.Dial;
import org.springframework.task.scheduling.utils.Commissions;

import java.util.concurrent.TimeUnit;

/***
 * 时间类型枚举
 */
public enum TimeTypeEnum{
    DAYS("天", TimeUnit.DAYS, new Commissions(), new Dial(365, 0), false, true),
    HOURS("时", TimeUnit.HOURS, new Commissions(), new Dial(24, 0), false, false),
    MINUTES("分", TimeUnit.MINUTES, new Commissions(), new Dial(60, 0), false, false),
    SECONDS("秒", TimeUnit.SECONDS, new Commissions(), new Dial(60, 0), true, false);

    /***
     * 名称
     */
    private String name;

    /***
     * 时间类型
     */
    private TimeUnit timeUnit;

    /***
     * 事件委托
     */
    private Commissions commissions;

    /***
     * 所属表盘
     */
    private Dial dial;

    /***
     * 是否为最小度量刻度
     */
    private boolean minimumScaleBool;

    /***
     * 是否没有观察者[true:是][false:否]没有观察者, 则说明此指针为刻度单位为最大时间单位。无需观察者订阅
     */
    private boolean maximumScaleBool;

    /***
     * 构造
     * @param name name
     * @param timeUnit timeUnit
     * @param commissions commissions
     * @param dial dial
     */
    TimeTypeEnum(String name, TimeUnit timeUnit, Commissions commissions, Dial dial, boolean minimumScaleBool, boolean maximumScaleBool){
        this.name = name;
        this.timeUnit = timeUnit;
        this.commissions = commissions;
        this.dial = dial;
        this.minimumScaleBool = minimumScaleBool;
        this.maximumScaleBool = maximumScaleBool;
    }

    public String getName(){
        return this.name;
    }

    public Dial getDial(){
        return this.dial;
    }

    public Commissions getCommissions(){
        return this.commissions;
    }

    public void addEvent(Object object, String methodName, Object... args){
        this.commissions.addEvent(object, methodName, args);
    }

    public boolean isMinimumScaleBool(){
        return this.minimumScaleBool;
    }

    public boolean isMaximumScaleBool(){
        return this.maximumScaleBool;
    }

    public TimeUnit getTimeUnit(){
        return this.timeUnit;
    }
}
