package com.xujl.util.proxyip.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;

import com.xujl.util.proxyip.core.ProxyIPool;

import org.restlet.ext.fileupload.RestletFileUpload;

@Path("/")
public class ProxyResource {
	 @POST
	 @Path("upload")
	 public Representation upload(Representation entity){
		 BufferedReader m = m( entity);
		 try {
			ProxyIPool.initIPs(m.lines());
		} catch (Exception e) {
			e.printStackTrace();
			return new StringRepresentation(e.getLocalizedMessage(), MediaType.TEXT_PLAIN);
		} 
		return new StringRepresentation(ProxyIPool.getAllIP(), MediaType.TEXT_PLAIN);
	 }
	private BufferedReader m(Representation entity) {
		Representation result = null;
		  try {
			
			  StringRepresentation ll;
		    if (entity != null) {
		        if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
		            // 1/ Create a factory for disk-based file items
		            DiskFileItemFactory factory = new DiskFileItemFactory();
		            factory.setSizeThreshold(1000240);

		            // 2/ Create a new file upload handler based on the Restlet
		            // FileUpload extension that will parse Restlet requests and
		            // generates FileItems.
		            RestletFileUpload upload = new RestletFileUpload(factory);

		            // 3/ Request is parsed by the handler which generates a
		            // list of FileItems
		            FileItemIterator fileIterator = upload.getItemIterator(entity);

		            if (fileIterator.hasNext() ) {
		                FileItemStream fi = fileIterator.next();
		                    // consume the stream immediately, otherwise the stream
		                    // will be closed.
		                    StringBuilder sb = new StringBuilder("media type: ");
		                    sb.append(fi.getContentType()).append("\n");
		                    sb.append("file name : ");
		                    sb.append(fi.getName()).append("\n");
		                    BufferedReader br = new BufferedReader( new InputStreamReader(fi.openStream()));
		                    return br;
		            }
		        } else {
		            // POST request with no entity.
		        }
		    }
			} catch (Exception e) {
				// TODO: handle exception
			}
		 return null;
	}
    @GET
    @Path("init")
    @Produces("text/html")
    public String init(@QueryParam("p") String path) {
    	System.out.println("<!doctype html>"+
    			"<html lang='en'> <head> <title>init</title> </head> <body>"+
    			  "<form method='POST' enctype='multipart/form-data' action='/upload'>"+
    			 "  File to upload: <input type='file' name='upfile'><br/>"+
    			  " <input type='submit' value='Press'> to upload the file!"+
    			"</form>"+
    			 "</body> </html>");
    	return "<!doctype html>"+
    			"<html lang='en'> <head> <title>init</title> </head> <body>"+
    			  "<form method='POST' enctype='multipart/form-data' action='upload'>"+
    			 "  File to upload: <input type='file' name='file'><br/>"+
    			  " <input type='submit' value='Press'> to upload the file!"+
    			"</form>"+
    			 "</body> </html>";
    	/*try {
			ProxyIPool.initIPs(path);
			 return "ok";
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
    	*/
    }
    @GET
    @Path("ipstat/{id}")
    @Produces("text/plain")
    public String getListByDomain(@PathParam("id") String id) {
        return ProxyIPool.getListByDomain(id);
    }
    @GET
    @Path("ipavailable/{id}")
    @Produces("text/plain")
    public String getAvailableByDomain(@PathParam("id") String id) {
        return "available:"+ProxyIPool.getAvailableByDomain(id);
    }
    @GET
    @Path("getip/{id}")
    public String getIP(@PathParam("id") String id) {
        return ProxyIPool.getIP(id);
    }
    @GET
    @Path("disable")
    public String disable(@QueryParam("d")String domain,@QueryParam("ip")String ip){
    	
    	return ProxyIPool.disableIP(domain, ip);
    }
 
    @GET
    @Path("addip/{ip}")
    @Produces("text/plain")
    public String addIP(@PathParam("ip") String ip){
    	ProxyIPool.addIP(ip);
    	return "added "+ip;
    }
    
    @GET
    @Path("add/{domain}")
    @Produces("text/plain")
    public String addDomain(@PathParam("domain") String domain,@QueryParam("t")int type,@QueryParam("u")String url,@QueryParam("c")String check){
    	
    	ProxyIPool.addDomain(domain, type, url, check);
    	return String.format("domain:%s, type:%s, url:%s, check:%s",domain, type, url, check);
    }
    @GET
    @Path("reg/{domain}")
    @Produces("text/plain")
    public String register(@PathParam("domain") String domain,@QueryParam("t")int type,@QueryParam("u")String url,@QueryParam("c")String check){
    	
    	ProxyIPool.addDomain(domain, type, url, check);
    	return String.format("registered domain:%s, type:%s, url:%s, check:%s",domain, type, url, check);
    }
    @GET
    @Path("recover")
    @Produces("text/plain")
    public String recover() throws Exception{
    	
    	ProxyIPool.load();
    	return ProxyIPool.getAllIP();
    }
    @GET
    @Path("alldomain")
    @Produces("text/plain")
    public String allDomain(){
    	
    	return ProxyIPool.getAllDomain();
    }
    @GET
    @Path("allip")
    @Produces("text/plain")
    public String allIP(){
    	
    	return ProxyIPool.getAllIP();
    }
}