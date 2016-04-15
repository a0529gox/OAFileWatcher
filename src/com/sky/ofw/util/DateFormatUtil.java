package com.sky.ofw.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {

	public static void main(String[] args) {
		System.out.println(toFullTime(new Date()));

	}

	public static String toFullTime(Date date) {
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		
		return formatter.format(date);
	}
}
