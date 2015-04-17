package org.esupportail.transferts.web.scheduler;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.esupportail.commons.services.i18n.ResourceBundleMessageSourceI18nServiceImpl;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.smtp.SmtpService;
import org.esupportail.transferts.domain.DomainService;
import org.esupportail.transferts.domain.DomainServiceOpi;
import org.esupportail.transferts.domain.beans.CodeSizeAnnee;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.WsPub;
import org.esupportail.transferts.utils.GestionDate;
import org.esupportail.transferts.web.utils.CompareByComposanteAccueil;
import org.esupportail.transferts.web.utils.CompareByComposanteDepart;
import org.esupportail.transferts.web.utils.MyAuthenticator;

import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

public class BusinessManager {

	private Integer currentAnnee;
	private String currentRne;
	private String currentMail;
	private DomainService domainService;
	private ResourceBundleMessageSourceI18nServiceImpl i18nService;
	private SmtpService smtpService;
	private boolean majOdfAutoForScheduler;
	private Integer timeOutConnexionWs;
	private Integer nbJourAvantAlertSilenceVautAccord;	
	private Integer nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord;
	private Logger logger = new LoggerImpl(getClass());

	public void runAction() 
	{
		CodeSizeAnnee csa = getDomainService().getCodeSizeDefaut();
		setCurrentAnnee(csa.getAnnee());
		this.setCurrentMail(getDomainService().getWsPubByRneAndAnnee(this.getCurrentRne(), this.getCurrentAnnee()).getMailCorrespondantFonctionnel());

		if(this.getCurrentAnnee()!=null)
		{
			List<EtudiantRef> lEtuAccueil = getDomainService().getAllDemandesTransfertsByAnnee(this.getCurrentAnnee(), "A");
			List<EtudiantRef> lEtuDepart = getDomainService().getAllDemandesTransfertsByAnnee(this.getCurrentAnnee(), "D");
			this.envoiMail(lEtuAccueil, "A");
			this.envoiMail(lEtuDepart, "D");
			if(this.isMajOdfAutoForScheduler())
				this.refreshAllPartenaire();
		}
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
					Collections.sort(listeEtudiantRefAlertSilenceVautAccord, new CompareByComposanteAccueil());
					sujet = "[transferts accueil] Silence vaut accord (délai de 6 semaines dépassés)";
					body = "Liste des des demande de transferts accueil dépassant le délai des 6 semaines : <BR />\r\n";
				}
				else
				{
					Collections.sort(listeEtudiantRefAlertSilenceVautAccord, new CompareByComposanteDepart());
					sujet = "[transferts départ] Silence vaut accord (délai de 6 semaines dépassés)";
					body = "Liste des des demande de transferts départ dépassant le délai des 6 semaines : <BR />\r\n";
				}
				System.out.println("===>############################################ listeEtudiantRefAlertSilenceVautAccord #####################################################################<===");
				String libComp="";
				boolean repeat=false;
				for(EtudiantRef etu : listeEtudiantRefAlertSilenceVautAccord)
				{
					if(source.equals("D"))
					{
						if(libComp.equals(""))
							libComp=etu.getComposante();
						else if(libComp.equals(etu.getComposante()))
							repeat=true;
						else
						{
							libComp=etu.getComposante();
							repeat=false;
						}
					}
					else
					{
						if(libComp.equals(""))
							libComp=etu.getTransferts().getOdf().getCodeComposante();
						else if(libComp.equals(etu.getTransferts().getOdf().getCodeComposante()))
							repeat=true;
						else
						{
							libComp=etu.getTransferts().getOdf().getCodeComposante();
							repeat=false;
						}
					}
					System.out.println("libComp===>"+libComp+"<===");		
					System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
					System.out.println("===>#################################################################################################################<===");

					if(!repeat)
						body+="<BR />\r\n"+libComp+"<BR />\r\n";
					body+=etu.getNomPatronymique()+" - "+etu.getPrenom1()+" ("+etu.getNumeroIne()+")<BR />\r\n";
				}

