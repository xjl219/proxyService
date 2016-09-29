package com.xujl.util.proxyip.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ClientInfoStatus;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class ProxyIPool implements Serializable{


	public static final int CHECK_TYPE_STATUS=0x01;
		
	
	public static final int CHECK_TYPE_LENGTH_LT=0x02;
		
	
	public static final int CHECK_TYPE_CONTAINS=0x04;
	
	private static final int CHECK_INTERVAL = 30;
	private static Thread 
	checkThread=	new Thread(new Runnable() {
		
		@Override
		public void run() {
			HttpClient client = new HttpClient();
			while(true){
				System.out.println("start check....");
			try {
				checkThread.sleep(1000*CHECK_INTERVAL);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			 for (int i = 0; i < ips.size(); i++) {
				 	final int j=i;
				 	HostConfiguration hc = new HostConfiguration();
				 	Long ip = ips.get(i);
				 	String[] longToIP = longToIP(ip).split(":");
				 	
				 	hc.setProxy(longToIP[0], Integer.parseInt(longToIP[1]));	
				 	client.setHostConfiguration(hc);
					domainMap.forEach((k,v1)->{
						System.out.println(k+" "+ longToIP[0]);
						if(v1.ipMap.get( j))return;
						
						
						 GetMethod get = new GetMethod(v1.url);
						 try {
							int code = client.executeMethod(get);
							System.out.println(k+" "+ longToIP[0]+" code:"+code);
							switch (v1.type) {
							case CHECK_TYPE_LENGTH_LT:
								v1.ipMap.set(j, get.getResponseBodyAsString().length() > v1.check.hashCode());
								break;
							case CHECK_TYPE_CONTAINS:
								v1.ipMap.set(j, get.getResponseBodyAsString().contains(v1.check.toString()));
								break;

							default:
								v1.ipMap.set(j, code == 200);
								break;
							}
							
						} catch (HttpException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					});
					try {
						save();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		
				
			}
		}
	});
	private static void save() throws IOException{
		ObjectOutputStream oo = new  ObjectOutputStream(new FileOutputStream("Serializable"));
		oo.writeObject(new Object[]{ ips,domainMap});
		oo.flush();
		oo.close();
		
	}
	public static void load() throws Exception{
		ObjectInputStream oi =  new ObjectInputStream(new FileInputStream("Serializable"));
		Object[] oo=(Object[]) oi.readObject();		
		ips= (List<Long>) oo[0];
		domainMap= (Map<String, Domain>) oo[1];
	}
	
	public static void close(){
		checkThread.stop();
		try {
			save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static class Domain  implements Serializable{
		Domain(BitSet ipMap,
		 int type,
		 String url,
		 Object check){
			this.check=check;
			this.ipMap= ipMap;
			this.type=type;
			this.url=url;
		}
		final BitSet ipMap;
		final int type;
		final String url;
		final Object check;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static List<Long> ips =new CopyOnWriteArrayList <Long> ();
	private static Map<String, Domain> domainMap = new HashMap<String, Domain> ();

	public static void initIPs(String file) throws IOException, URISyntaxException {
		
		System.out.println(Paths.get(file));
		Files.lines(Paths.get(file)).
		forEach(l -> {
			addIP(l);
		});
	}
	public static void initIPs(Stream<String> lines) throws IOException, URISyntaxException {
		
		lines. forEach(l -> {
			addIP(l);
		});
	}
	public static void addIP(String ipstr){
		long ip = ipToLong(ipstr);
		if(!ips.contains(ip) && ips.add(ip))domainMap.forEach((k,v) -> v.ipMap.set(ips.size()-1));
		try {
			save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void romIP(String ipstr){
		long ip = ipToLong(ipstr);
		ips.indexOf(ip);
	}
	public static void main(String[] args) {
		
		/*try {
			initIPs("D:\\workspace\\load\\src\\ips.txt");
		} catch (IOException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		addDomain("baidu", 1, "http://www.baidu.com", null);
		String ip = getIP("baidu");
		System.out.println(ip);
		check();
		
		for (int i = 0; i <1000; i++) {
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 ip = getIP("baidu");
			System.out.println("main:"+ip);
			disableIP("baidu",ip);
		}*/
	}
	public static void addDomain(String domain, int type,  String url, Object check){
		BitSet ipl = new BitSet();
		ipl.set(0, ips.size());
		domainMap.put(domain, new Domain(ipl,type,url,check));
		if(Thread.State.NEW.equals(checkThread.getState()))checkThread.start();
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println(getListByDomain(domain));
	}
	//将127.0.0.1形式的IP地址转换成十进制整数，这里没有进行任何错误处理  
    public static long ipToLong(String strIp) {  
        long[] ip = new long[4];  
        //先找到IP地址字符串中.的位置  
        int position1 = strIp.indexOf(".");  
        int position2 = strIp.indexOf(".", position1 + 1);  
        int position3 = strIp.indexOf(".", position2 + 1);  
        //将每个.之间的字符串转换成整型  
        ip[0] = Long.parseLong(strIp.substring(0, position1));  
        ip[1] = Long.parseLong(strIp.substring(position1+1, position2));  
        ip[2] = Long.parseLong(strIp.substring(position2+1, position3));  
        ip[3] = Long.parseLong(strIp.substring(position3+1,strIp.indexOf(":"))); 
        int port = Integer.parseInt(strIp.substring(strIp.indexOf(":")+1));
        return (ip[0] << 40) + (ip[1] << 32) + (ip[2] << 24) + (ip[3] << 16)+port;  
    }  
      
    //将十进制整数形式转换成127.0.0.1形式的ip地址  
    public static String longToIP(long longIp) {  
        StringBuffer sb = new StringBuffer("");  
        //直接右移24位  
        sb.append(String.valueOf((longIp >>> 40)));  
        sb.append(".");  
        //将高8位置0，然后右移16位  
        sb.append(String.valueOf((longIp & 0x00FFFFFFFFFFl) >>> 32));  
        sb.append(".");  
        //将高16位置0，然后右移8位  
        sb.append(String.valueOf((longIp & 0x0000FFFFFFFFl) >>> 24));  
        sb.append(".");  
        //将高24位置0  
        sb.append(String.valueOf((longIp & 0x000000FFFFFFl) >>>16 )); 
        sb.append(":");  
        //将高24位置0  
        sb.append(String.valueOf((longIp & 0x00000000FFFFl)  )); 
        return sb.toString();  
    }  
	
	public static String getIP(String domain){
		int nextSetBit = domainMap.get(domain).ipMap.nextSetBit(0);
		
		return nextSetBit == -1 ? null : longToIP( ips.get(nextSetBit));
	}
	
	public static String disableIP(String domain,String ip){
		int indexOf = ips.indexOf(ipToLong(ip));
		BitSet ipMap = domainMap.get(domain).ipMap;
		ipMap.clear(indexOf);
		
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getIP(domain);
	}
	
	public static void check(){
		

	checkThread.start();
		
	}
	public static String getAllIP(){
		StringBuffer b= new StringBuffer("all ip list\n");
		ips.forEach(e -> {
			b.append(longToIP(e)+"\n");
		});
		return b.toString();
	}
	public static String getAllDomain(){
		return domainMap.keySet().toString();
	}
	public static String getListByDomain(String domain){
		StringBuffer b= new StringBuffer(domain+" ip list\n");
		BitSet ipMap = domainMap.get(domain).ipMap;
		final int[] i = {0};
		ips.forEach(e -> {
			b.append(longToIP(e)+" "+ipMap.get(i[0]++)+"\n");
		});
		b.append("Available :"+ipMap.cardinality());
		return b.toString();
	}
	public static int getAvailableByDomain(String domain){
		BitSet ipMap = domainMap.get(domain).ipMap;
		return ipMap.cardinality();
	}
}
