package org.springframework.task.scheduling.release.service;

import java.util.Map;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/24
 * ReleaseDevice
 */
public interface ReleaseDevice{
    /***
     * 发布延迟任务
     * @param serverName 服务名称
     * @param url 接口地址
     * @param params 参数
     * @param delaySecond 延迟时间(秒)
     */
    void sendTimedTask(String serverName, String url, Map<String, Object> params, Long delaySecond);

    /***
     * 发布延迟任务
     * @param serverName 服务名称
     * @param url 接口地址
     * @param params 参数
     * @param delaySecond 延迟时间(秒)
     */
    void sendTimedTask(String serverName, String url, Object params, Long delaySecond);

    /***
     * 发布延迟广播任务
     * @param serverName 服务名称
     * @param url 地址
     * @param params 参数
     * @param delaySecond 延迟时间(单位:秒)
     */
    void sendTimedTaskFanout(String serverName, String url, Map<String, Object> params, Long delaySecond);
}
