package org.springframework.task.scheduling.constant;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/23
 * 任务调度所有服务接口列表
 */
public class InterfaceListConstant{
    /***
     * 任务存储服务
     */
    private static final String STORAGE = "/storage";

    /***
     * 保存延时任务
     */
    public static final String STORAGE_SAVE_TIMED_TASK = STORAGE + "/save/timed/task";

    /***
     * 接收当前的计时器时间,并推送过期(到了时间需要执行的任务)任务
     */
    public static final String STORAGE_PUSH_EXPIRED_TASK = STORAGE + "/push/expired/task";

    /***
     * 保存因为异常失败的任务
     */
    public static final String STORAGE_SAVE_ERROR_TASK = STORAGE + "/save/error/task";

    /***
     * 保存因为返回结果显示失败的任务(业务错误任务)
     */
    public static final String STORAGE_SAVE_FAILURE_TASK = STORAGE + "/save/failure/task";

    /***
     * 获取计时器当前时间
     */
    public static final String CLOCK_GET_TIME = "/clock/get/time";

    /***
     * 测试
     */
    public static final String STORAGE_TEST1 = STORAGE + "test1";
}
