/**
 * ESUP-Portail Blank Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-transferts
 */
package org.esupportail.transferts.web.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.transferts.domain.beans.CodeSizeAnnee;
import org.esupportail.transferts.domain.beans.Parametres;
import org.esupportail.transferts.domain.beans.User;
import org.esupportail.transferts.domain.beans.Versions;
import org.esupportail.transferts.services.auth.Authenticator;
import org.hibernate.exception.SQLGrammarException;
import org.primefaces.context.RequestContext;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.commons.utils.ContextUtils;
import org.esupportail.commons.utils.strings.StringUtils;
import org.esupportail.commons.web.controllers.ExceptionController;

/**
 * A bean to memorize the context of the application.
 */
public class SessionController extends AbstractDomainAwareBean {

	/*
	 ******************* PROPERTIES ******************** */

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -5936434246704000653L;

	/**
	 * The exception controller (called when logging in/out).
	 */
	private ExceptionController exceptionController;

	/**
	 * The authenticator.
	 */
	private Authenticator authenticator;

	/**
	 * The CAS logout URL.
	 */
	private String casLogoutUrl;
	private boolean error = false;
	private String rne;
	private String numeroSerieImmatriculation;
	private Integer annee;
	private Integer currentAnnee;	
	private String htmlCssStyle;
	private boolean boutonDeconnexion;
	private boolean transfertsAccueil;
	private String superGestionnaire;
	private List<String> listSuperGestionnaire = new ArrayList<String>();
	private String informaticiens;
	private List<String> listInformaticiens = new ArrayList<String>();
	private String validationAutomatique;
	private Integer regleGestionTE02;
	private boolean choixDuVoeuParComposante;
	private boolean majOdfAuto;
	private boolean planningFermeturesAuto;
	private String ajoutEtablissementManuellement;
	private boolean activEtablissementManuellement;
	private Integer nbJourAvantAlertSilenceVautAccord;	
	private Integer nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord;
	private boolean useCandidatures;
	private String wsCandidaturesWsdl;
	private String wsCandidaturesUser;
	private String wsCandidaturesPwd;
	private String timezone;
	private Date aujourdhui;
	private boolean defaultCodeSizeAnnee = false;
	private CodeSizeAnnee defaultCodeSize;
	private Logger logger = new LoggerImpl(getClass());

	/*
	 ******************* INIT ******************** */
	/**
	 * Constructor.
	 */
	public SessionController() {
		super();
	}

