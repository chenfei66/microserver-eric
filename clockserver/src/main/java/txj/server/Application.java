package txj.server;

import org.springframework.boot.container.core.ServerApplication;
import org.springframework.boot.container.core.annotation.micro.ClockServer;
import org.springframework.boot.container.core.annotation.micro.MicroServer;

import java.lang.annotation.Annotation;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/7/24
 * txj.server.Application
 */
@ClockServer
@MicroServer
public class Application extends ServerApplication{
    @Override
    public String getServerName(){
        return "clockServer";
    }

    public static void main(String[] args){
        new Application().run(Application.class, args);
    }
}
