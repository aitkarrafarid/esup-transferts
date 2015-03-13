package org.esupportail.transferts.web.scheduler;

import java.util.List;

import org.esupportail.transferts.domain.DomainService;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
 
public class BusinessManager {

	private DomainService domainService;

	public DomainService getDomainService() {
		return domainService;
	}

	public void setDomainService(DomainService domainService) {
		this.domainService = domainService;
	}

	public void runAction() {
//		System.out.println("In business manager, I call the business action");
//		System.out.println("getDomainService()===>"+getDomainService()+"<===");
		List<EtudiantRef> lEtu2 = getDomainService().getAllDemandesTransfertsByAnnee(2014, "A");
		System.out.println("lEtu2.size()===>"+lEtu2.size()+"<===");		
	}
}

