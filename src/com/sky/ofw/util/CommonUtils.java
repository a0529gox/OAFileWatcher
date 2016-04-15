package com.sky.ofw.util;

import java.io.File;
import java.nio.file.Path;

public class CommonUtils {

	public CommonUtils() {
		// TODO Auto-generated constructor stub
	}

	public static Path getPath(String path) {
		return new File(path).toPath();
	}
}
