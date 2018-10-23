package org.springframework.task.scheduling.clock;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.task.scheduling.clock.pojo.model.CurrentDateDO;
import org.springframework.task.scheduling.utils.BaseUtils;

import java.util.Date;
import java.util.Map;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2018/8/14
 * 故障保护。每隔一段时间记录当前时间,当发生故障后,
 * 重启取当前时间和之前的时间进行叠加得到当前应该的时间,将时间纠正.
 */
public class FaultProtection{
    private MongoOperations mongoOperations;

    public FaultProtection(MongoOperations mongoOperations){
        this.mongoOperations = mongoOperations;
    }

    /***
     * 修改时间
     * @param log 日志
     * @param address 地址
     * @param minPointerBeatNumber 最小指针
     */
    public void updateTime(String log, Map<String, Integer> address, long minPointerBeatNumber){
        try{
            Date currentDate = new Date();
            CurrentDateDO currentDateDO = new CurrentDateDO();
            CurrentDateDO.Data data = currentDateDO.getData();
            data.setCurrentTime(currentDate);
            data.setFormatDate(BaseUtils.formatDate(currentDate, "yyyy-MM-dd HH:mm:ss"));
            data.setAddress(address);
            data.setMinPointerBeatNumber(minPointerBeatNumber);
            data.setDateLog(log);
            currentDateDO.setData(data);
            this.mongoOperations.updateFirst(
                    new Query(new Criteria("_id").is(CurrentDateDO.FAULT_PROTECTION)),
                    new Update().set("data", data),
                    CurrentDateDO.class
            );
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
