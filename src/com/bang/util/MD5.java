package com.bang.util;

import java.security.MessageDigest;

/** 
* ����������
* ��˾������Ϊ
* ���ߣ��Ա��� 
* ���ڣ�Jun 8, 2011 10:32:07 AM 
* ��˵�� 
*/ 
public class MD5 {
	/*16λ���ܴ�*/
	public static String getMd5Str(String macaddress){
		String result=null;
		//String str=null;
		try {
			   MessageDigest md = MessageDigest.getInstance("MD5");
			   md.update(macaddress.getBytes());
			   byte b[] = md.digest();
			   int i;
			   StringBuffer buf = new StringBuffer("");
			   for (int offset = 0; offset < b.length; offset++) {
			    i = b[offset];
			    if (i < 0)
			     i += 256;
			    if (i < 16)
			     buf.append("0");
			    buf.append(Integer.toHexString(i));
			   }
			  // str = buf.toString();
			   result=buf.toString().substring(8,24);
			  } catch (Exception e) {
				  System.err.println("IOException " + e.getMessage());
			  }
		return  result;
	}
	/*32λ���ܴ�*/
	public static String getMd5Str32(String macaddress){
		String result=null;
		String str=null;
		try {
			   MessageDigest md = MessageDigest.getInstance("MD5");
			   md.update(macaddress.getBytes());
			   byte b[] = md.digest();
			   int i;
			   StringBuffer buf = new StringBuffer("");
			   for (int offset = 0; offset < b.length; offset++) {
			    i = b[offset];
			    if (i < 0)
			     i += 256;
			    if (i < 16)
			     buf.append("0");
			    buf.append(Integer.toHexString(i));
			   }
			   str = buf.toString();
			   result = str;
			  } catch (Exception e) {
				  System.err.println("IOException " + e.getMessage());
			  }
		return  result;
	}
	/*��������*/
	public static byte[] getMd5ByteArray(String macaddress){
	//	String result=null;
	//	String str=null;
		byte b[] = null;
		try {
			   MessageDigest md = MessageDigest.getInstance("MD5");
			   md.update(macaddress.getBytes());
			   b = md.digest();
			   /*for(int m=0;m<b.length;m++){
				   System.out.println("test="+b[m]);
			   }
			   int i;
			   StringBuffer buf = new StringBuffer("");
			   for (int offset = 0; offset < b.length; offset++) {
			    i = b[offset];
			    if (i < 0)
			     i += 256;
			    if (i < 16)
			     buf.append("0");
			    buf.append(Integer.toHexString(i));
			   }
			   str = buf.toString();
			   result = str;*/
			  } catch (Exception e) {
				  System.err.println("IOException " + e.getMessage());
			  }
		return  b;
	}
}
