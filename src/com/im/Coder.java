package com.im;


public class Coder {

	public static String unicodeToGB(String utfString){  
	    StringBuilder sb = new StringBuilder();  
	    int i = -1;  
	    int pos = 0;  
	      
	    while((i=utfString.indexOf("\\u", pos)) != -1){  
	        sb.append(utfString.substring(pos, i));  
	        if(i+5 < utfString.length()){  
	            pos = i+6;  
	            sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));  
	        }
	        if(pos<utfString.length()-2){
		        if(!"\\u".equals(utfString.substring(pos, pos+2))){
		        	int index = utfString.indexOf("\\u", pos);
		        	if(index!=-1){
		        		sb.append(utfString.substring(pos, index));
		        		pos = index;
		        	}
		        	else
		        		sb.append(utfString.substring(pos, utfString.length()));
	 	        }
	        }
	        else
	        	sb.append(utfString.substring(pos, utfString.length()));
	    }
	    return sb.toString().replace("/u", "\\u");  
	}  

	public static String toUnicodeString(String s) {
		s = s.replace("\\u", "/u");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			}
			else {
				sb.append("\\u"+Integer.toHexString(c));
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String str = "aaaaaa空间撒谎打开aaskjdhg\\u0212挥洒的/u";
		System.out.println(toUnicodeString(str));
		System.out.println(unicodeToGB(toUnicodeString(str)));
	}
}
