package com.xujl.util.proxyip.server;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;

import com.xujl.util.proxyip.core.ProxyIPool;

public class ProxyServer {

	public static void main(String[] args) throws Exception {
		int port = 8182;
		try {

			if (args.length == 1) {
				if ("-recover".equals(args[0]))
					ProxyIPool.load();
				else
					port = Integer.parseInt(args[0]);
			}
			if (args.length == 2) {
				if ("-f".equals(args[0])) {
					ProxyIPool.initIPs(args[1]);
				} else {
					port = Integer.parseInt(args[0]);
					ProxyIPool.load();
				}
			}
			if (args.length == 3) {
				port = Integer.parseInt(args[0]);
				ProxyIPool.initIPs(args[2]);
			}

		} catch (Exception e) {
			System.out.println("java -jar proxyService-0.5.1-SNAPSHOT.jar [port] [-f] [/home/ips.txt]");

			System.out.println("java -jar proxyService-0.5.1-SNAPSHOT.jar [port] [-recover");
			return;
		}
		System.out.println("java -jar proxyService-0.5.1-SNAPSHOT.jar [port] [-f] [/home/ips.txt]");

		System.out.println("java -jar proxyService-0.5.1-SNAPSHOT.jar [port] [-recover]");
	
		Component comp = new Component();
		// if(args.length )
		Server server = comp.getServers().add(Protocol.HTTP, port);

		// create JAX-RS runtime environment
		JaxRsApplication application = new JaxRsApplication(comp.getContext());

		// attach ApplicationConfig
		application.add(new ProxyApplication());

		// Attach the application to the component and start it
		comp.getDefaultHost().attach(application);
		comp.start();

		System.out.println("Server started on port " + server.getPort());
		System.out.println("Press key to stop server");
		System.in.read();
		System.out.println("Stopping server");
		comp.stop();
		System.out.println("Server stopped");
	}
}
