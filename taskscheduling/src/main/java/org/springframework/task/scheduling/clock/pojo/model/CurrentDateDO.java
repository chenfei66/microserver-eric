package org.springframework.task.scheduling.clock.pojo.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/14
 * 当前时间
 */
@Data
@Document(collection = "current_date")
public class CurrentDateDO{
    public static final String FAULT_PROTECTION = "fault_protection";

    @Id
    private String _id = FAULT_PROTECTION;

    private Data data;

    public CurrentDateDO(){
        this.data = new Data();
    }

    /***
     * 指针详细
     */
    public class Data{
        /***
         * 当前时间
         */
        private Date currentTime;

        /***
         * 格式化后的时间
         */
        private String formatDate;

        /***
         * 当时的时间log
         */
        private String dateLog;

        /***
         * 上次关闭前最后的最小指针跳动次数
         */
        private Long minPointerBeatNumber;

        /***
         * 上次的地址
         */
        private Map<String, Integer> address = new HashMap<>();

        public void setFormatDate(String formatDate){
            this.formatDate = formatDate;
        }

        public void setDateLog(String dateLog){
            this.dateLog = dateLog;
        }

        public void setCurrentTime(Date currentTime){
            this.currentTime = currentTime;
        }

        public void setAddress(Map<String, Integer> address){
            this.address = address;
        }

        public String getFormatDate(){
            return formatDate;
        }

        public String getDateLog(){
            return dateLog;
        }

        public Date getCurrentTime(){
            return currentTime;
        }

        public Long getMinPointerBeatNumber(){
            return this.minPointerBeatNumber;
        }

        public void setMinPointerBeatNumber(Long minPointerBeatNumber){
            this.minPointerBeatNumber = minPointerBeatNumber;
        }

        public Map<String, Integer> getAddress(){
            return address;
        }
    }

    public static void main(String[] args){
        CurrentDateDO currentDateDO = null;
        try{
           currentDateDO.getData();
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.toString());
            System.out.println(JSONObject.toJSONString(ex));
        }
    }
}
