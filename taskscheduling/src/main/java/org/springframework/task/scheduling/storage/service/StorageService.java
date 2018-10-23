package org.springframework.task.scheduling.storage.service;

import org.springframework.task.scheduling.clock.pojo.dto.ClockUpdateTimeDTO;
import org.springframework.task.scheduling.storage.pojo.model.ErrorTask;
import org.springframework.task.scheduling.storage.pojo.model.FailureTask;
import org.springframework.task.scheduling.storage.pojo.model.TimedTask;

import java.util.List;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/23
 * StorageService
 */
public interface StorageService{
    /***
     * 保存一个任务到DB
     * @param timedTask timedTask
     */
    void save(TimedTask timedTask);

    /****
     * 批量保存任务
     */
    void batchSave(List<TimedTask> taskContainerDOList);

    /***
     * 保存执行失败的任务
     * @param errorTask errorTask
     */
    void saveErrorTask(ErrorTask errorTask);

    /***
     * 接收定时器发送过来的当前时间的消息,并将到时间的任务推送到执行队列
     * @param clockUpdateTimeDTO
     */
    void pushExpiredTask(ClockUpdateTimeDTO clockUpdateTimeDTO);

    /***
     * 校验索引是否健全
     */
    void checkTaskIndex();

    /***
     * 保存执行器未抛异常,通过返回结果判断执行失败的任务.
     */
    void saveFailureTask(FailureTask failureTask);
}
