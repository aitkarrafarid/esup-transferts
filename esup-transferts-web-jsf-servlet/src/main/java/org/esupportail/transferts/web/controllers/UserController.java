package org.esupportail.transferts.web.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.sound.midi.MidiDevice.Info;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.DomainServiceOpi;
import org.esupportail.transferts.domain.beans.AccueilAnnee;
import org.esupportail.transferts.domain.beans.AccueilResultat;
import org.esupportail.transferts.domain.beans.Avis;
import org.esupportail.transferts.domain.beans.CGE;
import org.esupportail.transferts.domain.beans.Composante;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.EtudiantRefImp;
import org.esupportail.transferts.domain.beans.Fichier;
import org.esupportail.transferts.domain.beans.CodeSizeAnnee;
import org.esupportail.transferts.domain.beans.IndOpi;
import org.esupportail.transferts.domain.beans.InfosAccueil;
import org.esupportail.transferts.domain.beans.DatasExterne;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.Parametres;
import org.esupportail.transferts.domain.beans.ResultatEtape;
import org.esupportail.transferts.domain.beans.ResultatSession;
import org.esupportail.transferts.domain.beans.SituationUniversitaire;
import org.esupportail.transferts.domain.beans.TrBac;
import org.esupportail.transferts.domain.beans.TrBlocageDTO;
import org.esupportail.transferts.domain.beans.TrCommuneDTO;
import org.esupportail.transferts.domain.beans.TrDepartementDTO;
import org.esupportail.transferts.domain.beans.TrEtablissementDTO;
import org.esupportail.transferts.domain.beans.TrInfosAdmEtu;
import org.esupportail.transferts.domain.beans.TrPaysDTO;
import org.esupportail.transferts.domain.beans.TrResultatVdiVetDTO;
import org.esupportail.transferts.domain.beans.WsPub;
import org.esupportail.transferts.utils.RneModuleBase36;
import org.esupportail.transferts.web.comparator.ComparatorSelectItem;
import org.esupportail.transferts.web.utils.MyAuthenticator;
import org.esupportail.transferts.web.utils.PDFUtils;
import org.springframework.util.Assert;
import org.esupportail.transferts.web.dataModel.OdfDataModel;

