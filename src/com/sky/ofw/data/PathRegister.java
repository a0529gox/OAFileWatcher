package com.sky.ofw.data;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PathRegister {
	
	private static PathRegister self;
	private Map<Path, Path> registerMap = new HashMap<Path, Path>();

	private PathRegister() {
		// TODO Auto-generated constructor stub
	}

	public static PathRegister getInstance() {
		if (self == null) {
			self = new PathRegister();
		}
		return self;
	}
	
	public void register(Path client, Path server) {
		registerMap.put(client, server);
	}
	
	public Path get(Path client) {
		return registerMap.get(client);
	}
}
