package txj.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/***
 * @author 王强 Email : 
 * @version 创建时间：2018/8/2
 * MyPropertiesHandler 解决中文乱码的问题
 */
public class MyPropertiesHandler implements PropertySourceLoader{

    private static final Logger logger = LoggerFactory.getLogger(MyPropertiesHandler.class);

    @Override
    public String[] getFileExtensions(){
        return new String[]{"properties", "xml"};
    }

    @Override
    public PropertySource<?> load(String name, Resource resource, String profile) throws IOException{
        if(profile == null){
            Properties properties = getProperties(resource);
            if(!properties.isEmpty()){
                PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource(name, properties);
                return propertiesPropertySource;
            }
        }
        return null;
    }

    private Properties getProperties(Resource resource){
        Properties properties = new Properties();
        InputStream inputStream = null;
        try{
            inputStream = resource.getInputStream();
            properties.load(new InputStreamReader(inputStream, "utf-8"));
            inputStream.close();
        }catch(IOException e){
            logger.error("load inputstream failure...", e);
        }finally{
            if(inputStream != null){
                try{
                    inputStream.close();
                }catch(IOException e){
                    logger.error("close IO failure ....", e);
                }
            }
        }
        return properties;
    }
}