public class UserController extends AbstractContextAwareController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1084603907906407867L;
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());	
	private EtudiantRef currentEtudiant = null;
	private EtudiantRef currentEtudiantInterdit = null;
	private List<SelectItem> listeCommunes = new ArrayList<SelectItem>();
	private List<SelectItem> listePays = new ArrayList<SelectItem>();	
	private List<Avis> listeAvisByNumeroEtudiantAndAnnee;	
	private Avis selectedAvis;
	private String numeroEtudiant;
	private boolean presentBdd;
	private boolean etudiant;
	private boolean verifDateNaisApogee;
	private boolean rneVide = true;
	private String ineApogee;
	private Date dateNaissanceApogee;
	private Fichier selectedFichier;
	private String mailInformation;
	private String itemValueCodePay;
	private boolean ineToUpperCase;
	private CodeSizeAnnee defaultCodeSize;
	private boolean defaultCodeSizeAnnee=false;
	private Parametres parametreAppli;
	private String studentAffiliation;
	/*Debut propri�t� Concernant le voeux d'orientation*/
	private List<SelectItem> listeEtablissements = null;
	private List<SelectItem> listeTypesDiplome = null;
	private List<SelectItem> listeAnneesEtude = null;
	private List<SelectItem> listeLibellesDiplome = null;
	private List<OffreDeFormationsDTO> listeLibellesEtape;	
	private boolean deptVide = true;	
	private boolean typesDiplomeVide = true;
	private boolean typesDiplomeAutreVide = true;
	private boolean libelleDiplomeVide = true;
	private boolean libelleEtapeVide = true;	
	private boolean AnneeEtudeVide = true;
	private boolean composanteVide = true;
	private String codTypDip;
	private Integer codeNiveau;		
	private String codeDiplome;
	private OffreDeFormationsDTO currentOdf;
	private OdfDataModel odfDataModel;		
	private String source;
	Avis currentAvis;
	private Integer timeOutConnexionWs;
	private DomainServiceOpi domainServiceWSOpiExt;	
	private Parametres parametreAppliInfosDepart;	
	private String aideTypeTransfert;	
	private String codeComposante;
	private List<SelectItem> listeComposantes;

	@Override
	public void afterPropertiesSetInternal() {
		super.afterPropertiesSetInternal();
		Assert.hasText(numeroEtudiant, "property numeroEtudiant of class "
				+ this.getClass().getName() + " can not be null");	
		Assert.hasText(studentAffiliation, "property studentAffiliation of class "
				+ this.getClass().getName() + " can not be null");	
	}

	public void resetGeneral()
	{
		if (logger.isDebugEnabled())
			logger.debug("public void resetGeneral() --> "+ this.currentEtudiant.getTransferts().getDept());

		setLibelleEtapeVide(true);
		setAnneeEtudeVide(true);
		setLibelleDiplomeVide(true);		

		if(this.currentEtudiant.getTransferts().getDept() !=null && !this.currentEtudiant.getTransferts().getDept().equals(""))  
		{
			setDeptVide(false);
			currentEtudiant.getTransferts().setRne(null);
			setTypesDiplomeVide(true);
			setTypesDiplomeAutreVide(true);	
		}
		else
		{
			setDeptVide(true);
			this.listeEtablissements=null;
		}
	}

	public void resetTypeDiplome()
	{
		if (logger.isDebugEnabled())
			logger.debug("public void resetTypeDiplome()");

		currentOdf=null;
		currentEtudiant.getTransferts().setOdf(currentOdf);		
		setLibelleEtapeVide(true);
		setAnneeEtudeVide(true);
		setLibelleDiplomeVide(true);	

		if(this.currentEtudiant.getTransferts().getRne() !=null && !this.currentEtudiant.getTransferts().getRne().equals("") && isPartenaire()) 
		{
			setTypesDiplomeVide(false);
			setCodTypDip(null);
			setTypesDiplomeAutreVide(true);
			this.getListeTypesDiplome();
			if(this.getListeTypesDiplome()==null)
			{
				String summary = getString("ERREUR.ODF_INDISPONIBLE");
				String detail = getString("ERREUR.ODF_INDISPONIBLE");
				Severity severity=FacesMessage.SEVERITY_ERROR;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	
			}
		}
		else
		{
			setTypesDiplomeVide(true);
			setTypesDiplomeAutreVide(false);
			this.listeTypesDiplome=null;
		}
	}		

	public void resetAnneeEtude()
	{
		if (logger.isDebugEnabled())
			logger.debug("public void resetAnneeEtude()");		

		setTypesDiplomeAutreVide(true);
		if(getCodTypDip() !=null && !getCodTypDip().equals(""))  
		{
			setDeptVide(false);
			setTypesDiplomeVide(false);
			setCodeNiveau(null);
			setLibelleEtapeVide(true);
			setAnneeEtudeVide(false);
			if(getSessionController().isChoixDuVoeuParComposante())
				setComposanteVide(true);
			else
				setLibelleDiplomeVide(true);
			this.getListeAnneesEtude();  
		}
		else
		{
			setAnneeEtudeVide(true);
			setLibelleDiplomeVide(true);
			this.listeTypesDiplome=null;
			this.setListeAnneesEtude(null);
			this.setListeLibellesDiplome(null);
			this.setListeLibellesEtape(null);
		}
	}	

	public void resetComposante()
	{
		if (logger.isDebugEnabled())
			logger.debug("public void resetComposante()");

		setTypesDiplomeVide(false);
		setAnneeEtudeVide(false);
		setCodeDiplome(null);
		setLibelleEtapeVide(true);	
		setComposanteVide(false);
		setCodeComposante(null);
		setLibelleDiplomeVide(true); 
	}

	public void resetLibelleDiplome()
	{
		setTypesDiplomeAutreVide(true);
		if(getCodeNiveau() !=null && !getCodeNiveau().equals(""))  
		{
			setDeptVide(false);
			setTypesDiplomeVide(false);
			setAnneeEtudeVide(false);
			setCodeDiplome(null);
			setLibelleEtapeVide(true);			
			setLibelleDiplomeVide(false);
			this.getListeLibellesDiplome(); 
		}
		else
		{
			setLibelleDiplomeVide(true);
			this.listeLibellesDiplome=null;
			this.listeLibellesEtape=null;
		}
	}

	public void resetLibelleEtape()
	{
		if(getSessionController().isChoixDuVoeuParComposante())
		{
			if(getCodeComposante() !=null && !getCodeComposante().equals(""))  
			{
				setComposanteVide(false);
				setTypesDiplomeAutreVide(true);
				setTypesDiplomeVide(false);				
				setDeptVide(false);
				setAnneeEtudeVide(false);
				setLibelleDiplomeVide(false);
				setLibelleEtapeVide(false);
				odfDataModel=null;
				currentOdf=new OffreDeFormationsDTO();
				setListeLibellesEtape(this.getListeLibellesEtape());
			}
			else
			{
				setLibelleEtapeVide(true);
				this.listeLibellesEtape=null;
			}			
		}
		else
		{
			if(getCodeDiplome() !=null && !getCodeDiplome().equals(""))  
			{
				setTypesDiplomeAutreVide(true);
				setTypesDiplomeVide(false);				
				setDeptVide(false);
				setAnneeEtudeVide(false);
				setLibelleDiplomeVide(false);
				setLibelleEtapeVide(false);
				odfDataModel=null;
				currentOdf=new OffreDeFormationsDTO();
				setListeLibellesEtape(this.getListeLibellesEtape());
			}
			else
			{
				setLibelleEtapeVide(true);
				this.listeLibellesEtape=null;
			}			
		}
	}		

	public String goToAuthApogee()
	{
		if (logger.isDebugEnabled())
			logger.debug("goToAuthApogee");
		getSessionController().resetController();
		return "goToAuthApogee";
	}

	public void addDemandeTransferts() throws Exception
	{
		setSource("D");
		this.currentEtudiant.setAccueil(null);
		this.currentEtudiant.setSource(getSource());
		if(this.currentEtudiant.getAdresse().getCodPay().equals("100"))
			this.currentEtudiant.getAdresse().setCodeVilleEtranger(null);
		else
		{
			this.currentEtudiant.getAdresse().setLibPay(getDomainServiceScolarite().getPaysByCodePays(this.currentEtudiant.getAdresse().getCodPay()).getLibPay());
			this.currentEtudiant.getAdresse().setCodePostal(null);
			this.currentEtudiant.getAdresse().setCodeCommune(null);
			this.currentEtudiant.getAdresse().setNomCommune(null);
		}
		this.currentEtudiant.setAnnee(getSessionController().getCurrentAnnee());
		this.currentEtudiant.getAdresse().setAnnee(getSessionController().getCurrentAnnee());
		this.currentEtudiant.getTransferts().setTemoinTransfertValide(0);
		this.currentEtudiant.getTransferts().setDateDemandeTransfert(new Date());
		this.currentEtudiant.getTransferts().setAnnee(getSessionController().getCurrentAnnee());
		this.currentEtudiant.getTransferts().setFichier(null);

		if (logger.isDebugEnabled())
			logger.debug("getSessionController().getValidationAutomatique()-->"+getSessionController().getValidationAutomatique());

		if(!getSessionController().getValidationAutomatique().equals("") && getSessionController().getValidationAutomatique().equals("composante"))
		{
			Composante currentComposante = getDomainService().getComposantesFromBddByAnneeAndSourceAndCodeComposante(getSessionController().getCurrentAnnee(), getSource(), this.currentEtudiant.getComposante());
			if (logger.isDebugEnabled())
				logger.debug("currentComposante-->"+currentComposante);		

			if(currentComposante!=null)
			{
				if (logger.isDebugEnabled())
					logger.debug("if(currentComposante!=null)");
				if(currentComposante.getValidAuto().equals("oui"))
				{
					if (logger.isDebugEnabled())
						logger.debug("if(currentComposante!=null && currentComposante.getValidAuto().equals(oui))");
					this.addAvisFavorable();
				}
				else
				{
					if (logger.isDebugEnabled())
						logger.debug("if(currentComposante!=null && currentComposante.getValidAuto().equals(oui))");				
					this.addDemandeTransfertsSansValidationAuto();
				}
			}
			else
			{
				if (logger.isDebugEnabled())
					logger.debug("if(currentComposante==null)");
				this.addDemandeTransfertsSansValidationAuto();
			}

		}
		else if(!getSessionController().getValidationAutomatique().equals("") && getSessionController().getValidationAutomatique().equals("cge"))
		{
			CGE currentCGE = getDomainService().getCGEFromBddByAnneeAndSourceAndCodeCGE(getSessionController().getCurrentAnnee(), getSource(), this.currentEtudiant.getCodCge());
			if (logger.isDebugEnabled())
				logger.debug("currentCGE-->"+currentCGE);		

			if(currentCGE!=null)
			{
				if (logger.isDebugEnabled())
					logger.debug("if(currentCGE!=null)");
				if(currentCGE.getValidAuto().equals("oui"))
				{
					if (logger.isDebugEnabled())
						logger.debug("if(currentCGE!=null && currentCGE.getValidAuto().equals(oui))");
					this.addAvisFavorable();
				}
				else
				{
					if (logger.isDebugEnabled())
						logger.debug("if(currentCGE!=null && currentCGE.getValidAuto().equals(oui))");				
					this.addDemandeTransfertsSansValidationAuto();
				}
			}
			else
			{
				if (logger.isDebugEnabled())
					logger.debug("if(currentCGE==null)");
				this.addDemandeTransfertsSansValidationAuto();
			}
		}
		else
		{
			if (logger.isDebugEnabled())
				logger.debug("this.addDemandeTransfertsSansValidationAuto()==''-->"+getSessionController().getValidationAutomatique());
			this.addDemandeTransfertsSansValidationAuto();
		}
	}

	public void addDemandeTransfertsSansValidationAuto() throws Exception
	{
		getDomainService().addDemandeTransferts(this.getCurrentEtudiant());
		this.presentBdd=true;
		String sujet2 = getString("MAIL.ETUDIANT.SUJET");
		String body2 = getString("MAIL.ETUDIANT.BODY");
		try {
			body2=getString("MAIL.ETUDIANT.BODY",  this.currentEtudiant.getPrenom1(), this.currentEtudiant.getNomPatronymique());	

			getSmtpService().send(new InternetAddress(this.currentEtudiant.getAdresse().getEmail()),
					sujet2, 
					body2, 
					body2);			
		} 
		catch (AddressException e) 
		{
			String summary = getString("ERREUR.ENVOI_MAIL");
			String detail = getString("ERREUR.ENVOI_MAIL");
			Severity severity=FacesMessage.SEVERITY_INFO;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));			
		}			

		if(!this.getMailInformation().equals(""))
		{
			if(logger.isDebugEnabled())
				logger.debug("Thread.sleep(2000)");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			String sujet = getString("MAIL.INFORMATION.SUJET");
			String body = "";			
			try {
				body=getString("MAIL.INFORMATION.BODY",
						this.currentEtudiant.getNumeroIne(),
						this.currentEtudiant.getNumeroEtudiant(),
						this.currentEtudiant.getPrenom1(), 
						this.currentEtudiant.getNomPatronymique(),
						this.currentEtudiant.getDateNaissance());	

				getSmtpService().send(new InternetAddress(this.getMailInformation()),
						sujet, 
						body, 
						body);			
			} 
			catch (AddressException e) 
			{
				String summary = getString("ERREUR.ENVOI_MAIL");
				String detail = getString("ERREUR.ENVOI_MAIL");
				Severity severity=FacesMessage.SEVERITY_ERROR;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	
			}		
		}
		String summary = getString("ENREGISTREMENT.DEMANDE_TRANSFERT");
		String detail = getString("ENREGISTREMENT.DEMANDE_TRANSFERT");
		Severity severity=FacesMessage.SEVERITY_INFO;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	

		String summary2 = getString("MAIL.ETUDIANT.CONFIRMATION.ENVOI");
		String detail2 = getString("MAIL.ETUDIANT.CONFIRMATION.ENVOI");
		Severity severity2=FacesMessage.SEVERITY_INFO;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity2,summary2, detail2));		
	}

	public void addAvisFavorable() {
		currentAvis = new Avis();
		currentAvis.setNumeroEtudiant(this.currentEtudiant.getNumeroEtudiant());
		currentAvis.setAnnee(getSessionController().getCurrentAnnee());
		currentAvis.setDateSaisie(new Date());
		currentAvis.setIdDecisionDossier(2);
		currentAvis.setIdEtatDossier(1);
		currentAvis.setIdLocalisationDossier(1);

		this.currentEtudiant.getTransferts().setTemoinTransfertValide(2);
		getDomainService().addAvis(currentAvis);
		this.addDemandeTransfertsFromAvis(2);
		this.addTransfertOpiToListeTransfertsAccueil();
		//		this.addTransfertOpi();

		if((!getSessionController().getValidationAutomatique().equals("") && getSessionController().getValidationAutomatique().equals("cge")) 
				|| (!getSessionController().getValidationAutomatique().equals("") && getSessionController().getValidationAutomatique().equals("composante")))
		{
			String sujet = getString("TRANSFERT_AUTO_MAIL_SUJET");
			String body = getString("TRANSFERT_AUTO_MAIL_BODY");
			try {
				body = getString("TRANSFERT_AUTO_MAIL_BODY", this.currentEtudiant.getPrenom1(),
						this.currentEtudiant.getNomPatronymique());

				getSmtpService().send(new InternetAddress(this.currentEtudiant.getAdresse().getEmail()), sujet, body, body);
			}
			catch (AddressException e) 
			{
				String summary = getString("ERREUR.ENVOI_MAIL");
				String detail = getString("ERREUR.ENVOI_MAIL");
				Severity severity = FacesMessage.SEVERITY_INFO;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
			}			
		}
		else
		{
			String sujet = getString("TRANSFERT_MAIL_SUJET");
			String body = getString("TRANSFERT_MAIL_BODY");
			try {
				body = getString("TRANSFERT_MAIL_BODY", this.currentEtudiant.getPrenom1(),
						this.currentEtudiant.getNomPatronymique());

				getSmtpService().send(new InternetAddress(this.currentEtudiant.getAdresse().getEmail()), sujet, body, body);
			}
			catch (AddressException e) 
			{
				String summary = getString("ERREUR.ENVOI_MAIL");
				String detail = getString("ERREUR.ENVOI_MAIL");
				Severity severity = FacesMessage.SEVERITY_INFO;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
			}			
		} 

	}		

	private void addDemandeTransfertsFromAvis(Integer typeAvis) {
		if (logger.isDebugEnabled())
		{
			//getDomainService().getFichierDefautByAnneeAndFrom(getSessionController().getCurrentAnnee(), getSource()).getMd5()
			logger.debug("getDomainService().getFichierDefautByAnneeAndFrom(getSessionController().getCurrentAnnee(), getSource()).getMd5()-->"+getDomainService().getFichierDefautByAnneeAndFrom(getSessionController().getCurrentAnnee(), getSource()).getMd5());
			logger.debug("getSessionController().getCurrentAnnee()-->"+getSessionController().getCurrentAnnee());
			logger.debug("getSource()-->"+getSource());
		}
		this.currentEtudiant.getTransferts().setFichier(getDomainService().getFichierByIdAndAnneeAndFrom(getDomainService().getFichierDefautByAnneeAndFrom(getSessionController().getCurrentAnnee(), getSource()).getMd5(), getSessionController().getCurrentAnnee(), getSource()));
		if (logger.isDebugEnabled()) {
			if (currentAvis != null)
				logger.debug("currentAvis : " + this.currentAvis.toString());
		}
		if (typeAvis == 1)
			this.currentEtudiant.getTransferts().setTemoinTransfertValide(1);
		else
			this.currentEtudiant.getTransferts().setTemoinTransfertValide(2);

		//		if(this.currentEtudiant.getAdresse().getCodPay().equals("100"))
		//		{
		//			this.currentEtudiant.getAdresse().setCodeVilleEtranger(null);
		//			this.currentEtudiant.getAdresse().setCodPay(this.getCodePaysItems());
		//			this.currentEtudiant.getAdresse().setLibPay(getDomainServiceScolarite().getPaysByCodePays(this.getCodePaysItems()).getLibPay());
		//		} 
		//		else 
		//		{
		//			this.currentDemandeTransferts.getAdresse().setCodePostal(null);
		//			this.currentDemandeTransferts.getAdresse().setNomCommune(null);
		//			this.currentDemandeTransferts.getAdresse().setLibPay(getDomainServiceScolarite().getPaysByCodePays(this.currentDemandeTransferts.getAdresse().getCodPay()).getLibPay());
		//		}

		if(this.currentEtudiant.getAdresse().getCodPay().equals("100"))
			this.currentEtudiant.getAdresse().setCodeVilleEtranger(null);
		else
		{
			this.currentEtudiant.getAdresse().setLibPay(getDomainServiceScolarite().getPaysByCodePays(this.currentEtudiant.getAdresse().getCodPay()).getLibPay());
			this.currentEtudiant.getAdresse().setCodePostal(null);
			this.currentEtudiant.getAdresse().setCodeCommune(null);
			this.currentEtudiant.getAdresse().setNomCommune(null);
		}

		getDomainService().addDemandeTransferts(this.currentEtudiant);
		this.presentBdd=true;

		if((!getSessionController().getValidationAutomatique().equals("") && getSessionController().getValidationAutomatique().equals("cge")) 
				|| (!getSessionController().getValidationAutomatique().equals("") && getSessionController().getValidationAutomatique().equals("composante")))
		{
			String summary = getString("ENREGISTREMENT.DEMANDE_TRANSFERT_VALID_AUTO");
			String detail = getString("ENREGISTREMENT.DEMANDE_TRANSFERT_VALID_AUTO");
			Severity severity = FacesMessage.SEVERITY_INFO;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
		}
		else
		{
			String summary = getString("ENREGISTREMENT.DEMANDE_TRANSFERT");
			String detail = getString("ENREGISTREMENT.DEMANDE_TRANSFERT");
			Severity severity = FacesMessage.SEVERITY_INFO;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
		}
	}		

	public void addTransfertOpiToListeTransfertsAccueil()
	{
		if (logger.isDebugEnabled()) 
			logger.debug("addtransfertOpiToListeTransfertAccueil");

		WsPub p = getDomainService().getWsPubByRneAndAnnee(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee());

		/*Initialisation des variables pour TESTS*/
		//		WsPub p = getDomainService().getWsPubByRneAndAnnee("0623957P", getSessionController().getCurrentAnnee());		
		//		String numeroEtudiant = "0904009615X";
		/*Fin d'initialisation des variables pour TESTS*/

		// Appel du WebService de l'universite d'accueil
		if (p != null) 
		{
			Authenticator.setDefault(new MyAuthenticator(p.getIdentifiant(), p.getPassword()));

			if (this.testUrl(p.getUrl())) {
				try {
					String address = p.getUrl();
					JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
					factoryBean.setServiceClass(DomainServiceOpi.class);
					factoryBean.setAddress(address);
					DomainServiceOpi monService = (DomainServiceOpi) factoryBean.create();

					if (logger.isDebugEnabled()) 
						logger.debug("monService-->"+monService);

					EtudiantRef etu = getDomainService().getEtudiantRef(this.currentEtudiant.getNumeroEtudiant(), getSessionController().getCurrentAnnee());

					if(etu!=null)
					{
						etu.setNumeroEtudiant(etu.getNumeroIne());
						etu.getAdresse().setNumeroEtudiant(etu.getNumeroIne());
						etu.getTransferts().setNumeroEtudiant(etu.getNumeroIne());
						InfosAccueil ia = new InfosAccueil();
						/*Initialisation des variables pour TESTS*/
						//					ia.setNumeroEtudiant(numeroEtudiant);
						/*Fin d'initialisation des variables pour TESTS*/
						ia.setNumeroEtudiant(etu.getNumeroIne());
						ia.setAnnee(etu.getAnnee());

						TrResultatVdiVetDTO sessionsResultats = getDomainServiceScolarite().getSessionsResultats(this.currentEtudiant.getNumeroEtudiant(), "D");
						TrBac bac = getDomainServiceScolarite().getBaccalaureat(this.currentEtudiant.getNumeroEtudiant());
						TrInfosAdmEtu trInfosAdmEtu = getDomainServiceScolarite().getInfosAdmEtu(this.currentEtudiant.getNumeroEtudiant());
						TrEtablissementDTO trEtablissementDTO = getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne());

						List<SituationUniversitaire> listeSituationUniversitaire = new ArrayList<SituationUniversitaire>();
						if(!sessionsResultats.getEtapes().isEmpty())
						{
							int i=0;
							for(ResultatEtape re :  sessionsResultats.getEtapes())
							{
								if (logger.isDebugEnabled())
									logger.debug("re.getLibEtape() : " + re.getLibEtape());
								boolean test=true;

								for(ResultatSession rs : re.getSession())
								{
									if (logger.isDebugEnabled())
									{
										logger.debug("re.getSession().size() : " + re.getSession().size());
										logger.debug("re.getSession() : " + re.getSession());
									}

									if(rs.getResultat()!=null && !rs.getResultat().equals(""))
									{
										test=false;
										SituationUniversitaire su = new SituationUniversitaire();
										String timestamp = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
										su.setId(etu.getNumeroIne()+"_"+timestamp+"_P"+i);	
										i++;
										su.setLibAccueilAnnee(re.getAnnee());
										su.setLibelle(re.getLibEtape());									
										su.setLibAccueilResultat(rs.getLibSession()+" - "+rs.getResultat());	
										Integer idAccueilAnnee=0;
										AccueilAnnee aa = getDomainService().getAccueilAnneeByIdAccueilAnnee(idAccueilAnnee);
										Integer idAccueilResultat=0;
										AccueilResultat ar = getDomainService().getAccueilResultatByIdAccueilResultat(idAccueilResultat);
										su.setAnnee(aa);
										su.setResultat(ar);
										listeSituationUniversitaire.add(su);
									}
								}
								if(test)
								{
									SituationUniversitaire su = new SituationUniversitaire();
									String timestamp = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
									su.setId(etu.getNumeroIne()+"_"+timestamp+"_P"+i);	
									i++;
									su.setLibAccueilAnnee(re.getAnnee());
									su.setLibelle(re.getLibEtape());									
									su.setLibAccueilResultat("");	
									Integer idAccueilAnnee=0;
									AccueilAnnee aa = getDomainService().getAccueilAnneeByIdAccueilAnnee(idAccueilAnnee);
									Integer idAccueilResultat=0;
									AccueilResultat ar = getDomainService().getAccueilResultatByIdAccueilResultat(idAccueilResultat);
									su.setAnnee(aa);
									su.setResultat(ar);
									listeSituationUniversitaire.add(su);
								}
							}
						}
						ia.setSituationUniversitaire(listeSituationUniversitaire);
						ia.setFrom_source("P");
						if(bac!=null)
						{
							ia.setAnneeBac(bac.getAnneeObtentionBac());
							ia.setCodeBac(bac.getCodeBac());
						}

						if(trInfosAdmEtu!=null)
							ia.setCodePaysNat(trInfosAdmEtu.getCodPayNat());

						ia.setCodeRneUnivDepart(trEtablissementDTO.getCodeEtb());
						ia.setCodeDepUnivDepart(trEtablissementDTO.getCodeDep());	

						/*Initialisation des variables pour TESTS*/
						//					ia.setCodeRneUnivDepart("0593561A");
						//					ia.setCodeDepUnivDepart("059");					
						ia.setValidationOuCandidature(0);
						/*Fin d'initialisation des variables pour TESTS*/

						etu.setAccueil(ia);

						/*Initialisation des variables pour TESTS*/
						//					etu.setNumeroEtudiant(numeroEtudiant);
						//					etu.setAnnee(this.currentDemandeTransferts.getAnnee());
						//					etu.setNumeroIne(numeroEtudiant);
						//					etu.setNomPatronymique("AIT KARRA");;
						//					etu.setPrenom1("FARID");
						etu.setSource("A");
						//					etu.getAdresse().setNumeroEtudiant(numeroEtudiant);
						//					etu.getAdresse().setAnnee(this.currentDemandeTransferts.getAnnee());
						//					etu.getTransferts().setNumeroEtudiant(numeroEtudiant);
						//					etu.getTransferts().setAnnee(this.currentDemandeTransferts.getAnnee());

						Fichier f = new Fichier();
						f.setFrom("A");
						f.setMd5("ETABLISSEMENT_PARTENAIRE");
						f.setAnnee(this.currentEtudiant.getAnnee());
						f.setNom("ETABLISSEMENT_PARTENAIRE");
						f.setNomSignataire("ETABLISSEMENT_PARTENAIRE");
						f.setTaille(12345);
						etu.getTransferts().setFichier(f);
						etu.getTransferts().setRne(p.getRne());
						etu.getTransferts().setTemoinTransfertValide(0);
						etu.getTransferts().setTemoinOPIWs(null);
						etu.getTransferts().getOdf().setRne(p.getRne());
						/*Fin d'initialisation des variables pour TESTS*/

						monService.addTransfertOpiToListeTransfertsAccueil(etu);

						this.currentEtudiant.getTransferts().setTemoinTransfertValide(2);
						this.currentEtudiant.getTransferts().setTemoinOPIWs(1);

						String summary = getString("ENVOI.OPI");
						String detail = getString("ENVOI.OPI");
						Severity severity = FacesMessage.SEVERITY_INFO;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
					}
					else
					{
						String summary = getString("ERREUR.ETUDIANT_BDD");
						String detail = getString("ERREUR.ETUDIANT_BDD");	
						if (logger.isDebugEnabled())
							logger.debug("Aucun etudiant corresondant a l'INE suivant : "+this.currentEtudiant.getNumeroIne());
						Severity severity = FacesMessage.SEVERITY_FATAL;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));						
					}
				} 
				catch (Exception e) 
				{
					if (logger.isDebugEnabled())
						logger.debug("WebServiceException RNE : " + p.getRne());
					e.printStackTrace();
					this.currentEtudiant.getTransferts().setTemoinTransfertValide(2);
					this.currentEtudiant.getTransferts().setTemoinOPIWs(2);
					String summary = getString("ERREUR.ACCES_OPI2");
					String detail = getString("ERREUR.ACCES_OPI2");
					Severity severity = FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
				}
				if (logger.isDebugEnabled()) {
					logger.debug("getDomainServiceWS : " + p);
					logger.debug("domainServiceWSOpiExt : "	+ domainServiceWSOpiExt);
				}
			} 
			else 
			{
				this.currentEtudiant.getTransferts().setTemoinOPIWs(2);
				this.currentEtudiant.getTransferts().setTemoinTransfertValide(2);
				String summary = getString("ERREUR.ACCES_OPI3");
				String detail = getString("ERREUR.ACCES_OPI3");
				Severity severity = FacesMessage.SEVERITY_ERROR;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
			}
		} 
		else 
		{
			this.currentEtudiant.getTransferts().setTemoinOPIWs(0);
			String summary = getString("WARNING.ETABLISSEMENT_NON_PARTENAIRE");
			String detail = getString("WARNING.ETABLISSEMENT_NON_PARTENAIRE");
			Severity severity = FacesMessage.SEVERITY_WARN;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
		}
		getDomainService().addDemandeTransferts(this.currentEtudiant);
	}	

	public void addTransfertOpi() 
	{
		if (logger.isDebugEnabled()) 
		{
			logger.debug("Cle OPI depart : " + RneModuleBase36.genereCle(getSessionController().getRne()));
			logger.debug("Cle OPI accueil : "+ RneModuleBase36.genereCle(currentEtudiant.getTransferts().getRne()));
		}
		WsPub p = getDomainService().getWsPubByRneAndAnnee(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee());
		// Appel du WebService de l'universite d'accueil
		if (p != null) 
		{
			Authenticator.setDefault(new MyAuthenticator(p.getIdentifiant(), p.getPassword()));

			if (this.testUrl(p.getUrl())) {
				try {
					String address = p.getUrl();
					JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
					factoryBean.setServiceClass(DomainServiceOpi.class);
					factoryBean.setAddress(address);
					DomainServiceOpi monService = (DomainServiceOpi) factoryBean.create();

					this.currentEtudiant.getTransferts().setTemoinOPIWs(1);
					IndOpi opi = getDomainServiceScolarite().getInfosOpi(this.currentEtudiant.getNumeroEtudiant());

					String cleOpi = getDomainService().getCodeSizeByAnnee(getSessionController().getCurrentAnnee()).getCode()	+ RneModuleBase36.genereCle(getSessionController().getRne());
					opi.setNumeroOpi(cleOpi);
					opi.setCodPay(this.currentEtudiant.getAdresse().getCodPay());
					opi.setCodBdi(this.currentEtudiant.getAdresse().getCodePostal());
					opi.setCodCom(this.currentEtudiant.getAdresse().getCodeCommune());
					opi.setLibAd1(this.currentEtudiant.getAdresse().getLibAd1());
					opi.setLibAd2(this.currentEtudiant.getAdresse().getLibAd2());
					opi.setLibAd3(this.currentEtudiant.getAdresse().getLibAd3());
					opi.setLibAde(this.currentEtudiant.getAdresse().getCodeVilleEtranger());
					opi.setNumTel(this.currentEtudiant.getAdresse().getNumTel());
					opi.setNumTelPorOpi(this.currentEtudiant.getAdresse().getNumTelPortable());
					opi.setAdrMailOpi(this.currentEtudiant.getAdresse().getEmail());
					opi.setEtabDepart(getSessionController().getRne());
					opi.setAnnee(getSessionController().getCurrentAnnee());

					opi.getVoeux().setLibNomPatIndOpi(this.currentEtudiant.getNomPatronymique());
					opi.getVoeux().setLibPr1IndOpi(this.currentEtudiant.getPrenom1());

					opi.getVoeux().setCodDip(currentEtudiant.getTransferts().getOdf().getCodeDiplome());
					opi.getVoeux().setCodVrsVdi(currentEtudiant.getTransferts().getOdf().getCodeVersionDiplome());
					opi.getVoeux().setCodCge(currentEtudiant.getTransferts().getOdf().getCodeCentreGestion());
					opi.getVoeux().setCodEtp(currentEtudiant.getTransferts().getOdf().getCodeEtape());
					opi.getVoeux().setCodVrsVet(currentEtudiant.getTransferts().getOdf().getCodeVersionEtape());
					opi.getVoeux().setCodDemDos("C");
					opi.getVoeux().setNumCls("1");
					opi.getVoeux().setCodCmp(currentEtudiant.getTransferts().getOdf().getCodeComposante());

					if (logger.isDebugEnabled()) 
					{
						logger.debug("IndOpi: " + opi);
						logger.debug("WsPub p : " + p);
					}
					monService.addIndOpi(opi);
					this.currentEtudiant.getTransferts().setTemoinTransfertValide(2);
					String summary = getString("ENVOI.OPI");
					String detail = getString("ENVOI.OPI");
					Severity severity = FacesMessage.SEVERITY_INFO;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
				} 
				catch (Exception e) 
				{
					if (logger.isDebugEnabled()) {
						logger.debug("WebServiceException RNE : " + p.getRne());
						logger.debug("-----------------");
						logger.debug(e.getCause().getMessage());
						logger.debug("-----------------");
					}
					e.printStackTrace();
					this.currentEtudiant.getTransferts().setTemoinTransfertValide(2);
					this.currentEtudiant.getTransferts().setTemoinOPIWs(2);
					String summary = getString("ERREUR.ACCES_OPI2");
					String detail = getString("ERREUR.ACCES_OPI2");
					Severity severity = FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
				}
				if (logger.isDebugEnabled()) {
					logger.debug("getDomainServiceWS : " + p);
					logger.debug("domainServiceWSOpiExt : "	+ domainServiceWSOpiExt);
				}
			} 
			else 
			{
				this.currentEtudiant.getTransferts().setTemoinOPIWs(2);
				this.currentEtudiant.getTransferts().setTemoinTransfertValide(2);
				String summary = getString("ERREUR.ACCES_OPI3");
				String detail = getString("ERREUR.ACCES_OPI3");
				Severity severity = FacesMessage.SEVERITY_ERROR;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
			}
		} 
		else 
		{
			this.currentEtudiant.getTransferts().setTemoinOPIWs(0);
			String summary = getString("WARNING.ETABLISSEMENT_NON_PARTENAIRE");
			String detail = getString("WARNING.ETABLISSEMENT_NON_PARTENAIRE");
			Severity severity = FacesMessage.SEVERITY_WARN;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
		}
		getDomainService().addDemandeTransferts(this.currentEtudiant);
	}	

	private boolean testUrl(String host) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(host).openConnection();
			conn.setConnectTimeout(this.getTimeOutConnexionWs());
			conn.connect();
			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (MalformedURLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("MalformedURLException");
				logger.debug("host : " + host);
			}
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("IOException");
				logger.debug("host : " + host);
			}
			e.printStackTrace();
			return false;
		}
	}		

	public String goToUserManagerPage(){
		return "go_userManagerPage";
	}

	public String getCurrentUserLogin(){
		try {
			if (getSessionController().getCurrentUser()!=null)
			{
				if (logger.isDebugEnabled()) {
					logger.debug("Numero etudiant de l'objet User --> " + getSessionController().getCurrentUser().getNumeroEtudiant());
					logger.debug("Nom du champ LDAP numero d'un etudiant dans le config.properties --> " + numeroEtudiant);
					logger.debug("Affiliation d'un etudiant dans le config.properties --> " + this.getStudentAffiliation());
					logger.debug("Affiliation de la personne connect�e --> " + getSessionController().getCurrentUser().getAffiliation());
				}				
				if(this.getStudentAffiliation().contains(getSessionController().getCurrentUser().getAffiliation()))
				{
					if (logger.isDebugEnabled()) {
						logger.debug("L'utilisateur est un etudiant");
					}			
					getSessionController().getCurrentUser().setAuthorized(true);
					setEtudiant(true);
					this.currentEtudiantInterdit = getDomainServiceScolarite().getCurrentEtudiant(getSessionController().getCurrentUser().getNumeroEtudiant());
					if (logger.isDebugEnabled())
						logger.debug("this.currentEtudiantInterdit -->"+this.currentEtudiantInterdit);

					if(this.currentEtudiantInterdit!=null)
					{
						/**
						 * niveau de l'interdit
						 * 1 blocage de la saisie de la demande de transfert
						 * 2 VAP
						 * 3 etc...
						 */						
						List<DatasExterne> listeInterditBu = getDomainService().getAllDatasExterneByIdentifiantAndNiveau(this.currentEtudiantInterdit.getNumeroIne(), 1);

						if(listeInterditBu!=null && !listeInterditBu.isEmpty())
							this.currentEtudiantInterdit.setInterditLocal(true);

						this.existeBdd(getSessionController().getCurrentUser().getNumeroEtudiant());
						if(isPresentBdd() && (!currentEtudiantInterdit.isInterdit() || !currentEtudiantInterdit.isInterditLocal()))
						{
							if (logger.isDebugEnabled()) 
							{
								logger.debug("Demande de transferts existante !!!");
								logger.debug("Aucun interdit !!!");
							}
							this.currentEtudiant = getDomainService().getPresenceEtudiantRef(getSessionController().getCurrentUser().getNumeroEtudiant(), getSessionController().getCurrentAnnee());
							if(this.currentEtudiant.getSource().equals("A"))
							{
								String summary = "Vous ne pouvez pas effectuer une demande de transferts d�part car sous avez deja effecute une demande de transfert accueil";
								String detail = "Vous ne pouveze pas effectuer une demande de transferts d�part car sous avez deja effecute une demande de transfert accueil";
								Severity severity=FacesMessage.SEVERITY_ERROR;
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	
								getSessionController().setError(true);
								return null;
							}
							else
								this.initialiseNomenclatures();
						}
						else if(!isPresentBdd() && (!currentEtudiantInterdit.isInterdit() || !currentEtudiantInterdit.isInterditLocal()))
						{
							if (logger.isDebugEnabled())
							{
								logger.debug("Pas de demande de transferts !!!");
								logger.debug("Aucun interdit !!!");
							}
							if(this.currentEtudiant==null || this.currentEtudiant.getNumeroEtudiant()==null || this.currentEtudiant.getNumeroEtudiant().equals(""))
								this.currentEtudiant = this.currentEtudiantInterdit;
						}

						if(currentEtudiantInterdit.isInterdit() || currentEtudiantInterdit.isInterditLocal())
						{
							if (logger.isDebugEnabled()) {
								logger.debug("L'utilisateur est un etudiant mais a un/des interdit(s)");
							}
							getSessionController().setError(true);
							String summary = "";
							String detail = "";
							String tmp = "";

							for(TrBlocageDTO b : currentEtudiantInterdit.getListeBlocagesDTO())
							{
								tmp += b.getCodeBlocage()+" - "+b.getLibBlocage();
							}
							for(DatasExterne lInterditBu : listeInterditBu)
							{
								tmp += lInterditBu.getLibInterdit();
							}							
							summary = "Vous avez des Interdits : \n" + "- "+ tmp;
							detail = "Vous avez des Interdits : \n" + "- "+ tmp;
							Severity severity=FacesMessage.SEVERITY_ERROR;
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	

							String summary2 = getString("ERREUR.INTERDIT_BU");
							String detail2 = getString("ERREUR.INTERDIT_BU");
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary2, detail2));	
						}
						else
							this.initialiseTransientEtudiantRef();
					}
					else
					{
						String summary = getString("ERREUR.ETUDIANT_BDD");
						String detail = getString("ERREUR.ETUDIANT_BDD");
						Severity severity=FacesMessage.SEVERITY_ERROR;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));						
					}
				}
				else
				{
					if (logger.isDebugEnabled()) {
						logger.debug("L'utilisateur n'est pas un etudiant");
					}				
					getSessionController().setError(true);
					setEtudiant(false);
					String summary = getString("ERREUR.UTILISATEUR_NON_AUTORISE");
					String detail = getString("ERREUR.UTILISATEUR_NON_AUTORISE");
					Severity severity=FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	
				}
				return getSessionController().getCurrentUser().getLogin();
			}
			else
			{
				return "Invite";
			}
		} catch (Exception e) {
			return "Invite";
		}
	}

	public String authApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("ineApogee --> "+ineApogee);
			logger.debug("dateNaissanceApogee --> "+dateNaissanceApogee);
		}
		this.currentEtudiant = new EtudiantRef();
		if(isIneToUpperCase())
			this.currentEtudiant = getDomainServiceScolarite().getCurrentEtudiantIne(ineApogee.toUpperCase(), dateNaissanceApogee);
		else
			this.currentEtudiant = getDomainServiceScolarite().getCurrentEtudiantIne(ineApogee, dateNaissanceApogee);

		if (logger.isDebugEnabled())
			logger.debug("this.currentEtudiant -->"+this.currentEtudiant);

		if(this.currentEtudiant!=null)
		{
			/**
			 * niveau de l'interdit
			 * 1 blocage de la saisie de la demande de transfert
			 * 2 VAP
			 * 3 etc...
			 */			
			List<DatasExterne> listeInterditBu = getDomainService().getAllDatasExterneByIdentifiantAndNiveau(this.currentEtudiant.getNumeroIne(), 1);
			if(listeInterditBu!=null && !listeInterditBu.isEmpty())
			{
				this.currentEtudiant.setInterditLocal(true);
			}

			if(!this.currentEtudiant.isInterdit() && !this.currentEtudiant.isInterditLocal())
			{
				setVerifDateNaisApogee(true);
				this.existeBdd(this.currentEtudiant.getNumeroEtudiant());
				if(isPresentBdd())
				{
					if (logger.isDebugEnabled()) {
						logger.debug("Demande de transferts existante !!!");
					}
					this.currentEtudiant = getDomainService().getPresenceEtudiantRef(this.currentEtudiant.getNumeroEtudiant(), getSessionController().getCurrentAnnee());				
					if(this.currentEtudiant.getSource().equals("A"))
					{
						String summary = "Vous ne pouvez pas effectuer une demande de transferts d�part car vous avez deja effecute une demande de transfert accueil";
						String detail = "Vous ne pouvez pas effectuer une demande de transferts d�part car vous avez deja effecute une demande de transfert accueil";
						Severity severity=FacesMessage.SEVERITY_ERROR;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));						
						return null;
					}
					else
					{
						this.initialiseNomenclatures();
						return "goToRecapitulatifApogee";
					}					
				}
				else
				{
					if (logger.isDebugEnabled()) {
						logger.debug("Pas de demande de transferts !!!");
					}
					this.initialiseTransientEtudiantRef();
					return "goToEtatCivilApogee";
				}
			}
			else
			{
				String tmp = "";

				for(TrBlocageDTO b : this.currentEtudiant.getListeBlocagesDTO())
				{
					tmp += b.getCodeBlocage()+" - "+b.getLibBlocage();
				}
				for(DatasExterne lInterditBu : listeInterditBu)
				{
					tmp += lInterditBu.getLibInterdit();
				}				
				String summary = "Vous avez des Interdits : \n" + "- "+ tmp;
				String detail = "Vous avez des Interdits : \n" + "- "+ tmp;
				Severity severity=FacesMessage.SEVERITY_ERROR;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));
				return null;
			}
		}
		else
		{
			setVerifDateNaisApogee(false);
			String summary = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
			String detail = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
			return null;
		}

	}

	public void initialiseNomenclatures()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("initialiseNomenclatures");
		}

		List<SelectItem> communes = this.getListeCommunes();
		for(int i=0;i<communes.size();i++)
		{
			if(communes.get(i).getValue().equals(this.currentEtudiant.getAdresse().getCodeCommune()))
				this.currentEtudiant.getAdresse().setNomCommune(communes.get(i).getLabel());
		}

		this.initialiseTransientEtudiantRef();

		if(this.currentEtudiant.getTransferts().getTypeTransfert().equals("T"))
			this.currentEtudiant.getTransferts().setLibTypeTransfert("Total");
		else
			this.currentEtudiant.getTransferts().setLibTypeTransfert("Partiel");

		this.currentEtudiant.getTransferts().setLibDept(getDomainServiceScolarite().getEtablissementByRne(this.currentEtudiant.getTransferts().getRne()).getLibDep());
		this.currentEtudiant.getTransferts().setLibRne(getDomainServiceScolarite().getEtablissementByRne(this.currentEtudiant.getTransferts().getRne()).getLibEtb());		
		this.currentEtudiant.getAdresse().setLibPay(getDomainServiceScolarite().getPaysByCodePays(this.currentEtudiant.getAdresse().getCodPay()).getLibPay());
	}

	public void initialiseTransientEtudiantRef()
	{
		/*
		 * map.put("libWebVet", insAdmEtpDTO[i].getEtape().getLibWebVet());
		 * map.put("codeCGE", insAdmEtpDTO[i].getCge().getCodeCGE());
		 * map.put("libCGE", insAdmEtpDTO[i].getCge().getLibCGE());
		 * map.put("codeComposante", insAdmEtpDTO[i].getComposante().getLibComposante());
		 * map.put("libComposante", insAdmEtpDTO[i].getComposante().getCodComposante());
		 * 
		 * */			
		Map<String, String> map = getDomainServiceScolarite().getEtapePremiereAndCodeCgeAndLibCge(currentEtudiant.getNumeroEtudiant()); 
		for (String mapKey : map.keySet()) {
			if(mapKey.equals("libWebVet"))
				currentEtudiant.setLibEtapePremiereLocal(map.get(mapKey));
			if(mapKey.equals("codeCGE"))
				currentEtudiant.setCodCge(map.get(mapKey));		
			if(mapKey.equals("libCGE"))
				currentEtudiant.setLibCge(map.get(mapKey));		
			if(mapKey.equals("codeComposante"))
				currentEtudiant.setComposante(map.get(mapKey));				
			if(mapKey.equals("libComposante"))
				currentEtudiant.setLibComposante(map.get(mapKey));				
		}
	}	

	public Avis getAvisDetails(Integer id)
	{
		List<Avis> listAvis = getDomainService().getAvis(this.currentEtudiant.getNumeroEtudiant(), getSessionController().getCurrentAnnee());
		Avis avis = listAvis.get(0);
		return avis;
	}

	public String goToCas()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToCas");
		}
		this.isDefaultCodeSizeAnnee();
		this.currentEtudiantInterdit = new EtudiantRef();
		this.currentEtudiant = new EtudiantRef();
		setVerifDateNaisApogee(false);
		return "goToCas";
	}

	public String goToWelcomeApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToWelcomeApogee");
		}
		this.currentEtudiantInterdit = new EtudiantRef();
		this.currentEtudiant = new EtudiantRef();
		return "goToWelcomeApogee";
	}		

	public String goToEtatCivil()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToEtatCivil");
		}
		return "goToEtatCivil";
	}		

	public String goToEtatCivilApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToEtatCivilApogee");
		}
		return "goToEtatCivilApogee";
	}		

	public String goToAdresse()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToAdresse");
		}
		return "goToAdresse";
	}	

	public String goToAdresseApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToAdresseApogee");
		}
		if(!this.currentEtudiant.getAdresse().getCodPay().equals("100"))
			this.itemValueCodePay=this.currentEtudiant.getAdresse().getCodPay();
		return "goToAdresseApogee";
	}		

	public String goToListeDesDemandesDeTransferts()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToListeDesDemandesDeTransferts");
		}
		return "goToListeDesDemandesDeTransferts";
	}

	public String goToVoeuxOrientation()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToVoeuxOrientation");
		}
		if((this.currentEtudiant.getAdresse().getNumTel()==null || this.currentEtudiant.getAdresse().getNumTel().equals("")) 
				&& (this.currentEtudiant.getAdresse().getNumTelPortable()==null || this.currentEtudiant.getAdresse().getNumTelPortable().equals("")))
		{
			String summary = getString("ERREUR.TELEPHONE");
			String detail = getString("ERREUR.TELEPHONE");
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));
			return null;
		}
		else
		{
			return "goToVoeuxOrientation";
		}
	}

	public String goToVoeuxOrientationApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToVoeuxOrientationApogee");
		}
		if((this.currentEtudiant.getAdresse().getNumTel()==null || this.currentEtudiant.getAdresse().getNumTel().equals("")) 
				&& (this.currentEtudiant.getAdresse().getNumTelPortable()==null || this.currentEtudiant.getAdresse().getNumTelPortable().equals("")))
		{
			String summary = getString("ERREUR.TELEPHONE");
			String detail = getString("ERREUR.TELEPHONE");
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
			return null;
		}
		else
			return "goToVoeuxOrientationApogee";
	}	

	public String goToRecapitulatif()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToRecapitulatif");
		}
		if(currentOdf!=null && isPartenaire())
		{
			this.initialiseNomenclatures();
			if (logger.isDebugEnabled())
				logger.debug("currentOdf -->"+currentOdf);

			currentEtudiant.getTransferts().setLibelleTypeDiplome(null);
			currentEtudiant.getTransferts().setOdf(currentOdf);
			return "goToRecapitulatif";
		}
		else if(currentOdf==null && (!isPartenaire()||isPresentBdd()))
		{
			this.initialiseNomenclatures();
			currentEtudiant.getTransferts().setOdf(null);
			return "goToRecapitulatif";
		}				
		else
		{
			String summary = getString("ERREUR.PAS_DE_PARCOURS_SELECTIONNE");
			String detail = getString("ERREUR.PAS_DE_PARCOURS_SELECTIONNE");
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));		
			return null;
		}		
	}	

	public String goToRecapitulatifApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToRecapitulatifApogee");
		}
		if(currentOdf!=null && isPartenaire())
		{
			this.initialiseNomenclatures();
			currentEtudiant.getTransferts().setLibelleTypeDiplome(null);
			currentEtudiant.getTransferts().setOdf(currentOdf);
			return "goToRecapitulatifApogee";
		}
		else if(currentOdf==null && !isPartenaire())
		{
			this.initialiseNomenclatures();
			currentEtudiant.getTransferts().setOdf(null);
			return "goToRecapitulatifApogee";
		}				
		else
		{
			String summary = getString("ERREUR.PAS_DE_PARCOURS_SELECTIONNE");
			String detail = getString("ERREUR.PAS_DE_PARCOURS_SELECTIONNE");
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));		
			return null;
		}		
	}		

	public boolean isPartenaire()
	{
		boolean partenaire = false;
		if (currentEtudiant.getTransferts().getRne() != null) {
			List<WsPub> listeEtablissementsPartenaires = getDomainService().getWsPubByAnnee(getSessionController().getCurrentAnnee());
			for(WsPub eu : listeEtablissementsPartenaires)
			{
				if(currentEtudiant.getTransferts().getRne().equals(eu.getRne()))
					partenaire = true;
			}
		}
		return partenaire;
	}	

	public void setCurrentEtudiant(EtudiantRef currentEtudiant) {
		this.currentEtudiant = currentEtudiant;
	}

	public EtudiantRef getCurrentEtudiant() {
		if(this.currentEtudiant == null)
			this.currentEtudiant = new EtudiantRef();

		return this.currentEtudiant;
	}

	public void setListeCommunes(List<SelectItem> listeCommunes) {
		this.listeCommunes = listeCommunes;
	}

	public List<SelectItem> getListeCommunes() {
		if (logger.isDebugEnabled()) {
			logger.debug("public List<SelectItem> getListeCommunes()");
			logger.debug("getDomainServiceScolarite().getCommunes(getCurrentEtudiant().getAdresse().getCodePostal());");
			logger.debug("this.currentEtudiant.getAdresse().getCodePostal() -----> "+ this.currentEtudiant.getAdresse().getCodePostal());
			logger.debug("this.currentEtudiant.getAdresse().getNomCommune() -----> "+ this.currentEtudiant.getAdresse().getNomCommune());
		}
		List<SelectItem> communes = new ArrayList<SelectItem>();
		List<TrCommuneDTO> listeCommunes = getDomainServiceScolarite().getCommunes(this.currentEtudiant.getAdresse().getCodePostal());
		if(listeCommunes!=null)
		{
			for(TrCommuneDTO cDTO : listeCommunes)
			{				
				SelectItem option = new SelectItem(cDTO.getCodeCommune(), cDTO.getLibCommune());
				communes.add(option);
			}		
			Collections.sort(communes,new ComparatorSelectItem());
		}
		else
		{
			SelectItem option = new SelectItem("", "");
			communes.add(option);				
		}
		return communes;
	}	

	public void setListePays(List<SelectItem> listePays) {
		this.listePays = listePays;
	}

	public List<SelectItem> getListePays() {
		if (logger.isDebugEnabled()) {
			logger.debug("public List<SelectItem> getListePays()");
			logger.debug("getDomainServiceScolarite().getListePays();");
		}
		List<SelectItem> listePays = new ArrayList<SelectItem>();
		List<TrPaysDTO> listePaysDTO = getDomainServiceScolarite().getListePays();
		if(listePaysDTO!=null)
		{
			for(TrPaysDTO pDTO : listePaysDTO)
			{			
				SelectItem option = new SelectItem(pDTO.getCodePay(), pDTO.getLibPay());
				listePays.add(option);
			}	
			Collections.sort(listePays,new ComparatorSelectItem());
		}
		else
		{
			SelectItem option = new SelectItem("", "");
			listePays.add(option);	
		}
		return listePays;
	}

	public List<SelectItem> getListeDepartements() {
		if (logger.isDebugEnabled()) {
			logger.debug("public List<SelectItem> getListeDepartements()");
			logger.debug("getDomainServiceScolarite().getListeDepartements();");
		}
		List<SelectItem> listeDepartements = new ArrayList<SelectItem>();
		List<TrDepartementDTO> listeDepartementDTO = getDomainServiceScolarite().getListeDepartements();	
		if(listeDepartementDTO!=null)
		{
			for(TrDepartementDTO dDTO : listeDepartementDTO)
			{			
				SelectItem option = new SelectItem(dDTO.getCodeDept(), dDTO.getLibDept()+" ("+dDTO.getCodeDept()+")");
				listeDepartements.add(option);
			}	
			Collections.sort(listeDepartements,new ComparatorSelectItem());
		}
		else
		{
			SelectItem option = new SelectItem("", "");
			listeDepartements.add(option);	
		}
		return listeDepartements;		
	}	

	public List<SelectItem> getListeEtablissements() {
		if (logger.isDebugEnabled()) {
			logger.debug("public List<SelectItem> getListeEtablissements() --> " + currentEtudiant.getTransferts().getDept());
			logger.debug("getDomainServiceScolarite().getListeEtablissements(typesEtablissementSplit, currentDemandeTransferts.getTransferts().getDept());	");
		}
		if(!isDeptVide())
		{
			if (logger.isDebugEnabled())
				logger.debug("if(listeEtablissements==null) --> " + listeEtablissements);

			listeEtablissements = new ArrayList<SelectItem>();
			for (String typesEtablissementSplit : getTypesEtablissementListSplit()) 
			{
				List<TrEtablissementDTO> etablissementDTO = getDomainServiceScolarite().getListeEtablissements(typesEtablissementSplit, currentEtudiant.getTransferts().getDept());
				if (etablissementDTO != null) 
				{
					for (TrEtablissementDTO eDTO : etablissementDTO) 
					{
						if (logger.isDebugEnabled())
							logger.debug("etablissementDTO : " + etablissementDTO);

						if (!eDTO.getCodeEtb().equals(getSessionController().getRne())) 
						{
							SelectItem option = new SelectItem(eDTO.getCodeEtb(), eDTO.getLibEtb());
							listeEtablissements.add(option);
						}
					}
					Collections.sort(listeEtablissements, new ComparatorSelectItem());
				} else {
					if (logger.isDebugEnabled())
						logger.debug("etablissementDTO == null");
				}
			}
		}
		return listeEtablissements;
	}			

	public List<SelectItem> getListeTypesDiplome() {
		if (logger.isDebugEnabled()) {
			logger.debug("public List<SelectItem> getListeTypesDiplome()");
			logger.debug("getDomainService().getOdfTypesDiplomeByRneAndAnnee(etu.getTransferts().getRne(), getSessionController().getCurrentAnnee());");
		}
		if(!isTypesDiplomeVide())
		{
			if(listeTypesDiplome==null)
				logger.debug("if(listeTypesDiplome==null) --> " + listeTypesDiplome);		
			listeTypesDiplome = new ArrayList<SelectItem>();
			Map<String, String> listeTypesDiplomeDTO = getDomainService().getOdfTypesDiplomeByRneAndAnnee(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), true, "D");			
			if(listeTypesDiplomeDTO!=null && !listeTypesDiplomeDTO.isEmpty())
			{
				if (logger.isDebugEnabled()) {
					logger.debug("listeTypesDiplomeDTO : "+listeTypesDiplomeDTO);
				}
				for (String mapKey : listeTypesDiplomeDTO.keySet()) {
					// utilise ici hashMap.get(mapKey) pour acc�der aux valeurs
					SelectItem option = new SelectItem(mapKey, listeTypesDiplomeDTO.get(mapKey));
					listeTypesDiplome.add(option);					
				}				
				Collections.sort(listeTypesDiplome,new ComparatorSelectItem());
				return listeTypesDiplome;
			}
			else
				return null;
		}
		return listeTypesDiplome;
	}		

	public List<SelectItem> getListeAnneesEtude() {
		if (logger.isDebugEnabled())
			logger.debug("public List<SelectItem> getListeAnneesEtude()");

		listeAnneesEtude = new ArrayList<SelectItem>();
		Map<Integer, String> listeAnneesEtudeDTO = getDomainService().getAnneesEtudeByRneAndAnneeAndCodTypDip(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(), true, "D");

		if(listeAnneesEtudeDTO!=null && !listeAnneesEtudeDTO.isEmpty())
		{
			if (logger.isDebugEnabled()) {
				logger.debug("listeAnneesEtudeDTO : "+listeAnneesEtudeDTO);
			}
			for(Integer mapKey : listeAnneesEtudeDTO.keySet())
			{
				SelectItem option = new SelectItem(mapKey, listeAnneesEtudeDTO.get(mapKey));
				listeAnneesEtude.add(option);
			}
			Collections.sort(listeAnneesEtude,new ComparatorSelectItem());
			return listeAnneesEtude;
		}
		else
			return null;
	}		

	public List<SelectItem> getListeLibellesDiplome() {
		if (logger.isDebugEnabled())
			logger.debug("public List<SelectItem> getListeLibellesDiplome()");
		listeLibellesDiplome = new ArrayList<SelectItem>();
		Map<String, String> listeLibellesDiplomeDTO = null;
		if (logger.isDebugEnabled())
		{
			logger.debug("listeLibellesDiplomeDTO par diplome");
			logger.debug("getDomainService().getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveau(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(), getCodeNiveau(), true, D);");
			System.out.println("###################################### --> "+currentEtudiant.getTransferts().getRne()+"-----"+getSessionController().getCurrentAnnee()+"-----"+getCodTypDip()+"-----"+getCodeNiveau()+"-----"+true+"-----D");								
		}
		listeLibellesDiplomeDTO = getDomainService().getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveau(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(), getCodeNiveau(), true, "D");

		if(listeLibellesDiplomeDTO!=null && !listeLibellesDiplomeDTO.isEmpty())
		{
			if (logger.isDebugEnabled())
				logger.debug("listeLibellesDiplomeDTO : "+listeLibellesDiplomeDTO);
			for(String mapKey : listeLibellesDiplomeDTO.keySet())
			{
				SelectItem option = new SelectItem(mapKey, listeLibellesDiplomeDTO.get(mapKey));
				listeLibellesDiplome.add(option);
			}			
			Collections.sort(listeLibellesDiplome,new ComparatorSelectItem());
			return listeLibellesDiplome;
		}
		else
		{
			if (logger.isDebugEnabled())
				logger.debug("listeLibellesDiplomeDTO = null !!!");	
			return null;
		}

	}			

	public List<OffreDeFormationsDTO> getListeLibellesEtape() {
		if(getSessionController().isChoixDuVoeuParComposante())
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("public List<SelectItem> getListeLibellesEtape()");
				logger.debug("(etu.getTransferts() --> "+currentEtudiant.getTransferts().getRne() +"-----"+ getSessionController().getCurrentAnnee() +"-----"+ getCodTypDip() +"-----"+ getCodeNiveau() +"-----"+ getCodeComposante()+"-----D");
				logger.debug("(getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposante(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(),  getCodeNiveau(), getCodeComposante(), D)");			
			}
			return getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposante(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(),  getCodeNiveau(), getCodeComposante(), "D");			
		}
		else
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("public List<SelectItem> getListeLibellesEtape()");
				logger.debug("(etu.getTransferts() --> "+currentEtudiant.getTransferts().getRne() +"-----"+ getSessionController().getCurrentAnnee() +"-----"+ getCodTypDip() +"-----"+ getCodeNiveau() +"-----"+ getCodeDiplome()+"-----D");
				logger.debug("(getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip --> "+getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(),  getCodeNiveau(), getCodeDiplome(),"D").size());
			}
			return getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(),  getCodeNiveau(), getCodeDiplome(),"D");
		}
	}	

	/**
	 * Impression d'une personne
	 * @return PDF
	 */	
	public String imprimerDemandeTransfert(){
		/**
		 * @return String
		 */
		String retour = null;
		try	{
			/**
			 **  Methodes de creation des documents PDF selon l'edition demandee
			 **/
			String xslXmlPath = this.getXmlXslPath();
			String fileNameXml=generateXml();
			String fileNameXsl="etudiant.xsl";
			String fileNamePdf=this.currentEtudiant.getNumeroEtudiant()+""+this.currentEtudiant.getAnnee()+".pdf";
			PDFUtils.exportPDF(fileNameXml,FacesContext.getCurrentInstance(), 
					xslXmlPath, fileNamePdf, fileNameXsl);
		} catch (Exception f) {
			logger.error("ExportException ", f.fillInStackTrace());		
		}
		return retour;
	}			

	public String generateXml()
	{
		String nameXml = this.currentEtudiant.getNumeroEtudiant()+""+this.currentEtudiant.getAnnee()+".xml";
		try {
			// crÃ©ation d'un contexte JAXB sur la classe Marin
			JAXBContext context = JAXBContext.newInstance(EtudiantRefImp.class) ;

			// crÃ©ation d'un marshaller Ã  partir de ce contexte
			Marshaller marshaller = context.createMarshaller() ;

			// on choisit UTF-8 pour encoder ce fichier
			marshaller.setProperty("jaxb.encoding", "UTF-8") ;
			// et l'on demande Ã  JAXB de formatter ce fichier de faÃ§on 
			// Ã  pouvoir le lire Ã  l'oeil nu
			marshaller.setProperty("jaxb.formatted.output", true) ;

			//getSessionController().getRne();

			TrResultatVdiVetDTO trResultatVdiVetDTO;
			trResultatVdiVetDTO = getDomainServiceScolarite().getSessionsResultats(this.currentEtudiant.getNumeroEtudiant(), "D");

			Fichier file = getDomainService().getFichierByIdAndAnneeAndFrom(this.currentEtudiant.getTransferts().getFichier().getMd5(), getSessionController().getCurrentAnnee(), "D");

			if (logger.isDebugEnabled()) {
				logger.debug("file ----------------- " + file);
			}	

			String nom = this.getTempPath()+""+file.getNom();

			File fichierExiste = new File(nom);
			if(!fichierExiste.exists())
			{
				if (logger.isDebugEnabled()) {
					logger.debug("L'image n'existe pas");
				}					
				byte[] data = file.getImg();
				this.genererImage(nom, data);				
			}
			else
			{
				if (logger.isDebugEnabled()) {
					logger.debug("Génération de l'image");
				}					
			}

			//			this.currentEtudiant.getTransferts().getFichier().setChemin(nom);
			//			this.currentEtudiant.getTransferts().getFichier().setImg(new byte[0]);

			this.initialiseNomenclatures();

			EtudiantRefImp etudiantRefImp = new EtudiantRefImp(this.currentEtudiant.getNumeroEtudiant(), 
					this.currentEtudiant.getNumeroIne(), 
					this.currentEtudiant.getNomPatronymique(), 
					this.currentEtudiant.getNomUsuel(), 
					this.currentEtudiant.getPrenom1(), 
					this.currentEtudiant.getPrenom2(), 
					this.currentEtudiant.getDateNaissance(), 
					this.currentEtudiant.getLibNationalite(), 
					this.currentEtudiant.getAdresse(), 
					this.currentEtudiant.getTransferts(),
					getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()),
					getDomainServiceScolarite().getEtablissementByRne(this.currentEtudiant.getTransferts().getRne()),
					getDomainServiceScolarite().getBaccalaureat(this.currentEtudiant.getNumeroEtudiant()),
					trResultatVdiVetDTO);

			//			etudiantRefImp.getUniversiteDepart().setAdresseEtablissement(getDomainService().getAdresseEtablissementByRne(getSessionController().getRne()));
			//			etudiantRefImp.getUniversiteAccueil().setAdresseEtablissement(getDomainService().getAdresseEtablissementByRne(this.currentEtudiant.getTransferts().getRne()));

			etudiantRefImp.getTransferts().setFichier(getDomainService().getFichierByIdAndAnneeAndFrom(this.currentEtudiant.getTransferts().getFichier().getMd5(), getSessionController().getCurrentAnnee(), "D"));
			this.currentEtudiant.getTransferts().getFichier().setChemin(nom);
			this.currentEtudiant.getTransferts().getFichier().setImg(new byte[0]);

			Avis dernierAvis = getDomainService().getDernierAvisFavorable(this.currentEtudiant.getNumeroEtudiant(), getSessionController().getCurrentAnnee());
			if(dernierAvis.getId()!=0)
			{
				dernierAvis.setLibEtatDossier(getDomainService().getEtatDossierById(dernierAvis.getIdEtatDossier()).getLibelleLongEtatDossier());
				dernierAvis.setLibLocalisationDossier(getDomainService().getLocalisationDossierById(dernierAvis.getIdLocalisationDossier()).getLibelleLongLocalisationDossier());
				dernierAvis.setLibDecisionDossier(getDomainService().getDecisionDossierById(dernierAvis.getIdDecisionDossier()).getLibelleLongDecisionDossier());
			}
			etudiantRefImp.setAvis(dernierAvis);			

			etudiantRefImp.setDateDuJour(new Date());
			//			GregorianCalendar gCalendar = new GregorianCalendar();
			//			gCalendar.setTime(new Date());
			//			try {
			//				XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
			//				
			//			} catch (DatatypeConfigurationException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}			

			// Ecriture finale du document XML dans un fichier etudiant.xml
			marshaller.marshal(etudiantRefImp, new File(this.getXmlXslPath()+nameXml)) ;

		} catch (JAXBException ex) {
			ex.printStackTrace();
		}
		//return "etudiant.xml";
		return nameXml;
	}

	public void genererImage(String nom, byte[] data)
	{
		final int BUFFER_SIZE = 6124;
		InputStream in = new ByteArrayInputStream(data);

		File result = new File(nom);

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(result);

			byte[] buffer = new byte[BUFFER_SIZE];

			int bulk;
			InputStream inputStream = in;
			while (true) {
				bulk = inputStream.read(buffer);
				if (bulk < 0) {
					break;
				}
				fileOutputStream.write(buffer, 0, bulk);
				fileOutputStream.flush();
			}

			fileOutputStream.close();
			inputStream.close();

			if (logger.isDebugEnabled()) {
				logger.debug("Génération de l'image");
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String getNumeroEtudiant() {
		return numeroEtudiant;
	}

	public void setNumeroEtudiant(String numeroEtudiant) {
		this.numeroEtudiant = numeroEtudiant;
	}

	public void setEtudiant(boolean etudiant) {
		this.etudiant = etudiant;
	}

	public boolean isEtudiant() {
		return etudiant;
	}

	public void setVerifDateNaisApogee(boolean verifDateNaisApogee) {
		this.verifDateNaisApogee = verifDateNaisApogee;
	}

	public boolean isVerifDateNaisApogee() {
		return verifDateNaisApogee;
	}

	public void setTypesDiplomeVide(boolean typesDiplomeVide) {
		this.typesDiplomeVide = typesDiplomeVide;
	}

	public boolean isTypesDiplomeVide() {
		return typesDiplomeVide;
	}

	public void setRneVide(boolean rne) {
		this.rneVide = rne;
	}

	public boolean isRneVide() {
		return rneVide;
	}

	public void setDeptVide(boolean deptVide) {
		this.deptVide = deptVide;
	}

	public boolean isDeptVide() {
		return deptVide;
	}

	public void setAnneeEtudeVide(boolean anneeEtudeVide) {
		AnneeEtudeVide = anneeEtudeVide;
	}

	public boolean isAnneeEtudeVide() {
		return AnneeEtudeVide;
	}

	public void setLibelleDiplomeVide(boolean libelleDiplomeVide) {
		this.libelleDiplomeVide = libelleDiplomeVide;
	}

	public boolean isLibelleDiplomeVide() {
		return libelleDiplomeVide;
	}

	public void setTypesDiplomeAutreVide(boolean typesDiplomeAutreVide) {
		this.typesDiplomeAutreVide = typesDiplomeAutreVide;
	}

	public boolean isTypesDiplomeAutreVide() {
		return typesDiplomeAutreVide;
	}

	public void existeBdd(String numeroEtudiant)
	{
		if(getDomainService().getPresenceEtudiantRef(numeroEtudiant, getSessionController().getCurrentAnnee())!=null)
			setPresentBdd(true);
		else
			setPresentBdd(false);
	}

	public boolean isPresentBdd() 
	{
		return this.presentBdd;
	}

	public void setPresentBdd(boolean presentBdd) {
		this.presentBdd = presentBdd;
	}

	public void setIneApogee(String ineApogee) {
		this.ineApogee = ineApogee;
	}

	public String getIneApogee() {
		return ineApogee;
	}

	public void setDateNaissanceApogee(Date dateNaissanceApogee) {
		this.dateNaissanceApogee = dateNaissanceApogee;
	}

	public Date getDateNaissanceApogee() {
		return dateNaissanceApogee;
	}

	public void setCurrentEtudiantInterdit(EtudiantRef currentEtudiantInterdit) {
		this.currentEtudiantInterdit = currentEtudiantInterdit;
	}

	public EtudiantRef getCurrentEtudiantInterdit() {
		return currentEtudiantInterdit;
	}

	public void setSelectedFichier(Fichier selectedFichier) {
		this.selectedFichier = selectedFichier;
	}

	public Fichier getSelectedFichier() {
		return selectedFichier;
	}

	public List<Integer> getListeAnnees() 
	{
		return getDomainService().getListeAnnees();
	}

	public void setListeAvisByNumeroEtudiantAndAnnee(List<Avis> listeAvisByNumeroEtudiantAndAnnee)
	{
		this.listeAvisByNumeroEtudiantAndAnnee = listeAvisByNumeroEtudiantAndAnnee;
	}

	public List<Avis> getListeAvisByNumeroEtudiantAndAnnee() 
	{
		List<Avis> lAvis = getDomainService().getAvis(this.currentEtudiant.getNumeroEtudiant(), getSessionController().getCurrentAnnee());
		for(Avis a : lAvis)
		{
			a.setLibEtatDossier(getDomainService().getEtatDossierById(a.getIdEtatDossier()).getLibelleLongEtatDossier());
			a.setLibLocalisationDossier(getDomainService().getLocalisationDossierById(a.getIdLocalisationDossier()).getLibelleLongLocalisationDossier());
			a.setLibDecisionDossier(getDomainService().getDecisionDossierById(a.getIdDecisionDossier()).getLibelleLongDecisionDossier());
		}
		return lAvis;
	}

	public void setSelectedAvis(Avis selectedAvis) {
		this.selectedAvis = selectedAvis;
	}

	public Avis getSelectedAvis() {
		return selectedAvis;
	}

	public void setLibelleEtapeVide(boolean libelleEtapeVide) {
		this.libelleEtapeVide = libelleEtapeVide;
	}

	public boolean isLibelleEtapeVide() {
		return libelleEtapeVide;
	}

	public void setMailInformation(String mailInformation) {
		this.mailInformation = mailInformation;
	}

	public String getMailInformation() {
		return mailInformation;
	}

	public void setItemValueCodePay(String itemValueCodePay) {
		this.itemValueCodePay = itemValueCodePay;
	}

	public String getItemValueCodePay() {
		return itemValueCodePay;
	}

	public boolean isIneToUpperCase() {
		return ineToUpperCase;
	}

	public void setIneToUpperCase(boolean ineToUpperCase) {
		this.ineToUpperCase = ineToUpperCase;
	}

	public void setDefaultCodeSize(CodeSizeAnnee defaultCodeSize) {
		this.defaultCodeSize = defaultCodeSize;
	}

	public CodeSizeAnnee getDefaultCodeSize() {
		return defaultCodeSize;
	}

	public void setDefaultCodeSizeAnnee(boolean defaultCodeSizeAnnee) {
		this.defaultCodeSizeAnnee = defaultCodeSizeAnnee;
	}

	public boolean isDefaultCodeSizeAnnee() {
		this.defaultCodeSize=getDomainService().getCodeSizeDefaut();
		if(this.defaultCodeSize!=null)
		{
			setDefaultCodeSizeAnnee(true);
			//			getSessionController().setAnneeSerieImmatriculation(this.defaultCodeSize.getAnnee().toString());
			getSessionController().setNumeroSerieImmatriculation(this.defaultCodeSize.getCode());
			getSessionController().setAnnee(this.defaultCodeSize.getAnnee());
			getSessionController().setCurrentAnnee(this.defaultCodeSize.getAnnee());
		}
		return defaultCodeSizeAnnee;
	}

	public void setParametreAppli(Parametres parametreAppli) {
		this.parametreAppli = parametreAppli;
	}

	public Parametres getParametreAppli() {
		this.parametreAppli=getDomainService().getParametreByCode("ouvertureDepart");
		if(this.parametreAppli!=null)
		{
			if (logger.isDebugEnabled()) {
				logger.debug("this.parametreAppli.getCodeParametre --> "+this.parametreAppli.getCodeParametre());
				logger.debug("this.parametreAppli.getBool() --> "+this.parametreAppli.isBool());
				logger.debug("this.parametreAppli.getCommentaire() --> "+this.parametreAppli.getCommentaire());
			}
		}
		else
		{
			if (logger.isDebugEnabled()) {
				logger.debug("Aucun parametre nome 'ouverture' trouve dans la table parametre");
			}
			this.parametreAppli=new Parametres();
			this.parametreAppli.setBool(false);
		}
		return parametreAppli;
	}

	public String getStudentAffiliation() {
		return studentAffiliation;
	}

	public void setStudentAffiliation(String studentAffiliation) {
		this.studentAffiliation = studentAffiliation;
	}

	public String getCodTypDip() {
		return codTypDip;
	}

	public void setCodTypDip(String codTypDip) {
		this.codTypDip = codTypDip;
	}

	public Integer getCodeNiveau() {
		return codeNiveau;
	}

	public void setCodeNiveau(Integer codeNiveau) {
		this.codeNiveau = codeNiveau;
	}

	public String getCodeDiplome() {
		return codeDiplome;
	}

	public void setCodeDiplome(String codeDiplome) {
		this.codeDiplome = codeDiplome;
	}

	public OffreDeFormationsDTO getCurrentOdf() {
		return currentOdf;
	}

	public void setCurrentOdf(OffreDeFormationsDTO currentOdf) {
		this.currentOdf = currentOdf;
	}

	public void setListeLibellesEtape(List<OffreDeFormationsDTO> list) {
		this.listeLibellesEtape = list;
	}

	public OdfDataModel getOdfDataModel() {
		if(listeLibellesEtape!=null)
		{
			if(odfDataModel==null)
				odfDataModel = new OdfDataModel(listeLibellesEtape);
			return odfDataModel;
		}
		else
			return new OdfDataModel();
	}

	public void setOdfDataModel(OdfDataModel odfDataModel) {
		this.odfDataModel = odfDataModel;
	}

	public void setListeEtablissements(List<SelectItem> listeEtablissements) {
		this.listeEtablissements = listeEtablissements;
	}

	public void setListeTypesDiplome(List<SelectItem> listeTypesDiplome) {
		this.listeTypesDiplome = listeTypesDiplome;
	}

	public void setListeAnneesEtude(List<SelectItem> listeAnneesEtude) {
		this.listeAnneesEtude = listeAnneesEtude;
	}

	public void setListeLibellesDiplome(List<SelectItem> listeLibellesDiplome) {
		this.listeLibellesDiplome = listeLibellesDiplome;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Avis getCurrentAvis() {
		return currentAvis;
	}

	public void setCurrentAvis(Avis currentAvis) {
		this.currentAvis = currentAvis;
	}

	public Integer getTimeOutConnexionWs() {
		return timeOutConnexionWs;
	}

	public void setTimeOutConnexionWs(Integer timeOutConnexionWs) {
		this.timeOutConnexionWs = timeOutConnexionWs;
	}

	public DomainServiceOpi getDomainServiceWSOpiExt() {
		return domainServiceWSOpiExt;
	}

	public void setDomainServiceWSOpiExt(DomainServiceOpi domainServiceWSOpiExt) {
		this.domainServiceWSOpiExt = domainServiceWSOpiExt;
	}

	public Parametres getParametreAppliInfosDepart() 
	{
		this.parametreAppliInfosDepart=getDomainService().getParametreByCode("informationDepart");
		if(this.parametreAppliInfosDepart!=null)
		{
			if (logger.isDebugEnabled()) {
				logger.debug("this.parametreAppliInfosDepart.getCodeParametre --> "+this.parametreAppliInfosDepart.getCodeParametre());
				logger.debug("this.parametreAppliInfosDepart.getBool() --> "+this.parametreAppliInfosDepart.isBool());
				logger.debug("this.parametreAppliInfosDepart.getCommentaire() --> "+this.parametreAppliInfosDepart.getCommentaire());
			}
		}
		else
		{
			if (logger.isDebugEnabled()) {
				logger.debug("Aucun parametre nome 'informationDepart' trouve dans la table parametre");
			}
			this.parametreAppliInfosDepart=new Parametres();
			this.parametreAppliInfosDepart.setBool(false);
		}
		return parametreAppliInfosDepart;
	}

	public void setParametreAppliInfosDepart(Parametres parametreAppliInfosDepart) {
		this.parametreAppliInfosDepart = parametreAppliInfosDepart;
	}

	public String getAideTypeTransfert() {
		aideTypeTransfert = getString("AIDE.TYPE_TANSFERT", getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()).getLibOffEtb());
		return aideTypeTransfert;
	}

	public void setAideTypeTransfert(String aideTypeTransfert) {
		this.aideTypeTransfert = aideTypeTransfert;
	}

	public boolean isComposanteVide() {
		return composanteVide;
	}

	public void setComposanteVide(boolean composanteVide) {
		this.composanteVide = composanteVide;
	}

	public String getCodeComposante() {
		return codeComposante;
	}

	public void setCodeComposante(String codeComposante) {
		this.codeComposante = codeComposante;
	}

	public List<SelectItem> getListeComposantes() {
		if (logger.isDebugEnabled())
			logger.debug("getListeComposantes");

		listeComposantes = new ArrayList<SelectItem>();
		//		Map<String, String> listeComposantesDTO = getDomainService().getOdfComposanteByRneAndAnneeAndActif(getSessionController().getRne(), getSessionController().getCurrentAnnee());
		//		Map<String, String> listeComposantesDTO = getDomainService().getOdfComposanteByRneAndAnneeAndActifAndArrivee(getSessionController().getRne(), getSessionController().getCurrentAnnee());
//		Map<String, String> listeComposantesDTO = getDomainService().getOdfComposanteByRneAndAnneeAndActifAndArriveeAndCodTypDip(this.currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip());
		Map<String, String> listeComposantesDTO = getDomainService().getOdfComposanteByRneAndAnneeAndDepartOuArriveeAndCodTypDip(this.currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(), getSource());
		if(listeComposantesDTO!=null && !listeComposantesDTO.isEmpty())
		{
			if (logger.isDebugEnabled()) {
				logger.debug("listeComposantesDTO : "+listeComposantesDTO);
			}
			for(String mapKey : listeComposantesDTO.keySet())
			{
				SelectItem option = new SelectItem(mapKey, listeComposantesDTO.get(mapKey));
				listeComposantes.add(option);
			}			
			Collections.sort(listeComposantes,new ComparatorSelectItem());
			return listeComposantes;
		}
		else
			return null;
	}	

	public void setListeComposantes(List<SelectItem> listeComposantes) {
		this.listeComposantes = listeComposantes;
	}
}