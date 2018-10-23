package txj.server;

import org.springframework.boot.container.core.ServerApplication;
import org.springframework.boot.container.core.annotation.micro.TaskStorageServer;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/7/24
 * Application
 */
@TaskStorageServer
public class Application extends ServerApplication{
    @Override
    public String getServerName(){
        return "taskStorageServer";
    }

    public static void main(String[] args){
        new Application().run(Application.class, args);
//        new Application().run(Application.class, new String[]{"--server.port=8082"});
    }
}
