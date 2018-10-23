package org.springframework.task.scheduling.storage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.task.scheduling.clock.pojo.dto.ClockUpdateTimeDTO;
import org.springframework.task.scheduling.constant.Constant;
import org.springframework.task.scheduling.release.service.ReleaseDevice;
import org.springframework.task.scheduling.release.service.impl.ReleaseDeviceImpl;
import org.springframework.task.scheduling.storage.pojo.model.ErrorTask;
import org.springframework.task.scheduling.storage.pojo.model.FailureTask;
import org.springframework.task.scheduling.storage.pojo.model.TaskIndex;
import org.springframework.task.scheduling.storage.pojo.model.TimedTask;
import org.springframework.task.scheduling.storage.service.StorageService;
import org.springframework.task.scheduling.utils.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/23
 * StorageServiceImpl
 */
@Service
public class StorageServiceImpl implements StorageService{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String taskT = "task.";

    /***
     * mongo操作模型
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ReleaseDevice releaseDevice;

    private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(100);

    public void save(TimedTask timedTaskDO){
        String id = timedTaskDO.getIndex();//获取下标地址
        try{
            this.mongoTemplate.insert(timedTaskDO);
        }catch(Exception ex){
            throw new BusinessException("添加任务失败,任务内容--->" + JSONObject.toJSONString(timedTaskDO));
        }
        //获取索引
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.push(taskT + timedTaskDO.getMaxPointerAddress(), timedTaskDO.get_id());

        //更新索引
        WriteResult writeResult = mongoTemplate.updateFirst(query, update, TaskIndex.class);
        int n = writeResult.getN();
        if(n != 1){
            int reN = this.mongoTemplate.remove(new Query().addCriteria(new Criteria("_id").is(timedTaskDO.get_id())), TimedTask.class).getN();
            if(reN != 1){
                throw new BusinessException("更新任务索引失败 异常数据id:" + JSONObject.toJSONString(timedTaskDO.get_id()));
            }
            throw new BusinessException("更新任务索引失败");
        }
    }

    private ObjectId baseSave(TimedTask timedTaskDO){
        mongoTemplate.insert(timedTaskDO);
        return timedTaskDO.get_id();
    }

    public void batchSave(List<TimedTask> taskContainerDOList){
        this.mongoTemplate.insert(taskContainerDOList);
    }

    @Override
    public void saveErrorTask(ErrorTask errorTask){
        this.mongoTemplate.insert(errorTask);
    }

    public void pushExpiredTask(ClockUpdateTimeDTO clockUpdateTimeDTO){
        newFixedThreadPool.submit(new Runnable(){
            @Override
            public void run(){
                try{
                    String id = clockUpdateTimeDTO.getIndexAddress();
                    DBObject dbObject = new BasicDBObject();
                    BasicDBObject fieldsObject = new BasicDBObject();
                    fieldsObject.put(taskT + clockUpdateTimeDTO.getMaxPointerAddress(), true);
                    fieldsObject.put("pointer", true);
                    TaskIndex taskIndex = mongoTemplate.findOne(
                            new BasicQuery(dbObject, fieldsObject).addCriteria(new Criteria("_id").is(id)),
                            TaskIndex.class
                    );
                    Map<String, List<ObjectId>> taskIndexObjectIdMap = taskIndex.getTask();
                    List<ObjectId> objectIdList = taskIndexObjectIdMap.get(clockUpdateTimeDTO.getMaxPointerAddress());
                    if(objectIdList == null){
                        return;
                    }
                    if(objectIdList.size() == 0){
                        return;
                    }
                    Query query = new Query();
                    query.addCriteria(new Criteria("_id").in(objectIdList));
                    List<TimedTask> timedTaskList = mongoTemplate.find(query, TimedTask.class);
                    for(TimedTask timedTask : timedTaskList){
                        String serverName = timedTask.getServerName();
                        String url = timedTask.getUrl();
                        Map<String, Object> params = timedTask.getParams();
                        try{
                            releaseDevice.sendTimedTask(serverName, url, params, null);
                        }catch(Exception ex){
                            ex.printStackTrace();
                            logger.error("error:releaseDevice.sendTimedTask--->", ex);
                        }
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                    logger.error("推送到期任务发送错误--->", ex);
                }
            }
        });
    }

    @Override
    public void checkTaskIndex(){
        Long count = this.mongoTemplate.count(new Query(), TaskIndex.class);
        if(Constant.CYCLE_NUMBER.equals(count)){
            return;
        }
        for(Long i = 0L; i < Constant.CYCLE_NUMBER; i++){
            String id = ReleaseDeviceImpl.formatDelayTime(i);
            id = StorageServiceImpl.getAddress(id);
            TaskIndex taskIndex = this.mongoTemplate.findById(id, TaskIndex.class);
            if(taskIndex == null){
                taskIndex = new TaskIndex();
                taskIndex.set_id(id);
                Map<String, List<ObjectId>> paramMap = new HashMap<>();
                taskIndex.setTask(paramMap);
                this.mongoTemplate.insert(taskIndex);
            }
        }
    }

    @Override
    public void saveFailureTask(FailureTask failureTask){
        this.mongoTemplate.insert(failureTask);
    }

    /***
     * 根据时间格式化出任务下标
     * @return
     */
    public static String getAddress(String formatTimeStr){
        return formatTimeStr.substring(formatTimeStr.indexOf(".") + 1, formatTimeStr.length());
    }

    public static String getMaxPointerAddress(String formatTimeStr){
        return formatTimeStr.substring(0, formatTimeStr.indexOf("."));
    }


}
