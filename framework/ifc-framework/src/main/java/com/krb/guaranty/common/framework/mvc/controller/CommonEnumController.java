package com.krb.guaranty.common.framework.mvc.controller;

import com.krb.guaranty.common.framework.exception.ValidException;
import com.krb.guaranty.common.framework.mvc.controller.support.Message;
import com.krb.guaranty.common.framework.mvc.enums.OursBaseEnum;
import com.krb.guaranty.common.framework.mvc.enums.OursBaseStringEnum;
import com.krb.guaranty.common.framework.mvc.utils.OursCoreAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author owell
 * @date 2019/7/22 14:23
 */
@Api(value = "框架:枚举", tags = "框架:枚举")
@RestController
@RequestMapping("/common/enums")
public class CommonEnumController {


    @Autowired
    private GuarantyWebShowProperties guarantyWebShowProperties;

    @ApiOperation("获取枚举内容,返回值是 key(string),value(string)")
    @RequestMapping(value = "/{enumName}",method = {RequestMethod.GET,RequestMethod.POST})
    public Message<Map<String,String>> get(
            @ApiParam("枚举名称,例如XXXEnum或者EnumXXX")
            @PathVariable String enumName,
            @ApiParam("需要的类型默认: int , string")
            @RequestParam(required = false) String type){
        OursCoreAssert.notBlank(enumName,"枚举名称不能为空");
        Map<String,String> map = null;

        if(enumName.startsWith("com")){
            map = getEnumStringMap(enumName,type);
        }else{
            for (String scanEnumPackage : guarantyWebShowProperties.getScanEnumPackages()) {
                map = getEnumStringMap(scanEnumPackage.concat(".").concat(enumName),type);
                if(map != null){
                    break;
                }
            }
        }

        if(map == null){
            throw new ValidException("未知的枚举名称:"+enumName);
        }
        return Message.success(map);
    }

    private Map<String, String> getEnumStringMap(String enumName,String type){
        Map<String, String> map = null;
        Class clazz = null;
        try {
            clazz = Class.forName(enumName);
        } catch (ClassNotFoundException e) {
        }
        if(clazz != null && clazz.isEnum() ){
            if(OursBaseEnum.class.isAssignableFrom(clazz)){
                if(type == null || !type.equals("string")){
                    map = new HashMap<>();
                    Map<Integer,String> mm = OursBaseEnum.toMap(clazz);
                    for (Map.Entry<Integer, String> entry : mm.entrySet()) {
                        map.put(entry.getKey().toString(),entry.getValue());
                    }
                }
            }
            if(OursBaseStringEnum.class.isAssignableFrom(clazz)){
                if(type == null || type.equals("string")){
                    map = OursBaseStringEnum.toStringMap(clazz);
                }
            }
        }
        return map;
    }
}