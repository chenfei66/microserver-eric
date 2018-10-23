package org.springframework.task.scheduling.storage.pojo.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Message;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.task.scheduling.constant.Constant;
import org.springframework.task.scheduling.pojo.dto.MessageElement;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/27
 * ErrorTask 因为抛异常而失败的任务
 */
@Data
@Document(collection = Constant.COLLECTION_NAME_ERROR_TASK_LIST)
public class ErrorTask{
    @Id
    private ObjectId _id;

    private MessageElement messageElement;

    private Message message;
}
