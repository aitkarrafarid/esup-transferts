package org.esupportail.transferts.web.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Date oldDate = new Date(); // oldDate == current time
	    Date newDate = new Date(oldDate.getTime() + TimeUnit.HOURS.toMillis(2)); // Adds 2 hours
		System.out.println("===>"+newDate.toString()+"<===");
		
		Calendar c = Calendar.getInstance(); 
		c.setTime(new Date());
		int year = c.get(Calendar.YEAR); //A vérifier!!!!
		System.out.println("===>"+year+"<===");
		
	}

}
