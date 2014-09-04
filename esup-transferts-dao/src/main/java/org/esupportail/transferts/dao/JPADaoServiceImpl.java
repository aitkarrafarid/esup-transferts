/**
 * ESUP-Portail Blank Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-blank
 */
package org.esupportail.transferts.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.esupportail.commons.dao.AbstractGenericJPADaoService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.AccueilAnnee;
import org.esupportail.transferts.domain.beans.AccueilDecision;
import org.esupportail.transferts.domain.beans.AccueilResultat;
import org.esupportail.transferts.domain.beans.Avis;
import org.esupportail.transferts.domain.beans.CGE;
import org.esupportail.transferts.domain.beans.CGEPK;
import org.esupportail.transferts.domain.beans.CodeSizeAnnee;
import org.esupportail.transferts.domain.beans.Composante;
import org.esupportail.transferts.domain.beans.ComposantePK;
import org.esupportail.transferts.domain.beans.DatasExternePK;
import org.esupportail.transferts.domain.beans.DecisionDossier;
import org.esupportail.transferts.domain.beans.EtatDossier;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.EtudiantRefPK;
import org.esupportail.transferts.domain.beans.Fichier;
import org.esupportail.transferts.domain.beans.FichierPK;
import org.esupportail.transferts.domain.beans.IndOpi;
import org.esupportail.transferts.domain.beans.DatasExterne;
import org.esupportail.transferts.domain.beans.LocalisationDossier;
import org.esupportail.transferts.domain.beans.OffreDeFormationPK;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.Parametres;
import org.esupportail.transferts.domain.beans.PersonnelComposante;
import org.esupportail.transferts.domain.beans.PersonnelComposantePK;
import org.esupportail.transferts.domain.beans.SituationUniversitaire;
import org.esupportail.transferts.domain.beans.Test;
import org.esupportail.transferts.domain.beans.WsPub;
import org.esupportail.transferts.domain.beans.WsPubPK;
/**
 * @author Farid AIT KARRA (Universite d'Artois) - 2014
 * 
 * The Hiberate implementation of the DAO service.
 */
public class JPADaoServiceImpl extends AbstractGenericJPADaoService implements DaoService {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 3152554337896617315L;

	/**
	 * For Logging.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());	

	/**
	 * JPA entity manager
	 */
	EntityManager entityManager;	

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	//////////////////////////////////////////////////////////////
	// EntityManager
	//////////////////////////////////////////////////////////////

