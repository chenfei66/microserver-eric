package org.springframework.task.scheduling.storage.pojo.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.task.scheduling.constant.TaskTypeEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/7/24
 * TaskContainerDO 任务容器
 */
@Data
public class  Task implements Serializable{
    @Id
    private ObjectId _id;

    /***
     * 任务类型
     */
    private TaskTypeEnum type;

    /***
     * 执行时间 格式: 70.23.31.58 表示在任务调度时间的第70天, 23小时, 31分钟,58秒的时候执行该任务
     */
    private String formatExecutionTime;

    /***
     * 任务下标task_index表中id 入:【12.32.56】
     */
    private String index;

    /***
     * 最大刻度值
     */
    private String maxPointerAddress;

    /***
     * 发布任务时的计时器时间(格式化后)
     */
    private String clockCurrentFormatTime;

    /***
     * 发布任务时的计时器时间(单位:秒)
     */
    private Long clockCurrentTime;

    /***
     * 执行时间最小单位值(当前一共走过的秒数)
     */
    private Long executionTime;

    /***
     * 任务回调url地址
     */
    private String url;

    /****
     * 参数
     */
    private Map<String, Object> params;

    /***
     * 任务发布时间
     */
    private Date releaseTime;

    /***
     * 任务最后一次修改的时间
     */
    private Date updateTime;

    /***
     * 任务执行的服务名称(执行个该任务的服务)
     */
    private String serverName;
}
