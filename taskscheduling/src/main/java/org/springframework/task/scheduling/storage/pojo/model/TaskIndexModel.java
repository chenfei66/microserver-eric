package org.springframework.task.scheduling.storage.pojo.model;

import lombok.Data;
import org.bson.types.ObjectId;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/30
 * TaskIndexModel
 */
@Data
public class TaskIndexModel{
    /***
     * 队列头id
     */
    private ObjectId headId;

    /***
     * 队列尾id
     */
    private ObjectId endId;
}
