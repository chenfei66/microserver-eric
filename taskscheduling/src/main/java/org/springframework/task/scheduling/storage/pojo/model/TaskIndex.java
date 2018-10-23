package org.springframework.task.scheduling.storage.pojo.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/***
 * @author 王强 Email :
 * @version 创建时间：2018/8/20
 * TaskIndex
 */
@Data
@Document(collection = "task_index")
public class TaskIndex{
    public static final String TASK_T = "task";

    /***
     * 是id同时也是当前刻度的指针地址 格式:[23.55.12]
     */
    @Id
    private String _id;

    /***
     * 挂载的任务列表
     */
    private Map<String, List<ObjectId>> task;
}
