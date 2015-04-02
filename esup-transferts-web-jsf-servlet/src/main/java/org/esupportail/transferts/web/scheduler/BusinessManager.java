package org.esupportail.transferts.web.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.esupportail.commons.services.i18n.ResourceBundleMessageSourceI18nServiceImpl;
import org.esupportail.commons.services.smtp.SmtpService;
import org.esupportail.transferts.domain.DomainService;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.utils.GestionDate;

public class BusinessManager {

	private DomainService domainService;

	private ResourceBundleMessageSourceI18nServiceImpl i18nService;

	private SmtpService smtpService;

	public void runAction() 
	{
		List<EtudiantRef> lEtu2 = getDomainService().getAllDemandesTransfertsByAnnee(2015, "A");
		if(lEtu2!=null)
		{
			List<EtudiantRef> listeEtudiantRefAlertSilenceVautAccord = new ArrayList<EtudiantRef>();
			List<EtudiantRef> listeEtudiantRefAlertDepassementSilenceVautAccord = new ArrayList<EtudiantRef>();
			Date now = new Date();
			System.out.println("lEtu2.size()===>"+lEtu2.size()+"<===");
			for(EtudiantRef etu : lEtu2)
			{
				if(etu.getTransferts().getTemoinTransfertValide()!=2)
				{
					etu.setAlertDepassementSilenceVautAccord(GestionDate.ajouterMois(etu.getTransferts().getDateDemandeTransfert(), 2));
					etu.setAlertSilenceVautAccord(GestionDate.ajouterJour(etu.getTransferts().getDateDemandeTransfert(), 42));	
					if(now.after(etu.getAlertSilenceVautAccord()) && now.before(etu.getAlertDepassementSilenceVautAccord()))
					{
						//						System.out.println("===>#################################################################################################################<===");
						//						System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
						//						System.out.println("etu.getNomPatronymique()===>"+etu.getNomPatronymique()+"<===");	
						//						System.out.println("etu.getPrenom1()===>"+etu.getPrenom1()+"<===");	
						//						System.out.println("etu.getTransferts().getDateDemandeTransfert()===>"+etu.getTransferts().getDateDemandeTransfert()+"<===");
						//						System.out.println("etu.getAlertSilenceVautAccord()===>"+etu.getAlertSilenceVautAccord()+"<===");
						//						System.out.println("etu.getAlertDepassementSilenceVautAccord()===>"+etu.getAlertDepassementSilenceVautAccord()+"<===");
						//						System.out.println("now===>"+now+"<===");
						//						System.out.println("===>#################################################################################################################<===");
						listeEtudiantRefAlertSilenceVautAccord.add(etu);
					}
					if(now.after(etu.getAlertDepassementSilenceVautAccord()))
					{
						//						System.out.println("===>#################################################################################################################<===");
						//						System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
						//						System.out.println("etu.getNomPatronymique()===>"+etu.getNomPatronymique()+"<===");	
						//						System.out.println("etu.getPrenom1()===>"+etu.getPrenom1()+"<===");	
						//						System.out.println("etu.getTransferts().getDateDemandeTransfert()===>"+etu.getTransferts().getDateDemandeTransfert()+"<===");
						//						System.out.println("etu.getAlertSilenceVautAccord()===>"+etu.getAlertSilenceVautAccord()+"<===");
						//						System.out.println("etu.getAlertDepassementSilenceVautAccord()===>"+etu.getAlertDepassementSilenceVautAccord()+"<===");
						//						System.out.println("now===>"+now+"<===");
						//						System.out.println("===>#################################################################################################################<===");
						listeEtudiantRefAlertDepassementSilenceVautAccord.add(etu);
					}					
				}
			}

			
			
			if(listeEtudiantRefAlertSilenceVautAccord!=null && !listeEtudiantRefAlertSilenceVautAccord.isEmpty())
			{
				String sujet = "Silence vaut accord (délai de 6 semaines dépassés)";
				String body = "Liste des des demande de transferts dépassant le délai des 6 semaines : <BR /><BR />\r\n\r\n";
				System.out.println("===>############################################ listeEtudiantRefAlertSilenceVautAccord #####################################################################<===");
				for(EtudiantRef etu : listeEtudiantRefAlertSilenceVautAccord)
				{
					System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
//					System.out.println("etu.getNomPatronymique()===>"+etu.getNomPatronymique()+"<===");	
//					System.out.println("etu.getPrenom1()===>"+etu.getPrenom1()+"<===");	
//					System.out.println("etu.getTransferts().getDateDemandeTransfert()===>"+etu.getTransferts().getDateDemandeTransfert()+"<===");
//					System.out.println("etu.getAlertSilenceVautAccord()===>"+etu.getAlertSilenceVautAccord()+"<===");
//					System.out.println("etu.getAlertDepassementSilenceVautAccord()===>"+etu.getAlertDepassementSilenceVautAccord()+"<===");
//					System.out.println("now===>"+now+"<===");
					System.out.println("===>#################################################################################################################<===");
//					body+=etu.getNumeroIne()+" "+etu.getNomPatronymique()+" "+etu.getPrenom1()+"<BR /><BR />\r\n\r\n";
					body+=etu.getNumeroIne()+"<BR /><BR />\r\n\r\n";
				}
				try {
//					body = i18nService.getString("TRANSFERT_MAIL_BODY", "Farid",
//							"AIT KARRA");
					getSmtpService().send(new InternetAddress("farid.aitkarra@univ-artois.fr"), sujet, body, body);
				} 
				catch (AddressException e) 
				{
					System.out.println("===>Echec envoi de mail<===");
					e.printStackTrace();
				}	
			}
			else
			{
				System.out.println("===>Aucun étudiant<===");
				System.out.println("===>#################################################################################################################<===");
			}

			if(listeEtudiantRefAlertDepassementSilenceVautAccord!=null && !listeEtudiantRefAlertDepassementSilenceVautAccord.isEmpty())
			{
				String sujet = "Silence vaut accord (délai des 2 mois dépassés)";
				String body = "Liste des des demande de transferts dépassant le délai des 2 mois : <BR /><BR />\r\n\r\n";
				System.out.println("===>################################################## listeEtudiantRefAlertDepassementSilenceVautAccord ###############################################################<===");				
				for(EtudiantRef etu : listeEtudiantRefAlertDepassementSilenceVautAccord)
				{
					System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
//					System.out.println("etu.getNomPatronymique()===>"+etu.getNomPatronymique()+"<===");	
//					System.out.println("etu.getPrenom1()===>"+etu.getPrenom1()+"<===");	
//					System.out.println("etu.getTransferts().getDateDemandeTransfert()===>"+etu.getTransferts().getDateDemandeTransfert()+"<===");
//					System.out.println("etu.getAlertSilenceVautAccord()===>"+etu.getAlertSilenceVautAccord()+"<===");
//					System.out.println("etu.getAlertDepassementSilenceVautAccord()===>"+etu.getAlertDepassementSilenceVautAccord()+"<===");
//					System.out.println("now===>"+now+"<===");
					System.out.println("===>#################################################################################################################<===");
//					body+=etu.getNumeroIne()+" "+etu.getNomPatronymique()+" "+etu.getPrenom1()+"<BR /><BR />\r\n\r\n";
					body+=etu.getNumeroIne()+"<BR /><BR />\r\n\r\n";
				}	
				try {
//					body = i18nService.getString("TRANSFERT_MAIL_BODY", "Farid",
//							"AIT KARRA");
					getSmtpService().send(new InternetAddress("farid.aitkarra@univ-artois.fr"), sujet, body, body);
				} 
				catch (AddressException e) 
				{
					System.out.println("===>Echec envoi de mail<===");
					e.printStackTrace();
				}				
			}
			else
			{
				System.out.println("===>Aucun étudiant<===");
				System.out.println("===>#################################################################################################################<===");
			}			
		}
		else
		{
			System.out.println("lEtu2.size()===>0<===");
		}


		//		String sujet = i18nService.getString("TRANSFERT_MAIL_SUJET");
		//		String body = i18nService.getString("TRANSFERT_MAIL_BODY");
		//		try {
		//			body = i18nService.getString("TRANSFERT_MAIL_BODY", "Farid",
		//					"AIT KARRA");
		//			getSmtpService().send(new InternetAddress("farid.aitkarra@univ-artois.fr"), sujet, body, body);
		//		} 
		//		catch (AddressException e) 
		//		{
		//			System.out.println("===>Echec envoi de mail<===");
		//			e.printStackTrace();
		//		}
	}

	public DomainService getDomainService() {
		return domainService;
	}

	public void setDomainService(DomainService domainService) {
		this.domainService = domainService;
	}

	public ResourceBundleMessageSourceI18nServiceImpl getI18nService() {
		return i18nService;
	}

	public void setI18nService(ResourceBundleMessageSourceI18nServiceImpl i18nService) {
		this.i18nService = i18nService;
	}

	public SmtpService getSmtpService() {
		return smtpService;
	}

	public void setSmtpService(SmtpService smtpService) {
		this.smtpService = smtpService;
	}
}

