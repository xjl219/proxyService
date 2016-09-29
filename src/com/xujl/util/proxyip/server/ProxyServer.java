package com.xujl.util.proxyip.server;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;

public class ProxyServer {

    public static void main(String[] args) throws Exception {
        // create Component (as ever for Restlet)
        Component comp = new Component();
        int port = 8182;
//        if(args.length )
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
