package org.springframework.task.scheduling.clock;

import lombok.Data;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/10
 * Scale 刻度,循环链表结构
 */
@Data
public class Scale{
    /***
     * 此刻度的地址
     */
    private Integer address;

    /***
     * 下一个刻度
     */
    private Scale nextScale;

    Scale(Integer address){
        this.address = address;
    }

    public boolean hasNext(){
        return this.nextScale != null;
    }

    public Scale next(){
        return this.getNextScale();
    }
}
