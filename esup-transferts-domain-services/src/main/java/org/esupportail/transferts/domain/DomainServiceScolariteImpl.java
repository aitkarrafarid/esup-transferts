/**
 * ESUP-Portail Blank Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.esupportail.commons.utils.Assert;
import org.esupportail.transferts.domain.beans.CGE;
import org.esupportail.transferts.domain.beans.Composante;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.IdentifiantEtudiant;
import org.esupportail.transferts.domain.beans.IndOpi;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.PersonnelComposante;
import org.esupportail.transferts.domain.beans.TrBac;
import org.esupportail.transferts.domain.beans.TrCommuneDTO;
import org.esupportail.transferts.domain.beans.TrDepartementDTO;
import org.esupportail.transferts.domain.beans.TrEtablissementDTO;
import org.esupportail.transferts.domain.beans.TrInfosAdmEtu;
import org.esupportail.transferts.domain.beans.TrPaysDTO;
import org.esupportail.transferts.domain.beans.TrResultatVdiVetDTO;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Farid AIT KARRA (Universite de d'Artois) - 2011
 * 
 */
public class DomainServiceScolariteImpl implements DomainServiceScolarite, InitializingBean {

	/**
	 * For Serialize.
	 */
	private static final long serialVersionUID = 5562208937407153456L;

	private String sourceScol; 
	
	private String user;
	
	private String password;	
	
	private DomainServiceScolarite dss;
	
	private String forcerBlocage;
	private List<String> forcerBlocageListSplit = new ArrayList<String>();	

	/**
	 * Constructor.
	 */
	public DomainServiceScolariteImpl() {
		super();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(sourceScol, "property sourceScol of class "
				+ this.getClass().getName() + " can not be null");			

		if( getSourceScol().equals("RIMBAUS"))
			dss= new DomainServiceRimbausImpl(this.forcerBlocageListSplit);
		else if (getSourceScol().equals("APOGEE"))
//			dss= new DomainServiceApogeeImpl(this.forcerBlocageListSplit);
			dss= new DomainServiceApogeeImpl(this.forcerBlocageListSplit, user, password);
		else
			System.err.println("Propriete non renseigne");
		
		if(this.forcerBlocage!=null && this.forcerBlocage!="" && ((this.forcerBlocage.split(",")).length>1))
		{
			String[] tokens = this.forcerBlocage.split(",");
			for(int i=0; i<tokens.length; i++)
				this.forcerBlocageListSplit.add(tokens[i]);
		}
		else
			this.forcerBlocageListSplit.add(this.forcerBlocage);	
		
	}

	@Override
	public EtudiantRef getCurrentEtudiant(String supannEtuId) {
		return dss.getCurrentEtudiant(supannEtuId);
	}

	@Override
	public EtudiantRef getCurrentEtudiantIne(String ine, Date dateNaissance) {
		return dss.getCurrentEtudiantIne(ine, dateNaissance);
	}

	@Override
	public List<TrCommuneDTO> getCommunes(String codePostal) {
		return dss.getCommunes(codePostal);
	}

	@Override
	public List<TrPaysDTO> getListePays() {
		return dss.getListePays();
	}

	@Override
	public List<TrDepartementDTO> getListeDepartements() {
		return dss.getListeDepartements();
	}

	@Override
	public List<TrEtablissementDTO> getListeEtablissements(
			String typeEtablissement, String dept) {
		return dss.getListeEtablissements(typeEtablissement, dept);
	}

	@Override
	public TrEtablissementDTO getEtablissementByRne(String rne) {
		return dss.getEtablissementByRne(rne);
	}

	@Override
	public TrEtablissementDTO getEtablissementByDepartement(String dep) {
		return dss.getEtablissementByDepartement(dep);
	}

	@Override
	public TrBac getBaccalaureat(String supannEtuId) {
		return dss.getBaccalaureat(supannEtuId);
	}

	@Override
	public TrResultatVdiVetDTO getSessionsResultats(String supannEtuId) {
		return dss.getSessionsResultats(supannEtuId);
	}
	
	@Override
	public IndOpi getInfosOpi(String numeroEtudiant) {
		return dss.getInfosOpi(numeroEtudiant);
	}

	@Override
	public TrPaysDTO getPaysByCodePays(String codePays) {
		return dss.getPaysByCodePays(codePays);
	}

	@Override
	public String getComposante(String supannEtuId) {
		// TODO Auto-generated method stub
		//return null;
		return dss.getComposante(supannEtuId);
	}

//	@Override
//	public String getEtapePremiere(String supannEtuId) {
//		return dss.getEtapePremiere(supannEtuId);
//	}	

	@Override
	public List<OffreDeFormationsDTO> getOffreDeFormation(String rne, Integer annee) {
		return dss.getOffreDeFormation(rne, annee);
	}	

	@Override
	public List<IndOpi> synchroOpi(List<IndOpi> listeSynchroScolarite) {
		return dss.synchroOpi(listeSynchroScolarite);
	}	

	@Override
	public List<TrBac> recupererBacOuEquWS(String codeBac) {
		return dss.recupererBacOuEquWS(codeBac);
	}	

	@Override
	public List<PersonnelComposante> recupererComposante(String uid, String diplayName, String source, Integer annee) {
		return dss.recupererComposante(uid, diplayName, source, annee);
	}	

//	@Override
//	public String getNumeroEtudiantByIne(String ine, Date dateNaissance) {
//		return dss.getNumeroEtudiantByIne(ine, dateNaissance);
//	}	

	@Override
	public Integer getAuthEtu(String ine, Date dateNaissanceApogee) {
		return dss.getAuthEtu(ine, dateNaissanceApogee);
	}	

	@Override
	public List<Composante> recupererListeComposantes(Integer annee, String source) {
		return dss.recupererListeComposantes(annee, source);
	}	
	
	@Override
	public List<CGE> recupererListeCGE(Integer annee, String source) {
		return dss.recupererListeCGE(annee, source);
	}

	@Override
	public Map<String, String> getEtapePremiereAndCodeCgeAndLibCge(String supannEtuId) {
		return dss.getEtapePremiereAndCodeCgeAndLibCge(supannEtuId);
	}	

//	@Override
//	public Integer getCleIndByIne(String ine) {
//		return dss.getCleIndByIne(ine);
//	}
//
//	@Override
//	public Integer getCleIndByNumeroEtudiant(String numeroEtudiant) {
//		return dss.getCleIndByNumeroEtudiant(numeroEtudiant);
//	}	

	@Override
	public IdentifiantEtudiant getIdentifiantEtudiantByIne(String codNneIndOpi, String codCleNneIndOpi) 
	{
		return dss.getIdentifiantEtudiantByIne(codNneIndOpi, codCleNneIndOpi);
	}	

	@Override
	public TrInfosAdmEtu getInfosAdmEtu(String supannEtuId) {
		return dss.getInfosAdmEtu(supannEtuId);
	}	
	
	public String getSourceScol() {
		return sourceScol;
	}

	public void setSourceScol(String sourceScol) {
		System.err.println("SOURCESCOL"+sourceScol);
		this.sourceScol = sourceScol;
	}

	public String getForcerBlocage() {
		return forcerBlocage;
	}

	public void setForcerBlocage(String forcerBlocage) {
		this.forcerBlocage = forcerBlocage;
	}

	public List<String> getForcerBlocageListSplit() {
		return forcerBlocageListSplit;
	}

	public void setForcerBlocageListSplit(List<String> forcerBlocageListSplit) {
		this.forcerBlocageListSplit = forcerBlocageListSplit;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}