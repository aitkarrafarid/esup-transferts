/**
 * ESUP-Portail Blank Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-transferts-accueil
 */
package org.esupportail.transferts.accueil.web.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.AccueilAnnee;
import org.esupportail.transferts.domain.beans.AccueilDecision;
import org.esupportail.transferts.domain.beans.AccueilResultat;
import org.esupportail.transferts.domain.beans.AdresseRef;
import org.esupportail.transferts.domain.beans.Avis;
import org.esupportail.transferts.domain.beans.CodeSizeAnnee;
import org.esupportail.transferts.domain.beans.Correspondance;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.EtudiantRefImp;
import org.esupportail.transferts.domain.beans.Fichier;
import org.esupportail.transferts.domain.beans.IndOpi;
import org.esupportail.transferts.domain.beans.InfosAccueil;
import org.esupportail.transferts.domain.beans.DatasExterne;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.Parametres;
import org.esupportail.transferts.domain.beans.PersonnelComposante;
import org.esupportail.transferts.domain.beans.SituationUniversitaire;
import org.esupportail.transferts.domain.beans.TrBac;
import org.esupportail.transferts.domain.beans.TrBlocageDTO;
import org.esupportail.transferts.domain.beans.TrCommuneDTO;
import org.esupportail.transferts.domain.beans.TrDepartementDTO;
import org.esupportail.transferts.domain.beans.TrEtablissementDTO;
import org.esupportail.transferts.domain.beans.TrPaysDTO;
import org.esupportail.transferts.domain.beans.TrSituationUniversitaire;
import org.esupportail.transferts.domain.beans.WsPub;
import org.esupportail.transferts.utils.CheckBEA23;
import org.esupportail.transferts.utils.CheckNNE36;
import org.esupportail.transferts.accueil.web.comparator.ComparatorDateTimeCorrespondance;
import org.esupportail.transferts.accueil.web.comparator.ComparatorDateTimeAccueilDecision;
import org.esupportail.transferts.accueil.web.comparator.ComparatorSelectItem;
import org.esupportail.transferts.accueil.web.controllers.AbstractContextAwareController;
import org.esupportail.transferts.accueil.web.dataModel.OdfDataModel;
import org.esupportail.transferts.accueil.web.dataModel.SituationUniversitaireDataModel;
import org.esupportail.transferts.accueil.web.utils.PDFUtils;
import org.springframework.util.Assert;

/**
 * A bean to memorize the context of the application.
 */
public class UserController extends AbstractContextAwareController {

