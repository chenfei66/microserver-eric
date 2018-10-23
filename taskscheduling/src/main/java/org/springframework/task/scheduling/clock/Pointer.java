package org.springframework.task.scheduling.clock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.task.scheduling.utils.Commissions;
import org.springframework.task.scheduling.constant.TimeTypeEnum;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/10
 * Pointer 指针
 */
public class Pointer extends Thread{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /***
     * 指针当前地址(所在表盘的刻度)
     */
    private Integer pointerAddress;

    /***
     * 指针类型(控制表转动的速度)
     */
    private TimeTypeEnum pointerType;

    /***
     * 运行圈数
     */
    private Integer numberOfTurns = 0;

    /***
     * 最小刻度指针跳动次数
     */
    private Long minPointerBeatNumber;

    /***
     * 所属表盘
     */
    private Dial dial;

    /***
     * 指针所在刻度(指针当前指向的地方)
     */
    private Scale scale;

    /***
     * 通知者事物委托
     */
    private Commissions commissions;

    /***
     * 是否为最小刻度指针, 如果是的话则则不受通知者的影响, 可以自行转动。
     */
    private boolean minimumScaleBool = false;

    /***
     * 当前时间(多pointer共享对象)
     */
    private CurrentTime currentTime;

    /***
     * 是否没有观察者[true:是][false:否]没有观察者, 则说明此指针为刻度单位为最大时间单位。无需观察者订阅
     */
    private boolean isNullObserver = false;

    class OperationResult{
        /***
         * 得数
         */
        private long result;

        /***
         * 余数
         */
        private long remainder;

        public long getRemainder(){
            return remainder;
        }

        public long getResult(){
            return result;
        }

        public void setRemainder(long remainder){
            this.remainder = remainder;
        }

        public void setResult(long result){
            this.result = result;
        }
    }

    /***
     * 构造
     * @param pointerType pointerType
     */
    public Pointer(TimeTypeEnum pointerType){
        this.pointerType = pointerType;
        this.dial = this.pointerType.getDial();
        //此处pointerAddress属性冗余, 变更scale时一定要同步pointerAddress属性
        this.scale = this.dial.getFirstScale();
        this.pointerAddress = this.scale.getAddress();
        this.commissions = this.pointerType.getCommissions();
        this.minimumScaleBool = this.pointerType.isMinimumScaleBool();
        this.isNullObserver = this.pointerType.isMaximumScaleBool();//没有观察者, 则说明此指针为刻度单位为最大时间单位。无需观察者订阅
    }

    public void setMinPointerBeatNumber(Long minPointerBeatNumber){
        this.minPointerBeatNumber = minPointerBeatNumber;
    }

    public Long getMinPointerBeatNumber(){
        return this.minPointerBeatNumber;
    }

    public void setCurrentTime(CurrentTime currentTime){
        this.currentTime = currentTime;
    }

    public void run(){
        this.startPointer();
    }

    public void startPointer(){
        //判断是否为最小刻度,如果是的话则开启自循环转动
        if(!this.minimumScaleBool){
            return;
        }
        //输出第一次的时间
        this.currentTime.showTime();
        while(true){
            int rotationBase = 1;
            try{
                Thread.sleep(this.getPointerType().getTimeUnit().toMillis(rotationBase));
                this.beat();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /***
     * 使当前指针跳向所在表盘的下一个刻度
     */
    public void beat(){
        //向后移动一个刻度
        this.nextScale();
        if(this.isMinimumScaleBool()){
            this.setMinPointerBeatNumber(this.getMinPointerBeatNumber() + 1);
        }
        if(!dial.getFirst().equals(this.pointerAddress)){
            this.currentTime.showTime();
        }
        if(dial.getFirst().equals(this.pointerAddress)){//转动完一个刻度后判断是否回到了第一个刻度,如果是的话则判定为转够一圈,发出通知,并圈数加1
            this.numberOfTurns = this.numberOfTurns + 1;//圈数加一
            this.commissions.deliver();//发出通知
        }
    }

    /***
     * 向后移动一个刻度
     */
    private void nextScale(){
        this.scale = this.scale.next();
        if(this.isNullObserver){//如果有观察者则不进行循环,继续加1
            this.pointerAddress = this.pointerAddress + 1;
        }else{
            this.pointerAddress = this.scale.getAddress();
        }
    }

    /***
     * 根据基数算出自己的数字
     * @param difference 相差
     * @return
     */
    public OperationResult getNumber(long difference){
        int size = this.getPointerType().getDial().size();
        OperationResult operationResult = new OperationResult();
        operationResult.setResult(difference / size);
        operationResult.setRemainder(difference % size);
        return operationResult;
    }

    /***
     * 获取指针当前所指向的地址
     * @return
     */
    public Integer getPointerAddress(){
        return this.pointerAddress;
    }

    public TimeTypeEnum getPointerType(){
        return pointerType;
    }

    public Integer getNumberOfTurns(){
        return numberOfTurns;
    }

    public boolean isMinimumScaleBool(){
        return minimumScaleBool;
    }

    /***
     * 跳到指定地址
     * @param toAddress 跳到指定地址
     */
    public void toAddress(int toAddress){
        this.pointerAddress = toAddress;
        while(!this.scale.getAddress().equals(this.pointerAddress)){
            this.scale = this.scale.next();
        }
        this.pointerAddress = this.scale.getAddress();
    }
}
