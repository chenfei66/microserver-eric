package org.springframework.task.scheduling.actuator;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/9/29
 * PublicAPIConfiguration
 */
@Configuration
public class PublicAPIConfiguration{
    @LoadBalanced
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