	/*
	 ******************* PROPERTIES ******************** */

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1084603912306407867L;
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());	
	private CodeSizeAnnee defaultCodeSize;
	private boolean defaultCodeSizeAnnee=false;
	private boolean verifDateNaisApogee;
	private Parametres parametreAppli;
	private String ineApogee;
	private Date dateNaissanceApogee;
	private EtudiantRef currentEtudiant = null;
	private EtudiantRef newEtudiant = null;	
	private boolean etudiant;
	private boolean ineToUpperCase;
	private boolean presentBdd;
	private List<SelectItem> listeCommunes = new ArrayList<SelectItem>();
	private String itemValueCodePay;
	private List<SelectItem> listePays = new ArrayList<SelectItem>();	
	private boolean typesDiplomeVide = true;
	private boolean libelleDiplomeVide = true;
	private boolean libelleEtapeVide = true;	
	private boolean AnneeEtudeVide = true;	
	private boolean composanteVide = true;
	private String codTypDip;
	private Integer codeNiveau;		
	private String codeDiplome;
	private List<SelectItem> listeTypesDiplome = null;
	private List<SelectItem> listeAnneesEtude = null;
	private List<SelectItem> listeLibellesDiplome = null;
	private List<OffreDeFormationsDTO> listeLibellesEtape;		
	private OffreDeFormationsDTO currentOdf;
	private OdfDataModel odfDataModel;		
	private List<SelectItem> listeEtablissements = null;
	private boolean deptVide = true;	
	private SituationUniversitaire currentSituationUniv;
	private AccueilAnnee currentAccueilAnnee;
	private AccueilResultat currentAccueilResultat;
	private String currentCleAccueilAnnee;
	private String currentCleAccueilResultat;	
	private boolean maxSU;
	private SituationUniversitaire selectedSituationUniv;
	private SituationUniversitaireDataModel sudm;
	private String mailInformation;
	private boolean etabPartenaireSaisieDepart;
	private List<SelectItem> listeComposantes;
	private String codeComposante;
	private List<AccueilDecision> listeAccueilDecision = null;
	private Parametres parametreAppliInfosAccueil;
	private String aideTypeTransfert;
	private List<TrSituationUniversitaire> lTrSituationUniversitaire;
	private List<Correspondance> listeCorrespondances = null;
	/*
	 ******************* INIT ******************** */

	/**
	 * Constructor.
	 */
	public UserController() {
		super();
	}

	@Override
	public void afterPropertiesSetInternal() {
		super.afterPropertiesSetInternal();
		Assert.notNull(ineToUpperCase, "property ineToUpperCase of class "
				+ this.getClass().getName() + " can not be null");
	}

	public String goToWelcomeApogee()
	{
		if (logger.isDebugEnabled())
			logger.debug("goToWelcomeApogee");
		this.currentEtudiant = new EtudiantRef();
		return "goToWelcomeApogee";
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
			String fileNameXsl="etudiant_accueil.xsl";
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
		String nameXml = this.currentEtudiant.getNumeroEtudiant() + ""+ this.currentEtudiant.getAnnee() + ".xml";
		try {
			// creation d'un contexte JAXB sur la classe EtudiantRefImp
			JAXBContext context = JAXBContext.newInstance(EtudiantRefImp.class);
			// creation d'un marshaller a partir de ce contexte
			Marshaller marshaller = context.createMarshaller();
			// on choisit UTF-8 pour encoder ce fichier
			marshaller.setProperty("jaxb.encoding", "UTF-8");
			// et l'on demande à JAXB de formatter ce fichier de facon
			// a� pouvoir le lire a l'oeil nu
			marshaller.setProperty("jaxb.formatted.output", true);

			if (logger.isDebugEnabled())
				logger.debug("this.currentEtudiant.toString()-->"+this.currentEtudiant.toString());			

			Fichier file=null;

			if(this.currentEtudiant.getTransferts()!=null && this.currentEtudiant.getTransferts().getFichier()!=null)
				file = getDomainService().getFichierByIdAndAnneeAndFrom(this.currentEtudiant.getTransferts().getFichier().getMd5(),getSessionController().getCurrentAnnee(), this.currentEtudiant.getSource());

			if (logger.isDebugEnabled())
				logger.debug("file-->"+file);			

			if(file==null)
				file = getDomainService().getFichierDefautByAnneeAndFrom(getSessionController().getCurrentAnnee(), this.currentEtudiant.getSource());		

			String nom = this.getTempPath() + "" + file.getMd5();

			File fichierExiste = new File(nom);
			if (!fichierExiste.exists()) {
				if (logger.isDebugEnabled())
					logger.debug("L'image n'existe pas");
				byte[] data = file.getImg();
				this.genererImage(nom, data);
			}
			else 
			{
				if (logger.isDebugEnabled())
					logger.debug("L'image existe deja");
			}			

			this.initialiseNomenclatures();
			EtudiantRefImp etudiantRefImp = new EtudiantRefImp();
			etudiantRefImp.setNumeroEtudiant(this.currentEtudiant.getNumeroEtudiant());
			etudiantRefImp.setNumeroIne(this.currentEtudiant.getNumeroIne());
			etudiantRefImp.setNomPatronymique(this.currentEtudiant.getNomPatronymique());
			etudiantRefImp.setNomUsuel(this.currentEtudiant.getNomUsuel());
			etudiantRefImp.setPrenom1(this.currentEtudiant.getPrenom1());
			etudiantRefImp.setPrenom2(this.currentEtudiant.getPrenom2());
			etudiantRefImp.setDateNaissance(this.currentEtudiant.getDateNaissance());
			etudiantRefImp.setLibNationalite(this.currentEtudiant.getLibNationalite());
			etudiantRefImp.setAdresse(this.currentEtudiant.getAdresse());
			etudiantRefImp.setTransferts(this.currentEtudiant.getTransferts());
			etudiantRefImp.getTransferts().setFichier(file);
			this.currentEtudiant.getTransferts().getFichier().setChemin(nom);
			this.currentEtudiant.getTransferts().getFichier().setImg(new byte[0]);
			etudiantRefImp.setTrBac(getDomainServiceScolarite().recupererBacOuEquWS(this.currentEtudiant.getAccueil().getCodeBac()).get(0));
			etudiantRefImp.getTrBac().setAnneeObtentionBac(this.currentEtudiant.getAccueil().getAnneeBac());
			etudiantRefImp.setUniversiteDepart(getDomainServiceScolarite().getEtablissementByRne(this.currentEtudiant.getAccueil().getCodeRneUnivDepart()));
			etudiantRefImp.setUniversiteAccueil(getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()));
			//			etudiantRefImp.getUniversiteDepart().setAdresseEtablissement(getDomainService().getAdresseEtablissementByRne(this.currentEtudiant.getAccueil().getCodeRneUnivDepart()));
			//			etudiantRefImp.getUniversiteAccueil().setAdresseEtablissement(getDomainService().getAdresseEtablissementByRne(getSessionController().getRne()));
			List<TrSituationUniversitaire> lTrSU = new ArrayList<TrSituationUniversitaire>();
			for(SituationUniversitaire su : this.currentEtudiant.getAccueil().getSituationUniversitaire())
			{
				String annee = "";
				String resultat = "";
				if(su.getAnnee().getIdAccueilAnnee()!=0)
					annee = su.getAnnee().getLibelle();
				else
					annee = su.getLibAccueilAnnee();
				if(su.getResultat().getIdAccueilResultat()!=0)
					resultat = su.getResultat().getLibelle();
				else
					resultat = su.getLibAccueilResultat();				
				//lTrSU.add(new TrSituationUniversitaire(su.getId(), su.getAnnee().getLibelle(), su.getLibelle(), su.getResultat().getLibelle()));
				lTrSU.add(new TrSituationUniversitaire(su.getId(), annee, su.getLibelle(), resultat));
			}
			etudiantRefImp.setSituationUniversitaire(lTrSU);
			etudiantRefImp.setDateDuJour(new Date());

			listeAccueilDecision=null;

			if(this.currentEtudiant.getAccueilDecision()!=null && !this.currentEtudiant.getAccueilDecision().isEmpty())
			{
				if (logger.isDebugEnabled())
					logger.debug("Decision saisie");
				etudiantRefImp.setCodeDecision(this.getListeAccueilDecision().get(0).getAvis());
				etudiantRefImp.setDecision(this.getListeAccueilDecision().get(0).getDecision());
			}
			else
				if (logger.isDebugEnabled())
					logger.debug("Pas de decision saisie");			

			etudiantRefImp.setAnneeUniversitaire(getSessionController().getCurrentAnnee()+ "/"+ (getSessionController().getCurrentAnnee() + 1));
			marshaller.marshal(etudiantRefImp, new File(this.getXmlXslPath()+nameXml)) ;
		} 
		catch (JAXBException ex) 
		{
			ex.printStackTrace();
		}
		return nameXml;
	}		

	public void genererImage(String nom, byte[] data) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		

	public void addDemandeTransferts() throws Exception
	{
		this.currentEtudiant.setSource("A");

		if(this.currentEtudiant.getBddScol()==0)
		{
			this.currentEtudiant.setNomPatronymique(this.currentEtudiant.getNomPatronymique().toUpperCase());
			this.currentEtudiant.setNomUsuel(this.currentEtudiant.getNomUsuel().toUpperCase());
			this.currentEtudiant.setPrenom1(this.currentEtudiant.getPrenom1().toUpperCase());
			this.currentEtudiant.setPrenom2(this.currentEtudiant.getPrenom2().toUpperCase());
		}

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
		this.currentEtudiant.getAccueil().setAnnee(getSessionController().getCurrentAnnee());
		this.currentEtudiant.getAccueil().setNumeroEtudiant(this.currentEtudiant.getNumeroEtudiant());
		this.currentEtudiant.getAccueil().setFrom_source("L");
		getDomainService().addDemandeTransferts(this.getCurrentEtudiant());
		this.presentBdd=true;
		String sujet = getString("MAIL.ETUDIANT.SUJET");
		try {
			String body=getString("MAIL.ETUDIANT.BODY",  this.currentEtudiant.getPrenom1(), this.currentEtudiant.getNomPatronymique());	
			getSmtpService().send(new InternetAddress(this.currentEtudiant.getAdresse().getEmail()),
					sujet, 
					body, 
					body);			
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
				e1.printStackTrace();
			}			
			String sujet2 = getString("MAIL.INFORMATION.SUJET");
			String body2 = "";			
			try {
				body2=getString("MAIL.INFORMATION.BODY",
						this.currentEtudiant.getNumeroIne(),
						this.currentEtudiant.getNumeroEtudiant(),
						this.currentEtudiant.getNomPatronymique(),
						this.currentEtudiant.getPrenom1(), 
						this.currentEtudiant.getDateNaissance());	

				getSmtpService().send(new InternetAddress(this.getMailInformation()),
						sujet2, 
						body2, 
						body2);			
			} 
			catch (AddressException e) 
			{
				String summary = getString("ERREUR.ENVOI_MAIL");
				String detail = getString("ERREUR.ENVOI_MAIL");
				Severity severity=FacesMessage.SEVERITY_ERROR;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	
			}		
		}
		String summary = getString("ENREGISTREMENT.DEMANDE_TRANSFERT_ACCUEIL");
		String detail = getString("ENREGISTREMENT.DEMANDE_TRANSFERT_ACCUEIL");
		Severity severity=FacesMessage.SEVERITY_INFO;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));	

		String summary2 = getString("MAIL.ETUDIANT.CONFIRMATION.ENVOI");
		String detail2 = getString("MAIL.ETUDIANT.CONFIRMATION.ENVOI");
		Severity severity2=FacesMessage.SEVERITY_INFO;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity2,summary2, detail2));		
	}	

	public String goToRecapitulatifApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToRecapitulatifApogee");
		}
		if(currentOdf!=null)
		{
			this.initialiseNomenclatures();
			currentEtudiant.getTransferts().setLibelleTypeDiplome(null);
			currentEtudiant.getTransferts().setOdf(currentOdf);
			currentEtudiant.setSource("A");
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

	public void deleteSelectedSituationUniv()
	{
		if(selectedSituationUniv!=null)
		{
			if (logger.isDebugEnabled())
				logger.debug("################ selectedSituationUniv.getLibelle() ################ >"+selectedSituationUniv.getLibelle()+"<");
			this.currentEtudiant.getAccueil().getSituationUniversitaire().remove(selectedSituationUniv);
			selectedSituationUniv=null;
		}
		else
		{
			String summary = getString("ERREUR.ANNEE_ETUDE");
			String detail = getString("ERREUR.ANNEE_ETUDE");			
			//			String summary = "Vous devez selectionner une annee d'etude";
			//			String detail = "Vous devez selectionner une annee d'etude";
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
		}
	}

	public void ajouterSituationUniv()
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("################ currentCleAccueilAnnee ################ --> "+currentCleAccueilAnnee);
			logger.debug("################ currentCleAccueilResultat ################ --> "+currentCleAccueilResultat);
			logger.debug("################ currentSituationUniv.getLibelle() ################ >"+currentSituationUniv.getLibelle()+"<");
		}
		if(this.currentEtudiant.getAccueil().getSituationUniversitaire()==null)
		{
			List<SituationUniversitaire> lSitUniv = new ArrayList<SituationUniversitaire>();
			this.currentEtudiant.getAccueil().setSituationUniversitaire(lSitUniv);
		}
		if(currentCleAccueilAnnee!=null && !currentCleAccueilAnnee.equals("") 
				&& currentCleAccueilResultat!=null && !currentAccueilResultat.equals("") 
				&& currentSituationUniv.getLibelle()!=null && !currentSituationUniv.getLibelle().equals(""))
		{
			currentAccueilAnnee = getDomainService().getAccueilAnneeById(Integer.parseInt(currentCleAccueilAnnee));
			currentAccueilResultat = getDomainService().getAccueilResultatById(Integer.parseInt(currentCleAccueilResultat));
			String timestamp = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
			currentSituationUniv.setId(this.currentEtudiant.getNumeroEtudiant()+"_"+timestamp);
			currentSituationUniv.setAnnee(currentAccueilAnnee);
			currentSituationUniv.setResultat(currentAccueilResultat);
			this.currentEtudiant.getAccueil().getSituationUniversitaire().add(currentSituationUniv);
			/*Reinitialisation des currents objects*/
			currentCleAccueilAnnee=null;
			currentCleAccueilResultat=null;	
			AccueilAnnee currentAccueilAnnee = new AccueilAnnee();
			currentAccueilResultat = new AccueilResultat();			
			currentSituationUniv = new SituationUniversitaire();
		}
		else
		{
			String summary = getString("ERREUR.CHAMP_OBLIGATOIRE_SITUATION_UNIVERSITAIRE");
			String detail = getString("ERREUR.CHAMP_OBLIGATOIRE_SITUATION_UNIVERSITAIRE");
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));					
		}
	}

	public String goToSituationUniversitaire()
	{
		if (logger.isDebugEnabled())
			logger.debug("goToSituationUniversitaire");

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
			currentAccueilAnnee = new AccueilAnnee();
			currentAccueilResultat = new AccueilResultat();			
			currentSituationUniv = new SituationUniversitaire();
			setTypesDiplomeVide(false);
			return "goToSituationUniversitaire";
		}		
	}		

	public void resetGeneral()
	{
		if (logger.isDebugEnabled())
			logger.debug("public void resetGeneral() --> "+ this.currentEtudiant.getTransferts().getDept());

		if(this.currentEtudiant.getAccueil().getCodeDepUnivDepart() !=null && !this.currentEtudiant.getAccueil().getCodeDepUnivDepart().equals(""))  
		{
			setDeptVide(false);
			currentEtudiant.getAccueil().setCodeRneUnivDepart(null);
		}
		else
		{
			setDeptVide(true);
			this.listeEtablissements=null;
		}
	}	

	public void resetAnneeEtude()
	{
		if (logger.isDebugEnabled())
			logger.debug("public void resetAnneeEtude()");		

		if(getCodTypDip() !=null && !getCodTypDip().equals(""))  
		{
			setTypesDiplomeVide(false);
			setCodeNiveau(null);
			setLibelleEtapeVide(true);
			setAnneeEtudeVide(false);
			setLibelleDiplomeVide(true);
			setComposanteVide(true);
			this.getListeAnneesEtude();  
		}
		else
		{
			setAnneeEtudeVide(true);
			setLibelleDiplomeVide(true);
			this.setListeComposantes(null);
			this.setListeTypesDiplome(null);
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
		//this.getListeLibellesDiplome(); 
	}

	//	public void resetLibelleDiplome()
	//	{
	//		if (logger.isDebugEnabled())
	//			logger.debug("public void resetLibelleDiplome()");
	//
	//		if(getCodeNiveau() !=null && !getCodeNiveau().equals(""))  
	//		{
	//			setTypesDiplomeVide(false);
	//			setAnneeEtudeVide(false);
	//			setCodeDiplome(null);
	//			setLibelleEtapeVide(true);	
	//			setLibelleDiplomeVide(false);
	//			this.getListeLibellesDiplome(); 
	//		}
	//		else
	//		{
	//			setLibelleDiplomeVide(true);
	//			this.listeLibellesDiplome=null;
	//			this.listeLibellesEtape=null;
	//		}
	//	}

	public void resetLibelleEtape()
	{
		if (logger.isDebugEnabled())
			logger.debug("public void resetLibelleEtape()");

		if(getCodeNiveau() !=null && !getCodeNiveau().equals(""))  
		{
			//setTypesDiplomeVide(false);
			setAnneeEtudeVide(false);
			setLibelleDiplomeVide(false);
			setLibelleEtapeVide(false);
			setOdfDataModel(null);
			setCurrentOdf(new OffreDeFormationsDTO());
			setListeLibellesEtape(this.getListeLibellesEtape());
		}
		else
		{
			setLibelleEtapeVide(true);
			this.listeLibellesEtape=null;
		}
	}				

	public void resetLibelleEtapeSansCodeComposante()
	{
		if (logger.isDebugEnabled())
			logger.debug("public void resetLibelleEtape()");

		if(getCodeDiplome() !=null && !getCodeDiplome().equals(""))  
		{
			setTypesDiplomeVide(false);
			setAnneeEtudeVide(false);
			setLibelleDiplomeVide(false);
			setLibelleEtapeVide(false);
			setOdfDataModel(null);
			setCurrentOdf(new OffreDeFormationsDTO());
			setListeLibellesEtape(this.getListeLibellesEtape());
		}
		else
		{
			setLibelleEtapeVide(true);
			this.listeLibellesEtape=null;
		}
	}			

	public String goToVoeuxOrientationApogee()
	{
		if (logger.isDebugEnabled())
			logger.debug("goToVoeuxOrientationApogee");
		if(this.currentEtudiant.getAccueil().getSituationUniversitaire()!=null && this.currentEtudiant.getAccueil().getSituationUniversitaire().size()!=0)
			return "goToVoeuxOrientationApogee";
		else
		{
			String summary = getString("ERREUR.SITUATION_UNIVERSITAIRE");
			String detail = getString("ERREUR.SITUATION_UNIVERSITAIRE");
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));		
			return null;
		}			
	}		

	public String goToEtatCivilApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("goToEtatCivilApogee");
		}
		return "goToEtatCivilApogee";
	}		

	public String goToAdresseApogee()
	{
		if (logger.isDebugEnabled())
			logger.debug("goToAdresseApogee");
		if(currentEtudiant!=null && !this.currentEtudiant.getAdresse().getCodPay().equals("100"))
			this.itemValueCodePay=this.currentEtudiant.getAdresse().getCodPay();
		return "goToAdresseApogee";
	}		

	public String verifieBeaOrIne()
	{
		currentEtudiant=null;
		newEtudiant=null;
		String retour=null;
		String numero = getIneApogee();
		numero = getIneApogee().toUpperCase();

		if (numero.length()==11)
		{
			String lettreCle = numero.substring(10, numero.length());
			String numeroSansCle = numero.substring(0, 10);

			if (numero.length()==11 && numeroSansCle.matches("[0-9]+") && lettreCle.matches("[a-zA-Z]+") && !lettreCle.matches("[ioqIOQ]+"))
			{		
				if(CheckBEA23.verifie(numero)==0)
				{
					if (logger.isDebugEnabled())
						logger.debug("################## Numero BEA OK #####################");
					retour=this.authApogee();
				}
				else if(CheckBEA23.verifie(numero)==3)
				{
					if (logger.isDebugEnabled())
						logger.debug("################## !!! 3 cle invalide, <> cle calcule !!! #####################");	
					String summary = getString("ERREUR.VERIFICATION_BEA");
					String detail = getString("ERREUR.VERIFICATION_BEA");				
					Severity severity=FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));						
				}
				else
				{
					String summary = getString("ERREUR.FATAL.VERIFICATION_BEA");
					String detail = getString("ERREUR.FATAL.VERIFICATION_BEA");
					Severity severity=FacesMessage.SEVERITY_FATAL;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));					
				}
			}
			else if (numero.length()==11 && numero.matches("[0-9a-zA-Z]+") && !lettreCle.matches("[ioqIOQ]+"))
			{
				if(CheckNNE36.verifie(numero)==0)
				{
					if (logger.isDebugEnabled())
						logger.debug("##################  Numero INE OK #####################");			
					retour=this.authApogee();
				}
				else if(CheckNNE36.verifie(numero)==3)
				{
					if (logger.isDebugEnabled())
						logger.debug("################## !!! 3 cle invalide, <> cle calcule !!! #####################");				
					String summary = getString("ERREUR.VERIFICATION_INE");
					String detail = getString("ERREUR.VERIFICATION_INE");					
					Severity severity=FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));					
				}
				else
				{
					String summary = getString("ERREUR.FATAL.VERIFICATION_INE");
					String detail = getString("ERREUR.FATAL.VERIFICATION_INE");				
					Severity severity=FacesMessage.SEVERITY_FATAL;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));					
				}			
			}
			else
			{
				String summary = getString("ERREUR.INVALIDE_INE_BEA");
				String detail = getString("ERREUR.INVALIDE_INE_BEA");		
				Severity severity=FacesMessage.SEVERITY_ERROR;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));
			}
		}
		else
		{
			String summary = getString("ERREUR.INVALIDE_INE_BEA");
			String detail = getString("ERREUR.INVALIDE_INE_BEA");		
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));
		}		
		return retour;
	}	

	public boolean isDefaultCodeSizeAnnee() {
		this.defaultCodeSize=getDomainService().getCodeSizeDefaut();
		if(this.defaultCodeSize!=null)
		{
			setDefaultCodeSizeAnnee(true);
			getSessionController().setCurrentAnnee(this.defaultCodeSize.getAnnee());
		}
		return defaultCodeSizeAnnee;
	}

	public String goToAuthApogee()
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("goToAuthApogee");
			logger.debug("getSessionController().isEditionPdfAccueilSansDecision()-->"+getSessionController().isEditionPdfAccueilSansDecision());
		}
		getSessionController().resetController();
		return "goToAuthApogee";
	}	

	public String getCurrentUserLogin(){
		return "Invite";
	}	

	public String authApogee()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("ineApogee --> "+ineApogee);
			logger.debug("dateNaissanceApogee --> "+dateNaissanceApogee);
		}
		String ine="";

		if(isIneToUpperCase())
			ine=ineApogee.toUpperCase();
		else
			ine=ineApogee;

		this.currentEtudiant = new EtudiantRef();
		/* 0 == INE et date de naissance correcte
		 * 1 == INE ok mais date de naissance incorrect
		 * 2 == Introuvable dans apogee
		 * */
		Integer authEtuScol = getDomainServiceScolarite().getAuthEtu(ine, dateNaissanceApogee);
		if (logger.isDebugEnabled())
			logger.debug("getDomainServiceScolarite().getAuthEtu(ine, dateNaissanceApogee)--->"+authEtuScol);

		//IndOpi etu = getDomainService().getPresenceEtudiantOPiByIneAndAnnee(ine, getSessionController().getCurrentAnnee());
		IndOpi etu = null;

		if(etu==null)
		{
			this.currentEtudiant = getDomainService().getPresenceEtudiantRefByIne(ine, getSessionController().getCurrentAnnee());	
			if(this.currentEtudiant!=null)
			{
				if (logger.isDebugEnabled())
					logger.debug("Etudiant present dans la table des transferts");					
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");				
				if(dateFormat.format(dateNaissanceApogee).equals(dateFormat.format(this.currentEtudiant.getDateNaissance())))
				{
					if (logger.isDebugEnabled())
						logger.debug("Etudiant present dans la table des transferts - Compare date OK");

					this.presentBdd=true;
					setVerifDateNaisApogee(true);
					if(this.currentEtudiant.getSource().equals("D"))
					{
						if (logger.isDebugEnabled())
							logger.debug("Etudiant present dans la table des transferts - demande de transfert depart existante !!!");						
						String summary = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE");
						String detail = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE");						
						Severity severity=FacesMessage.SEVERITY_ERROR;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));						
						return null;					
					}
					else
					{
						if (logger.isDebugEnabled())
							logger.debug("goToRecapitulatifApogee");
						this.currentEtudiant = getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource(this.currentEtudiant.getNumeroEtudiant(), this.currentEtudiant.getAnnee(), this.currentEtudiant.getSource());
						this.initialiseNomenclatures();
						return "goToRecapitulatifApogee";
					}
				}
				else
				{
					if (logger.isDebugEnabled())
						logger.debug("Etudiant present dans la table des transferts - Compare date FAUX !!!");
					setVerifDateNaisApogee(false);
					String summary = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
					String detail = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
					Severity severity=FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
					return null;					
				}
			}
			else
			{
				this.currentEtudiant = getDomainServiceScolarite().getCurrentEtudiantIne(ine, dateNaissanceApogee);
				if(this.currentEtudiant!=null && authEtuScol==0)
				{
					if (logger.isDebugEnabled())
						logger.debug("Etudiant dans present dans APOGEE - Compare date OK");

					/**
					 * niveau de l'interdit
					 * 1 blocage de la saisie de la demande de transfert
					 * 2 Pas de blocage de la saisie mais une alert coté gestionnaire
					 * 3 etc...
					 */	
					List<DatasExterne> listeInterditBu = getDomainService().getAllDatasExterneByIdentifiantAndNiveau(this.currentEtudiant.getNumeroIne(), 1);

					if(listeInterditBu!=null && !listeInterditBu.isEmpty())
					{
						if (logger.isDebugEnabled())
							logger.debug("Etudiant a des interdits");				
						this.currentEtudiant.setInterditLocal(true);
					}

					if(!this.currentEtudiant.isInterdit() && !this.currentEtudiant.isInterditLocal())
					{
						setVerifDateNaisApogee(true);
						if (logger.isDebugEnabled())
							logger.debug("Pas de demande de transferts !!!");
						this.presentBdd=false;
						setEtabPartenaireSaisieDepart(false);
						currentEtudiant.setBddScol(1);
						currentEtudiant.getTransferts().setDept(getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()).getCodeDep());
						currentEtudiant.getTransferts().setRne(getSessionController().getRne());
						//						getDomainServiceScolarite().getDerniereIAByNumeroEtudiant(this.currentEtudiant.getNumeroEtudiant());	
						return "goToEtatCivilApogee";
					}
					else
					{
						String tmp = "";

						for(TrBlocageDTO b : this.currentEtudiant.getListeBlocagesDTO())
						{
							tmp += b.getCodeBlocage()+" - "+b.getLibBlocage();
						}
						if(listeInterditBu !=null)
						{
							for(DatasExterne lInterditBu : listeInterditBu)
							{
								tmp += lInterditBu.getLibInterdit();
							}
						}
						String summary = "Vous avez des Interdits : \n" + "- "+ tmp;
						String detail = "Vous avez des Interdits : \n" + "- "+ tmp;
						Severity severity=FacesMessage.SEVERITY_ERROR;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));
						return null;
					}					
				}
				else if(this.currentEtudiant==null && authEtuScol==1)
				{
					if (logger.isDebugEnabled())
						logger.debug("Etudiant dans pr�sent dans APOGEE - Compare date FAUX !!!");
					setVerifDateNaisApogee(false);
					String summary = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
					String detail = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
					Severity severity=FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
					return null;						
				}
				else if(this.currentEtudiant==null  && authEtuScol==2)
				{
					if (logger.isDebugEnabled())
					{
						logger.debug("Etudiant absent d APOGEE");	
						logger.debug("Etudiant absent des tables OPI et transferts");	
						logger.debug("goToEtatCivilApogee");
						logger.debug("ine-->"+ine);
					}			

					//					List<DatasExterne> listeInterditVAP = null;
					List<DatasExterne> listeInterditVAP = getDomainService().getAllDatasExterneByIdentifiantAndNiveau(ine, 1);

					if(listeInterditVAP==null || listeInterditVAP.isEmpty())
					{				
						if (logger.isDebugEnabled())
							logger.debug("if(listeInterditVAP==null || listeInterditVAP.isEmpty())");	

						this.presentBdd=false;
						setVerifDateNaisApogee(true);
						setEtabPartenaireSaisieDepart(false);
						currentEtudiant = new EtudiantRef();
						InfosAccueil ia = new InfosAccueil();
						currentEtudiant.setAccueil(ia);
						currentEtudiant.setNumeroIne(ine);
						currentEtudiant.setNumeroEtudiant(getIneApogee().toUpperCase());
						currentEtudiant.getAdresse().setNumeroEtudiant(getIneApogee().toUpperCase());
						currentEtudiant.getTransferts().setNumeroEtudiant(getIneApogee().toUpperCase());
						currentEtudiant.setDateNaissance(getDateNaissanceApogee());
						currentEtudiant.setBddScol(0);
						currentEtudiant.getTransferts().setDept(getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()).getCodeDep());
						currentEtudiant.getTransferts().setRne(getSessionController().getRne());
						currentEtudiant.getAdresse().setCodPay("100");
						return "goToEtatCivilApogee";
					}
					else
					{
						if (logger.isDebugEnabled())
							logger.debug("if(listeInterditVAP!=null && !listeInterditVAP.isEmpty())");

						String tmp = "";
						for(DatasExterne lInterditVAP : listeInterditVAP)
						{
							tmp += lInterditVAP.getLibInterdit();
							if (logger.isDebugEnabled())
								logger.debug("Libellé de l'interdit-->"+tmp);							
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
					String summary = "Probleme interne - veuillez contacter l'administrateur de l'application";
					String detail = "Probleme interne - veuillez contacter l'administrateur de l'application";
					Severity severity=FacesMessage.SEVERITY_ERROR;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
					return null;	
				}
			}
		}
		else
		{
			if (logger.isDebugEnabled())
				logger.debug("Etudiant dans pr�sent dans les tables OPI");			
			String summary = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE_OPI");
			String detail = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE_OPI");
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));						
			return null;			
		}
	}	

	//	public String authApogee2()
	//	{
	//		if (logger.isDebugEnabled()) {
	//			logger.debug("ineApogee --> "+ineApogee);
	//			logger.debug("dateNaissanceApogee --> "+dateNaissanceApogee);
	//		}
	//		String ine="";
	//
	//		if(isIneToUpperCase())
	//			ine=ineApogee.toUpperCase();
	//		else
	//			ine=ineApogee;
	//		this.currentEtudiant = new EtudiantRef();
	//
	//		/* 0 == INE et date de naissance correcte
	//		 * 1 == INE ok mais date de naissance incorrect
	//		 * 2 == Introuvable dans apogee
	//		 * */
	//		Integer authEtuScol = getDomainServiceScolarite().getAuthEtu(ine, dateNaissanceApogee);
	//		if (logger.isDebugEnabled())
	//			logger.debug("getDomainServiceScolarite().getAuthEtu(ine, dateNaissanceApogee)--->"+authEtuScol);
	//
	//		this.currentEtudiant = getDomainServiceScolarite().getCurrentEtudiantIne(ine, dateNaissanceApogee);
	//		IndOpi etu = getDomainService().getPresenceEtudiantOPiByIneAndAnnee(ine, getSessionController().getCurrentAnnee());
	//
	//		if(this.currentEtudiant!=null)
	//		{
	//			if (logger.isDebugEnabled())
	//				logger.debug("Etudiant dans pr�sent dans APOGEE");
	//
	//			List<InterditBu> listeInterditBu = getDomainService().getInterditBu(this.currentEtudiant.getNumeroEtudiant());
	//			if(listeInterditBu!=null && !listeInterditBu.isEmpty())
	//			{
	//				if (logger.isDebugEnabled())
	//					logger.debug("Etudiant a des interdits");				
	//				this.currentEtudiant.setInterditLocal(true);
	//			}
	//
	//			if(!this.currentEtudiant.isInterdit() && !this.currentEtudiant.isInterditLocal())
	//			{
	//				setVerifDateNaisApogee(true);
	//				this.existeBdd(this.currentEtudiant.getNumeroEtudiant());
	//				if(isPresentBdd() || etu!=null)
	//				{
	//					if (logger.isDebugEnabled()) {
	//						logger.debug("Demande de transferts existante !!!");
	//					}
	//					this.currentEtudiant = getDomainService().getPresenceEtudiantRef(this.currentEtudiant.getNumeroEtudiant(), getSessionController().getCurrentAnnee());		
	//					if(this.currentEtudiant!=null)
	//						currentEtudiant.setBddScol(1);
	//					if((this.currentEtudiant!=null && this.currentEtudiant.getSource().equals("D")) || etu!=null)
	//					{
	//						String summary = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE_OPI");
	//						String detail = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE_OPI");
	//						//						String summary = "Vous ne pouvre pas effectuer une demande de transferts accueil car sous avez deja effecute une demande de transfert depart";
	//						//						String detail = "Vous ne pouvre pas effectuer une demande de transferts accueil car sous avez deja effecute une demande de transfert depart";
	//						Severity severity=FacesMessage.SEVERITY_ERROR;
	//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));						
	//						return null;
	//					}
	//					else
	//					{
	//						this.initialiseNomenclatures();
	//						return "goToRecapitulatifApogee";
	//					}
	//				}
	//				else
	//				{
	//					if (logger.isDebugEnabled())
	//						logger.debug("Pas de demande de transferts !!!");
	//					setEtabPartenaireSaisieDepart(false);
	//					currentEtudiant.setBddScol(1);
	//					currentEtudiant.getTransferts().setDept(getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()).getCodeDep());
	//					currentEtudiant.getTransferts().setRne(getSessionController().getRne());
	//					return "goToEtatCivilApogee";
	//				}
	//			}
	//			else
	//			{
	//				String tmp = "";
	//
	//				for(TrBlocageDTO b : this.currentEtudiant.getListeBlocagesDTO())
	//				{
	//					tmp += b.getCodeBlocage()+" - "+b.getLibBlocage();
	//				}
	//				for(InterditBu lInterditBu : listeInterditBu)
	//				{
	//					tmp += lInterditBu.getLibInterdit();
	//				}				
	//				String summary = "Vous avez des Interdits : \n" + "- "+ tmp;
	//				String detail = "Vous avez des Interdits : \n" + "- "+ tmp;
	//				Severity severity=FacesMessage.SEVERITY_ERROR;
	//				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));
	//				return null;
	//			}
	//		}
	//		else
	//		{
	//			this.currentEtudiant = getDomainService().getPresenceEtudiantRefByIne(ine, getSessionController().getCurrentAnnee());	
	//
	//			if (logger.isDebugEnabled())
	//			{
	//				logger.debug("Etudiant absent d APOGEE");	
	//				logger.debug("authEtuScol-->"+authEtuScol);	
	//				logger.debug("this.currentEtudiant-->"+this.currentEtudiant);	
	//				logger.debug("etu-->"+etu);
	//			}
	//
	//			if(etu!=null)
	//			{
	//				if (logger.isDebugEnabled())
	//					logger.debug("Etudiant present dans la table OPI");				
	//				String summary = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE_OPI");
	//				String detail = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE_OPI");						
	//				Severity severity=FacesMessage.SEVERITY_ERROR;
	//				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));						
	//				return null;					
	//			}
	//			else if(this.currentEtudiant!=null && etu==null)
	//			{
	//				if (logger.isDebugEnabled())
	//					logger.debug("Etudiant present dans la table des transferts");					
	//				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");				
	//				if(dateFormat.format(dateNaissanceApogee).equals(dateFormat.format(this.currentEtudiant.getDateNaissance())))
	//				{
	//					if (logger.isDebugEnabled())
	//						logger.debug("UserController --> Compare date OK");
	//
	//					setVerifDateNaisApogee(true);
	//					if(this.currentEtudiant.getSource().equals("D"))
	//					{
	//						String summary = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE");
	//						String detail = getString("ERREUR.DEMANDE_TRANSFERT_DEPART_EXISTANTE");						
	//						//						String summary = "Vous ne pouvre pas effectuer une demande de transferts accueil car sous avez deja effecute une demande de transfert depart";
	//						//						String detail = "Vous ne pouvre pas effectuer une demande de transferts accueil car sous avez deja effecute une demande de transfert depart";
	//						Severity severity=FacesMessage.SEVERITY_ERROR;
	//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));						
	//						return null;					
	//					}
	//					else
	//					{
	//						if (logger.isDebugEnabled())
	//							logger.debug("goToRecapitulatifApogee");
	//						this.initialiseNomenclatures();
	//						return "goToRecapitulatifApogee";
	//					}
	//				}
	//				else
	//				{
	//					if (logger.isDebugEnabled())
	//						logger.debug("UserController --> Compare date FAUX !!!");
	//					setVerifDateNaisApogee(false);
	//					String summary = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
	//					String detail = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
	//					Severity severity=FacesMessage.SEVERITY_ERROR;
	//					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
	//					return null;					
	//				}
	//
	//			}			
	//			else if(this.currentEtudiant==null && etu==null && authEtuScol==2)  
	//			{
	//				if (logger.isDebugEnabled())
	//					logger.debug("Etudiant absent des tables OPI et transferts");					
	//				if (logger.isDebugEnabled())
	//					logger.debug("goToEtatCivilApogee");
	//				setVerifDateNaisApogee(true);
	//				setEtabPartenaireSaisieDepart(false);
	//				currentEtudiant = new EtudiantRef();
	//				InfosAccueil ia = new InfosAccueil();
	//				currentEtudiant.setAccueil(ia);
	//				currentEtudiant.setNumeroIne(getIneApogee());
	//				currentEtudiant.setNumeroEtudiant(getIneApogee().toUpperCase());
	//				currentEtudiant.getAdresse().setNumeroEtudiant(getIneApogee().toUpperCase());
	//				currentEtudiant.getTransferts().setNumeroEtudiant(getIneApogee().toUpperCase());
	//				currentEtudiant.setDateNaissance(getDateNaissanceApogee());
	//				currentEtudiant.setBddScol(0);
	//				currentEtudiant.getTransferts().setDept(getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()).getCodeDep());
	//				currentEtudiant.getTransferts().setRne(getSessionController().getRne());
	//				currentEtudiant.getAdresse().setCodPay("100");
	//				return "goToEtatCivilApogee";
	//			}
	//			else
	//			{
	//				if (logger.isDebugEnabled())
	//					logger.debug("else final");					
	//				setVerifDateNaisApogee(false);
	//				String summary = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
	//				String detail = getString("ERREUR.CONNEXION_BDD_SCOLARITE");
	//				Severity severity=FacesMessage.SEVERITY_ERROR;
	//				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
	//				return null;				
	//			}
	//		}
	//
	//	}	

	public void existeBdd(String numeroEtudiant)
	{
		if(getDomainService().getPresenceEtudiantRef(numeroEtudiant, getSessionController().getCurrentAnnee())!=null)
			setPresentBdd(true);
		else
			setPresentBdd(false);
	}	

	public void initialiseNomenclatures()
	{
		if (logger.isDebugEnabled())
			logger.debug("initialiseNomenclatures");

		List<SelectItem> communes = this.getListeCommunes();
		for(int i=0;i<communes.size();i++)
		{
			if(communes.get(i).getValue().equals(this.currentEtudiant.getAdresse().getCodeCommune()))
				this.currentEtudiant.getAdresse().setNomCommune(communes.get(i).getLabel());
		}

		this.currentEtudiant.setComposante(null);
		this.currentEtudiant.setLibEtapePremiereLocal(null);

		if(this.currentEtudiant.getTransferts().getTypeTransfert().equals("T"))
			this.currentEtudiant.getTransferts().setLibTypeTransfert("Total");
		else
			this.currentEtudiant.getTransferts().setLibTypeTransfert("Partiel");

		this.currentEtudiant.getTransferts().setLibDept(getDomainServiceScolarite().getEtablissementByRne(this.currentEtudiant.getTransferts().getRne()).getLibDep());
		this.currentEtudiant.getTransferts().setLibRne(getDomainServiceScolarite().getEtablissementByRne(this.currentEtudiant.getTransferts().getRne()).getLibEtb());		
		this.currentEtudiant.getAdresse().setLibPay(getDomainServiceScolarite().getPaysByCodePays(this.currentEtudiant.getAdresse().getCodPay()).getLibPay());
		this.currentEtudiant.getAccueil().setLibDepUnivDepart(getDomainServiceScolarite().getEtablissementByRne(this.currentEtudiant.getAccueil().getCodeRneUnivDepart()).getLibDep());
		this.currentEtudiant.getAccueil().setLibRneUnivDepart(getDomainServiceScolarite().getEtablissementByRne(this.currentEtudiant.getAccueil().getCodeRneUnivDepart()).getLibEtb());
		this.currentEtudiant.setLibNationalite(getDomainServiceScolarite().getPaysByCodePays(this.currentEtudiant.getAccueil().getCodePaysNat()).getLibNationalite());
		this.currentEtudiant.getAccueil().setLibelleBac(getDomainServiceScolarite().recupererBacOuEquWS(this.currentEtudiant.getAccueil().getCodeBac()).get(0).getLibBac());
	}	

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + "#" + hashCode();
	}

	public CodeSizeAnnee getDefaultCodeSize() {
		return defaultCodeSize;
	}

	public void setDefaultCodeSize(CodeSizeAnnee defaultCodeSize) {
		this.defaultCodeSize = defaultCodeSize;
	}

	public void setDefaultCodeSizeAnnee(boolean defaultCodeSizeAnnee) {
		this.defaultCodeSizeAnnee = defaultCodeSizeAnnee;
	}

	public boolean isVerifDateNaisApogee() {
		return verifDateNaisApogee;
	}

	public void setVerifDateNaisApogee(boolean verifDateNaisApogee) {
		this.verifDateNaisApogee = verifDateNaisApogee;
	}

	public Parametres getParametreAppli() {
		this.parametreAppli=getDomainService().getParametreByCode("ouvertureAccueil");
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

	public void setParametreAppli(Parametres parametreAppli) {
		this.parametreAppli = parametreAppli;
	}

	public String getIneApogee() {
		return ineApogee;
	}

	public void setIneApogee(String ineApogee) {
		this.ineApogee = ineApogee;
	}

	public Date getDateNaissanceApogee() {
		return dateNaissanceApogee;
	}

	public void setDateNaissanceApogee(Date dateNaissanceApogee) {
		this.dateNaissanceApogee = dateNaissanceApogee;
	}

	public boolean isIneToUpperCase() {
		return ineToUpperCase;
	}

	public void setIneToUpperCase(boolean ineToUpperCase) {
		this.ineToUpperCase = ineToUpperCase;
	}

	public boolean isPresentBdd() {
		return presentBdd;
	}

	public void setPresentBdd(boolean presentBdd) {
		this.presentBdd = presentBdd;
	}

	public EtudiantRef getNewEtudiant() {
		return newEtudiant;
	}

	public void setNewEtudiant(EtudiantRef newEtudiant) {
		this.newEtudiant = newEtudiant;
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
		if (logger.isDebugEnabled())
			logger.debug("getDomainServiceScolarite().getListePays();");

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

	public List<SelectItem> getListeNationalite() {
		if (logger.isDebugEnabled())
			logger.debug("getDomainServiceScolarite().getListePays();");

		List<SelectItem> listePays = new ArrayList<SelectItem>();
		List<TrPaysDTO> listePaysDTO = getDomainServiceScolarite().getListePays();
		if(listePaysDTO!=null)
		{
			for(TrPaysDTO pDTO : listePaysDTO)
			{			
				SelectItem option = new SelectItem(pDTO.getCodePay(), pDTO.getLibNationalite());
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

	public List<SelectItem> getListeBacOuEqu() {
		if (logger.isDebugEnabled())
			logger.debug("getDomainServiceScolarite().recupererBacOuEquWS()");

		List<SelectItem> listeBac = new ArrayList<SelectItem>();
		List<TrBac> listeBacDTO = getDomainServiceScolarite().recupererBacOuEquWS(null);
		if(listeBacDTO!=null)
		{
			for(TrBac bacDTO : listeBacDTO)
			{			
				SelectItem option = new SelectItem(bacDTO.getCodeBac(),bacDTO.getLibBac());
				listeBac.add(option);
			}	
			Collections.sort(listeBac,new ComparatorSelectItem());
		}
		else
		{
			SelectItem option = new SelectItem("", "");
			listeBac.add(option);	
		}
		return listeBac;
	}

	public void setListeCommunes(List<SelectItem> listeCommunes) {
		this.listeCommunes = listeCommunes;
	}

	public EtudiantRef getCurrentEtudiant() {
		return currentEtudiant;
	}

	public void setCurrentEtudiant(EtudiantRef currentEtudiant) {
		this.currentEtudiant = currentEtudiant;
	}

	public boolean isEtudiant() {
		return etudiant;
	}

	public void setEtudiant(boolean etudiant) {
		this.etudiant = etudiant;
	}

	public String getItemValueCodePay() {
		return itemValueCodePay;
	}

	public void setItemValueCodePay(String itemValueCodePay) {
		this.itemValueCodePay = itemValueCodePay;
	}

	public boolean isTypesDiplomeVide() {
		return typesDiplomeVide;
	}

	public void setTypesDiplomeVide(boolean typesDiplomeVide) {
		this.typesDiplomeVide = typesDiplomeVide;
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

	public boolean isLibelleDiplomeVide() {
		return libelleDiplomeVide;
	}

	public void setLibelleDiplomeVide(boolean libelleDiplomeVide) {
		this.libelleDiplomeVide = libelleDiplomeVide;
	}

	public boolean isLibelleEtapeVide() {
		return libelleEtapeVide;
	}

	public void setLibelleEtapeVide(boolean libelleEtapeVide) {
		this.libelleEtapeVide = libelleEtapeVide;
	}

	public boolean isAnneeEtudeVide() {
		return AnneeEtudeVide;
	}

	public void setAnneeEtudeVide(boolean anneeEtudeVide) {
		AnneeEtudeVide = anneeEtudeVide;
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

	public void setListeLibellesEtape(List<OffreDeFormationsDTO> listeLibellesEtape) {
		this.listeLibellesEtape = listeLibellesEtape;
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
			Map<String, String> listeTypesDiplomeDTO = getDomainService().getOdfTypesDiplomeByRneAndAnnee(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), true, "A");			
			if(listeTypesDiplomeDTO!=null && !listeTypesDiplomeDTO.isEmpty())
			{
				if (logger.isDebugEnabled()) {
					logger.debug("listeTypesDiplomeDTO : "+listeTypesDiplomeDTO);
				}
				for (String mapKey : listeTypesDiplomeDTO.keySet()) {
					// utilise ici hashMap.get(mapKey) pour acceder aux valeurs
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
		Map<Integer, String> listeAnneesEtudeDTO = getDomainService().getAnneesEtudeByRneAndAnneeAndCodTypDip(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(), true, "A");

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
		if (logger.isDebugEnabled()) {
			logger.debug("public List<SelectItem> getListeLibellesDiplome()");
			logger.debug("getDomainService().getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveauAndComposante(etu.getTransferts().getRne(), getSessionController().getCurrentAnnee(),etu.getTransferts().getCodTypDip(), etu.getTransferts().getCodeNiveau());");
		}
		listeLibellesDiplome = new ArrayList<SelectItem>();
		Map<String, String> listeLibellesDiplomeDTO = getDomainService().getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveauAndComposante(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(), getCodeNiveau(), getCodeComposante(), true);
		if(listeLibellesDiplomeDTO!=null && !listeLibellesDiplomeDTO.isEmpty())
		{
			if (logger.isDebugEnabled()) {
				logger.debug("listeLibellesDiplomeDTO : "+listeLibellesDiplomeDTO);
			}
			for(String mapKey : listeLibellesDiplomeDTO.keySet())
			{
				SelectItem option = new SelectItem(mapKey, listeLibellesDiplomeDTO.get(mapKey));
				listeLibellesDiplome.add(option);
			}			
			Collections.sort(listeLibellesDiplome,new ComparatorSelectItem());
			return listeLibellesDiplome;
		}
		else
			return null;
	}	

	public List<OffreDeFormationsDTO> getListeLibellesEtape() {
		if (logger.isDebugEnabled())
		{
			logger.debug("public List<SelectItem> getListeLibellesEtape()");
			logger.debug("(etu.getTransferts() --> "+currentEtudiant.getTransferts().getRne() +"-----"+ getSessionController().getCurrentAnnee() +"-----"+ getCodTypDip() +"-----"+ getCodeNiveau() +"-----"+ getCodeComposante());
			//			logger.debug("(getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip --> "+getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(),  getCodeNiveau(), getCodeDiplome(),"A").size());
		}
		return getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposante(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(),  getCodeNiveau(), getCodeComposante(), "A");
	}

	public List<OffreDeFormationsDTO> getListeLibellesEtapeOld() {
		if (logger.isDebugEnabled())
		{
			logger.debug("public List<SelectItem> getListeLibellesEtape()");
			logger.debug("(etu.getTransferts() --> "+currentEtudiant.getTransferts().getRne() +"-----"+ getSessionController().getCurrentAnnee() +"-----"+ getCodTypDip() +"-----"+ getCodeNiveau() +"-----"+ getCodeDiplome());
			//			logger.debug("(getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip --> "+getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(),  getCodeNiveau(), getCodeDiplome(),"A").size());
		}
		return getDomainService().getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip(currentEtudiant.getTransferts().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip(),  getCodeNiveau(), getCodeDiplome(),"A");
	}	

	public OffreDeFormationsDTO getCurrentOdf() {
		return currentOdf;
	}

	public void setCurrentOdf(OffreDeFormationsDTO currentOdf) {
		this.currentOdf = currentOdf;
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
			logger.debug("public List<SelectItem> getListeEtablissements() --> " + currentEtudiant.getAccueil().getCodeDepUnivDepart());
			logger.debug("getDomainServiceScolarite().getListeEtablissements(typesEtablissementSplit, currentDemandeTransferts.getTransferts().getDept());	");
		}
		if(!isDeptVide())
		{
			if (logger.isDebugEnabled())
				logger.debug("if(listeEtablissements==null) --> " + listeEtablissements);

			listeEtablissements = new ArrayList<SelectItem>();

			if (logger.isDebugEnabled())
				if(getTypesEtablissementListSplit()!=null)
					logger.debug("getTypesEtablissementListSplit().size() --> " + getTypesEtablissementListSplit().size());	

			for (String typesEtablissementSplit : getTypesEtablissementListSplit()) 
			{
				List<TrEtablissementDTO> etablissementDTO = getDomainServiceScolarite().getListeEtablissements(typesEtablissementSplit, currentEtudiant.getAccueil().getCodeDepUnivDepart());

				if (logger.isDebugEnabled())
					if(etablissementDTO!=null)
						logger.debug("etablissementDTO --> " + etablissementDTO.size());				

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

	public List<SelectItem> getListeComposantes() {
		if (logger.isDebugEnabled())
			logger.debug("getListeComposantes");

		listeComposantes = new ArrayList<SelectItem>();
		//		Map<String, String> listeComposantesDTO = getDomainService().getOdfComposanteByRneAndAnneeAndActif(getSessionController().getRne(), getSessionController().getCurrentAnnee());
		//		Map<String, String> listeComposantesDTO = getDomainService().getOdfComposanteByRneAndAnneeAndActifAndArrivee(getSessionController().getRne(), getSessionController().getCurrentAnnee());
		Map<String, String> listeComposantesDTO = getDomainService().getOdfComposanteByRneAndAnneeAndCodTypDip(getSessionController().getRne(), getSessionController().getCurrentAnnee(), getCodTypDip());
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

	public void verifEtablissementPartenaire()
	{
		List<WsPub> lWsPub = getDomainService().getWsPubByAnnee(getSessionController().getCurrentAnnee());
		boolean test = false;
		for(WsPub ws :lWsPub)
			if(this.currentEtudiant.getAccueil().getCodeRneUnivDepart().equals(ws.getRne()))
				test=true;
		if(test)
		{
			String summary = getString("ERREUR.DEMANDE_TRANSFERT_ACCUEIL_ETAB_PARTENAIRE");
			String detail = getString("ERREUR.DEMANDE_TRANSFERT_ACCUEIL_ETAB_PARTENAIRE");			
			Severity severity=FacesMessage.SEVERITY_ERROR;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));			
			setEtabPartenaireSaisieDepart(true);
		}
		else
			setEtabPartenaireSaisieDepart(false);
	}

	public void setListeEtablissements(List<SelectItem> listeEtablissements) {
		this.listeEtablissements = listeEtablissements;
	}

	public boolean isDeptVide() {
		return deptVide;
	}

	public void setDeptVide(boolean deptVide) {
		this.deptVide = deptVide;
	}

	public SituationUniversitaire getCurrentSituationUniv() {
		return currentSituationUniv;
	}

	public void setCurrentSituationUniv(SituationUniversitaire currentSituationUniv) {
		this.currentSituationUniv = currentSituationUniv;
	}		

	public List<SelectItem> getListeAccueilAnnee()
	{
		if (logger.isDebugEnabled())
			logger.debug("public List<AccueilAnnee> getListeAccueilAnnee()");

		List<SelectItem> listeAccueilAnnee = new ArrayList<SelectItem>();
		List<AccueilAnnee> listeAnneeAccueilDTO = getDomainService().getAccueilAnneeSansNull();
		if(listeAnneeAccueilDTO!=null)
		{
			for(AccueilAnnee aa : listeAnneeAccueilDTO)
			{			
				SelectItem option = new SelectItem(aa.getIdAccueilAnnee(), aa.getLibelle());
				listeAccueilAnnee.add(option);
			}	
			//			Collections.sort(listeAccueilAnnee,new ComparatorSelectItem());
		}
		else
		{
			SelectItem option = new SelectItem("", "");
			listeAccueilAnnee.add(option);	
		}
		return listeAccueilAnnee;			
	}

	public List<SelectItem> getListeAccueilResultat()
	{
		if (logger.isDebugEnabled())
			logger.debug("public List<AccueilAnnee> getListeAccueilAnnee()");

		List<SelectItem> listeAccueilResultat = new ArrayList<SelectItem>();
		List<AccueilResultat> listeAnneeResultatDTO = getDomainService().getAccueilResultatSansNull();
		if(listeAnneeResultatDTO!=null)
		{
			for(AccueilResultat ar : listeAnneeResultatDTO)
			{			
				SelectItem option = new SelectItem(ar.getIdAccueilResultat(), ar.getLibelle());
				listeAccueilResultat.add(option);
			}	
			Collections.sort(listeAccueilResultat,new ComparatorSelectItem());
		}
		else
		{
			SelectItem option = new SelectItem("", "");
			listeAccueilResultat.add(option);	
		}
		return listeAccueilResultat;		
		//		return getDomainService().getListeAccueilResultat();
	}

	public AccueilAnnee getCurrentAccueilAnnee() {
		return currentAccueilAnnee;
	}

	public void setCurrentAccueilAnnee(AccueilAnnee currentAccueilAnnee) {
		this.currentAccueilAnnee = currentAccueilAnnee;
	}

	public AccueilResultat getCurrentAccueilResultat() {
		return currentAccueilResultat;
	}

	public void setCurrentAccueilResultat(AccueilResultat currentAccueilResultat) {
		this.currentAccueilResultat = currentAccueilResultat;
	}

	public String getCurrentCleAccueilAnnee() {
		return currentCleAccueilAnnee;
	}

	public void setCurrentCleAccueilAnnee(String currentCleAccueilAnnee) {
		this.currentCleAccueilAnnee = currentCleAccueilAnnee;
	}

	public String getCurrentCleAccueilResultat() {
		return currentCleAccueilResultat;
	}

	public void setCurrentCleAccueilResultat(String currentCleAccueilResultat) {
		this.currentCleAccueilResultat = currentCleAccueilResultat;
	}

	public SituationUniversitaire getSelectedSituationUniv() {
		return selectedSituationUniv;
	}

	public void setSelectedSituationUniv(SituationUniversitaire selectedSituationUniv) {
		this.selectedSituationUniv = selectedSituationUniv;
	}

	public SituationUniversitaireDataModel getSudm() {
		if(this.currentEtudiant.getAccueil().getSituationUniversitaire()!=null)
			return new SituationUniversitaireDataModel(this.currentEtudiant.getAccueil().getSituationUniversitaire());
		else
			return new SituationUniversitaireDataModel();	
	}

	public List<SituationUniversitaire> getSudm2() {
		if(this.currentEtudiant.getAccueil().getSituationUniversitaire()!=null)
			return this.currentEtudiant.getAccueil().getSituationUniversitaire();
		return null;
	}	

	public void setSudm(SituationUniversitaireDataModel sudm) {
		this.sudm = sudm;
	}

	public String getMailInformation() {
		return mailInformation;
	}

	public void setMailInformation(String mailInformation) {
		this.mailInformation = mailInformation;
	}

	public boolean isEtabPartenaireSaisieDepart() {
		return etabPartenaireSaisieDepart;
	}

	public void setEtabPartenaireSaisieDepart(boolean etabPartenaireSaisieDepart) {
		this.etabPartenaireSaisieDepart = etabPartenaireSaisieDepart;
	}

	public void setListeComposantes(List<SelectItem> listeComposantes) {
		this.listeComposantes = listeComposantes;
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

	public boolean isMaxSU() {
		if(this.currentEtudiant.getAccueil().getSituationUniversitaire() == null)
			return false;
		else if(this.currentEtudiant.getAccueil().getSituationUniversitaire() != null && this.currentEtudiant.getAccueil().getSituationUniversitaire().size()<7)
			return false;
		else
			return true;
	}

	public void setMaxSU(boolean maxSU) {
		this.maxSU = maxSU;
	}

	public List<AccueilDecision> getListeAccueilDecision() {
		//		return listeAccueilDecision;
		if(this.currentEtudiant.getAccueilDecision()!=null)
		{
			listeAccueilDecision = new ArrayList<AccueilDecision>();
			Iterator i=this.currentEtudiant.getAccueilDecision().iterator(); // on cr�e un Iterator pour parcourir notre HashSet
			while(i.hasNext()) // tant qu'on a un suivant
			{
				listeAccueilDecision.add((AccueilDecision) i.next());
			}			
			Collections.sort(listeAccueilDecision, new ComparatorDateTimeAccueilDecision());
		}
		return listeAccueilDecision;		
	}

	public void setListeAccueilDecision(List<AccueilDecision> listeAccueilDecision) {
		this.listeAccueilDecision = listeAccueilDecision;
	}

	public Parametres getParametreAppliInfosAccueil() 
	{
		this.parametreAppliInfosAccueil=getDomainService().getParametreByCode("informationAccueil");
		if(this.parametreAppliInfosAccueil!=null)
		{
			if (logger.isDebugEnabled()) {
				logger.debug("this.parametreAppliInfosAccueil.getCodeParametre --> "+this.parametreAppliInfosAccueil.getCodeParametre());
				logger.debug("this.parametreAppliInfosAccueil.getBool() --> "+this.parametreAppliInfosAccueil.isBool());
				logger.debug("this.parametreAppliInfosAccueil.getCommentaire() --> "+this.parametreAppliInfosAccueil.getCommentaire());
			}
		}
		else
		{
			if (logger.isDebugEnabled()) {
				logger.debug("Aucun parametre nome 'informationAccueil' trouve dans la table parametre");
			}
			this.parametreAppliInfosAccueil=new Parametres();
			this.parametreAppliInfosAccueil.setBool(false);
		}
		return parametreAppliInfosAccueil;
	}

	public void setParametreAppliInfosAccueil(Parametres parametreAppliInfosAccueil) {
		this.parametreAppliInfosAccueil = parametreAppliInfosAccueil;
	}

	public String getAideTypeTransfert() {
		aideTypeTransfert = getString("AIDE.TYPE_TANSFERT", getDomainServiceScolarite().getEtablissementByRne(getSessionController().getRne()).getLibOffEtb());
		return aideTypeTransfert;
	}

	public void setAideTypeTransfert(String aideTypeTransfert) {
		this.aideTypeTransfert = aideTypeTransfert;
	}

	public List<TrSituationUniversitaire> getlTrSituationUniversitaire() 
	{
		List<TrSituationUniversitaire> lTrSU = new ArrayList<TrSituationUniversitaire>();
		//this.currentEtudiant.getAccueil().setSituationUniversitaire(getDomainService().getSituationUniversitaireByNumeroEtudiantAndAnnee(this.currentEtudiant.getNumeroEtudiant(), this.currentEtudiant.getAnnee()));
		if(this.currentEtudiant.getAccueil()!=null && this.currentEtudiant.getAccueil().getSituationUniversitaire()!=null){
			for(SituationUniversitaire su : this.currentEtudiant.getAccueil().getSituationUniversitaire())
			{
				String annee = "";
				String resultat = "";
				if(su.getAnnee().getIdAccueilAnnee()!=0)
					annee = su.getAnnee().getLibelle();
				else
					annee = su.getLibAccueilAnnee();
				if(su.getResultat().getIdAccueilResultat()!=0)
					resultat = su.getResultat().getLibelle();
				else
					resultat = su.getLibAccueilResultat();				
				lTrSU.add(new TrSituationUniversitaire(su.getId(), annee, su.getLibelle(), resultat));
			}
			return lTrSU;
		}else
			return null;
	}

	public void setlTrSituationUniversitaire(List<TrSituationUniversitaire> lTrSituationUniversitaire) 
	{
		this.lTrSituationUniversitaire = lTrSituationUniversitaire;
	}

	public List<Correspondance> getListeCorrespondances() {
		if(this.currentEtudiant.getCorrespondances()!=null)
		{
			listeCorrespondances = new ArrayList<Correspondance>();
			Iterator i=this.currentEtudiant.getCorrespondances().iterator(); // on cr�e un Iterator pour parcourir notre HashSet
			while(i.hasNext()) // tant qu'on a un suivant
			{
				listeCorrespondances.add((Correspondance) i.next());
			}			
			Collections.sort(listeCorrespondances, new ComparatorDateTimeCorrespondance());
		}
		return listeCorrespondances;
	}

	public void setListeCorrespondances(List<Correspondance> listeCorrespondances) {
		this.listeCorrespondances = listeCorrespondances;
	}
}