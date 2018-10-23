package org.springframework.task.scheduling.clock.pojo.dto;

import lombok.Data;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/28
 * 更新当前计时器时间
 */
@Data
public class ClockUpdateTimeDTO{
    /***
     * 未格式化的时间(单位:秒)
     */
    private long time;
    /***
     * 格式化后的时间
     */
    private String formatDate;

    /***
     * 最大刻度值
     */
    private String maxPointerAddress;

    /***
     * 索引id
     */
    private String indexAddress;
}
