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
		List<EtudiantRef> lEtuAccueil = getDomainService().getAllDemandesTransfertsByAnnee(2015, "A");
		List<EtudiantRef> lEtuDepart = getDomainService().getAllDemandesTransfertsByAnnee(2015, "D");
		this.envoiMail(lEtuAccueil, "A");
		this.envoiMail(lEtuDepart, "D");

	}

	private void envoiMail(List<EtudiantRef> lEtu, String source)
	{
		if(lEtu!=null)
		{
			List<EtudiantRef> listeEtudiantRefAlertSilenceVautAccord = new ArrayList<EtudiantRef>();
			List<EtudiantRef> listeEtudiantRefAlertDepassementSilenceVautAccord = new ArrayList<EtudiantRef>();
			Date now = new Date();
			String sujet="";
			String body="";
			System.out.println("lEtu.size()===>"+lEtu.size()+"<===");

			for(EtudiantRef etu : lEtu)
			{
				if(etu.getTransferts().getTemoinTransfertValide()!=2)
				{
					etu.setAlertDepassementSilenceVautAccord(GestionDate.ajouterMois(etu.getTransferts().getDateDemandeTransfert(), 2));
					etu.setAlertSilenceVautAccord(GestionDate.ajouterJour(etu.getTransferts().getDateDemandeTransfert(), 42));	
					if(now.after(etu.getAlertSilenceVautAccord()) && now.before(etu.getAlertDepassementSilenceVautAccord()))
					{
						listeEtudiantRefAlertSilenceVautAccord.add(etu);
					}
					if(now.after(etu.getAlertDepassementSilenceVautAccord()))
					{
						listeEtudiantRefAlertDepassementSilenceVautAccord.add(etu);
					}					
				}
			}

			if(listeEtudiantRefAlertSilenceVautAccord!=null && !listeEtudiantRefAlertSilenceVautAccord.isEmpty())
			{
				
				if(source.equals("A"))
				{
					sujet = "[transferts accueil] Silence vaut accord (délai de 6 semaines dépassés)";
					body = "Liste des des demande de transferts accueil dépassant le délai des 6 semaines : <BR /><BR />\r\n\r\n";
				}
				else
				{
					sujet = "[transferts départ] Silence vaut accord (délai de 6 semaines dépassés)";
					body = "Liste des des demande de transferts départ dépassant le délai des 6 semaines : <BR /><BR />\r\n\r\n";
				}
				System.out.println("===>############################################ listeEtudiantRefAlertSilenceVautAccord #####################################################################<===");
				for(EtudiantRef etu : listeEtudiantRefAlertSilenceVautAccord)
				{
					System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
					System.out.println("===>#################################################################################################################<===");
					body+=etu.getNumeroIne()+"<BR /><BR />\r\n\r\n";
				}
				try {
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
				if(source.equals("A"))
				{				
				sujet = "[transferts accueil] Silence vaut accord (délai des 2 mois dépassés)";
				body = "Liste des des demande de transferts accueil dépassant le délai des 2 mois : <BR /><BR />\r\n\r\n";
				}
				else
				{
					sujet = "[transferts départ] Silence vaut accord (délai des 2 mois dépassés)";
					body = "Liste des des demande de transferts départ dépassant le délai des 2 mois : <BR /><BR />\r\n\r\n";					
				}
				System.out.println("===>################################################## listeEtudiantRefAlertDepassementSilenceVautAccord ###############################################################<===");				
				for(EtudiantRef etu : listeEtudiantRefAlertDepassementSilenceVautAccord)
				{
					System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
					System.out.println("===>#################################################################################################################<===");
					body+=etu.getNumeroIne()+"<BR /><BR />\r\n\r\n";
				}	
				try {
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
			if(source.equals("A"))
				System.out.println("[accueil]===>lEtu.size()===>0<===");
			else
				System.out.println("[départ]===>lEtu.size()===>0<===");
		}		
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

