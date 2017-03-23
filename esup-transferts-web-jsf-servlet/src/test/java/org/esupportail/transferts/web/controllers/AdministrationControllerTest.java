package org.esupportail.transferts.web.controllers;

import org.esupportail.commons.utils.Assert;
import org.esupportail.transferts.domain.DomainService;
import org.esupportail.transferts.domain.DomainServiceScolarite;
import org.esupportail.transferts.domain.beans.*;
import org.esupportail.transferts.utils.CheckNNE36;
import org.esupportail.transferts.utils.Fonctions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Farid AIT KARRA (Universite d'Artois) - 2015
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:META-INF/applicationContext_TEST_UNITAIRE.xml")
public class AdministrationControllerTest {

	@Autowired
	DomainService domainService;

	@Autowired
	DomainServiceScolarite domainServiceScolarite;

	@Value("${currentAnnee}")
	private Integer currentAnnee;

	@Value("${rne.depart}")
	private String rneDepart;

	@Value("${rne.accueil}")
	private String rneAccueil;

	@Value("${method.execute.total.depart.et.accueil}")
	private boolean totalDemandeTransfertsDepartEtAccueil;

	@Value("${method.execute.delete.demandes.transfert.depart.by.currentAnnee}")
	private boolean deleteDemandesTransfertDepartByCurrentAnneeTest;

	@Value("${method.execute.delete.demandes.transfert.accueil.by.currentAnnee}")
	private boolean deleteDemandesTransfertAccueiltByCurrentAnneeTest;

	@Value("${method.execute.delete.opi.by.currentAnnee}")
	private boolean deleteOpiByCurrentAnnee;

	/*
	 * ajoutListeDemandesTransfertDepart
	 */
	@Value("${method.execute.ajout.liste.demandes.transfert.depart}")
	private boolean ajoutListeDemandesTransfertDepart;

	@Value("${param.ajout.liste.demandes.transfert.depart.max}")
	private Integer nombreMaxDeDemandesACreer;

	@Value("${param.ajout.liste.demandes.transfert.depart.envoi.opi}")
	private boolean envoiOpi;

	@Value("${param.ajout.liste.demandes.transfert.depart.from.TestUnitaireEtudiantRef}")
	private boolean fromTestUnitaireEtudiantRef;	
	/**/

	EtudiantRef currentDemandeTransferts;

	List<EtudiantRef> listeEtudiants;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void afterPropertiesSet()
	{
		Assert.notNull(currentAnnee, "property currentAnnee of class"
				+ this.getClass().getName() + " can not be null");
		Assert.hasText(rneDepart, "property rneDepart of class"
				+ this.getClass().getName() + " can not be null");
		Assert.hasText(rneAccueil, "property rneAccueil of class"
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(totalDemandeTransfertsDepartEtAccueil, "property totalDemandeTransfertsTest of class "
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(deleteDemandesTransfertDepartByCurrentAnneeTest, "property deleteDemandesTransfertDepartByCurrentAnneeTest of class "
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(deleteDemandesTransfertAccueiltByCurrentAnneeTest, "property deleteDemandesTransfertAccueiltByCurrentAnneeTest of class "
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(deleteOpiByCurrentAnnee, "property deleteOpiByCurrentAnnee of class "
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(ajoutListeDemandesTransfertDepart, "property ajoutListeDemandesTransfertDepart of class "
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(nombreMaxDeDemandesACreer, "property nombreMaxDeDemandesACreer of class "
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(envoiOpi, "property envoiOpi of class "
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(fromTestUnitaireEtudiantRef, "property fromTestUnitaireEtudiantRef of class "
				+ this.getClass().getName() + " can not be null");
	}

	@Test
	public void LancementDesTestUnitaire()
	{
		System.out.println("############################################################################################################################################");
		System.out.println("===>public void LancementDesTestUnitaire()<===");
		System.out.println("currentAnnee===>"+currentAnnee+"<===");
		System.out.println("rneDepart===>"+rneDepart+"<===");
		System.out.println("rneAccueil===>"+rneAccueil+"<===");
		System.out.println("totalDemandeTransfertsDepartEtAccueil===>"+totalDemandeTransfertsDepartEtAccueil+"<===");
		System.out.println("deleteDemandesTransfertDepartByCurrentAnneeTest===>"+deleteDemandesTransfertDepartByCurrentAnneeTest+"<===");
		System.out.println("deleteDemandesTransfertAccueiltByCurrentAnneeTest===>"+deleteDemandesTransfertAccueiltByCurrentAnneeTest+"<===");
		System.out.println("ajoutListeDemandesTransfertDepart===>"+ajoutListeDemandesTransfertDepart+"<===");
		System.out.println("nombreMaxDeDemandesACreer===>"+nombreMaxDeDemandesACreer+"<===");
		System.out.println("envoiOpi===>"+envoiOpi+"<===");
		System.out.println("fromTestUnitaireEtudiantRef===>"+fromTestUnitaireEtudiantRef+"<===");
		System.out.println("############################################################################################################################################");

		//this.getCleIne(); 

		if(totalDemandeTransfertsDepartEtAccueil)
			this.getTotalDemandeTransfertsTest(getCurrentAnnee());

		if(deleteDemandesTransfertDepartByCurrentAnneeTest)
			this.deleteAllDemandesTransfertDepartByCurrentAnneeTest();

		if(deleteDemandesTransfertAccueiltByCurrentAnneeTest)
			this.deleteAllDemandesTransfertAccueilByCurrentAnneeTest();

		if(deleteOpiByCurrentAnnee)
			this.deleteOpiByCurrentAnnee();

		if(ajoutListeDemandesTransfertDepart)
			this.addDemandeTransfertDepartTest(getNombreMaxDeDemandesACreer(), isEnvoiOpi(), fromTestUnitaireEtudiantRef);

		if(totalDemandeTransfertsDepartEtAccueil)
			this.getTotalDemandeTransfertsTest(getCurrentAnnee());

		List<EtudiantRef> lEtu = getDomainService().getAllDemandesTransfertsByAnnee(getCurrentAnnee(), "D");
	}

	public void getCleIne()
	{
		System.out.println("===>public void getCleIne()<===");
		System.out.println("Clé INE===>"+CheckNNE36.calculCLeIne("0DDG5R0001")+"<===");
	}

	public String rand(int nb) {
		String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
		StringBuilder res = new StringBuilder();
		for(int i = 0; i < nb; i++) {
			res.append(chars.charAt(new Random().nextInt(chars.length())));
		}
		return res.toString();
	}

	public void getTotalDemandeTransfertsTest(Integer annee)
	{
		System.out.println("===>public void getTotalDemandeTransfertsTest()<===");
		System.out.println("getDomainService().getAllDemandesTransfertsByAnnee(annee, D)===>"+annee+"<===");
		List<EtudiantRef> lEtu = getDomainService().getAllDemandesTransfertsByAnnee(annee, "D");
		if(lEtu!=null)
			System.out.println("Total des demandes de transferts départ===>"+lEtu.size()+"<===");
		else
			System.out.println("Total des demandes de transferts départ===>0<===");
		List<EtudiantRef> lEtu2 = getDomainService().getAllDemandesTransfertsByAnnee(annee, "A");
		if(lEtu2!=null)
			System.out.println("Total des demandes de transferts accueil===>"+lEtu2.size()+"<===");
		else
			System.out.println("Total des demandes de transferts accueil===>0<===");
	}

	public void addDemandeTransfertDepartTest(Integer nbDemandeACreer, boolean envoi, boolean local)
	{
		System.out.println("===>public void addDemandeTransfertDepartTest()<===");

		String myAnneeRequeteWs = getCurrentAnnee().toString();
//		Integer myAnneeInt = currentAnnee;
		Integer myAnneeInt = 2017;

//		String codeDiplome = "CL3LEAE";
//		String versionDiplome = "140";
//		String codeEtape = "3IAEME";
//		String versionEtape = "160";

		/*Lille 1*/
        String codeDiplome = "DL2DRDR";
        String versionDiplome = "140";
        String codeEtape = "1IDRIT";
        String versionEtape = "140";

		List<EtudiantRef> listeEtu;

		if(local)
		{
			listeEtu = new ArrayList<EtudiantRef>();
			List<TestUnitaireEtudiantRef> listeEtuTest = getDomainService().getAllTestUnitaireEtudiantRefBySource("D");

			if(listeEtuTest!=null && !listeEtuTest.isEmpty())
			{
				// shuffle the list
				Collections.shuffle(listeEtuTest);
				System.out.println("aaaaa");
				for(TestUnitaireEtudiantRef etu : listeEtuTest)
				{
					EtudiantRef tmp = new EtudiantRef();
					tmp.setNumeroEtudiant(etu.getNumeroEtudiant());
					listeEtu.add(tmp);
				}
			}
			else
			{
				System.out.println("===>La liste des étudiants TestUnitaireEtudiantRef est vide<===");
			}
		}
		else
		{
			listeEtu = getDomainServiceScolarite().recupererListeEtudiants(String.valueOf(getCurrentAnnee()-1), codeDiplome, versionDiplome, codeEtape, versionEtape);
		}

		List<EtudiantRef> listeEtudiantATransferer = new ArrayList<EtudiantRef>();
		int compteur=0;

//		System.out.println("listeEtu===>"+listeEtu.size()+"<===");

		if(listeEtu!=null && !listeEtu.isEmpty())
		{
			for(EtudiantRef etu : listeEtu)
			{
				if(compteur<nbDemandeACreer)
				{
					System.out.println("etu.getNumeroEtudiant()===>" +etu.getNumeroEtudiant()+"<===");
					EtudiantRef etudiant = getDomainServiceScolarite().getCurrentEtudiant(etu.getNumeroEtudiant());
					etudiant.setNomPatronymique("TEST_"+etudiant.getNumeroIne());
					etudiant.setPrenom1("UNITAIRE_"+etudiant.getNumeroIne());
					etudiant.setAnnee(myAnneeInt);
					etudiant.setSource("D");

					Map<String, String> map = getDomainServiceScolarite().getEtapePremiereAndCodeCgeAndLibCge(etu.getNumeroEtudiant());
					for (String mapKey : map.keySet()) {
						if(mapKey.equals("libWebVet"))
							etudiant.setLibEtapePremiereLocal(map.get(mapKey));
						if(mapKey.equals("codeCGE"))
							etudiant.setCodCge(map.get(mapKey));
						if(mapKey.equals("libCGE"))
							etudiant.setLibCge(map.get(mapKey));
						if(mapKey.equals("codeComposante"))
							etudiant.setComposante(map.get(mapKey));
						if(mapKey.equals("libComposante"))
							etudiant.setLibComposante(map.get(mapKey));
					}

					etudiant.getAdresse().setAnnee(myAnneeInt);
					etudiant.getAdresse().setEmail(etudiant.getPrenom1()+"."+etudiant.getNomPatronymique()+"@testunitaire.com");

					etudiant.getTransferts().setDateDemandeTransfert(new Date());
					etudiant.getTransferts().setAnnee(myAnneeInt);
					etudiant.getTransferts().setRne(this.getRneDepart());
					etudiant.getTransferts().setTemoinTransfertValide(0);
					etudiant.getTransferts().setTemoinOPIWs(null);
					etudiant.getTransferts().setTypeTransfert("T");
					etudiant.getTransferts().setDept(this.getRneAccueil().substring(0, 3));
					etudiant.getTransferts().setRne(this.getRneAccueil());

					List<OffreDeFormationsDTO> lodfdto = getDomainService().getAllOffreDeFormationByAnneeAndRneAndAtifOuPas(myAnneeInt, this.getRneAccueil());
					OffreDeFormationsDTO o = null;

					if(lodfdto==null)
					{
						System.out.println("etudiant.toString()===>" +etudiant.toString()+"<===");
						System.out.println("Aucune formation correspondante===>"+this.getRneAccueil()+" - "+myAnneeInt+"<===");
					}
					else
					{
						Collections.shuffle(lodfdto);
						o = getDomainService().getOdfByPK(this.getRneAccueil(), myAnneeInt, lodfdto.get(0).getCodeDiplome(), lodfdto.get(0).getCodeVersionDiplome(), lodfdto.get(0).getCodeEtape(), lodfdto.get(0).getCodeVersionEtape(), lodfdto.get(0).getCodeCentreGestion());
					}
					etudiant.getTransferts().setOdf(o);
					etudiant.getTransferts().getOdf().setRne(this.getRneAccueil());

					System.out.println("etudiant.toString()===>" +etudiant.toString()+"<===");

					etudiant.setAccueil(null);
					etudiant.getTransferts().setTemoinRetourTransfertAccueil(0);
					etudiant = getDomainService().addDemandeTransferts(etudiant);
					listeEtudiantATransferer.add(etudiant);
				}
				compteur++;
			}

			if(envoi)
				this.addTransfertOpiToListeTransfertsAccueilTest(listeEtudiantATransferer);
		}
		else
		{
			System.out.println("===>La liste des étudiants EtudiantRef est vide<===");
		}
	}

	public void addTransfertOpiToListeTransfertsAccueilTest(List<EtudiantRef> listeEtudiants)
	{
		System.out.println("===>public void addTransfertOpiToListeTransfertsAccueilTest(String this.getRneAccueil(), List<EtudiantRef> listeEtudiants)<===");
		WsPub p = getDomainService().getWsPubByRneAndAnnee(this.getRneAccueil(), getCurrentAnnee());

		// Appel du WebService de l'universite d'accueil
		if (p != null)
		{
			InfosAccueil ia = new InfosAccueil();

			for(EtudiantRef etu : listeEtudiants)
			{
				TrResultatVdiVetDTO sessionsResultats = getDomainServiceScolarite().getSessionsResultats(etu.getNumeroEtudiant(), "A");
				TrBac bac = getDomainServiceScolarite().getBaccalaureat(etu.getNumeroEtudiant());
				TrInfosAdmEtu trInfosAdmEtu = getDomainServiceScolarite().getInfosAdmEtu(etu.getNumeroEtudiant());
				TrEtablissementDTO trEtablissementDTO = getDomainServiceScolarite().getEtablissementByRne(this.getRneDepart());

				List<SituationUniversitaire> listeSituationUniversitaire = new ArrayList<SituationUniversitaire>();
				if(!sessionsResultats.getEtapes().isEmpty())
				{
					int i=0;
					for(ResultatEtape re :  sessionsResultats.getEtapes())
					{
						System.out.println("re.getLibEtape() : " + re.getLibEtape());
						boolean test=true;

						for(ResultatSession rs : re.getSession())
						{
							System.out.println("re.getSession().size() : " + re.getSession().size());
							System.out.println("re.getSession() : " + re.getSession());

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



				Fichier signatureParDefaut = getDomainService().getFichierDefautByAnneeAndFrom(getCurrentAnnee(), "D");
				if(signatureParDefaut!=null)
				{
					etu.getTransferts().setTemoinOPIWs(1);
					etu.getTransferts().setTemoinTransfertValide(2);
					Avis currentAvis = new Avis();
					currentAvis.setNumeroEtudiant(etu.getNumeroEtudiant());
					currentAvis.setAnnee(getCurrentAnnee());
					currentAvis.setDateSaisie(new Date());
					currentAvis.setIdDecisionDossier(2);
					currentAvis.setIdEtatDossier(1);
					currentAvis.setIdLocalisationDossier(1);

					getDomainService().addAvis(currentAvis);

					etu.getTransferts().setFichier(getDomainService().getFichierDefautByAnneeAndFrom(getCurrentAnnee(), "D"));

					getDomainService().addDemandeTransferts(etu);

					ia.setAnnee(getCurrentAnnee());
					ia.setFrom_source("P");
					ia.setSituationUniversitaire(listeSituationUniversitaire);
					if(bac!=null)
					{
						ia.setAnneeBac(bac.getAnneeObtentionBac());
						ia.setCodeBac(bac.getCodeBac());
					}
					if(trInfosAdmEtu!=null)
						ia.setCodePaysNat(trInfosAdmEtu.getCodPayNat());
					ia.setCodeRneUnivDepart(trEtablissementDTO.getCodeEtb());
					ia.setCodeDepUnivDepart(trEtablissementDTO.getCodeDep());
					ia.setValidationOuCandidature(0);
					etu.setAccueil(ia);
					etu.setNumeroEtudiant(etu.getNumeroIne());
					etu.setSource("A");
					etu.getAdresse().setNumeroEtudiant(etu.getNumeroIne());
					etu.getTransferts().setNumeroEtudiant(etu.getNumeroIne());
					ia.setNumeroEtudiant(etu.getNumeroIne());

					if(etu.getAccueil()!=null)
					{
						System.out.println("etu.getAccueil()===>" +etu.getAccueil().getNumeroEtudiant()+"---"+etu.getAccueil().getAnnee()+"<===");
						System.out.println("etu.getAccueil().getSituationUniversitaire().size()===>" +etu.getAccueil().getSituationUniversitaire().size()+"<===");
					}
					else
						System.out.println("etu.getAccueil()===>null<===");

					List<SituationUniversitaire> lSu = ia.getSituationUniversitaire();
					if(lSu!=null && !lSu.isEmpty())
					{
						for(int j=0;j<lSu.size();j++)
						{
							String timestamp = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
							lSu.get(j).setId(etu.getNumeroEtudiant()+"_"+timestamp+"_P"+j);
						}
					}

					//==============Préparation des données à envoyer==============
					etu.getTransferts().setTemoinOPIWs(0);
					etu.getTransferts().setTemoinTransfertValide(0);
					etu.getTransferts().setFichier(null);
					//=============================================================

//					monService.addTransfertOpiToListeTransfertsAccueil(etu);
					Object tabReturn[] = Fonctions.appelWSAuth(p.getUrl(),
							p.getIdentifiant(),
							p.getPassword(),
							"org.esupportail.transferts.domain.DomainServiceOpi",
							"addTransfertOpiToListeTransfertsAccueil",
							"object",
							5000,
							etu);

					Integer etatConnexion = (Integer) tabReturn[1];

					if(etatConnexion==1)
					{
						System.out.println("OPI Envoyé vers : ===>"+p.getLibEtab()+"<===");
					}
					else{
						System.out.println("Erreur envoi OPI vers : ===>"+p.getLibEtab()+"<===");
					}


					System.out.println("Envoyé vers : ===>"+p.getLibEtab()+"<===");
				}
				else
				{
					System.out.println("pas Envoyé vers : ===>"+p.getLibEtab()+" car pas de signature par défaut<===");
				}
			}
		}
	}

	//@Test
	public void deleteAllDemandesTransfertDepartByCurrentAnneeTest()
	{
		System.out.println("===>public void deleteAllDemandesTransfertDepartByCurrentAnneeTest()"+getCurrentAnnee()+" ----- D<===");
		List<EtudiantRef> lEtuDepart = getDomainService().getAllDemandesTransfertsByAnnee(getCurrentAnnee(), "D");
		if(lEtuDepart!=null && !lEtuDepart.isEmpty())
			for(EtudiantRef etuDepart : lEtuDepart)
				getDomainService().deleteDemandeTransfert(etuDepart, getCurrentAnnee());
	}

	//@Test
	public void deleteAllDemandesTransfertAccueilByCurrentAnneeTest()
	{
		System.out.println("===>public void deleteAllDemandesTransfertAccueilByCurrentAnneeTest()"+getCurrentAnnee()+" ----- A<===");
		List<EtudiantRef> lEtuAccueil = getDomainService().getAllDemandesTransfertsByAnnee(getCurrentAnnee(), "A");
		if(lEtuAccueil!=null && !lEtuAccueil.isEmpty())
			for(EtudiantRef etuAccueil : lEtuAccueil)
				getDomainService().deleteDemandeTransfert(etuAccueil,getCurrentAnnee());
	}

	//@Test
	public void deleteOpiByCurrentAnnee()
	{
		System.out.println("===>public void deleteOpiByCurrentAnnee()"+getCurrentAnnee()+"<===");
		List<IndOpi> lOpis = getDomainService().getAllIndOpiByAnnee(getCurrentAnnee());
		if(lOpis!=null && !lOpis.isEmpty())
			for(IndOpi opi : lOpis)
				getDomainService().deleteOpi(opi);
	}

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

	public EtudiantRef getCurrentDemandeTransferts() {
		return currentDemandeTransferts;
	}

	public void setCurrentDemandeTransferts(EtudiantRef currentDemandeTransferts) {
		this.currentDemandeTransferts = currentDemandeTransferts;
	}

	public List<EtudiantRef> getListeEtudiants() {
		return listeEtudiants;
	}

	public void setListeEtudiants(List<EtudiantRef> listeEtudiants) {
		this.listeEtudiants = listeEtudiants;
	}

	public Integer getCurrentAnnee() {
		return currentAnnee;
	}

	public void setCurrentAnnee(Integer currentAnnee) {
		this.currentAnnee = currentAnnee;
	}

	public String getRneDepart() {
		return rneDepart;
	}

	public void setRneDepart(String rneDepart) {
		this.rneDepart = rneDepart;
	}

	public String getRneAccueil() {
		return rneAccueil;
	}

	public void setRneAccueil(String rneAccueil) {
		this.rneAccueil = rneAccueil;
	}

	public boolean isTotalDemandeTransfertsDepartEtAccueil() {
		return totalDemandeTransfertsDepartEtAccueil;
	}

	public void setTotalDemandeTransfertsDepartEtAccueil(
			boolean totalDemandeTransfertsDepartEtAccueil) {
		this.totalDemandeTransfertsDepartEtAccueil = totalDemandeTransfertsDepartEtAccueil;
	}

	public boolean isFromTestUnitaireEtudiantRef() {
		return fromTestUnitaireEtudiantRef;
	}

	public void setFromTestUnitaireEtudiantRef(boolean fromTestUnitaireEtudiantRef) {
		this.fromTestUnitaireEtudiantRef = fromTestUnitaireEtudiantRef;
	}

	public boolean isDeleteDemandesTransfertDepartByCurrentAnneeTest() {
		return deleteDemandesTransfertDepartByCurrentAnneeTest;
	}

	public void setDeleteDemandesTransfertDepartByCurrentAnneeTest(
			boolean deleteDemandesTransfertDepartByCurrentAnneeTest) {
		this.deleteDemandesTransfertDepartByCurrentAnneeTest = deleteDemandesTransfertDepartByCurrentAnneeTest;
	}

	public boolean isDeleteDemandesTransfertAccueiltByCurrentAnneeTest() {
		return deleteDemandesTransfertAccueiltByCurrentAnneeTest;
	}

	public void setDeleteDemandesTransfertAccueiltByCurrentAnneeTest(
			boolean deleteDemandesTransfertAccueiltByCurrentAnneeTest) {
		this.deleteDemandesTransfertAccueiltByCurrentAnneeTest = deleteDemandesTransfertAccueiltByCurrentAnneeTest;
	}

	public boolean isAjoutListeDemandesTransfertDepart() {
		return ajoutListeDemandesTransfertDepart;
	}

	public void setAjoutListeDemandesTransfertDepart(
			boolean ajoutListeDemandesTransfertDepart) {
		this.ajoutListeDemandesTransfertDepart = ajoutListeDemandesTransfertDepart;
	}

	public Integer getNombreMaxDeDemandesACreer() {
		return nombreMaxDeDemandesACreer;
	}

	public void setNombreMaxDeDemandesACreer(Integer nombreMaxDeDemandesACreer) {
		this.nombreMaxDeDemandesACreer = nombreMaxDeDemandesACreer;
	}

	public boolean isEnvoiOpi() {
		return envoiOpi;
	}

	public void setEnvoiOpi(boolean envoiOpi) {
		this.envoiOpi = envoiOpi;
	}

	public boolean isDeleteOpiByCurrentAnnee() {
		return deleteOpiByCurrentAnnee;
	}

	public void setDeleteOpiByCurrentAnnee(boolean deleteOpiByCurrentAnnee) {
		this.deleteOpiByCurrentAnnee = deleteOpiByCurrentAnnee;
	}
}
