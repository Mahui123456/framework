import com.krb.guaranty.common.utils.IFCJSONUtil;
import com.krb.guaranty.common.utils.RestTemplateJSONUtil;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;

/**
 * @author owell
 * @date 2020/9/10 10:48
 */
@Component
public class TestRestTemplateJsonUtil{

    public static void main(String[] args) {
        RestTemplateJSONUtil uu = new RestTemplateJSONUtil();
        uu.setRestTemplate(new RestTemplate());;
        uu.getRestTemplate().getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        final HashMap<Object, Object> map = new HashMap<>();
        map.put("long",12398719273918723L);
        map.put("date",new Date());
        map.put("string","123");
        map.put("int",123);
        final String json = uu.getRequestBodyJSON(map);
        System.out.println(json);
        System.out.println(IFCJSONUtil.toJSONString(map));
    }
}
