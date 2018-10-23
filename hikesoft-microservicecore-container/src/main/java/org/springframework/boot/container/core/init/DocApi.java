package org.springframework.boot.container.core.init;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.container.core.constant.Constant;

import java.io.*;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/5/18
 * DocApi
 */
public class DocApi{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public void createApi(String path){
        try{
            String execStr = String.format("apidoc -i %s -o %s" + Constant.API_STATIC_FILE_RELATICE_PATH, path, path);
            logger.info("execStr = " + execStr);
            Process p = Runtime.getRuntime().exec(execStr);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}