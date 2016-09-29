package com.xujl.util.proxyip.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;


public class ProxyApplication extends Application {

    public Set<Class<?>> getClasses() {
        Set<Class<?>> rrcs = new HashSet<Class<?>>();
        rrcs.add(ProxyResource.class);
        return rrcs;
    }
}