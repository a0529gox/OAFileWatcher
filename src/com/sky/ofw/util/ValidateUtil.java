package com.sky.ofw.util;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import javafx.scene.control.CheckBox;

public class ValidateUtil {

	public ValidateUtil() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isValidDirPath(String path) {
		return path != null && new File(path).isDirectory();
	}

	public static boolean isValidDirPath(Path path) {
		return path != null && path.toFile().isDirectory();
	}
	
	public static boolean isSelectedAtLeastOne(List<CheckBox> cbs) {
		for (CheckBox cb : cbs) {
			if (cb.isSelected()) {
				return true;
			}
		}
		return false;
	}
}
