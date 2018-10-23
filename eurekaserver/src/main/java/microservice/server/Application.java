package microservice.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/6/26
 * 架构基础服务, eureka server.
 */
@EnableEurekaServer
@SpringBootApplication
public class Application{
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
