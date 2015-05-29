package org.esupportail.transferts.web.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.commons.collections.ListUtils;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.LdapUserService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.CGE;
import org.esupportail.transferts.domain.beans.Composante;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.PersonnelComposante;
import org.esupportail.transferts.domain.beans.User;
import org.hibernate.mapping.Array;
import org.hibernate.validator.util.NewInstance;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.springframework.util.Assert;

public class ManagerController extends AbstractContextAwareController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1084617895906407867L;
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	private User personnelChoisi;
	private Map<String, String> pc;
	private List<User> users;
	private String nomRecherche;
	private String prenomRecherche;
	private List<User> personnelsRecherche;
	private DualListModel<PersonnelComposante> players;  
	List<PersonnelComposante> source;  
	List<PersonnelComposante> target;  
	/*Permet de r�soudre le probleme avec le p:ajaxStatus dans le header*/
	private boolean use = false;
	private Integer typePersonnel;
	private String from;
	private String employeeAffiliation;
	private String ldapAffiliation;
	private String listeComposantesFerme;
	private List<String> listeComposantesFermeSplit = new ArrayList<String>();	
	private List<Composante> listeComposantes; 
	private List<Composante> listeComposantesMerge;
	private List<CGE> listeCGE; 
	private List<CGE> listeCGEMerge;
	private List<Composante> filteredComposantes;  
	private List<CGE> filteredCGE; 
	private List<PersonnelComposante> listeComposantesDetailsDroits;
	private List<PersonnelComposante> listeComposantesDetailsDroitsMerge;
	private List<PersonnelComposante> listePlayersDetailsDroits;
	private List<PersonnelComposante> filteredDetailsDroits;


	/**
	 * LdapUserService
	 */
	private LdapUserService ldapUserService;

	@Override
	public void afterPropertiesSetInternal()
	{
		super.afterPropertiesSetInternal();
		Assert.notNull(this.ldapUserService, "property ldapUserService of class " 
				+ this.getClass().getName() + " can not be null");		
		Assert.hasText(employeeAffiliation, "property employeeAffiliation of class "
				+ this.getClass().getName() + " can not be null");			
		Assert.hasText(ldapAffiliation, "property ldapAffiliation of class "
				+ this.getClass().getName() + " can not be null");				
	}	

	public String goToDetailDroits()
	{
		if (logger.isDebugEnabled())
			logger.debug("public String goToDetailDroits()");
		//		setFrom("D");
		listeComposantesDetailsDroits=getPlayers().getTarget();
		//listeComposantesDetailsDroits = getDomainService().getListeComposantesByUidAndSourceAndAnnee(this.personnelChoisi.getLogin(), getFrom(), getSessionController().getCurrentAnnee());
		//this.addPersonnelComposante();
		//listeComposantesDetailsDroits = getDomainService().getListeComposantesByUidAndSourceAndAnnee(this.personnelChoisi.getLogin(), getFrom(), getSessionController().getCurrentAnnee());		
		return "goToDetailDroits"; 		
	}

	public void addValidationAutoByComposante()
	{
		if (logger.isDebugEnabled())
		{
			for(Composante c : this.listeComposantesMerge)
				logger.debug("c.getCodeComposante()################c.getValidAuto()-->"+ c.getCodeComposante()+"-----"+c.getValidAuto());
		}
		getDomainService().addValidationAutoByComposante(this.listeComposantesMerge);
		this.listeComposantesMerge=null;
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vos modifications ont bien ete prises en compte", "Vos modifications ont bien ete prises en compte");  
		FacesContext.getCurrentInstance().addMessage(null, msg);  
	}

	public void addValidationAutoByCGE()
	{
		if (logger.isDebugEnabled())
		{
			for(CGE c : this.listeCGEMerge)
				logger.debug("c.getCodeCGE()################c.getValidAuto()-->"+ c.getCodeCGE()+"-----"+c.getValidAuto());
		}
		getDomainService().addValidationAutoByCGE(this.listeCGEMerge);
		this.listeCGEMerge=null;
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vos modifications ont bien ete prises en compte", "Vos modifications ont bien ete prises en compte");  
		FacesContext.getCurrentInstance().addMessage(null, msg);  
	}	

	public String goToValidationComposantesAutoTransfertsDepart()
	{
		if (logger.isDebugEnabled())
			logger.debug("public String goToValidationTransfertsDepart()");
		setFrom("D");
		if(getDomainService().getFichierDefautByAnneeAndFrom(getSessionController().getCurrentAnnee(), getFrom())!=null)
		{
			setFilteredComposantes(null);
			listeComposantesFermeSplit=null;
			this.listeComposantesMerge=null;
			return "goToValidationComposantesAutoTransfertsDepart";
		}
		else
			return "goToSignatureParDefautObligatoire";		
	}    

	public String goToValidationCGEAutoTransfertsDepart()
	{
		if (logger.isDebugEnabled())
			logger.debug("public String goToValidationCGEAutoTransfertsDepart()");

		setFilteredCGE(null);
		setFrom("D");
		if(getDomainService().getFichierDefautByAnneeAndFrom(getSessionController().getCurrentAnnee(), getFrom())!=null)
			return "goToValidationCGEAutoTransfertsDepart";
		else
			return "goToSignatureParDefautObligatoire";
	}

	public void addPersonnelComposante()
	{
		if(listeComposantesDetailsDroits!=null && listeComposantesDetailsDroits.size()>0)
			if (logger.isDebugEnabled())
				logger.debug("################ listeComposantesDetailsDroits #################### --> "+listeComposantesDetailsDroits.size());			

		List<PersonnelComposante> lPc = new ArrayList<PersonnelComposante>();
		for(int i=0;i<listeComposantesDetailsDroits.size();i++)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("##########################################################################################");
				logger.debug("################ listeComposantesDetailsDroits.get(i).getUid() #################### --> "+listeComposantesDetailsDroits.get(i).getUid());
				logger.debug("################ listeComposantesDetailsDroits.get(i).getCodeComposante() #################### --> "+listeComposantesDetailsDroits.get(i).getCodeComposante());
				logger.debug("################ listeComposantesDetailsDroits.get(i).getLibelleComposante() #################### --> "+listeComposantesDetailsDroits.get(i).getLibelleComposante());
				logger.debug("################ listeComposantesDetailsDroits.get(i).getSource() #################### --> "+listeComposantesDetailsDroits.get(i).getSource());
				logger.debug("##########################################################################################");					
			}

			if(getFrom().equals("D"))
				typePersonnel=0;

			PersonnelComposante p = new PersonnelComposante(listeComposantesDetailsDroits.get(i).getUid(), 
					listeComposantesDetailsDroits.get(i).getCodeComposante(),
					listeComposantesDetailsDroits.get(i).getSource(), 
					getSessionController().getCurrentAnnee(),
					listeComposantesDetailsDroits.get(i).getDisplayName(),
					listeComposantesDetailsDroits.get(i).getLibelleComposante(),
					typePersonnel,
					listeComposantesDetailsDroits.get(i).getDroitSuppression(),
					listeComposantesDetailsDroits.get(i).getDroitEditionPdf(),
					listeComposantesDetailsDroits.get(i).getDroitAvis(),
					listeComposantesDetailsDroits.get(i).getDroitDecision(),
					listeComposantesDetailsDroits.get(i).getDroitDeverrouiller());

			lPc.add(p);						


		}
		getDomainService().addPersonnelComposante(personnelChoisi.getLogin(), getFrom(), getSessionController().getCurrentAnnee(), lPc);
		listeComposantesDetailsDroits = getDomainService().getListeComposantesByUidAndSourceAndAnnee(this.personnelChoisi.getLogin(), getFrom(), getSessionController().getCurrentAnnee());			
		String summary = "L'affectation de vos composantes a bien ete prise en compte";
		String detail = "L'affectation de vos composantes a bien ete prise en compte";
		Severity severity=FacesMessage.SEVERITY_INFO;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
	}

	public void addPersonnelComposanteWithDetails()
	{
		if(logger.isDebugEnabled())
			logger.debug("public void addPersonnelComposanteWithDetails()");	

		getDomainService().addPersonnelComposante(personnelChoisi.getLogin(), getFrom(), getSessionController().getCurrentAnnee(), listeComposantesDetailsDroits);
		listeComposantesDetailsDroits = getDomainService().getListeComposantesByUidAndSourceAndAnnee(this.personnelChoisi.getLogin(), getFrom(), getSessionController().getCurrentAnnee());
		String summary = "L'affectation de vos composantes a bien ete prise en compte";
		String detail = "L'affectation de vos composantes a bien ete prise en compte";
		Severity severity=FacesMessage.SEVERITY_INFO;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,summary, detail));				
	}

	public void onTransfer(TransferEvent event) {  
		StringBuilder builder = new StringBuilder();  
		for(Object item : event.getItems()) {  
			builder.append(((PersonnelComposante) item).getCodeComposante()).append("<br />");  
		}  
	}  	

	public String goToManagerChoixLicence()
	{
		if(logger.isDebugEnabled())
			logger.debug("goToManagerChoixLicence");
		setUse(true);
		listeComposantesFermeSplit=null;
		if(this.getTarget()!=null)
			players = new DualListModel<PersonnelComposante>(this.getSource(), this.getTarget());
		else
			players = new DualListModel<PersonnelComposante>(this.getSource(), new ArrayList<PersonnelComposante>());
		if(logger.isDebugEnabled())
			logger.debug("############# typePersonnel #############-->"+typePersonnel);
		return "goToManagerChoixLicence";
	}

	public String rechercherPersonnel(){
		if(logger.isDebugEnabled())
			logger.debug("rechercherPersonnel");

		String ret = null;
		this.personnelsRecherche = new ArrayList<User>();

		if ((this.prenomRecherche == null || this.prenomRecherche.isEmpty())
				&& (this.nomRecherche == null || this.nomRecherche.isEmpty())){
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erreur : ", "Vous devez renseigner au moins un critere de recherche"));
		} else {
			if (this.prenomRecherche == null || this.prenomRecherche.isEmpty()){
				this.prenomRecherche = "";
			}
			if (this.nomRecherche == null || this.nomRecherche.isEmpty()){
				this.nomRecherche = "";
			}

			String filter = "";

			if(this.employeeAffiliation!=null && this.employeeAffiliation!="")
			{
				String[] tokens = this.employeeAffiliation.split(",");
				filter = "(&(givenName="+this.prenomRecherche+"*)(sn="+this.nomRecherche+"*)";
				for(int i=0; i<tokens.length; i++)
				{
					if(i==0)
						filter+="(|("+ldapAffiliation+"="+tokens[i]+")";
					else
						filter+="("+ldapAffiliation+"="+tokens[i]+")";
				}
				filter+="))";
			}
			else
				logger.error("Aucune affiliation d�finie pour un employe");

			// creation du filtre sur le nom et le prenom
			//			String filter = "(&(givenName="+this.prenomRecherche+"*)(sn="+this.nomRecherche+"*))";
			//			String filter = "(&(givenName="+this.prenomRecherche+"*)(sn="+this.nomRecherche+"*)(|(eduPersonPrimaryAffiliation=STAFF)(eduPersonPrimaryAffiliation=member)(eduPersonPrimaryAffiliation=employee)))";
			String[] tokens2 = this.employeeAffiliation.split(",");
			if(logger.isDebugEnabled())
			{
				logger.debug("tokens2-->"+ tokens2.length);	
				logger.debug("filter -->"+ filter);
			}
			List<LdapUser> resultList = ldapUserService.getLdapUsersFromFilter(filter);

			// Via l'objet User on utilise DisplayName pour stocker le nom et Language pour stocker le prenom...
			for(LdapUser ldapUser : resultList){
				User u = new User();
				u.setLogin(ldapUser.getAttribute(ldapUserService.getIdAttribute()));
				u.setDisplayName(ldapUser.getAttribute("displayName"));
				if (!this.personnelsRecherche.contains(u)){
					this.personnelsRecherche.add(u);
				}
			}
			ret = "goToManagerResultats";
		}
		return ret;
	}	

	public String goToManagerRecherche()
	{
		if(logger.isDebugEnabled())
			logger.debug("goToManagerRecherche");		
		return "goToManagerRecherche";
	}

	public String goToListeManagerAccueil()
	{
		if(logger.isDebugEnabled())
			logger.debug("goToListeManagerAccueil");		

		setFrom("A");
		pc=null;
		users = new ArrayList<User>();
		return "goToListeManagerAccueil";
	}

	public String goToListeManagerDepart()
	{
		if(logger.isDebugEnabled())
			logger.debug("goToListeManagerDepart");		

		setFrom("D");
		pc=null;
		users = new ArrayList<User>();
		return "goToListeManagerDepart";
	}	

	public Logger getLogger() {
		return logger;
	}

	public User getPersonnelChoisi() {
		return personnelChoisi;
	}

	public void setPersonnelChoisi(User personnelChoisi) {
		this.personnelChoisi = personnelChoisi;
	}

	public Map<String, String> getPc() {
		if(pc==null)
		{
			pc = getDomainService().getListeManagersBySourceAndAnnee(getFrom(), getSessionController().getCurrentAnnee());
		}
		else
			pc=null;
		return pc;
	}

	public void setPc(Map<String, String> pc) {
		this.pc = pc;
	}

	public String getNomRecherche() {
		return nomRecherche;
	}

	public void setNomRecherche(String nomRecherche) {
		this.nomRecherche = nomRecherche;
	}

	public String getPrenomRecherche() {
		return prenomRecherche;
	}

	public void setPrenomRecherche(String prenomRecherche) {
		this.prenomRecherche = prenomRecherche;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<User> getPersonnelsRecherche() {
		return personnelsRecherche;
	}

	public void setPersonnelsRecherche(List<User> personnelsRecherche) {
		this.personnelsRecherche = personnelsRecherche;
	}

	public LdapUserService getLdapUserService() {
		return ldapUserService;
	}

	public void setLdapUserService(LdapUserService ldapUserService) {
		this.ldapUserService = ldapUserService;
	}	

	public List<PersonnelComposante> getSource() {

		List<PersonnelComposante> lpSource = getDomainServiceScolarite().recupererComposante(this.personnelChoisi.getLogin(), this.personnelChoisi.getDisplayName(), getFrom(), getSessionController().getCurrentAnnee());

		if(getListeComposantesFermeSplit()!=null)
		{
			for(String comp : listeComposantesFermeSplit)
				lpSource.add(new PersonnelComposante(this.personnelChoisi.getLogin(), comp.substring(0, comp.indexOf("||")), getFrom(), getSessionController().getCurrentAnnee(), this.personnelChoisi.getDisplayName(), comp.substring(comp.indexOf("||")+2,comp.length()),0,"oui","oui","oui","oui","oui"));
		}

		List<PersonnelComposante> lpTarget = this.getTarget();
		List<PersonnelComposante> lpDiff = new ArrayList<PersonnelComposante>();

		if(lpTarget !=null && !lpTarget.isEmpty())
		{
			for(PersonnelComposante pcSource : lpSource)
			{
				for(PersonnelComposante pcTarget : lpTarget)
				{
					String source = pcSource.getSource()+pcSource.getCodeComposante()+pcSource.getUid();
					String target = pcTarget.getSource()+pcTarget.getCodeComposante()+pcTarget.getUid();
					if(source.equals(target))
						lpDiff.add(pcSource);
				}
			}
		}
		lpSource.removeAll(lpDiff);
		return lpSource;
	}

	public List<PersonnelComposante> getTarget() {
		//		return target;
		return getDomainService().getListeComposantesByUidAndSourceAndAnnee(this.personnelChoisi.getLogin(), getFrom(), getSessionController().getCurrentAnnee());
	}

	public void setSource(List<PersonnelComposante> source) {
		this.source = source;
	}

	public void setTarget(List<PersonnelComposante> target) {
		this.target = target;
	}

	public DualListModel<PersonnelComposante> getPlayers() {
		return players;
	}

	public void setPlayers(DualListModel<PersonnelComposante> players) {
		this.players = players;
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public List<User> getUsers() {
		//		users = new ArrayList<User>();
		if(users==null || users.isEmpty())
		{
			Map<String, String> lpc = new HashMap();
			lpc = this.getPc();
			if(lpc!=null)
			{
				for(String mapKey : lpc.keySet())
				{
					if (logger.isDebugEnabled())
						logger.debug("===>"+mapKey+"<===");
					User u = new User();
					u.setLogin(mapKey);
					u.setDisplayName(lpc.get(mapKey));
					users.add(u);			
				}
			}
		}	
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Integer getTypePersonnel() {
		List<PersonnelComposante> lpc = this.getTarget();
		if(lpc!=null && !lpc.isEmpty())
			setTypePersonnel(lpc.get(0).getTypePersonnel());
		else
			setTypePersonnel(null);		
		return typePersonnel;
	}

	public void setTypePersonnel(Integer typePersonnel) {
		this.typePersonnel = typePersonnel;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getEmployeeAffiliation() {
		return employeeAffiliation;
	}

	public void setEmployeeAffiliation(String employeeAffiliation) {
		this.employeeAffiliation = employeeAffiliation;
	}

	public String getLdapAffiliation() {
		return ldapAffiliation;
	}

	public void setLdapAffiliation(String ldapAffiliation) {
		this.ldapAffiliation = ldapAffiliation;
	}

	public String getListeComposantesFerme() {
		return listeComposantesFerme;
	}

	public void setListeComposantesFerme(String listeComposantesFerme) {
		this.listeComposantesFerme = listeComposantesFerme;
	}

	public List<String> getListeComposantesFermeSplit() {
		if(listeComposantesFerme!=null && !listeComposantesFerme.equals("") && this.listeComposantesFermeSplit==null)
		{
			listeComposantesFermeSplit = new ArrayList<String>();
			if(this.listeComposantesFerme.split(",").length>1)
			{
				String[] tokens = this.listeComposantesFerme.split(",");
				for(int i=0; i<tokens.length; i++)
				{
					this.listeComposantesFermeSplit.add(tokens[i]);
					if(logger.isDebugEnabled())
						logger.debug("listeComposantesFermee-->"+tokens[i]);					
				}
			}
			else
				this.listeComposantesFermeSplit.add(this.listeComposantesFerme);		
		}
		return listeComposantesFermeSplit;
	}

	public void setListeComposantesFermeSplit(
			List<String> listeComposantesFermeSplit) {
		this.listeComposantesFermeSplit = listeComposantesFermeSplit;
	}

	public List<Composante> getListeComposantes() {

		if(listeComposantesMerge==null)
		{
			List<Composante> listeComposantesFromBdd = getDomainService().getListeComposantesFromBddByAnneeAndSource(getSessionController().getCurrentAnnee(),getFrom());
			this.listeComposantes = getDomainServiceScolarite().recupererListeComposantes(getSessionController().getCurrentAnnee(),getFrom());
			if(listeComposantesFromBdd!=null)
				this.listeComposantesMerge = new ArrayList<Composante>(listeComposantesFromBdd);
			else
				this.listeComposantesMerge = new ArrayList<Composante>();

			List<Composante> listeComposantesDistinct = new ArrayList<Composante>();

			if(logger.isDebugEnabled())
				logger.debug("getListeComposantesFermeSplit()-->"+getListeComposantesFermeSplit());			

			if(getListeComposantesFermeSplit()!=null && !getListeComposantesFermeSplit().equals(""))
			{				
				for(String s : listeComposantesFermeSplit)
				{
					if(logger.isDebugEnabled())
					{
						logger.debug("Code listeComposantesFermee-->"+s.substring(0, s.indexOf("||")));
						logger.debug("Libellé listeComposantesFermee-->"+s.substring(s.indexOf("||")+2,s.length()));
					}
					listeComposantes.add(new Composante(s.substring(0, s.indexOf("||")), getFrom(), getSessionController().getCurrentAnnee(), s.substring(s.indexOf("||")+2,s.length()), "non"));
				}
			}


			if(logger.isDebugEnabled())
			{
				logger.debug("listeComposantes.size()--->"+listeComposantes.size());
				logger.debug("listeComposantesFromBdd.size()--->"+listeComposantesFromBdd.size());
			}

			for(Composante c1 : listeComposantes)
			{
				if(listeComposantesFromBdd!=null)
				{
					for(Composante c2 : listeComposantesFromBdd)
					{
						if(c1.getAnnee().equals(c2.getAnnee())
								&& c1.getCodeComposante().equals(c2.getCodeComposante())
								&& c1.getSource().equals(c2.getSource()))
						{
							listeComposantesDistinct.add(c1);
						}
					}
				}
			}

			if(logger.isDebugEnabled())
				logger.debug("listeComposantesDistinct.size()--->"+listeComposantesDistinct.size());

			listeComposantes.removeAll(listeComposantesDistinct);

			if(logger.isDebugEnabled())
				logger.debug("Nouvelle(s) composante(s) ----- listeComposantes.size()--->"+listeComposantes.size());

			for(Composante c3 : listeComposantes)
			{
				if(logger.isDebugEnabled())
					logger.debug("####################-->"+c3.toString());
				listeComposantesMerge.add(c3);				
			}
		}
		return listeComposantesMerge;
	}

	public void setListeComposantes(List<Composante> listeComposantes) {
		this.listeComposantes = listeComposantes;
	}

	public List<Composante> getListeComposantesMerge() {
		if(this.listeComposantesMerge==null)
			this.getListeComposantes();
		return listeComposantesMerge;
	}

	public void setListeComposantesMerge(List<Composante> listeComposantesMerge) {
		this.listeComposantesMerge = listeComposantesMerge;
	}

	public List<CGE> getListeCGE() {

		if(listeCGEMerge==null)
		{
			List<CGE> listeCGEFromBdd = getDomainService().getListeCGEFromBddByAnneeAndSource(getSessionController().getCurrentAnnee(),getFrom());
			this.listeCGE = getDomainServiceScolarite().recupererListeCGE(getSessionController().getCurrentAnnee(), getFrom());
			if(listeCGEFromBdd!=null)
				this.listeCGEMerge = new ArrayList<CGE>(listeCGEFromBdd);
			else
				this.listeCGEMerge = new ArrayList<CGE>();
			List<CGE> listeCGEDistinct = new ArrayList<CGE>();


			if(logger.isDebugEnabled())
			{
				logger.debug("listeCGE.size()--->"+listeCGE.size());
				logger.debug("listeCGEFromBdd.size()--->"+listeCGEFromBdd.size());
			}

			for(CGE c1 : listeCGE)
			{
				if(listeCGEFromBdd!=null)
				{
					for(CGE c2 : listeCGEFromBdd)
					{
						if(c1.getAnnee().equals(c2.getAnnee())
								&& c1.getCodeCGE().equals(c2.getCodeCGE())
								&& c1.getSource().equals(c2.getSource()))
						{
							listeCGEDistinct.add(c1);
						}
					}
				}
			}

			if(logger.isDebugEnabled())
				logger.debug("listeCGEDistinct.size()--->"+listeCGEDistinct.size());

			listeCGE.removeAll(listeCGEDistinct);

			if(logger.isDebugEnabled())
				logger.debug("Nouveaux(s) CGE(s) ----- listeCGE.size()--->"+listeCGE.size());

			for(CGE c3 : listeCGE)
			{
				if(logger.isDebugEnabled())
					logger.debug("####################-->"+c3.toString());
				listeCGEMerge.add(c3);				
			}
		}
		return listeCGEMerge;
	}

	public void setListeCGE(List<CGE> listeCGE) {
		this.listeCGE = listeCGE;
	}

	public List<CGE> getListeCGEMerge() {
		if(this.listeCGEMerge==null)
			this.getListeCGE();		
		return listeCGEMerge;
	}

	public void setListeCGEMerge(List<CGE> listeCGEMerge) {
		this.listeCGEMerge = listeCGEMerge;
	}

	public List<Composante> getFilteredComposantes() {
		return filteredComposantes;
	}

	public void setFilteredComposantes(List<Composante> filteredComposantes) {
		this.filteredComposantes = filteredComposantes;
	}

	public List<CGE> getFilteredCGE() {
		return filteredCGE;
	}

	public void setFilteredCGE(List<CGE> filteredCGE) {
		this.filteredCGE = filteredCGE;
	}

	public List<PersonnelComposante> getListeComposantesDetailsDroits() 
	{
		return listeComposantesDetailsDroits;
	}

	public void setListeComposantesDetailsDroits(
			List<PersonnelComposante> listeComposantesDetailsDroits) {
		this.listeComposantesDetailsDroits = listeComposantesDetailsDroits;
	}

	public List<PersonnelComposante> getFilteredDetailsDroits() {
		return filteredDetailsDroits;
	}

	public void setFilteredDetailsDroits(List<PersonnelComposante> filteredDetailsDroits) {
		this.filteredDetailsDroits = filteredDetailsDroits;
	}

	public List<PersonnelComposante> getListeComposantesDetailsDroitsMerge() 
	{
		return listeComposantesDetailsDroitsMerge;
	}

	public void setListeComposantesDetailsDroitsMerge(
			List<PersonnelComposante> listeComposantesDetailsDroitsMerge) {
		this.listeComposantesDetailsDroitsMerge = listeComposantesDetailsDroitsMerge;
	}

	public List<PersonnelComposante> getListePlayersDetailsDroits() {
		return listePlayersDetailsDroits;
	}

	public void setListePlayersDetailsDroits(
			List<PersonnelComposante> listePlayersDetailsDroits) {
		this.listePlayersDetailsDroits = listePlayersDetailsDroits;
	}
}