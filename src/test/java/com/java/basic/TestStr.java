/**
 * All rights Reserved, Designed By Letv
 *
 * @Title: TestStr.java
 * @Package com.java.basic
 * @Description: TODO(用一句话描述该文件做什么)
 * @author: liqiuwei
 * @date: 2016年1月11日 上午11:06:07
 * @version
 */
package com.java.basic;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;

/**
 * @author liqiuwei
 * @create time:2016年1月11日上午11:06:07
 * @Description:TODO(这里用一句话描述这个类的作用)
 */
public class TestStr {
    final static int len_unit = 1024;
    public static Random random = new Random();
    public static Integer maxInt = 999999;

    public static void main(String[] args) {
        /*byte[] b1k=new byte[len_unit];
        String str1k=new String(b1k);
		//byte int short 默认值为0
		for(int i=0;i<len_unit;i++){
			System.out.println("##"+(str1k.charAt(i)==0));
		}
		System.out.println("###"+str1k.length());
		try {
			System.out.println("###str1k="+str1k.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("1FF8EAC60E08F1663B7DCA500361F50F".length());
		for(int i=0;i<1000;i++){  
		System.out.println(String.format("%0"+6+"d",random.nextInt(maxInt)));
		}*/
    /* String[] atr=new String[]{"vf_uuid","COOKIE_SESSION_ID"};
     String str2 = String.format("%s=&%s=", atr);
     System.out.println("str2##"+str2);
     StringBuilder sbResult = new StringBuilder();  
     System.out.println(sbResult.toString()); 
     String str="erereee";
    str = str.replaceAll("e", "a");*/
        String str = "com/3/@6";

        System.out.println(Arrays.toString("".split(",")));


        System.out.println(str.replaceAll("[com/|/|@]", ""));

        String folder = System.getProperty("java.io.tmpdir");
        System.out.println(folder);

        String str1 = "1";

        String[] strs = StringUtils.split(str1, ",");

        System.out.println(strs.length);
        testStringJoin();
    }
//优雅连接字符串
   public  static void testStringJoin(){
       StringJoiner joiner=new StringJoiner(",","{","}");
       joiner.add("1").add("2").add("3");
       System.out.println("Joiner::"+joiner);
       StringJoiner joiner2=new StringJoiner(",","【","】");
       joiner2.add("a").add("b").add("c");
       joiner.merge(joiner2);
       System.out.println("joiner2::"+joiner);
       String join = String.join("-", "1", "3");

       System.out.println(join);

   }
}
