package com.bang.im;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpClient {

	public static String send(String url,Map<String, String> head, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
			for(String key:head.keySet()){
				conn.setRequestProperty(key, head.get(key));
			}
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if(!"".equals(param)){
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Length", param.length()+"");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				// ��ȡURLConnection�����Ӧ�������
				out = new PrintWriter(conn.getOutputStream());
				// �����������
				out.print(param);
				// flush������Ļ���
				out.flush();
			}else {
				conn.setRequestMethod("GET");
				conn.connect();
			}
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			result = "{\"statusMsg\":\""+e.getMessage()+"\",\"statusCode\":\"999999\"}";
		}
		//ʹ��finally�����ر��������������
		finally{
			try{
				if(out!=null){
					out.close();
				}
				if(in!=null){
					in.close();
				}
			}
			catch(IOException ex){
				result = "{\"statusMsg\":\""+ex.getMessage()+"\",\"statusCode\":\"999999\"}";
			}
		}
		return result;
	}    
}
