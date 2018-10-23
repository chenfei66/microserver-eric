package org.springframework.task.scheduling.storage.pojo.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.task.scheduling.constant.Constant;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/7/24
 * TimedTaskDO 延时任务数据模型
 */
@Data
@Document(collection = Constant.COLLECTION_NAME_TASK_LIST)
public class TimedTask extends Task{
    /***
     * 将要延迟的时间(计量单位为秒)
     */
    private Long delay;
}
