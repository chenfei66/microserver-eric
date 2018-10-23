package org.springframework.task.scheduling.clock;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/10
 * Dial 表盘(循环链表结构)
 */
public class Dial{
    /***
     * 此表盘长度
     */
    private Integer size;

    /***
     * 表盘刻度
     */
    private Scale firstScale;

    /***
     * 第一个刻度的值
     */
    private Integer first;

    /**
     * 初始化表盘
     * @param size  生成的表盘大小
     * @param first 第一个刻度的值,默认为0
     */
    public Dial(Integer size, Integer first){
        this.size = size;
        this.first = first;
        init();
    }

    /***
     * 初始化表盘
     * @param size 生成的表盘大小
     */
    public Dial(Integer size){
        this.size = size;
        this.first = 0;
        init();
    }

    /***
     * 根据Dial的属性生成循环队列(递归方式)
     * @param scale 递归对象(循环队列中的下一个实例)
     */
    private void init(Scale scale){
        if(scale == null){//如果为空则说明为第一个
            this.firstScale = new Scale(this.first);
            this.firstScale.setNextScale(new Scale(this.firstScale.getAddress() + 1));
            this.init(this.firstScale);
        }else{
            Integer address = scale.getAddress();
            Integer nextAddress;
            if(address == (this.size - 1)){//是否为最后一个如果是的话，则将下一个地址填为第一个
                scale.setNextScale(this.firstScale);
                return;
            }else{
                nextAddress = scale.getAddress() + 1;
            }
            Scale nextScale = new Scale(nextAddress);
            scale.setNextScale(nextScale);
            this.init(nextScale);
        }
    }



    /***
     * 根据Dial的属性生成循环队列(循环方式)
     */
    private void init(){
        Integer address = this.first;
        this.firstScale = new Scale(address);
        Scale tempScale = this.firstScale;
        for(int i = 0; i < this.size - 1; i++){
            address = address + 1;
            Scale nextScale = new Scale(address);
            tempScale.setNextScale(nextScale);
            tempScale = nextScale;
        }
        tempScale.setNextScale(this.firstScale);
    }

    public Integer getFirst(){
        return first;
    }

    public int size(){
        return this.size;
    }

    public Scale getFirstScale(){
        return this.firstScale;
    }

    public static void main(String[] args){
        Dial dial = new Dial(60);
        Scale scale = dial.getFirstScale();
        while(scale.hasNext()){
            try{
                Thread.sleep(50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(scale.getAddress());
            scale = scale.getNextScale();
        }
    }
}
