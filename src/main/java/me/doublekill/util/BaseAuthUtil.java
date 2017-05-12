package me.doublekill.util;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by wangjunling on 2017/5/9.
 */
public class BaseAuthUtil
{
    public static String encode(String namePassword) throws Exception
    {
        byte[] bytes2 = Base64.encodeBase64(namePassword.getBytes("utf-8"));
        return new String(bytes2, "utf-8");
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("Basic " + encode("username:password"));
    }
}