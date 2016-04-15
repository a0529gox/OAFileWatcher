package com.sky.ofw.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

import com.sky.ofw.util.CommonUtils;

public class OaPathGetter {

	public static final String propertiesName = "com.sky.ofw.properties.oa_path";
	private static ResourceBundle bundle = null;
	
	Path client;
	Path server;
	Path appData;

	public OaPathGetter(Path client, Path server, Path appData) {
		this.client = client;
		this.server = server;
		this.appData = appData;
	}
	
	public static void main(String[] args) {
		Path client = new File("D:\\OA\\workspace").toPath();
		Path server = new File("C:\\IBM\\SDP\\runtimes\\base_v7\\profiles\\AppSrv01\\installedApps\\Cellt").toPath();
		Path appData = new File("C:\\home\\wasadm\\tashome\\share\\appdata").toPath();
		OaPathGetter self = new OaPathGetter(client, server, appData);
		
		Map<Path, Path> paths = self.getPaths("qic");
		
    	for (Path clientPath : paths.keySet()) {
    		Path serverPath = paths.get(clientPath);
    		
    		System.out.println(clientPath);
    		System.out.println(serverPath);
    	}
	}

	public Map<Path, Path> getPaths(String q16) {
		Map<Path, Path> paths = new HashMap<Path, Path>();
		
		String clientAweb = get("client.aweb", q16);
		String clientSweb = get("client.sweb", q16);
		String clientJrxml = get("client.jrxml", q16);
		String serverAweb = get("server.aweb", q16);
		String serverSweb = get("server.sweb", q16);
		String serverJrxml = get("server.jrxml", q16);
		
		paths.put(CommonUtils.getPath(clientAweb), CommonUtils.getPath(serverAweb));
		paths.put(CommonUtils.getPath(clientSweb), CommonUtils.getPath(serverSweb));
		paths.put(CommonUtils.getPath(clientJrxml), CommonUtils.getPath(serverJrxml));
		
		return paths;
	}

	private static ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(propertiesName, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
		}
		return bundle;
	}
	
	private String get(String key, String q16) {
		String msg =  getBundle().getString(key);
		try {
			msg = new String(msg.getBytes("iso8859-1"), "utf8");
			msg = format(msg, q16);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return msg;
	}
	
	private String format(String msg, String q16) {
		return msg.replaceAll("\\{client\\}", Matcher.quoteReplacement(client.toString()))
				.replaceAll("\\{server\\}", Matcher.quoteReplacement(server.toString()))
				.replaceAll("\\{appData\\}", Matcher.quoteReplacement(appData.toString()))
				.replaceAll("\\{q16\\}", q16);
	}
}
