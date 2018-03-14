package com.baibei.accountservice.paycenter.utill;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

public class HttpClientUtils {
	
	@SuppressWarnings("deprecation")
	public static String doPost(String url, Map<String, String> headParams, String content) throws Exception{
		HttpClient httpclient = HttpsClientSSL.newHttpsClient();
		try{
			HttpPost post = new HttpPost(url);
			if(!CollectionUtils.isEmpty(headParams)){
				Set<Map.Entry<String, String>> headParamsSet = headParams.entrySet();
				for(Map.Entry<String, String> entry : headParamsSet){
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}
			StringEntity entity = new StringEntity(content, "UTF-8"); 
			post.setEntity(entity);
			return invoke(httpclient, post);
		}catch(Exception e){
			throw e;
		}finally{
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String doPost(String url, Map<String, String> headParams, Map<String, String> params) throws Exception{
		HttpClient httpclient = HttpsClientSSL.newHttpsClient();
		try{
			HttpPost post = postForm(url, params);
			if(!CollectionUtils.isEmpty(headParams)){
				Set<Map.Entry<String, String>> headParamsSet = headParams.entrySet();
				for(Map.Entry<String, String> entry : headParamsSet){
					post.setHeader(entry.getKey(), entry.getValue());
				}
			}
			return invoke(httpclient, post);
		}catch(Exception e){
			throw e;
		}finally{
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	
	   @SuppressWarnings("deprecation")
	    public static String doGet(String url, Map<String, String> headParams, Map<String, String> params) throws Exception{
	        HttpClient httpclient = HttpsClientSSL.newHttpsClient();
	        try{
	            HttpGet get = new HttpGet(url);
	            if(!CollectionUtils.isEmpty(headParams)){
	                Set<Map.Entry<String, String>> headParamsSet = headParams.entrySet();
	                for(Map.Entry<String, String> entry : headParamsSet){
	                    get.setHeader(entry.getKey(), entry.getValue());
	                }
	            }
	            return invoke(httpclient, get);
	        }catch(Exception e){
	            throw e;
	        }finally{
	            httpclient.getConnectionManager().shutdown();
	        }
	    }
	
	
	@SuppressWarnings("deprecation")
	public static String doGet(String url) throws Exception{
		HttpClient httpclient = HttpsClientSSL.newHttpsClient();
		try{
			HttpGet get = new HttpGet(url);
			return invoke(httpclient, get);
		}catch(Exception e){
			throw e;
		}finally{
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	private static String invoke(HttpClient httpclient, HttpUriRequest httpost) throws Exception{
		HttpResponse response = sendRequest(httpclient, httpost);
		return paseResponse(response);
	}

	private static String paseResponse(HttpResponse response) throws Exception{
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity);
	}

	private static HttpResponse sendRequest(HttpClient httpclient, HttpUriRequest httpost) throws Exception{
		return httpclient.execute(httpost);
	}

	@SuppressWarnings("deprecation")
	private static HttpPost postForm(String url, Map<String, String> params){
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList <NameValuePair>();
		Set<String> keySet = params.keySet();
		for(String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpost;
	}
	
	public static void main(String[] args) throws Exception{
		String url = "http://112.74.89.192:8080/api/userinfo/freeze";
		String content = "{\"userId\": \"130\",\"freeze\": 1}";
		Map<String, String> headParams = new HashMap<String, String>();
		headParams.put("APP_ID", "bybPortal");
		headParams.put("APP_KEY", "0c7109057cb749ffb1341237c5f1d6b3");
		headParams.put("Content-Type", "application/json;charset=utf-8");
		System.out.println("result:" + HttpClientUtils.doPost(url, headParams, content));
	}
}
