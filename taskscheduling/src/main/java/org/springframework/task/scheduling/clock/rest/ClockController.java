package org.springframework.task.scheduling.clock.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.task.scheduling.clock.service.ClockService;
import org.springframework.task.scheduling.constant.InterfaceListConstant;
import org.springframework.task.scheduling.pojo.dto.ServerResponse;
import org.springframework.task.scheduling.utils.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/28
 *
 */
@RestController
public class ClockController{
   @Autowired
   private ClockService clockService;

    /***
     * 获取当前计时器时间
     * @return
     */
   @GetMapping(InterfaceListConstant.CLOCK_GET_TIME)
    public ServerResponse getTime(){
       ServerResponse serverResponse = new ServerResponse();
       try{
           serverResponse.setSuccess(clockService.getTime());
       }catch(BusinessException bx){
           serverResponse.setCode(bx.getCode());
           serverResponse.setMsg(bx.getMessage());
       }
       return serverResponse;
   }
}
