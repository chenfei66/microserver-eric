package org.springframework.task.scheduling.clock.service;

import org.springframework.task.scheduling.clock.pojo.model.CurrentDateDO;

/***
 * @author 王强 Email :
 * @version 创建时间：2018/8/28
 * ClockService
 */
public interface ClockService{
    /***
     * 更新当前服务的计时器时间
     */
    void updateTime();

    CurrentDateDO getTime();
}
