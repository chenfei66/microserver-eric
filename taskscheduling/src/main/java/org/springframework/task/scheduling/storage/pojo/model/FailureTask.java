package org.springframework.task.scheduling.storage.pojo.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.task.scheduling.constant.Constant;
import org.springframework.task.scheduling.pojo.dto.MessageElement;
import org.springframework.task.scheduling.pojo.dto.ServerResponse;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/9/5
 * FailureTask 执行时未发生异常,执行后返回结果显示失败的任务
 */
@Data
@Document(collection = Constant.FAILURE_TASK_LIST)
public class FailureTask{
    /***
     * 执行结果
     */
    private ServerResponse serverResponse;

    /***
     * 任务内容
     */
    private MessageElement messageElement;
}
