package org.springframework.task.scheduling.storage.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.task.scheduling.clock.pojo.dto.ClockUpdateTimeDTO;
import org.springframework.task.scheduling.constant.InterfaceListConstant;
import org.springframework.task.scheduling.pojo.dto.ServerResponse;
import org.springframework.task.scheduling.storage.pojo.model.ErrorTask;
import org.springframework.task.scheduling.storage.pojo.model.FailureTask;
import org.springframework.task.scheduling.storage.pojo.model.TimedTask;
import org.springframework.task.scheduling.storage.service.StorageService;
import org.springframework.task.scheduling.utils.BusinessException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/23
 * StorageController
 */
@RestController
public class StorageController{
    @Autowired
    private StorageService storageService;

    /***
     * 保存延时任务
     * @param timedTask timedTask
     * @return ServerResponse
     */
    @PostMapping(InterfaceListConstant.STORAGE_SAVE_TIMED_TASK)
    public ServerResponse saveTimedTask(TimedTask timedTask){
        ServerResponse serverResponse = new ServerResponse();
        try{
            this.storageService.save(timedTask);
            serverResponse.setSuccess();
        }catch(BusinessException bx){
            serverResponse.setCode(bx.getCode());
            serverResponse.setMsg(bx.getMessage());
        }
        return serverResponse;
    }

    /***
     * 保存因为抛异常而执行失败的任务
     * @param errorTask
     * @return
     */
    @PostMapping(InterfaceListConstant.STORAGE_SAVE_ERROR_TASK)
    public ServerResponse saveErrorTask(ErrorTask errorTask){
        ServerResponse serverResponse = new ServerResponse();
        try{
            this.storageService.saveErrorTask(errorTask);
            serverResponse.setSuccess();
        }catch(BusinessException bx){
            serverResponse.setCode(bx.getCode());
            serverResponse.setMsg(bx.getMessage());
        }
        return serverResponse;
    }

    /***
     * 接收定时器发送过来的当前时间的消息
     * @param clockUpdateTimeDTO
     * @return
     */
    @PostMapping(InterfaceListConstant.STORAGE_PUSH_EXPIRED_TASK)
    public ServerResponse pushExpiredTask(ClockUpdateTimeDTO clockUpdateTimeDTO){
        ServerResponse serverResponse = new ServerResponse();
        try{
            this.storageService.pushExpiredTask(clockUpdateTimeDTO);
            serverResponse.setSuccess();
        }catch(BusinessException bx){
            serverResponse.setCode(bx.getCode());
            serverResponse.setMsg(bx.getMessage());
        }
        return serverResponse;
    }

    /***
     * 保存执行后返回值显示失败的任务
     * @param failureTask failureTask
     * @return
     */
    @PostMapping(InterfaceListConstant.STORAGE_SAVE_FAILURE_TASK)
    public ServerResponse saveFailureTask(FailureTask failureTask){
        ServerResponse serverResponse = new ServerResponse();
        try{
            this.storageService.saveFailureTask(failureTask);
            serverResponse.setSuccess();
        }catch(BusinessException bx){
            serverResponse.setCode(bx.getCode());
            serverResponse.setMsg(bx.getMessage());
        }
        return serverResponse;
    }

    /***
     * 测试
     * @return
     */
    @PostMapping(InterfaceListConstant.STORAGE_TEST1)
    public ServerResponse test1(){
        ServerResponse serverResponse = new ServerResponse();
        try{
            serverResponse.setSuccess();
        }catch(BusinessException bx){
            serverResponse.setCode(bx.getCode());
            serverResponse.setMsg(bx.getMessage());
        }
        return serverResponse;
    }
}
