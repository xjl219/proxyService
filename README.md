# proxyService
register a proxy ip service for specified domain and check rule of  available ip
The a proxy ip service for crawler。

It can auto check IP is available。

specified domain and check rule of  available ip

http://localhost:8182/getip/domain returns a ip of available。

mvn assembly:single 

java -jar proxyService-0.5.1-SNAPSHOT.jar

open Browser input：
 http://localhost:8182/init
 
 upload a IP file。
 
 exmaple
 
192.168.11.129:8080
192.168.11.130:8090
192.168.11.131:8010
192.168.11.132:8000
192.168.11.133:80
 
look all ip

http://localhost:8182/allip

regester:
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
	 
domain:www.baidu.com, type:1, url:http://www.baidu.com, check:200

http://localhost:8182/register/www.baidu.com?u=http://www.baidu.com&t=1&c=200
 domain ：add a proxy domain . exmaple: www.baidu.com
 u is url: http://www.baidu.com
 
 
 
 returns  available ip
 http://localhost:8182/getip/www.baidu.com
 
 
 disable a ip and return a  available ip
 
 http://localhost:8182/disable?d=/www.baidu.com&ip=192.168.11.106:80
 