	/**
	 * @param em the em to set
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
	}

	/**
	 * @see org.esupportail.commons.dao.AbstractGenericJPADaoService#getEntityManager()
	 */
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}	

	@Override
	public EtudiantRef getEtudiantRef(String numeroEtudiant, Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("getEtudiantRef(String numeroEtudiant, Integer annee)");
		}
		try{
			EtudiantRefPK cleEtudiantRef = new EtudiantRefPK(numeroEtudiant, annee);
			EtudiantRef etudiant = entityManager.find(EtudiantRef.class, cleEtudiantRef);
			return etudiant;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void addDemandeTransferts(EtudiantRef currentEtudiant) {
		if (logger.isDebugEnabled()){
			logger.debug("currentEtudiant --> "+currentEtudiant);
		}
		entityManager.merge(currentEtudiant);	
	}

	//	@Override
	//	public List<EtudiantRef> getAllDemandesTransferts() {
	//		Query q = entityManager.createNamedQuery("allDemandesTransferts");
	//		@SuppressWarnings("unchecked")
	//		List<EtudiantRef> ret = (List<EtudiantRef>)q.getResultList();
	//		return ret;
	//	}

	@Override
	public List<IndOpi> getAllIndOpiBySource(Integer annee, String source) {
		if (logger.isDebugEnabled())
			logger.debug("public List<IndOpi> getAllIndOpiBySource(Integer annee, String source)-->"+annee+"-----"+source);		
		Query q = entityManager.createNamedQuery("allIndOpiBySource");
//		Query q = entityManager.createNamedQuery("allIndOpi");
		q.setParameter("annee", annee);
		q.setParameter("source", source);
		@SuppressWarnings("unchecked")
		List<IndOpi> ret = (List<IndOpi>)q.getResultList();
		return ret;
	}

	@Override
	public List<WsPub> getListeWsPub() {
		Query q = entityManager.createNamedQuery("allWsPub");
		@SuppressWarnings("unchecked")
		List<WsPub> ret = (List<WsPub>)q.getResultList();
		return ret;
	}

	@Override
	public WsPub getWsPubByRneAndAnnee(String rne, Integer annee) {
		if (logger.isDebugEnabled())
			logger.debug("getWsPubByRneAndAnnee(String rne, Integer annee)");
		try{
			//			Query q = entityManager.createNamedQuery("getWsPubByRneAndAnnee");
			//			q.setParameter("rne", rne);
			//			q.setParameter("annee", annee);
			WsPubPK cleWsPub = new WsPubPK(rne, annee);
			WsPub ret = entityManager.find(WsPub.class, cleWsPub);				
			//			WsPub ret = (WsPub)q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<WsPub> getWsPubByAnnee(Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("getWsPubByAnnee(Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getWsPubByAnnee");
			q.setParameter("annee", annee);
			@SuppressWarnings("unchecked")
			List<WsPub> ret = (List<WsPub>)q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}	

	@Override
	public void addAvis(Avis a) {
		if (logger.isDebugEnabled()){
			logger.debug("addAvis()");
			logger.debug("Avis --> "+a);
		}
		entityManager.merge(a);	
	}

	@Override
	public List<Avis> getAvis(String numeroEtudiant, Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("getAvis(String numeroEtudiant)");
		}
		try{
			Query q = entityManager.createNamedQuery("getAvisByNumeroEtudiantAndAnnee");
			q.setParameter("numEtu", numeroEtudiant);
			q.setParameter("annee", annee);
			@SuppressWarnings("unchecked")
			List<Avis> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return new ArrayList<Avis>();
		}
	}

	@Override
	public List<EtatDossier> getEtatsDossier() {
		if (logger.isDebugEnabled()){
			logger.debug("getEtatsDossier()");
		}
		try{
			Query q = entityManager.createNamedQuery("getEtatsDossier");
			@SuppressWarnings("unchecked")
			List<EtatDossier> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return new ArrayList<EtatDossier>();
		}
	}

	@Override
	public EtatDossier getEtatDossierById(Integer idEtatDossier) {
		if (logger.isDebugEnabled()){
			logger.debug("getEtatDossierById(Integer idEtatDossier)");
		}
		try{
			Query q = entityManager.createNamedQuery("getEtatDossierByIdEtatDossier");
			q.setParameter("idEtatDossier", idEtatDossier);
			EtatDossier ret = (EtatDossier) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<LocalisationDossier> getLocalisationDossier() {
		if (logger.isDebugEnabled()){
			logger.debug("getLocalisationDossier()");
		}
		try{
			Query q = entityManager.createNamedQuery("getLocalisationDossier");
			@SuppressWarnings("unchecked")
			List<LocalisationDossier> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return new ArrayList<LocalisationDossier>();
		}
	}

	@Override
	public LocalisationDossier getLocalisationDossierById(Integer idLocalisationDossier) 
	{
		if (logger.isDebugEnabled()){
			logger.debug("getLocalisationDossierById(Integer idLocalisationDossier)");
		}
		try{
			Query q = entityManager.createNamedQuery("getLocalisationDossierByIdLocalisationDossier");
			q.setParameter("idLocalisationDossier", idLocalisationDossier);
			LocalisationDossier ret = (LocalisationDossier) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<DecisionDossier> getDecisionDossier() {
		if (logger.isDebugEnabled()){
			logger.debug("getDecisionDossier()");
		}
		try{
			Query q = entityManager.createNamedQuery("getDecisionsDossier");
			@SuppressWarnings("unchecked")
			List<DecisionDossier> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return new ArrayList<DecisionDossier>();
		}
	}

	@Override
	public DecisionDossier getDecisionDossierById(Integer idDecisionDossier) {
		if (logger.isDebugEnabled()){
			logger.debug("getDecisionDossierById(Integer idDecisionDossier)");
		}
		try{
			Query q = entityManager.createNamedQuery("getDecisionDossierByIdDecisionDossier");
			q.setParameter("idDecisionDossier", idDecisionDossier);
			DecisionDossier ret = (DecisionDossier) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void addFichier(Fichier f) {
		if (logger.isDebugEnabled()){
			logger.debug("addFichier(Fichier f)");
			logger.debug("Fichier --> "+f);
		}		
		entityManager.merge(f);
	}

	@Override
	public List<Fichier> getFichiersByAnneeAndFrom(Integer annee, String from) {
		if (logger.isDebugEnabled()){
			logger.debug("getFichiersByAnnee()");
		}
		try{
			Query q = entityManager.createNamedQuery("getFichiersByAnneeAndFrom");
			q.setParameter("annee", annee);
			q.setParameter("from", from);
			@SuppressWarnings("unchecked")
			List<Fichier> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return new ArrayList<Fichier>();
		}
	}

	@Override
	public void updateDefautFichier(Fichier fichier, Integer annee, String from) {
		if (logger.isDebugEnabled()){
			logger.debug("updateDefautFichier(Fichier fichier)");
			logger.debug("fichier --> "+fichier);
		}
		Query q = entityManager.createNamedQuery("resetDefautFichierByAnneeAndFrom");
		q.setParameter("annee", annee);
		q.setParameter("from", from);
		q.executeUpdate();
		entityManager.merge(fichier);
	}

	@Override
	public Fichier getFichierDefautByAnneeAndFrom(Integer annee, String from) {
		if (logger.isDebugEnabled()){
			logger.debug("getFichierDefaut");
		}
		try{
			Query q = entityManager.createNamedQuery("getFichierDefautByAnneeAndFrom");
			q.setParameter("annee", annee);
			q.setParameter("from", from);
			Fichier ret = (Fichier) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void addIndOpi(IndOpi opi) {
		if (logger.isDebugEnabled()){
			logger.debug("addIndOpi()");
			logger.debug("opi --> "+opi);
		}
		try{
			String sql = "select lpad(OPI_SEQ.NEXTVAL,4,'0') from DUAL";
			@SuppressWarnings("rawtypes")
			List results = entityManager.createNativeQuery(sql).getResultList();
			String value = (String)results.iterator().next();
			if (logger.isDebugEnabled()){
				logger.debug("Numero de sequence --> "+value);
			}		
			String moduloBase32 = opi.getNumeroOpi();
			opi.setNumeroOpi(moduloBase32+value);
			//opi.setSynchro(0);
			opi.getVoeux().setNumeroOpi(moduloBase32+value);
			entityManager.merge(opi);
		}
		catch(NoResultException e){
			e.fillInStackTrace();
		}		
	}

	@Override
	public List<EtudiantRef> getAllDemandesTransfertsByAnnee(Integer annee, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("getAllDemandesTransfertsByAnnee(Integer annee, String source)");
		}
		try{
			Query q = entityManager.createNamedQuery("allDemandesTransfertsByAnnee");
			q.setParameter("annee", annee);
			q.setParameter("source", source);
			@SuppressWarnings("unchecked")
			List<EtudiantRef> ret = (List<EtudiantRef>) q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<Integer> getListeAnnees() {
		if (logger.isDebugEnabled()){
			logger.debug("getListeAnnees()");
		}
		try{
			Query q = entityManager.createNamedQuery("getAnneeCodeSize");
			@SuppressWarnings("unchecked")
			List<Integer> ret = (List<Integer>) q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Fichier getFichierByIdAndAnneeAndFrom(String md5, Integer annee, String from) {
		if (logger.isDebugEnabled())
			logger.debug("getFichierByIdAndAnneeAndFrom(String md5, Integer annee, String from)-->"+md5+"-----"+annee+"-----"+from);
		try
		{
			FichierPK cleFichier = new FichierPK(md5, annee, from);
			Fichier fichier = entityManager.find(Fichier.class, cleFichier);
			return fichier;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Avis getDernierAvisFavorable(String numeroEtudiant, Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("getDernierAvisFavorable(String numeroEtudiant, Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getDernierAvisFavorableByNumeroEtudiantAndAnnee");
			q.setParameter("numEtu", numeroEtudiant);
			q.setParameter("annee", annee);
			q.setMaxResults(1);
			Avis ret = (Avis) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return new Avis();
		}
	}

//	@Override
//	public AdresseEtablissement getAdresseEtablissementByRne(String rne) {
//		if (logger.isDebugEnabled()){
//			logger.debug("getAdresseEtablissementByRne(String rne)");
//		}
//		try{
//			Query q = entityManager.createNamedQuery("getAdresseEtablissementByRne");
//			q.setParameter("rne", rne);
//			AdresseEtablissement ret = (AdresseEtablissement) q.getSingleResult();
//			return ret;
//		}
//		catch(NoResultException e){
//			return new AdresseEtablissement();
//		}
//	}

	@Override
	public void deleteDemandeTransfert(EtudiantRef demandeTransferts, Integer annee) 
	{
		if (logger.isDebugEnabled()){
			logger.debug("deleteDemandeTransfert(EtudiantRef demandeTransferts)");
		}
		try{
			Query q = entityManager.createNamedQuery("deleteAvisByNumeroEtudiantAndAnnee");
			q.setParameter("annee", annee);
			q.setParameter("numEtu", demandeTransferts.getNumeroEtudiant());
			int delete = q.executeUpdate();
			if (logger.isDebugEnabled()){
				logger.debug("delete ---> "+delete);
			}			
			EtudiantRefPK cleEtudiantRef = new EtudiantRefPK(demandeTransferts.getNumeroEtudiant(), annee);
			EtudiantRef etudiant = entityManager.find(EtudiantRef.class, cleEtudiantRef);
			entityManager.remove(etudiant);
		}
		catch(NoResultException e){
			e.printStackTrace();
		}		
	}

	/*Statistiques*/
	@Override
	public Long getDemandesTransfertsByEnCoursAndAnnee(Integer annee, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("getDemandesTransfertsByEnCoursAndAnnee(Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getDemandesTransfertsByEnCoursAndAnnee");
			q.setParameter("annee", annee);
			q.setParameter("source", source);
			Long ret = (long) q.getResultList().size();
			if (logger.isDebugEnabled())
				logger.debug("getDemandesTransfertsByEnCoursAndAnnee(Integer annee) --> " + ret);
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Long getDemandesTransfertsByAvisSaisieAndAnnee(Integer annee, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("getDemandesTransfertsByAvisSaisieAndAnnee(Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getDemandesTransfertsByAvisSaisieAndAnnee");
			q.setParameter("annee", annee);
			q.setParameter("source", source);
			Long ret = (long) q.getResultList().size();
			if (logger.isDebugEnabled())
				logger.debug("getDemandesTransfertsByAvisSaisieAndAnnee(Integer annee) --> " + ret);			
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Long getDemandesTransfertsByTerminerAndAnnee(Integer annee, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("getDemandesTransfertsByTerminerAndAnnee(Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getDemandesTransfertsByTerminerAndAnnee");
			q.setParameter("annee", annee);
			q.setParameter("source", source);
			Long ret = (long) q.getResultList().size();
			logger.debug("getDemandesTransfertsByTerminerAndAnnee(Integer annee) --> " + ret);				
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Long getDemandesTransfertsByPbOpiAndAnnee(Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("getDemandesTransfertsByPbOpiAndAnnee(Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getDemandesTransfertsByPbOpiAndAnnee");
			q.setParameter("annee", annee);
			Long ret = (long) q.getResultList().size();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Long getDemandesTransfertsByEtabNonPartenaireAndAnnee(Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("getDemandesTransfertsByEtabNonPartenaireAndAnnee(Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getDemandesTransfertsByEtabNonPartenaireAndAnnee");
			q.setParameter("annee", annee);
			Long ret = (long) q.getResultList().size();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Long getDemandesTransfertsByOpiOkAndAnnee(Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("getDemandesTransfertsByOpiOkAndAnnee(Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getDemandesTransfertsByOpiOkAndAnnee");
			q.setParameter("annee", annee);
			Long ret = (long) q.getResultList().size();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	//	@Override
	//	public List<DatasExterne> getInterditBu(String numeroEtudiant) {
	//		if (logger.isDebugEnabled()){
	//			logger.debug("getInterditBu()");
	//		}
	//		try{
	//			Query q = entityManager.createNamedQuery("getAllInterditBuByNumeroEtudiant");
	//			q.setParameter("numeroEtudiant", numeroEtudiant);
	//			@SuppressWarnings("unchecked")
	//			List<DatasExterne> ret = q.getResultList();
	//			return ret;
	//		}
	//		catch(NoResultException e){
	//			return null;
	//		}
	//	}

	@Override
	public List<DatasExterne> getAllDatasExterneByIdentifiant(String identifiant) {
		if (logger.isDebugEnabled())
			logger.debug("public List<DatasExterne> getAllDatasExterneByIdentifiant(String identifiant)-->"+identifiant);
		try{
			Query q = entityManager.createNamedQuery("getAllDatasExterneByIdentifiant");
			q.setParameter("identifiant", identifiant);
			@SuppressWarnings("unchecked")
			List<DatasExterne> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}	

	@Override
	public List<DatasExterne> getAllDatasExterneByIdentifiantAndNiveau(String identifiant, Integer niveau) 
	{
		if (logger.isDebugEnabled())
			logger.debug("public List<DatasExterne> getAllDatasExterneByIdentifiantAndNiveau(String identifiant, Integer niveau)-->"+identifiant+"-----"+niveau);
		try{
			Query q = entityManager.createNamedQuery("getAllDatasExterneByIdentifiantAndNiveau");
			q.setParameter("identifiant", identifiant);
			q.setParameter("niveau", niveau);
			@SuppressWarnings("unchecked")
			List<DatasExterne> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}	
	
	@Override
	public DatasExterne getAllDatasExterneByCodeInterditAndNumeroEtudiant(String identifiant, String code) 
	{
		if (logger.isDebugEnabled())
			logger.debug("public List<DatasExterne> getAllDatasExterneByCodeInterditAndNumeroEtudiant(String numeroEtudiant, String codeInterdit)-->"+identifiant+"-----"+code);
		try{
			DatasExternePK cleDatasExterne = new DatasExternePK(identifiant, code);
			DatasExterne ret = entityManager.find(DatasExterne.class, cleDatasExterne);
			return ret;			
		}
		catch(NoResultException e){
			return null;
		}	
	}	

	@Override
	public List<String> getIndOpiExtractBySource(Integer annee, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("public List<String> getIndOpiExtractBySource(Integer annee, String source)-->"+annee+"-----"+source);
		}
		try{
			Query q = entityManager.createNamedQuery("getIndOpiExtractBySource");
			q.setParameter("annee", annee);
			q.setParameter("source", source);
			@SuppressWarnings("unchecked")
			List<String> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<String> getVoeuxInsExtractBySource(Integer annee, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("public List<String> getVoeuxInsExtractBySource(Integer annee, String Source)-->"+annee+"-----"+source);
		}
		try{
			Query q = entityManager.createNamedQuery("getVoeuxInsExtractBySource");
			q.setParameter("annee", annee);
			q.setParameter("source", source);
			@SuppressWarnings("unchecked")
			List<String> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public CodeSizeAnnee getCodeSizeByAnnee(Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("getCodeSizeByAnnee(Integer annee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getCodeSizeByAnnee");
			q.setParameter("annee", annee);
			CodeSizeAnnee ret = (CodeSizeAnnee) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<CodeSizeAnnee> getAllCodeSize() {
		if (logger.isDebugEnabled()){
			logger.debug("getAllCodeSize()");
		}
		try{
			Query q = entityManager.createNamedQuery("getAllCodeSize");
			@SuppressWarnings("unchecked")
			List<CodeSizeAnnee> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void addCodeSize(CodeSizeAnnee cs) {
		if (logger.isDebugEnabled()){
			logger.debug("addCodeSize(CodeSizeAnnee cs)");
			logger.debug("CodeSizeAnnee --> "+cs);
		}		
		entityManager.merge(cs);
	}

	@Override
	public void updateDefautCodeSize(CodeSizeAnnee cs) {
		if (logger.isDebugEnabled()){
			logger.debug("updateDefautCodeSize(CodeSizeAnnee cs)");
			logger.debug("CodeSizeAnnee --> "+cs);
		}
		Query q = entityManager.createNamedQuery("resetDefautCodeSize");
		q.executeUpdate();
		entityManager.merge(cs);
	}

	@Override
	public CodeSizeAnnee getCodeSizeDefaut() {
		if (logger.isDebugEnabled()){
			logger.debug("getCodeSizeDefaut()");
		}
		try{
			Query q = entityManager.createNamedQuery("getCodeSizeDefaut");
			CodeSizeAnnee ret = (CodeSizeAnnee) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<Parametres> getAllParametres() {
		if (logger.isDebugEnabled()){
			logger.debug("getAllParametres()");
		}
		try{
			Query q = entityManager.createNamedQuery("getAllParametres");
			@SuppressWarnings("unchecked")
			List<Parametres> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Parametres getParametreByCode(String codeParametre) {
		if (logger.isDebugEnabled()){
			logger.debug("getCodeSizeDefaut()");
		}
		try{
			Query q = entityManager.createNamedQuery("getParametreByCode");
			q.setParameter("codeParametre", codeParametre);
			Parametres ret = (Parametres) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void addParametre(Parametres param) {
		if (logger.isDebugEnabled()){
			logger.debug("addParametre(Parametres param)");
			logger.debug("param --> "+param);
		}
		entityManager.merge(param);	
	}

	@Override
	public List<EtudiantRef> getAllDemandesTransfertsByAnneeAndNonTraite(Integer annee, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("getAllDemandesTransfertsByAnneeAndNonTraite(Integer annee, String source)");
		}
		try{
			Query q = entityManager.createNamedQuery("allDemandesTransfertsByAnneeAndNonTraite");
			q.setParameter("annee", annee);
			q.setParameter("source", source);
			@SuppressWarnings("unchecked")
			List<EtudiantRef> ret = (List<EtudiantRef>) q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void updateLibelleVersionEtapeLocal(IndOpi etu) {
		if (logger.isDebugEnabled()){
			logger.debug("updateLibelleVersionEtapeLocal(IndOpi etu)");
			logger.debug("Etudiant --> "+etu);
		}
		entityManager.merge(etu);
	}

	@Override
	public void deleteFichier(String md5, Integer annee, String from) throws Exception
	{
		if (logger.isDebugEnabled()){
			logger.debug("deleteFichier(String md5, Integer annee, String from)");
			logger.debug("-->"+md5+"-"+annee+"-"+from);
		}
		FichierPK cleFichier = new FichierPK(md5, annee, from);
		Fichier fichier = entityManager.find(Fichier.class, cleFichier);
		entityManager.remove(fichier);
	}

	@Override
	public List<OffreDeFormationsDTO> getSelectedOdfs(Integer currentAnnee, String rne) {
		if (logger.isDebugEnabled())
			logger.debug("getSelectedOdfs(Integer currentAnnee, String rne)");
		try{
			Query q = entityManager.createNamedQuery("getAllOffreDeFormationByAnneeAndRne");
			q.setParameter("annee", currentAnnee);
			q.setParameter("rne", rne);
			@SuppressWarnings("unchecked")
			List<OffreDeFormationsDTO> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}	

	@Override
	public List<OffreDeFormationsDTO> getAllOffreDeFormationByAnneeAndRneAndAtifOuPas(Integer currentAnnee, String rne) {
		if (logger.isDebugEnabled())
			logger.debug("getAllOffreDeFormationByAnneeAndRneAndAtifOuPas(Integer currentAnnee, String rne)");
		try{
			Query q = entityManager.createNamedQuery("getAllOffreDeFormationByAnneeAndRneAndAtifOuPas");
			q.setParameter("annee", currentAnnee);
			q.setParameter("rne", rne);
			@SuppressWarnings("unchecked")
			List<OffreDeFormationsDTO> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}		

	@Override
	public void addOdfs(OffreDeFormationsDTO[] selectedOdfs) {
		if (logger.isDebugEnabled())
			logger.debug("addOdfs(OffreDeFormationsDTO[] selectedOdfs)");
		Date date = new Date();
		for(OffreDeFormationsDTO odf : selectedOdfs)
		{
			odf.setDateMaj(date);
			entityManager.merge(odf);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getOdfTypesDiplomeByRneAndAnnee(String rne, Integer currentAnnee, boolean actif, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("getOdfTypesDiplomeByRneAndAnnee(String rne, Integer currentAnnee, boolean actif)");
		}
		try{
			Query q;
			if(actif)
			{
				if(source.equals("D"))
					q = entityManager.createNamedQuery("getOdfTypesDiplomeByRneAndAnneeAndDepart");
				else
					q = entityManager.createNamedQuery("getOdfTypesDiplomeByRneAndAnneeAndArrivee");
			}
			else
				q = entityManager.createNamedQuery("getAllOdfTypesDiplomeByRneAndAnnee");
			q.setParameter("rne", rne);
			q.setParameter("annee", currentAnnee);
			//List<String> ret = (List<String>) q.getResultList();
			Map<String, String> map = new HashMap<String, String>();

			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String codeTypeDiplome = (String)resultElement[0];
				String libTypeDiplome = (String)resultElement[1];
				if (logger.isDebugEnabled()){
					logger.debug("################### codeTypeDiplome --> " + codeTypeDiplome);
					logger.debug("################### libTypeDiplome --> " + libTypeDiplome);
				}		        
				map.put(codeTypeDiplome, libTypeDiplome);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void updateWsPub(WsPub partenaire) {
		entityManager.merge(partenaire);
	}

	@Override
	public Date getDateMaxMajByRneAndAnnee(Integer annee, String rne) {
		if (logger.isDebugEnabled())
			logger.debug("getDateMaxMajByRneAndAnnee(Integer annee, String rne)");
		try{
			Query q = entityManager.createNamedQuery("getDateMaxMajByRneAndAnnee");
			q.setParameter("annee", annee);
			q.setParameter("rne", rne);
			Date ret = (Date) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<OffreDeFormationsDTO> getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(Date maxDate, Integer annee, String rne) {
		if (logger.isDebugEnabled())
			logger.debug("getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne(Date maxDate, Integer annee, String rne)");
		try{
			Query q = entityManager.createNamedQuery("getFormationsByMaxDateLocalDifferentDateMaxDistantAndAnneeAndRne");
			q.setParameter("dateMax", maxDate);
			q.setParameter("annee", annee);
			q.setParameter("rne", rne);
			@SuppressWarnings("unchecked")
			List<OffreDeFormationsDTO> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<OffreDeFormationsDTO> getFormationsByRneAndAnnee(String rne, Integer annee) {
		if (logger.isDebugEnabled())
			logger.debug("getFormationsByRneAndAnnee(String rne, Integer annee)");
		try{
			Query q = entityManager.createNamedQuery("getFormationsByRneAndAnnee");
			q.setParameter("rne", rne);
			q.setParameter("annee", annee);
			@SuppressWarnings("unchecked")
			List<OffreDeFormationsDTO> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<Integer, String> getAnneesEtudeByRneAndAnneeAndCodTypDip(String rne, Integer annee, String codTypDip, boolean actif, String source) {
		if (logger.isDebugEnabled())
			logger.debug("getAnneesEtudeByRneAndAnneeAndCodTypDip(String rne, Integer annee, String codTypDip, boolean actif)");
		try{
			Query q;
			if(actif)
				if(source.equals("D"))
					q = entityManager.createNamedQuery("getAnneesEtudeByRneAndAnneeAndCodTypDipAndDepart");
				else
					q = entityManager.createNamedQuery("getAnneesEtudeByRneAndAnneeAndCodTypDipAndArrivee");
			else
				q = entityManager.createNamedQuery("getAllAnneesEtudeByRneAndAnneeAndCodTypDip");
			q.setParameter("rne", rne);
			q.setParameter("annee", annee);
			q.setParameter("codTypDip", codTypDip);
			Map<Integer, String> map = new HashMap<Integer, String>();
			@SuppressWarnings("unchecked")
			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				Integer codeNiveau = (Integer)resultElement[0];
				String libNiveau = (String)resultElement[1];
				if (logger.isDebugEnabled())
				{
					logger.debug("################### codeNiveau --> " + codeNiveau);
					logger.debug("################### libNiveau --> " + libNiveau);
				}
				map.put(codeNiveau, libNiveau);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, String> getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveau(String rne, Integer currentAnnee, String codTypDip,	Integer codeNiveau, boolean actif, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveau(String rne, Integer currentAnnee, String codTypDip,	Integer codeNiveau, boolean actif)");
		}
		try{
			Query q;
			if(actif)
				if(source.equals("D"))
					q = entityManager.createNamedQuery("getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveauAndDepart");
				else
					q = entityManager.createNamedQuery("getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveauAndArrivee");
			else
				q = entityManager.createNamedQuery("getAllLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveau");
			q.setParameter("rne", rne);
			q.setParameter("annee", currentAnnee);
			q.setParameter("codTypDip", codTypDip);
			q.setParameter("codeNiveau", codeNiveau);
			//List<String> ret = (List<String>) q.getResultList();
			Map<String, String> map = new HashMap<String, String>();

			@SuppressWarnings("unchecked")
			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String codeDiplome = (String)resultElement[0];
				String libDiplome = (String)resultElement[1];
				if (logger.isDebugEnabled())
				{
					logger.debug("################### codeDiplome --> " + codeDiplome);
					logger.debug("################### libDiplome --> " + libDiplome);
				}		        
				map.put(codeDiplome, libDiplome);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, String> getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveauAndComposante(String rne, 
			Integer currentAnnee, 
			String codTypDip, 
			Integer codeNiveau, 
			String codeComposante,
			boolean actif) 
			{
		if (logger.isDebugEnabled()){
			logger.debug("getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveauAndComposante(String rne, Integer currentAnnee, String codTypDip, Integer codeNiveau, String codeComposante) ");
		}
		try{
			Query q;
			if(actif)
				q = entityManager.createNamedQuery("getLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveauAndComposante");
			else
				q = entityManager.createNamedQuery("getAllLibellesDiplomeByRneAndAnneeAndCodTypDipAndcodeNiveauAndComposante");
			q.setParameter("rne", rne);
			q.setParameter("annee", currentAnnee);
			q.setParameter("codTypDip", codTypDip);
			q.setParameter("codeNiveau", codeNiveau);
			q.setParameter("codeComposante", codeComposante);
			//List<String> ret = (List<String>) q.getResultList();
			Map<String, String> map = new HashMap<String, String>();

			@SuppressWarnings("unchecked")
			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String codeDiplome = (String)resultElement[0];
				String libDiplome = (String)resultElement[1];
				if (logger.isDebugEnabled())
				{
					logger.debug("################### codeDiplome --> " + codeDiplome);
					logger.debug("################### libDiplome --> " + libDiplome);
				}		        
				map.put(codeDiplome, libDiplome);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
			}	

	@Override
	public List<OffreDeFormationsDTO> getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip(String rne, Integer annee, String codTypDip, Integer codeNiveau, String codeDiplome, String source) {
		if (logger.isDebugEnabled())
			logger.debug("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDip(String rne, Integer annee, String codTypDip, Integer codeNiveau, String codeDiplome)");
		try{
			Query q;
			if(source.equals("D"))
				q = entityManager.createNamedQuery("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDipAndDepart");
			else
				q = entityManager.createNamedQuery("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDipAndArrivee");
			q.setParameter("annee", annee);
			q.setParameter("rne", rne);
			q.setParameter("codTypDip", codTypDip);
			q.setParameter("codeNiveau", codeNiveau);
			q.setParameter("codeDiplome", codeDiplome);
			@SuppressWarnings("unchecked")
			List<OffreDeFormationsDTO> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<OffreDeFormationsDTO> getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposante(String rne, Integer annee, String codTypDip, Integer codeNiveau, String codeComposante, String source) {
		if (logger.isDebugEnabled())
			logger.debug("public List<OffreDeFormationsDTO> getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposante(String rne, Integer annee, String codTypDip, Integer codeNiveau, String codeComposante, String source)");
		try{
			Query q;
			if(source.equals("D"))
				q = entityManager.createNamedQuery("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposanteAndDepart");
			else
				q = entityManager.createNamedQuery("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposanteAndArrivee");
			q.setParameter("annee", annee);
			q.setParameter("rne", rne);
			q.setParameter("codTypDip", codTypDip);
			q.setParameter("codeNiveau", codeNiveau);
			q.setParameter("codeComposante", codeComposante);
			@SuppressWarnings("unchecked")
			List<OffreDeFormationsDTO> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}	
	
	@Override
	public List<OffreDeFormationsDTO> getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDipAndAtifOuPas(String rne, Integer annee, String codTypDip, Integer codeNiveau, String codeDiplome) {
		if (logger.isDebugEnabled())
			logger.debug("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDipAndAtifOuPas(String rne, Integer annee, String codTypDip, Integer codeNiveau, String codeDiplome)");
		try{
			Query q = entityManager.createNamedQuery("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodDipAndAtifOuPas");
			q.setParameter("annee", annee);
			q.setParameter("rne", rne);
			q.setParameter("codTypDip", codTypDip);
			q.setParameter("codeNiveau", codeNiveau);
			q.setParameter("codeDiplome", codeDiplome);
			@SuppressWarnings("unchecked")
			List<OffreDeFormationsDTO> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}	

	@Override
	public List<IndOpi> getAllIndOpiNonSynchroAndSource(Integer annee, String source) {
		if (logger.isDebugEnabled()){
			logger.debug("public List<IndOpi> getAllIndOpiNonSynchroAndSource(Integer annee, String source)");
		}		
		Query q = entityManager.createNamedQuery("allIndOpiNonSynchroAndSource");
		q.setParameter("annee", annee);
		q.setParameter("source", source);
		@SuppressWarnings("unchecked")
		List<IndOpi> ret = (List<IndOpi>)q.getResultList();
		return ret;
	}

	public void updateIndOpi(IndOpi opi) {
		if (logger.isDebugEnabled()){
			logger.debug("public void updateIndOpi(IndOpi opi)");
			logger.debug("opi --> "+opi);
		}
		try{
			entityManager.merge(opi);
		}
		catch(NoResultException e){
			e.fillInStackTrace();
		}		
	}

	@Override
	public List<AccueilAnnee> getListeAccueilAnnee() {
		if (logger.isDebugEnabled()){
			logger.debug("getListeAccueilAnnee()");
		}
		try{
			Query q = entityManager.createNamedQuery("getAccueilAnnee");
			@SuppressWarnings("unchecked")
			List<AccueilAnnee> ret = (List<AccueilAnnee>)q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<AccueilAnnee> getAccueilAnneeSansNull() {
		if (logger.isDebugEnabled()){
			logger.debug("getAccueilAnneeSansNull()");
		}
		try{
			Query q = entityManager.createNamedQuery("getAccueilAnneeSansNull");
			@SuppressWarnings("unchecked")
			List<AccueilAnnee> ret = (List<AccueilAnnee>)q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}
	
	@Override
	public List<AccueilResultat> getListeAccueilResultat() {
		if (logger.isDebugEnabled()){
			logger.debug("getListeAccueilResultat()");
		}
		try{
			Query q = entityManager.createNamedQuery("getAccueilResultat");
			@SuppressWarnings("unchecked")
			List<AccueilResultat> ret = (List<AccueilResultat>)q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<AccueilResultat> getAccueilResultatSansNull() {
		if (logger.isDebugEnabled()){
			logger.debug("getAccueilResultatSansNull()");
		}
		try{
			Query q = entityManager.createNamedQuery("getAccueilResultatSansNull");
			@SuppressWarnings("unchecked")
			List<AccueilResultat> ret = (List<AccueilResultat>)q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}	
	
	@Override
	public AccueilAnnee getAccueilAnneeById(Integer id) {
		if (logger.isDebugEnabled())
			logger.debug("getAccueilAnneeById(Integer id)");
		try{
			AccueilAnnee ret = entityManager.find(AccueilAnnee.class, id);
			return ret;			
		}
		catch(NoResultException e){
			return null;
		}		
	}

	@Override
	public AccueilResultat getAccueilResultatById(Integer id) {
		if (logger.isDebugEnabled())
			logger.debug("getAccueilResultatById(Integer id)");
		try{
			AccueilResultat ret = entityManager.find(AccueilResultat.class, id);
			return ret;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Map<String, String> getListeManagersBySourceAndAnnee(String source, Integer annee)
	{
		if (logger.isDebugEnabled()){
			logger.debug("getListeManagersBySourceAndAnnee(String source, Integer annee)-->"+source+"-----"+annee);
		}
		try{
			Query q = entityManager.createNamedQuery("getListeManagersBySourceAndAnnee");
			q.setParameter("source", source);
			q.setParameter("annee", annee);
			Map<String, String> map = new HashMap<String, String>();

			@SuppressWarnings("unchecked")
			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String codeDiplome = (String)resultElement[0];
				String libDiplome = (String)resultElement[1];
				if (logger.isDebugEnabled())
				{
					logger.debug("################### codeDiplome --> " + codeDiplome);
					logger.debug("################### libDiplome --> " + libDiplome);
				}		        
				map.put(codeDiplome, libDiplome);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void addPersonnelComposante(String uid, String source, Integer annee, List<PersonnelComposante> target) {
		if (logger.isDebugEnabled()){
			logger.debug("addPersonnelComposante(String uid, String source, Integer annee, List<PersonnelComposante> target)");
			logger.debug("uid --> "+uid);
			logger.debug("source --> "+source);
			logger.debug("annee --> "+annee);
			logger.debug("target.size() --> "+target.size());
		}		
		Query q = entityManager.createNamedQuery("deleteListeManagersByUidAndSourceAndAnnee");
		q.setParameter("uid", uid);
		q.setParameter("source", source);
		q.setParameter("annee", annee);
		int delete = q.executeUpdate();
		if (logger.isDebugEnabled()){
			logger.debug("delete ---> "+delete);
		}			
		for(PersonnelComposante pc : target)
		{
			entityManager.merge(pc);
		}
	}

	@Override
	public List<PersonnelComposante> getListeComposantesByUidAndSourceAndAnnee(String uid, String source, Integer annee)
	{
		if (logger.isDebugEnabled()){
			logger.debug("public List<PersonnelComposante> getListeComposantesByUidAndSourceAndAnnee(String uid, String source, Integer annee)");
			logger.debug("uid --> "+uid);
			logger.debug("source --> "+source);		
			logger.debug("annee --> "+annee);
		}
		try{
			Query q = entityManager.createNamedQuery("getListeComposantesByUidAndSourceAndAnnee");
			q.setParameter("uid", uid);
			q.setParameter("source", source);
			q.setParameter("annee", annee);
			@SuppressWarnings("unchecked")
			List<PersonnelComposante> ret = (List<PersonnelComposante>) q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Map<String, String> getOdfComposanteByRneAndAnneeAndActif(String rne, Integer annee) {
		if (logger.isDebugEnabled())
			logger.debug("Map<Integer, String> getOdfComposanteByRneAndAnneeAndActif(String rne, Integer annee)");
		try{
			Query q = entityManager.createNamedQuery("getOdfComposanteByRneAndAnneeAndActif");
			q.setParameter("rne", rne);
			q.setParameter("annee", annee);
			Map<String, String> map = new HashMap<String, String>();
			@SuppressWarnings("unchecked")
			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String codeComposante = (String)resultElement[0];
				String libComposante = (String)resultElement[1];
				if (logger.isDebugEnabled())
				{
					logger.debug("################### codeComposante --> " + codeComposante);
					logger.debug("################### libComposante --> " + libComposante);
				}
				map.put(codeComposante, libComposante);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}	

	public Map<String, String> getOdfComposanteByRneAndAnneeAndActifAndArrivee(String rne, Integer annee) {
		if (logger.isDebugEnabled())
			logger.debug("public Map<String, String> getOdfComposanteByRneAndAnneeAndActifAndArrivee(String rne, Integer annee, String codTypDip)-->"+rne+"-----"+annee);
		try{
			Query q = entityManager.createNamedQuery("getOdfComposanteByRneAndAnneeAndActifAndArrivee");
			q.setParameter("rne", rne);
			q.setParameter("annee", annee);
			Map<String, String> map = new HashMap<String, String>();
			@SuppressWarnings("unchecked")
			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String codeComposante = (String)resultElement[0];
				String libComposante = (String)resultElement[1];
				if (logger.isDebugEnabled())
				{
					logger.debug("################### codeComposante --> " + codeComposante);
					logger.debug("################### libComposante --> " + libComposante);
				}
				map.put(codeComposante, libComposante);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, String> getOdfComposanteByRneAndAnneeAndActifAndArriveeAndCodTypDip(String rne, Integer annee, String codTypDip) {
		if (logger.isDebugEnabled())
			logger.debug("public Map<String, String> getOdfComposanteByRneAndAnneeAndActifAndArriveeAndCodTypDip(String rne, Integer annee, String codTypDip)-->"+rne+"-----"+annee+"-----"+codTypDip);
		try{
			Query q = entityManager.createNamedQuery("getOdfComposanteByRneAndAnneeAndActifAndArriveeAndCodTypDip");
			q.setParameter("rne", rne);
			q.setParameter("annee", annee);
			q.setParameter("codTypDip", codTypDip);
			Map<String, String> map = new HashMap<String, String>();
			@SuppressWarnings("unchecked")
			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String codeComposante = (String)resultElement[0];
				String libComposante = (String)resultElement[1];
				if (logger.isDebugEnabled())
				{
					logger.debug("################### codeComposante --> " + codeComposante);
					logger.debug("################### libComposante --> " + libComposante);
				}
				map.put(codeComposante, libComposante);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void addWsPub(WsPub ws) {
		if (logger.isDebugEnabled()){
			logger.debug("public void addWsPub(WsPub ws)");
			logger.debug("ws --> "+ws);
		}
		entityManager.merge(ws);	
	}

	@Override
	public void deleteWsPub(WsPub wsPub) {
		if (logger.isDebugEnabled()){
			logger.debug("deleteWsPub(WsPub wsPub)");
			logger.debug("-->"+wsPub);
		}
		WsPubPK cleWsPub = new WsPubPK(wsPub.getRne(),wsPub.getAnnee());
		WsPub part = entityManager.find(WsPub.class, cleWsPub);
		entityManager.remove(part);	
	}

	@Override
	public AccueilDecision getDecisionByNumeroEtudiantAndAnnee(String numeroEtudiant, Integer currentAnnee) 
	{
		if (logger.isDebugEnabled())
			logger.debug("getDecisionByNumeroEtudiantAndAnnee(String numeroEtudiant, Integer currentAnnee)");
		try{
			EtudiantRefPK cleAccueilAnnee = new EtudiantRefPK(numeroEtudiant, currentAnnee);
			AccueilDecision ret = entityManager.find(AccueilDecision.class, cleAccueilAnnee);
			return ret;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void deleteSituationUniversitaireByNumeroEtudiantAndAnneeIsNull() {
		Query query = entityManager.createNativeQuery("delete FROM SITUATION_UNIVERSITAIRE su WHERE su.numeroEtudiant IS NULL AND su.annee IS NULL");  
		int delete = query.executeUpdate();
		if (logger.isDebugEnabled()){
			logger.debug("delete ---> "+delete);
		}
	}

	@Override
	public EtudiantRef getPresenceEtudiantRefByIne(String ine, Integer currentAnnee) {
		if (logger.isDebugEnabled()){
			logger.debug("public EtudiantRef getPresenceEtudiantRefByIne(String ine, Integer currentAnnee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getPresenceEtudiantRefByIneAndAnnee");
			q.setParameter("numeroIne", ine);
			q.setParameter("annee", currentAnnee);
			EtudiantRef etudiant = (EtudiantRef) q.getSingleResult();
			return etudiant;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public IndOpi getPresenceEtudiantOPiByIneAndAnnee(String ine, Integer currentAnnee) {
		if (logger.isDebugEnabled()){
			logger.debug("public EtudiantRef getPresenceEtudiantOPiByIneAndAnnee(String ine, Integer currentAnnee)");
		}
		try{
			Query q = entityManager.createNamedQuery("getPresenceEtudiantOPiByIneAndAnnee");

			String codNneIndOpi = ine.substring(0, ine.length()-1);
			String codCleNneIndOpi = ine.substring(ine.length()-1, ine.length());
			q.setParameter("codNneIndOpi", codNneIndOpi);
			q.setParameter("codCleNneIndOpi", codCleNneIndOpi);
			q.setParameter("annee", currentAnnee);
			IndOpi etudiant = (IndOpi) q.getSingleResult();
			return etudiant;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<PersonnelComposante> getListePersonnelsComposantesBySourceAndAnnee(String source, Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("public List<PersonnelComposante> getListePersonnelsComposantesBySourceAndAnnee(String source, Integer annee)");
			logger.debug("source --> "+source);			
			logger.debug("annee --> "+annee);			
		}
		try{
			Query q = entityManager.createNamedQuery("getListePersonnelsComposantesBySourceAndAnnee");
			q.setParameter("source", source);
			q.setParameter("annee", annee);
			@SuppressWarnings("unchecked")
			List<PersonnelComposante> ret = (List<PersonnelComposante>) q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public OffreDeFormationsDTO getOdfByPK(String rne, Integer annee, String codDip, Integer codVrsVdi, String codEtp, String codVrsVet, String codeCge) {
		if (logger.isDebugEnabled()){
			logger.debug("getOdfByPK(String rne, String annee, String codDip, Integer codVrsVdi, String codEtp, String codVrsVet, String codeCge)");
		}
		try{
			OffreDeFormationPK cleOdf = new OffreDeFormationPK(rne, annee, codDip, codVrsVdi, codEtp, codVrsVet, codeCge);
			OffreDeFormationsDTO odf = entityManager.find(OffreDeFormationsDTO.class, cleOdf);
			return odf;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public boolean getDroitsTransferts(String login, String source, Integer annee) {
		if (logger.isDebugEnabled())
			logger.debug("public boolean getDroitsTransferts(String login, String source, Integer annee) -->"+login +"-----"+source+"-----"+annee);
		try{
			Query q = entityManager.createNamedQuery("getListeComposantesByUidAndSourceAndAnnee");
			q.setParameter("uid", login);
			q.setParameter("source", source);
			q.setParameter("annee", annee);
			@SuppressWarnings("unchecked")
			List<PersonnelComposante> ret = (List<PersonnelComposante>) q.getResultList();
			if(ret.isEmpty() || ret.size()==0)
				return false;
			else
				return true;
		}
		catch(NoResultException e){
			return false;
		}
	}

	@Override
	public List<IndOpi> getAllIndOpiBySynchroAndSource(Integer currentAnnee, Integer synchro, String source) {
		if (logger.isDebugEnabled())
			logger.debug("public List<IndOpi> getAllIndOpiBySynchroAndSource(Integer currentAnnee, Integer synchro, String source) -->"+currentAnnee +"-----"+synchro+"-----"+source);		
		Query q = entityManager.createNamedQuery("allIndOpiBySynchroAndSource");
		q.setParameter("annee", currentAnnee);
		q.setParameter("synchro", synchro);
		q.setParameter("source", source);
		@SuppressWarnings("unchecked")
		List<IndOpi> ret = (List<IndOpi>)q.getResultList();
		return ret;
	}

	@Override
	public List<IndOpi> getAllIndOpiBySynchroAndExcluAndSource(Integer currentAnnee, String source) {
		if (logger.isDebugEnabled())
			logger.debug("public List<IndOpi> getAllIndOpiBySynchroAndExcluAndSource(Integer currentAnnee, String source) -->"+currentAnnee+"-----"+source);		
		Query q = entityManager.createNamedQuery("allIndOpiBySynchroAndExcluAndSource");
		q.setParameter("annee", currentAnnee);
		q.setParameter("source", source);
		@SuppressWarnings("unchecked")
		List<IndOpi> ret = (List<IndOpi>)q.getResultList();
		return ret;
	}
	
	@Override
	public Map<String, Long> getStatistiquesTransfert(String source, Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("public Map<String, String> getStatistiquesTransfert(String source) -->"+source);
		}
		try{
			Query q = entityManager.createNamedQuery("getStatistiquesTransfertDepart");
			q.setParameter("source", source);
			q.setParameter("annee", annee);
			//List<String> ret = (List<String>) q.getResultList();
			Map<String, Long> map = new HashMap<String, Long>();

			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String rne = (String)resultElement[0];
				Long total = (Long)resultElement[1];
				if (logger.isDebugEnabled()){
					logger.debug("################### rne --> " + rne);
					logger.debug("################### total --> " + total);
				}		        
				map.put(rne, total);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, Long> getStatistiquesTransfertAccueil(String source, Integer annee) {
		if (logger.isDebugEnabled()){
			logger.debug("public Map<String, Long> getStatistiquesTransfertAccueil(String source, Integer annee) -->"+source);
		}
		try{
			Query q = entityManager.createNamedQuery("getStatistiquesTransfertAccueil");
			q.setParameter("source", source);
			q.setParameter("annee", annee);
			//List<String> ret = (List<String>) q.getResultList();
			Map<String, Long> map = new HashMap<String, Long>();

			List<Object[]> result1 = q.getResultList();
			for (Object[] resultElement : result1) 
			{
				String rne = (String)resultElement[0];
				Long total = (Long)resultElement[1];
				if (logger.isDebugEnabled()){
					logger.debug("################### rne --> " + rne);
					logger.debug("################### total --> " + total);
				}		        
				map.put(rne, total);
			}
			return map;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Long getStatistiquesNombreTotalTransfertDepart(Integer currentAnnee) {
		if (logger.isDebugEnabled())
			logger.debug("public Long getStatistiquesNombreTotalTransfertDepart(Integer currentAnnee) -->"+ currentAnnee);

		try{
			Query q = entityManager.createNamedQuery("getStatistiquesNombreTotalTransfertDepart");
			q.setParameter("annee", currentAnnee);
			Long nb = (Long) q.getSingleResult();
			return nb;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Long getStatistiquesNombreTotalTransfertAccueil(Integer currentAnnee) {
		if (logger.isDebugEnabled())
			logger.debug("public Long getStatistiquesNombreTotalTransfertAccueil(Integer currentAnnee) -->"+ currentAnnee);

		try{
			Query q = entityManager.createNamedQuery("getStatistiquesNombreTotalTransfertAccueil");
			q.setParameter("annee", currentAnnee);
			Long nb = (Long) q.getSingleResult();
			return nb;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Long getStatistiquesNombreTotalTransfertOPI(Integer currentAnnee) {
		if (logger.isDebugEnabled())
			logger.debug("public Long getStatistiquesNombreTotalTransfertOPI(Integer currentAnnee) -->"+ currentAnnee);

		try{
			Query q = entityManager.createNamedQuery("getStatistiquesNombreTotalTransfertOPI");
			q.setParameter("annee", currentAnnee);
			Long nb = (Long) q.getSingleResult();
			return nb;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Test getTest(Integer id) {
		if (logger.isDebugEnabled()){
			logger.debug("public Test getTest(Integer id)");
		}
		try{
			Query q = entityManager.createNamedQuery("getTest");
			q.setParameter("id", id);
			Test test = (Test) q.getSingleResult();
			return test;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<Test> getListeTests() {
		if (logger.isDebugEnabled())
			logger.debug("public List<Test> getListeTests()");
		try{
			Query q = entityManager.createNamedQuery("getListeTests");
			@SuppressWarnings("unchecked")
			List<Test> ret = (List<Test>) q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void addTest(Test test) {
		if (logger.isDebugEnabled())
			logger.debug("test --> "+test);
		entityManager.merge(test);	
	}

	@Override
	public void addValidationAutoByComposante(List<Composante> listeComposantes) {
		if (logger.isDebugEnabled())
			logger.debug("public void addValidationAutoByComposante(List<Composante> listeComposantes) --> "+listeComposantes.size());
		if(listeComposantes!=null && listeComposantes.size()!=0)
			for(Composante c : listeComposantes)
				entityManager.merge(c);
	}

	@Override
	public List<Composante> getListeComposantesFromBddByAnneeAndSource(Integer currentAnnee, String source) {
		if (logger.isDebugEnabled())
			logger.debug("public void getListeComposantesFromBddByAnneeAndSource(Integer currentAnnee, String source)-->"+currentAnnee+"-----"+source);
		try{
			Query q = entityManager.createNamedQuery("getListeComposantesFromBddByAnneeAndSource");
			q.setParameter("annee", currentAnnee);
			q.setParameter("source", source);
			@SuppressWarnings("unchecked")
			List<Composante> ret = (List<Composante>) q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public Composante getComposantesFromBddByAnneeAndSourceAndCodeComposante(Integer annee, String source, String codeComposante) {
		if (logger.isDebugEnabled()){
			logger.debug("public Composante getComposantesFromBddByAnneeAndSourceAndCodeComposante(Integer annee, String source, String codeComposante)-->"+annee+"-----"+source+"-----"+codeComposante);
		}
		try{
			ComposantePK cleComposantePK = new ComposantePK(annee, codeComposante, source);
			Composante composante = entityManager.find(Composante.class, cleComposantePK);
			return composante;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<CGE> getListeCGEFromBddByAnneeAndSource(Integer currentAnnee,String source) {
		if (logger.isDebugEnabled())
			logger.debug("public List<CGE> getListeCGEFromBddByAnneeAndSource(Integer currentAnnee,String from)-->"+currentAnnee+"-----"+source);
		try{
			Query q = entityManager.createNamedQuery("getListeCGEFromBddByAnneeAndSource");
			q.setParameter("annee", currentAnnee);
			q.setParameter("source", source);
			@SuppressWarnings("unchecked")
			List<CGE> ret = (List<CGE>) q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public void addValidationAutoByCGE(List<CGE> listeCGEMerge) {
		if (logger.isDebugEnabled())
			logger.debug("public void addValidationAutoByCGE(List<CGE> listeCGEMerge) --> "+listeCGEMerge.size());
		if(listeCGEMerge!=null && listeCGEMerge.size()!=0)
			for(CGE c : listeCGEMerge)
				entityManager.merge(c);
	}

	@Override
	public CGE getCGEFromBddByAnneeAndSourceAndCodeCGE(Integer annee, String source, String codeCGE) {
		if (logger.isDebugEnabled()){
			logger.debug("public CGE getCGEFromBddByAnneeAndSourceAndCodeCGE(Integer currentAnnee, String source, String codeCGE)-->"+annee+"-----"+source+"-----"+codeCGE);
		}
		try{
			CGEPK cleCGEPK = new CGEPK(annee, codeCGE, source);
			CGE cge = entityManager.find(CGE.class, cleCGEPK);
			return cge;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public PersonnelComposante getDroitPersonnelComposanteByUidAndSourceAndAnneeAndCodeComposante(String login, String source, Integer currentAnnee, String composante) 
	{
		if (logger.isDebugEnabled())
			logger.debug("public PersonnelComposante getDroitPersonnelComposanteByUidAndSourceAndAnneeAndCodeComposante(String login, String source, Integer currentAnnee, String composante)-->"+login+"-----"+source+"-----"+currentAnnee+"-----"+composante);
		try
		{
			PersonnelComposantePK clePersonnelComposante = new PersonnelComposantePK(login, composante, source, currentAnnee);
			PersonnelComposante pc = entityManager.find(PersonnelComposante.class, clePersonnelComposante);
			return pc;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public DatasExterne getDataExterneByIdentifiantAndCode(String identifiant, String code) {
		if (logger.isDebugEnabled()){
			logger.debug("public DatasExterne getDataExterneByIdentifiantAndCode(String identifiant, String code)-->"+identifiant+"-----"+code);
		}
		try{
			DatasExternePK cleDatasExterne = new DatasExternePK(identifiant,code);
			DatasExterne data = entityManager.find(DatasExterne.class, cleDatasExterne);
			return data;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public IndOpi getOpiByNumeroOpi(IndOpi opi) {
		if (logger.isDebugEnabled()){
			logger.debug("public IndOpi getOpiByNumeroOpi(IndOpi opi)-->"+opi);
		}
		try{
			IndOpi currentOpi = entityManager.find(IndOpi.class, opi.getNumeroOpi());
			return currentOpi;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public AccueilAnnee getAccueilAnneeByLibelle(String libelle) {
		if (logger.isDebugEnabled())
			logger.debug("public AccueilAnnee getAccueilAnneeByLibelle(String libelle)-->"+libelle);
		try{
			Query q = entityManager.createNamedQuery("getAccueilAnneeByLibelle");
			q.setParameter("libelle", libelle);
			@SuppressWarnings("unchecked")
			AccueilAnnee ret = (AccueilAnnee) q.getSingleResult();
			return ret;
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public AccueilAnnee getAccueilAnneeByIdAccueilAnnee(Integer idAccueilAnnee) {
		if (logger.isDebugEnabled()){
			logger.debug("public AccueilAnnee getAccueilAnneeByIdAccueilAnnee(Integer idAccueilAnnee)-->"+idAccueilAnnee);
		}
		try{
			AccueilAnnee accueilAnnee = entityManager.find(AccueilAnnee.class, idAccueilAnnee);
			return accueilAnnee;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public AccueilResultat getAccueilResultatByIdAccueilResultat(Integer idAccueilResultat) {
		if (logger.isDebugEnabled()){
			logger.debug("public AccueilAnnee getAccueilAnneeByIdAccueilAnnee(Integer idAccueilAnnee)-->"+idAccueilResultat);
		}
		try{
			AccueilResultat accueilResultat = entityManager.find(AccueilResultat.class, idAccueilResultat);
			return accueilResultat;			
		}
		catch(NoResultException e){
			return null;
		}
	}

	@Override
	public List<OffreDeFormationsDTO> getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposanteAndAtifOuPas(
			String rne, Integer currentAnnee, String codTypDip,
			Integer codeNiveau, String codeComposante, String source) {
		if (logger.isDebugEnabled())
			logger.debug("public List<OffreDeFormationsDTO> getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposanteAndAtifOuPas(String rne, Integer annee, String codTypDip, Integer codeNiveau, String codeComposante, String source)");
		try{
			Query q;
			if(source.equals("D"))
				q = entityManager.createNamedQuery("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposanteAndDepartAndAtifOuPas");
			else
				q = entityManager.createNamedQuery("getVersionEtapeByRneAndAnneeAndCodTypDipAndcodeNiveauAndCodeComposanteAndArriveeAndAtifOuPas");
			q.setParameter("annee", currentAnnee);
			q.setParameter("rne", rne);
			q.setParameter("codTypDip", codTypDip);
			q.setParameter("codeNiveau", codeNiveau);
			q.setParameter("codeComposante", codeComposante);
			@SuppressWarnings("unchecked")
			List<OffreDeFormationsDTO> ret = q.getResultList();
			return ret;
		}
		catch(NoResultException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Parametres updateConfiguration(Parametres param) {
		if (logger.isDebugEnabled()){
			logger.debug("addParametre(Parametres param)");
			logger.debug("param --> "+param);
		}
		Parametres p = entityManager.merge(param);	
		return p;
	}

	@Override
	public void deleteSelectedOpi(IndOpi selectedOpiForDelete) 
	{
		if (logger.isDebugEnabled())
			logger.debug("public void deleteSelectedOpi(IndOpi selectedOpiForDelete)-->"+ selectedOpiForDelete);
		IndOpi opi = entityManager.find(IndOpi.class, selectedOpiForDelete.getNumeroOpi());
		if (logger.isDebugEnabled())
			logger.debug("opi-->"+ opi);		
		entityManager.remove(opi);
	}	

}