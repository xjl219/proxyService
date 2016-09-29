package com.xujl.util.proxyip.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class ProxyClient  {
	final static HttpClient client = new HttpClient();

	public static final int CHECK_TYPE_STATUS=0x01;
		
	
	public static final int CHECK_TYPE_LENGTH_LT=0x02;
		
	
	public static final int CHECK_TYPE_CONTAINS=0x04;
	
	private static final String GETIP="/getip/";
	
	private static final String DISABLEIP="/disable?d=%s&ip=%s";
	
	private static final String AVAILABLE="/ipavailable/";
	
	private static final String REGISTER="/reg/%s?t=%s&u=%s&c=%s";
	
//	/add/aaa.a?u=http://www.baidu.com&t=1&c=200
	
	private String host,curentDomain;
	
	/**
	 * 
	 * @param host proxy ip server address
	 * exmaple http://192.168.1.8:8182
	 */
	public ProxyClient(String host){
		this.host= host;
	}
	/**
	 * 	 * 
	 * register a proxy ip service for specified domain and check rule of  available ip
	 * 
	 * @param host proxy ip server address
	 * Example http://192.168.1.8:8182
	 * @param domain 
	 * Example baidu.com
	 * @param type Check the way 
	 * 	1: state is 200 
	 *  2: Content length less then specified number
	 *  4: Content cantains specified key
	 * @param url use it to check
	 * @param rule rule of check it  
	 * @throws Exception
	 */

	public ProxyClient(String host,String domain, int type,  String url, Object rule) throws Exception{
		this.host= host;
		curentDomain=domain;
		register(domain, type, url, rule);
	}
	public String getIP() throws HttpException, IOException{
		 return getIP(curentDomain);
	}
	
	public String getIP(String domain) throws HttpException, IOException{
		 return exe( GETIP +domain );
	}

	public  int getAvailableByDomain(String domain)throws Exception{
		return Integer.parseInt(exe(AVAILABLE+domain));
	}
	public  int getAvailableByDomain()throws Exception{
		return getAvailableByDomain(curentDomain);
	}
	private String exe(String query) throws IOException, HttpException {
		GetMethod get = new GetMethod(host + query);
		 int code = client.executeMethod(get);
		 if(200 == code)
			 return get.getResponseBodyAsString();
		 else return "";
	}
	public String disableIP(String domain,String ip)throws Exception{
		 return exe(String.format(DISABLEIP, domain,ip));
	 }
	public String disableIP(String ip)throws Exception{
		 return disableIP(curentDomain,ip);
	 }
	
	public String register(String domain, int type,  String url, Object rule) throws Exception{
		 return exe(String.format(REGISTER,domain, type, url, rule));
	}
	
	public static void main(String[] args) {
		ProxyClient pc = new ProxyClient("http://localhost:8182");
		try {
			System.out.println(pc.getIP("aaa.a"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
