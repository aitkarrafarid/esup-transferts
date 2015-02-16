package org.esupportail.transferts.web.controllers;

import java.util.List;

import org.esupportail.transferts.dao.DaoService;
import org.esupportail.transferts.dao.JPADaoServiceImpl;
import org.esupportail.transferts.domain.DomainService;
import org.esupportail.transferts.domain.DomainServiceApogeeImpl;
import org.esupportail.transferts.domain.DomainServiceImpl;
import org.esupportail.transferts.domain.DomainServiceScolarite;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.User;
import org.esupportail.transferts.services.auth.Authenticator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:META-INF/testApplicationContext.xml")
public class AdministrationControllerTest  {

	@Autowired
	DomainService domainService; 
	
	@Autowired
	DomainServiceScolarite domainServiceScolarite;
	
//	@Autowired
//	Authenticator authenticator;
	
	@Test
	public void testGetAllDemandesTransferts() 
	{
		System.out.println("getDomainService()===>"+getDomainService()+"<===");

		List<EtudiantRef> lEtu = getDomainService().getAllDemandesTransfertsByAnnee(2014, "D");
		for (EtudiantRef etu : lEtu) 
		{
			System.out.println(etu.getNomPatronymique());
		}
	}

	@Test
	public void testGetInfosEtudiant()
	{
		System.out.println("getDomainServiceScolarite().getEtablissementByDepartement(059)===>"+getDomainServiceScolarite().getEtablissementByDepartement("059")+"<===");
	}
	
//	@Test
//	public void testGetUser()
//	{
//		User user = null;
//		try {
//			user = authenticator.getUser();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("user===>"+user+"<===");
//	}
	
	public DomainService getDomainService() {
		return domainService;
	}

	public void setDomainService(DomainService domainService) {
		this.domainService = domainService;
	}

	public DomainServiceScolarite getDomainServiceScolarite() {
		return domainServiceScolarite;
	}

	public void setDomainServiceScolarite(
			DomainServiceScolarite domainServiceScolarite) {
		this.domainServiceScolarite = domainServiceScolarite;
	}

//	public Authenticator getAuthenticator() {
//		return authenticator;
//	}
//
//	public void setAuthenticator(Authenticator authenticator) {
//		this.authenticator = authenticator;
//	}
}
