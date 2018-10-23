package org.springframework.task.scheduling.constant;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/23
 * Constant
 */
public class Constant{
    /***
     * 任务调度交换机名称
     */
    public static final String EXCHANGE = "task-scheduling-exchange";

    /***
     * 任务调度广播交换机名称
     */
    public static final String EXCHANGE_FANOUT = "task-scheduling-fanout-exchange";

    /***
     * 任务存储服务名称(同时也是该服务消息队列的routingKey)
     */
    public static final String TASK_STORAGE_SERVER_NAME = "taskStorageServer";

    /***
     * 计时器服务
     */
    public static final String CLOCK_SERVER_NAME = "clockServer";

    /***
     * 任务列表集合名称(mongodb的集合名称)
     */
    public static final String COLLECTION_NAME_TASK_LIST = "task_list";

    /***
     * 失败(执行过程中抛异常或返回执行码不正确的任务)任务列表集合名(mongodb的集合名称)
     */
    public static final String COLLECTION_NAME_ERROR_TASK_LIST = "error_task_list";

    /***
     * 返回结果显示执行失败的任务
     */
    public static final String FAILURE_TASK_LIST = "failure_task_list";

    /***
     * 业务异常任务重复执行的次数上限
     */
    public static final int NUMBER_OF_FAILURES = 5;

    /***
     * 任务最小延迟时间(单位:秒)
     */
    public static final int MIN_DELAY = 60;

    /***
     * 一个周期指针跳动的次数,
     */
    public static final Long CYCLE_NUMBER = (long) (TimeTypeEnum.HOURS.getDial().size() * TimeTypeEnum.MINUTES.getDial().size() * TimeTypeEnum.SECONDS.getDial().size());


    /***
     * 失败任务类型
     */
    public enum ErrorTaskType{
        EXCEPTION_TASK("执行时发生了异常的任务(此类型任务不再重复执行,只进行单纯的内容保存)"),
        BUSINESS_TASK("业务异常任务,返回的成功识别码不是正确的,此任务会当下重复执行配置次书,后进入失败队列");

        private String introduction;

        ErrorTaskType(String introduction){
            this.introduction = introduction;
        }

        public String getIntroduction(){
            return this.introduction;
        }
    }
}