	/**
	 * @see org.esupportail.transferts.web.controllers.AbstractDomainAwareBean#afterPropertiesSetInternal()
	 */
	@Override
	public void afterPropertiesSetInternal() {
		Assert.notNull(this.exceptionController, "property exceptionController of class " 
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(this.authenticator, "property authenticator of class " 
				+ this.getClass().getName() + " can not be null");
		Assert.hasText(rne, "property rne of class "
				+ this.getClass().getName() + " can not be null");		
		Assert.hasText(htmlCssStyle, "property htmlCssStyle of class "
				+ this.getClass().getName() + " can not be null");	
		Assert.notNull(this.boutonDeconnexion, "property boutonDeconnexion of class " 
				+ this.getClass().getName() + " can not be null");	
		Assert.notNull(this.transfertsAccueil, "property transfertsAccueil of class " 
				+ this.getClass().getName() + " can not be null");	

		if(this.superGestionnaire!=null && this.superGestionnaire!="" && ((this.superGestionnaire.split(",")).length>1))
		{
			String[] tokens = this.superGestionnaire.split(",");
			for(int i=0; i<tokens.length; i++)
				this.listSuperGestionnaire.add(tokens[i]);
		}
		else
			this.listSuperGestionnaire.add(this.superGestionnaire);		

		Assert.notNull(this.nbJourAvantAlertSilenceVautAccord, "property nbJourAvantAlertSilenceVautAccord of class " 
				+ this.getClass().getName() + " can not be null");	

		Assert.notNull(this.nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord, "property nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord of class " 
				+ this.getClass().getName() + " can not be null");	

		Assert.notNull(this.useCandidatures, "property useCandidatures of class " 
				+ this.getClass().getName() + " can not be null");	

		if(this.isUseCandidatures())
		{
			Assert.hasText(wsCandidaturesWsdl, "property wsCandidaturesWsdl of class "
					+ this.getClass().getName() + " can not be null");	

			Assert.hasText(wsCandidaturesUser, "property wsCandidaturesUser of class "
					+ this.getClass().getName() + " can not be null");	

			Assert.hasText(wsCandidaturesPwd, "property wsCandidaturesPwd of class "
					+ this.getClass().getName() + " can not be null");	
		}

		Assert.hasText(timezone, "property timezone of class "
				+ this.getClass().getName() + " can not be null");

		if(this.informaticiens!=null && this.informaticiens!="" && ((this.informaticiens.split(",")).length>1))
		{
			String[] tokens = this.informaticiens.split(",");
			for(int i=0; i<tokens.length; i++)
				this.listInformaticiens.add(tokens[i]);
		}
		else
			this.listInformaticiens.add(this.informaticiens);
	}

	@PostConstruct
	public void init() {
		setAujourdhui(new Date());
		Enumeration<?> liste = System.getProperties().propertyNames();
		String cle;

		while( liste.hasMoreElements() ) {
			cle = (String)liste.nextElement();
			if (logger.isDebugEnabled())
				logger.debug("===>"+cle + " = " + System.getProperty(cle)+"<===");
		} 		

		Versions version = null;
		Parametres choixDuVoeuParComposante = null;
		String text="";
		text = "Liste des erreurs : <BR /><BR />";

		try{
			version = getDomainService().getVersionByEtat(1);
		}
		catch(Exception  e)
		{
			if(e.getCause()!=null && e.getCause().getCause()!=null)
				text += "- Erreurs : "+e.getCause().getCause().getMessage()+" (Versions) ===> NOK <BR />";
			else
				text += "- Erreurs : "+e.getMessage()+" ===> NOK <BR />";
		}
			
		try{
			defaultCodeSize = getDomainService().getCodeSizeDefaut();
			if (defaultCodeSize != null)
			{
				setDefaultCodeSizeAnnee(true);
				setNumeroSerieImmatriculation(this.defaultCodeSize.getCode());
				setAnnee(this.defaultCodeSize.getAnnee());
				setCurrentAnnee(this.defaultCodeSize.getAnnee());
			}
		}
		catch(Exception  e)
		{
			if(e.getCause()!=null && e.getCause().getCause()!=null)
				text += "- Erreurs : "+e.getCause().getCause().getMessage()+" (code_size) ===> NOK <BR />";
			else
				text += "- Erreurs : "+e.getMessage()+" ===> NOK <BR />";
		}
			
		try{
			choixDuVoeuParComposante = getDomainService().getParametreByCode("choixDuVoeuParComposante");
			if(choixDuVoeuParComposante!=null)
				setChoixDuVoeuParComposante(choixDuVoeuParComposante.isBool());
			else
				setChoixDuVoeuParComposante(true);

			Parametres maj_odf_auto = getDomainService().getParametreByCode("maj_odf_auto");
			if(maj_odf_auto!=null)
				setMajOdfAuto(maj_odf_auto.isBool());
			else
				setMajOdfAuto(true);

			Parametres planning_fermetures_auto = getDomainService().getParametreByCode("planning_fermetures");
			if(planning_fermetures_auto!=null)
				setPlanningFermeturesAuto(planning_fermetures_auto.isBool());
			else
				setPlanningFermeturesAuto(true);

			Parametres ajout_etablissement_manuellement = getDomainService().getParametreByCode("ajout_etablissement_manuellement");
			if(ajout_etablissement_manuellement!=null) {
				setActivEtablissementManuellement(ajout_etablissement_manuellement.isBool());
				setAjoutEtablissementManuellement(ajout_etablissement_manuellement.getCommentaire());
			}
			else
			{
				setActivEtablissementManuellement(false);
				setAjoutEtablissementManuellement("");
			}
		}
		catch(Exception  e)
		{
			if(e.getCause()!=null && e.getCause().getCause()!=null)
				text += "- Erreurs : "+e.getCause().getCause().getMessage()+" (parametres) ===> NOK <BR />";
			else
				text += "- Erreurs : "+e.getMessage()+" ===> NOK <BR />";
		}
	
		
		String v = getApplicationService().getVersion().toString();
		if (logger.isDebugEnabled())
			logger.debug("Version Application ===>"+v.toString()+"<===");

		text += "- Version de l'application : "+v.toString()+" ===> OK <BR />";

		if(version==null)
		{
			if (logger.isDebugEnabled())
				logger.debug("===>if(version==null)<===");
			text += "- Version de la base de données est null ===> NOK (initialiser les nomenclatures ou vérifier l'état à 1 dans la table version)<BR />";
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "L'application a rencontré des erreurs lors de son lancement", text);
			RequestContext.getCurrentInstance().showMessageInDialog(message);
		}
		else
		{	
			if(!version.getNumero().equals(v.toString()))
			{
				text += "- Version de la base de données : "+version.getNumero()+" est différente de la version de l'application ("+v.toString()+"), veuillez passer le script de migration correspondant ===> NOK <BR />";
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "L'application a rencontré des erreurs lors de son lancement", text);
				RequestContext.getCurrentInstance().showMessageInDialog(message);
				if (logger.isDebugEnabled()) {
					logger.debug("Version BDD ===>" + version.toString() + "<===");
					logger.debug("Version Application ===>" + v.toString() + "<===");
				}
			}

		}
	}	

	/**
	 * @return the current user, or null if guest.
	 * @throws Exception 
	 */
	@Override
	public User getCurrentUser() throws Exception {
		User user = authenticator.getUser();
		// Verification du login authorise 
		if (listSuperGestionnaire.size() > 0) {
			if(user!=null)
			{
				user.setAdmin(false);
				for (String ident : listSuperGestionnaire) {
					if (ident.equals(user.getLogin()))
					{
						if (logger.isDebugEnabled()) {
							logger.debug("SuperGestionnaire===>"+ user.getLogin()+" est un super gestionnaire<===");
						}							
						user.setAdmin(true);
						break;
					}
					else
					{
						if (logger.isDebugEnabled()) {
							logger.debug("SuperGestionnaire===>"+ user.getLogin()+" n'est pas un super gestionnaire<===");
						}													
					}
				}
				user.setInformaticien(false);
				for (String ident : listInformaticiens) {
					if (ident.equals(user.getLogin()))
					{
						if (logger.isDebugEnabled()) {
							logger.debug("Informaticien===>"+ user.getLogin()+" est un informaticien<===");
						}
						user.setInformaticien(true);
						user.setAdmin(true);
						break;
					}
					else
					{
						if (logger.isDebugEnabled()) {
							logger.debug("Informaticien===>"+ user.getLogin()+" n'est pas un informaticien<===");
						}
					}
				}
			}
		}
		return user;
	}

	/**
	 * JSF callback.
	 * @return a String.
	 * @throws IOException 
	 */
	public String logout() throws IOException {
		if (ContextUtils.isPortlet()) {
			throw new UnsupportedOperationException("logout() should not be called in portlet mode.");
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		String test = request.getRequestURL().toString();
		String returnUrl=null;
		if(test.contains("/stylesheets/gestionnaire/"))
			returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/.*", "/stylesheets/gestionnaire/welcome.xhtml");
		else if(test.contains("/stylesheets/depart/"))
			returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/.*", "/stylesheets/depart/welcome.xhtml");
		else if(test.contains("/stylesheets/arrivee/"))
			returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/.*", "/stylesheets/arrivee/welcome.xhtml");				
		String forwardUrl;
		Assert.hasText(
				casLogoutUrl, 
				"property casLogoutUrl of class " + getClass().getName() + " is null");
		forwardUrl = String.format(casLogoutUrl, StringUtils.utf8UrlEncode(returnUrl));
		// note: the session beans will be kept even when invalidating 
		// the session so they have to be reset (by the exception controller).
		// We invalidate the session however for the other attributes.
		request.getSession().invalidate();
		request.getSession(true);
		// calling this method will reset all the beans of the application
		exceptionController.restart();
		externalContext.redirect(forwardUrl);
		facesContext.responseComplete();
		return null;
	}

	@Override
	public String toString() {
		return "SessionController [exceptionController=" + exceptionController
				+ ", authenticator=" + authenticator + ", casLogoutUrl="
				+ casLogoutUrl + ", error=" + error + ", rne=" + rne
				+ ", numeroSerieImmatriculation=" + numeroSerieImmatriculation
				+ ", annee=" + annee + ", currentAnnee=" + currentAnnee
				+ ", htmlCssStyle=" + htmlCssStyle + ", boutonDeconnexion="
				+ boutonDeconnexion + ", opiReinscription=" + ", transfertsAccueil="
				+ transfertsAccueil + ", superGestionnaire="
				+ superGestionnaire + ", listSuperGestionnaire="
				+ listSuperGestionnaire + ", validationAutomatique="
				+ validationAutomatique + ", logger=" + logger + "]";
	}

	/**
	 * JSF callback.
	 * @return a String.
	 * @throws IOException 
	 */
	public String logout2() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		String returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/[^/]*$", "");
		String forwardUrl;
		forwardUrl = String.format(casLogoutUrl, StringUtils.utf8UrlEncode(returnUrl));
		// note: the session beans will be kept even when invalidating 
		// the session so they have to be reset (by the exception controller).
		// We invalidate the session however for the other attributes.
		request.getSession().invalidate();
		request.getSession(true);
		// calling this method will reset all the beans of the application
		exceptionController.restart();
		externalContext.redirect(forwardUrl);
		facesContext.responseComplete();
		return null;
	}	

	public void resetController()
	{
		// calling this method will reset all the beans of the application
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();		
		request.getSession().invalidate();
		request.getSession(true);		
		exceptionController.restart();	
	}





	/*
	 ******************* ACCESSORS ******************** */




	/**
	 * @param exceptionController the exceptionController to set
	 */
	public void setExceptionController(final ExceptionController exceptionController) {
		this.exceptionController = exceptionController;
	}

	/**
	 * @param authenticator the authenticator to set
	 */
	public void setAuthenticator(final Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	/**
	 * @return the casLogoutUrl
	 */
	protected String getCasLogoutUrl() {
		return casLogoutUrl;
	}

	/**
	 * @param casLogoutUrl the casLogoutUrl to set
	 */
	public void setCasLogoutUrl(final String casLogoutUrl) {
		this.casLogoutUrl = StringUtils.nullIfEmpty(casLogoutUrl);
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isError() {
		return error;
	}

	public String getRne() {
		return rne;
	}

	public void setRne(String rne) {
		this.rne = rne;
	}

	public void setNumeroSerieImmatriculation(String numeroSerieImmatriculation) {
		this.numeroSerieImmatriculation = numeroSerieImmatriculation;
	}

	public String getNumeroSerieImmatriculation() {
		return numeroSerieImmatriculation;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setCurrentAnnee(Integer currentAnnee) {
		this.currentAnnee = currentAnnee;
	}

	public Integer getCurrentAnnee() {
		return currentAnnee;
	}

	public String getHtmlCssStyle() {
		return htmlCssStyle;
	}

	public void setHtmlCssStyle(String htmlCssStyle) {
		this.htmlCssStyle = htmlCssStyle;
	}

	public void setBoutonDeconnexion(boolean boutonDeconnexion) {
		this.boutonDeconnexion = boutonDeconnexion;
	}

	public boolean isBoutonDeconnexion() {
		return boutonDeconnexion;
	}

	public String getSuperGestionnaire() {
		return superGestionnaire;
	}

	public void setSuperGestionnaire(String superGestionnaire) {
		this.superGestionnaire = superGestionnaire;
	}

	public List<String> getListSuperGestionnaire() {
		return listSuperGestionnaire;
	}

	public Integer getNbJourAvantAlertSilenceVautAccord() {
		return nbJourAvantAlertSilenceVautAccord;
	}

	public void setNbJourAvantAlertSilenceVautAccord(
			Integer nbJourAvantAlertSilenceVautAccord) {
		this.nbJourAvantAlertSilenceVautAccord = nbJourAvantAlertSilenceVautAccord;
	}

	public void setListSuperGestionnaire(List<String> listSuperGestionnaire) {
		this.listSuperGestionnaire = listSuperGestionnaire;
	}

	public boolean isTransfertsAccueil() {
		return transfertsAccueil;
	}

	public void setTransfertsAccueil(boolean transfertsAccueil) {
		this.transfertsAccueil = transfertsAccueil;
	}

	public String getValidationAutomatique() {
		return validationAutomatique;
	}

	public void setValidationAutomatique(String validationAutomatique) {
		this.validationAutomatique = validationAutomatique;
	}

	public Integer getRegleGestionTE02() {
		return regleGestionTE02;
	}

	public void setRegleGestionTE02(Integer regleGestionTE02) {
		this.regleGestionTE02 = regleGestionTE02;
	}

	public boolean isChoixDuVoeuParComposante() {
		return choixDuVoeuParComposante;
	}

	public void setChoixDuVoeuParComposante(boolean choixDuVoeuParComposante) {
		this.choixDuVoeuParComposante = choixDuVoeuParComposante;
	}

	public Integer getNbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord() {
		return nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord;
	}

	public void setNbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord(
			Integer nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord) {
		this.nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord = nbMoisAvantAccordSuiteNouvelleLoiSilenceVautAccord;
	}

	public boolean isMajOdfAuto() {
		return majOdfAuto;
	}

	public void setMajOdfAuto(boolean majOdfAuto) {
		this.majOdfAuto = majOdfAuto;
	}

	public boolean isUseCandidatures() {
		return useCandidatures;
	}

	public void setUseCandidatures(boolean useCandidatures) {
		this.useCandidatures = useCandidatures;
	}

	public String getWsCandidaturesWsdl() {
		return wsCandidaturesWsdl;
	}

	public void setWsCandidaturesWsdl(String wsCandidaturesWsdl) {
		this.wsCandidaturesWsdl = wsCandidaturesWsdl;
	}

	public String getWsCandidaturesUser() {
		return wsCandidaturesUser;
	}

	public void setWsCandidaturesUser(String wsCandidaturesUser) {
		this.wsCandidaturesUser = wsCandidaturesUser;
	}

	public String getWsCandidaturesPwd() {
		return wsCandidaturesPwd;
	}

	public void setWsCandidaturesPwd(String wsCandidaturesPwd) {
		this.wsCandidaturesPwd = wsCandidaturesPwd;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public boolean isPlanningFermeturesAuto() {
		return planningFermeturesAuto;
	}

	public void setPlanningFermeturesAuto(boolean planningFermeturesAuto) {
		this.planningFermeturesAuto = planningFermeturesAuto;
	}

	public Date getAujourdhui() {
		return aujourdhui;
	}

	public void setAujourdhui(Date aujourdhui) {
		this.aujourdhui = aujourdhui;
	}

	public boolean isDefaultCodeSizeAnnee() {
		return defaultCodeSizeAnnee;
	}

	public void setDefaultCodeSizeAnnee(boolean defaultCodeSizeAnnee) {
		this.defaultCodeSizeAnnee = defaultCodeSizeAnnee;
	}

	public CodeSizeAnnee getDefaultCodeSize() {
		return defaultCodeSize;
	}

	public void setDefaultCodeSize(CodeSizeAnnee defaultCodeSize) {
		this.defaultCodeSize = defaultCodeSize;
	}

	public List<String> getListInformaticiens() {
		return listInformaticiens;
	}

	public void setListInformaticiens(List<String> listInformaticiens) {
		this.listInformaticiens = listInformaticiens;
	}

	public String getInformaticiens() {
		return informaticiens;
	}

	public void setInformaticiens(String informaticiens) {
		this.informaticiens = informaticiens;
	}

	public String getAjoutEtablissementManuellement() {
		return ajoutEtablissementManuellement;
	}

	public void setAjoutEtablissementManuellement(String ajoutEtablissementManuellement) {
		this.ajoutEtablissementManuellement = ajoutEtablissementManuellement;
	}

	public boolean isActivEtablissementManuellement() {
		return activEtablissementManuellement;
	}

	public void setActivEtablissementManuellement(boolean activEtablissementManuellement) {
		this.activEtablissementManuellement = activEtablissementManuellement;
	}

}
