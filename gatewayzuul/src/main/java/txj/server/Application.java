package txj.server;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/7/13
 * Application
 */
@EnableZuulProxy
@SpringCloudApplication
public class Application{
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
