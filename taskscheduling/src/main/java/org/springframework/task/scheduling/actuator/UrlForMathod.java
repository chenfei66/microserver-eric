package org.springframework.task.scheduling.actuator;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.task.scheduling.utils.BaseUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/6
 * UrlForMathod
 */
@Component
public class UrlForMathod{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public static Map<String, MethodData> METHOD_DATA_MAP;

    public UrlForMathod(RequestMappingHandlerMapping requestMappingHandlerMapping){
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /***
     * 获取所有接口url与controller方法的对应关系
     * @return Map
     */
    @Bean
    private Map<String, MethodData> getMethodDataMapInit(){
        Map<String, MethodData> methodDataMap = new HashMap<>();
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for(Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()){
            MethodData methodData = new MethodData();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            methodData.setClassName(method.getMethod().getDeclaringClass().getName());
            MethodParameter[] methodParameters = method.getMethodParameters();
            List<String> paramClassList = new ArrayList<>();
            for(MethodParameter methodParameter : methodParameters){
                paramClassList.add(methodParameter.getParameterType().getTypeName());
            }
            methodData.setParamClass(paramClassList.toArray(new String[paramClassList.size()]));
            PatternsRequestCondition p = info.getPatternsCondition();
            for(String url : p.getPatterns()){
                url = BaseUtils.StringUtilsSon.removeLastMark(url, "/");
                methodData.setUrl(url);
            }
            methodData.setMethod(method.getMethod().getName());
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            String type = methodsCondition.toString();
            if(type != null && type.startsWith("[") && type.endsWith("]")){
                type = type.substring(1, type.length() - 1);
                methodData.setType(type);
            }
            methodDataMap.put(methodData.getUrl(), methodData);
        }
        logger.info("当前服务接口列表:" + JSONObject.toJSONString(methodDataMap));
        UrlForMathod.METHOD_DATA_MAP = methodDataMap;
        return methodDataMap;
    }

    public static MethodData getMethodDataToUrl(String url){
        return METHOD_DATA_MAP.get(url);
    }
}
