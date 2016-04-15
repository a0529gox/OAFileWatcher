package com.sky.ofw.util;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class NodeFadeUtil {

	public NodeFadeUtil() {
		// TODO Auto-generated constructor stub
	}

	public static void fadeIn(Node node, long milisec) {
		FadeTransition fadeTransition 
	        = new FadeTransition(Duration.millis(milisec), node);
	    fadeTransition.setFromValue(0);
	    fadeTransition.setToValue(1);
	    fadeTransition.play();
	}
	
	public static void fadeOut(Node node, long milisec) {
		FadeTransition fadeTransition 
	        = new FadeTransition(Duration.millis(milisec), node);
	    fadeTransition.setFromValue(1);
	    fadeTransition.setToValue(0);
	    fadeTransition.play();
	}
}