				try {
					getSmtpService().send(new InternetAddress(this.getCurrentMail()), sujet, body, body);
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
					Collections.sort(listeEtudiantRefAlertDepassementSilenceVautAccord, new CompareByComposanteAccueil());
					sujet = "[transferts accueil] Silence vaut accord (délai des 2 mois dépassés)";
					body = "Liste des des demande de transferts accueil dépassant le délai des 2 mois : <BR /><BR />\r\n\r\n";
				}
				else
				{
					Collections.sort(listeEtudiantRefAlertDepassementSilenceVautAccord, new CompareByComposanteDepart());
					sujet = "[transferts départ] Silence vaut accord (délai des 2 mois dépassés)";
					body = "Liste des des demande de transferts départ dépassant le délai des 2 mois : <BR /><BR />\r\n\r\n";					
				}
				System.out.println("===>################################################## listeEtudiantRefAlertDepassementSilenceVautAccord ###############################################################<===");				
				String libComp="";
				boolean repeat=false;
				for(EtudiantRef etu : listeEtudiantRefAlertDepassementSilenceVautAccord)
				{
					//					System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
					//					System.out.println("===>#################################################################################################################<===");
					//					body+=etu.getNumeroIne()+"<BR /><BR />\r\n\r\n";
					if(source.equals("D"))
					{
						if(libComp.equals(""))
							libComp=etu.getComposante();
						else if(libComp.equals(etu.getComposante()))
							repeat=true;
						else
						{
							libComp=etu.getComposante();
							repeat=false;
						}
					}
					else
					{
						if(libComp.equals(""))
							libComp=etu.getTransferts().getOdf().getCodeComposante();
						else if(libComp.equals(etu.getTransferts().getOdf().getCodeComposante()))
							repeat=true;
						else
						{
							libComp=etu.getTransferts().getOdf().getCodeComposante();
							repeat=false;
						}
					}
					System.out.println("libComp===>"+libComp+"<===");		
					System.out.println("etu.getNumeroIne()===>"+etu.getNumeroIne()+"<===");		
					System.out.println("===>#################################################################################################################<===");

					if(!repeat)
						body+="<BR />\r\n"+libComp+"<BR />\r\n";
					body+=etu.getNomPatronymique()+" - "+etu.getPrenom1()+" ("+etu.getNumeroIne()+")<BR />\r\n";
				}	
				try {
					getSmtpService().send(new InternetAddress(this.getCurrentMail()), sujet, body, body);
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

	private void refreshAllPartenaire()
	{
		List<WsPub> listePartenaires = getDomainService().getWsPubByAnnee(this.getCurrentAnnee());
		if(listePartenaires!=null)
		{
			for(WsPub part : listePartenaires)
			{
				if (part.getUrl() != null) 
				{
					logger.info("===>"+part.getUrl()+"<===");
					Authenticator.setDefault(new MyAuthenticator(part.getIdentifiant(), part.getPassword()));
					if (this.testUrl(part.getUrl())) 
					{
						System.out.println("aaaaa");
						try {
							System.out.println("bbbbb");
							String address = part.getUrl();
							JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
							factoryBean.setServiceClass(DomainServiceOpi.class);
							factoryBean.setAddress(address);
							DomainServiceOpi monService = (DomainServiceOpi) factoryBean.create();
							part.setOnline(1);
							if(!(part.getRne().equals(this.getCurrentRne())))
							{
								Date d = getDomainService().getDateMaxMajByRneAndAnnee(this.getCurrentAnnee(), part.getRne());
								if (logger.isDebugEnabled())
									logger.debug("######################### Date Max MAJ ################################" + d);	
								if(d!=null)
								{
									if (logger.isDebugEnabled())
										logger.debug("monService.getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(d, getSessionController().getCurrentAnnee(), part.getRne());");
									List<OffreDeFormationsDTO> lOdf = monService.getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(d, this.getCurrentAnnee(), part.getRne());
									if(lOdf!=null)
									{
										OffreDeFormationsDTO[] tabFormationsMaj = new OffreDeFormationsDTO[lOdf.size()]; 
										for(int i=0; i<lOdf.size();i++)
											tabFormationsMaj[i]=lOdf.get(i);
										getDomainService().addOdfs(tabFormationsMaj);
										part.setSyncOdf(1);
									}
									else
									{
										if (logger.isDebugEnabled())
											logger.debug("######################### Auncune Offre de formation a mettre a jour ################################");										
										part.setSyncOdf(1);
									}
								}
								else
								{
									if (logger.isDebugEnabled())
										logger.debug("monService.getFormationsByRneAndAnnee(part.getRne(), getSessionController().getCurrentAnnee());");
									List<OffreDeFormationsDTO> lOdf = monService.getFormationsByRneAndAnnee(part.getRne(), this.getCurrentAnnee());
									if(lOdf!=null)
									{
										OffreDeFormationsDTO[] tabFormationsMaj = new OffreDeFormationsDTO[lOdf.size()]; 
										for(int i=0; i<lOdf.size();i++)
											tabFormationsMaj[i]=lOdf.get(i);
										getDomainService().addOdfs(tabFormationsMaj);
										part.setSyncOdf(1);
									}
									else
										part.setSyncOdf(3);
								}
							}
							else
							{
								part.setSyncOdf(1);
							}
							logger.info("===>Mise à jour de l'ODF des partenaires réussie<===");
						}
						catch (Exception e) 
						{
							System.out.println("ccccc");
							logger.info("WebServiceException RNE : " + part.getRne());
							logger.info("-----------------");
							logger.info(e.getCause().getMessage());
							logger.info("-----------------");
							e.printStackTrace();
							part.setOnline(0);
							part.setSyncOdf(0);
						}		
					}
					else
					{
						part.setOnline(0);
						part.setSyncOdf(0);
						logger.info("===>Echec dela mise à jour de l'ODF du partenaire===>"+part.getRne()+"<===");
					}
					AuthCacheValue.setAuthCache(new AuthCacheImpl());
					Authenticator.setDefault(null);
				}
				else
				{
					part.setOnline(0);
					part.setSyncOdf(0);
				}			
			}
		}
		else
		{
			logger.info("===>Aucun établissement partenaire<===");	
		}
	}

	private boolean testUrl(String host) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(host).openConnection();
			conn.setConnectTimeout(getTimeOutConnexionWs());
			conn.connect();
			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (MalformedURLException e) {
			logger.info("MalformedURLException");
			logger.info("host : " + host);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			logger.info("IOException");
			logger.info("host : " + host);
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			logger.info("Exception");
			logger.info("host : " + host);
			e.printStackTrace();
			return false;				
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

	public Integer getTimeOutConnexionWs() {
		return timeOutConnexionWs;
	}

	public void setTimeOutConnexionWs(Integer timeOutConnexionWs) {
		this.timeOutConnexionWs = timeOutConnexionWs;
	}

	public Integer getCurrentAnnee() {
		return currentAnnee;
	}

	public void setCurrentAnnee(Integer currentAnnee) {
		this.currentAnnee = currentAnnee;
	}

	public Integer getNbJourAvantAlertSilenceVautAccord() {
		return nbJourAvantAlertSilenceVautAccord;
	}

	public void setNbJourAvantAlertSilenceVautAccord(
			Integer nbJourAvantAlertSilenceVautAccord) {
		this.nbJourAvantAlertSilenceVautAccord = nbJourAvantAlertSilenceVautAccord;
	}

	public Integer getNbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord() {
		return nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord;
	}

	public void setNbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord(
			Integer nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord) {
		this.nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord = nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord;
	}

	public String getCurrentRne() {
		return currentRne;
	}

	public void setCurrentRne(String currentRne) {
		this.currentRne = currentRne;
	}

	public boolean isMajOdfAutoForScheduler() {
		return majOdfAutoForScheduler;
	}

	public void setMajOdfAutoForScheduler(boolean majOdfAutoForScheduler) {
		this.majOdfAutoForScheduler = majOdfAutoForScheduler;
	}

	public String getCurrentMail() {
		return currentMail;
	}

	public void setCurrentMail(String currentMail) {
		this.currentMail = currentMail;
	}
}

