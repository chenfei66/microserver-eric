package txj.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/7/31
 * Application
 */
@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class Application{
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}