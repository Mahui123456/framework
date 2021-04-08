import com.krb.guaranty.common.utils.AESUtil;
import org.junit.Test;

public class TestAES {

    @Test
    public void test() {
        String key = "guaranty-krn";
        String value = "马辉";
        String encode = AESUtil.encrypt(value, key);
        System.out.println("根据输入的规则" + key + "明文:" + value + ",加密后的密文是:" + encode);
        System.out.println("根据输入的规则" + key + "解密后的明文是:" + AESUtil.decrypt(encode, key));

    }

}