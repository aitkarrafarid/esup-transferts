package org.esupportail.transferts.web.scheduler;

public class QuartzJob {
	private BusinessManager businessManager;

	public void execute(){
//		System.out.println("In quartz job, I call the business manager") ;
		businessManager.runAction();
	}
 
	public void setBusinessManager(BusinessManager businessManager) {
		this.businessManager = businessManager;
	}
}

