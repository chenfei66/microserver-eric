package org.springframework.task.scheduling.clock.service.impl;

import lombok.Data;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/28
 * CurrentTime
 */
@Data
public class CurrentTime{
    /***
     * 当前计时器时间
     */
    protected static long TIME;

    /***
     * 格式化后当前计时器时间
     */
    protected static String FORMAT_TIME;

    /***
     * set
     * @param time time
     * @param formatTime formatTime
     */
    protected static void set(long time, String formatTime){
        CurrentTime.TIME = time;
        CurrentTime.FORMAT_TIME = formatTime;
    }

    /***
     * 获取当前时间的最小刻度单位的数字
     * @return
     */
    public static long getTime(){
        return CurrentTime.TIME;
    }

    /***
     * 获取格式化后的时间
     * @return
     */
    public static String getFormatTime(){
        return CurrentTime.FORMAT_TIME;
    }
}
