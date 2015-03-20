package org.esupportail.transferts.web.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.esupportail.transferts.dao.DaoService;
import org.esupportail.transferts.dao.JPADaoServiceImpl;
import org.esupportail.transferts.domain.DomainService;
import org.esupportail.transferts.domain.DomainServiceApogeeImpl;
import org.esupportail.transferts.domain.DomainServiceImpl;
import org.esupportail.transferts.domain.DomainServiceOpi;
import org.esupportail.transferts.domain.DomainServiceScolarite;
import org.esupportail.transferts.domain.beans.AccueilAnnee;
import org.esupportail.transferts.domain.beans.AccueilResultat;
import org.esupportail.transferts.domain.beans.Avis;
import org.esupportail.transferts.domain.beans.DatasExterne;
import org.esupportail.transferts.domain.beans.EtudiantRef;
import org.esupportail.transferts.domain.beans.Fichier;
import org.esupportail.transferts.domain.beans.InfosAccueil;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.PersonnelComposante;
import org.esupportail.transferts.domain.beans.ResultatEtape;
import org.esupportail.transferts.domain.beans.ResultatSession;
import org.esupportail.transferts.domain.beans.SituationUniversitaire;
import org.esupportail.transferts.domain.beans.TrBac;
import org.esupportail.transferts.domain.beans.TrEtablissementDTO;
import org.esupportail.transferts.domain.beans.TrInfosAdmEtu;
import org.esupportail.transferts.domain.beans.TrResultatVdiVetDTO;
import org.esupportail.transferts.domain.beans.Transferts;
import org.esupportail.transferts.domain.beans.User;
import org.esupportail.transferts.domain.beans.WsPub;
import org.esupportail.transferts.web.utils.MyAuthenticator;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:META-INF/testApplicationContext.xml")
public class AdministrationControllerTest  {

	@Autowired
	DomainService domainService; 

	@Autowired
	DomainServiceScolarite domainServiceScolarite;

	private Integer currentAnnee;
	private String currentRne;

	EtudiantRef currentDemandeTransferts;

	List<EtudiantRef> listeEtudiants;

	@Before
	public void setUp() throws Exception {
		setCurrentAnnee(2015);
		setCurrentRne("0623957P");
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void LancementDesTestUnitaire()
	{
		System.out.println("===>public void LancementDesTestUnitaire()<===");
		//		this.getTotalDemandeTransfertsTest(2015, "D");

		//		this.getAllDemandesTransfertsByAnneeAndSourceTest(getCurrentAnnee(),"D");

		//		this.getDemandeTransfertByAnneeAndNumeroEtudiantAndSource("20054890", 2015, "D");

		//this.deleteAllDemandesTransfertDepartTest();

		//this.deleteAllDemandesTransfertAccueilTest();

		//this.addDemandeTransfertDepartTest(50, false, "0593561A");
	}	

	public String rand(int nb) {
		String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
		StringBuilder res = new StringBuilder();
		for(int i = 0; i < nb; i++) {
			res.append(chars.charAt(new Random().nextInt(chars.length())));
		}
		return res.toString();
	}

	public void  getDemandeTransfertByAnneeAndNumeroEtudiantAndSource(String numeroEtudiant, Integer annee, String source)
	{
		System.out.println("===>public void  getDemandeTransfertByAnneeAndNumeroEtudiantAndSource(String numeroEtudiant, Integer annee, String source)<===");
		System.out.println("getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource()===>"+numeroEtudiant+"---"+annee+"---"+source+"<===");
		EtudiantRef etu = getDomainService().getDemandeTransfertByAnneeAndNumeroEtudiantAndSource(numeroEtudiant, annee, source);
		System.out.println("etu===>"+etu+"<==="); 
	}

	public void getTotalDemandeTransfertsTest(Integer annee, String source)
	{
		System.out.println("===>public void getTotalDemandeTransfertsTest()<===");
		System.out.println("getDomainService().getAllDemandesTransfertsByAnnee(annee, source)===>"+annee+"---"+source+"<===");
		List<EtudiantRef> lEtu2 = getDomainService().getAllDemandesTransfertsByAnnee(annee, source);
		System.out.println("lEtu2.size()===>"+lEtu2.size()+"<===");
	}	

	public void getAllDemandesTransfertsByAnneeAndSourceTest(Integer annee, String source)
	{
		System.out.println("===>public void getAllDemandesTransfertsByAnnee()<===");	

		System.out.println("getDomainService().getAllDemandesTransfertsByAnnee(this.getEtudiantAccueil().getAnnee(), this.getEtudiantAccueil().getSource());===>"+annee+"-----"+source+"<===");

		listeEtudiants = getDomainService().getAllDemandesTransfertsByAnnee(annee, source);

		for(EtudiantRef etu : listeEtudiants)
		{
			System.out.println("etu.getNomPatronymique()===>"+etu.getNomPatronymique()+"<===");
			System.out.println("etu.getAdresse()===>"+etu.getAdresse().toString()+"<===");
			System.out.println("etu.getAdresse().getEmail()===>"+etu.getAdresse().getEmail()+"<===");
			System.out.println("etu.getAccueil().getSituationUniversitaire()===>"+etu.getAccueil().getSituationUniversitaire()+"<===");
			if(etu.getTransferts().getFichier()!=null)
				System.out.println("etu.getTransferts().getFichier().getMd5()===>"+etu.getTransferts().getFichier().getMd5()+"<===");
			else
				System.out.println("etu.getTransferts().getFichier().getMd5()===>null<===");
			System.out.println("etu===>"+etu.toString()+"<===");			
		}
	}

	public void addDemandeTransfertDepartTest(Integer nbDemandeACreer, boolean envoi, String rneEtabAccueil)
	{
		System.out.println("===>public void addDemandeTransfertDepartTest()<===");
		String myAnneeRequeteWs = "2014";
		Integer myAnneeInt = 2015;
		String codeDiplome = "CL2LEAE";
		String versionDiplome = "140";
		String codeEtape = "1ILEAS";
		String versionEtape = "140";
		List<EtudiantRef> listeEtu = getDomainServiceScolarite().recupererListeEtudiants(myAnneeRequeteWs, codeDiplome, versionDiplome, codeEtape, versionEtape);
		List<EtudiantRef> listeEtudiantATransferer = new ArrayList<EtudiantRef>();
		int compteur=0;

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

				etudiant.getTransferts().setDateDemandeTransfert(new Date());
				etudiant.getTransferts().setAnnee(myAnneeInt);
				etudiant.getTransferts().setRne(getCurrentRne());
				etudiant.getTransferts().setTemoinTransfertValide(0);
				etudiant.getTransferts().setTemoinOPIWs(null);
				etudiant.getTransferts().setTypeTransfert("T");
				etudiant.getTransferts().setDept("059");
				etudiant.getTransferts().setRne(rneEtabAccueil);

				OffreDeFormationsDTO o = getDomainService().getOdfByPK(rneEtabAccueil, myAnneeInt, "BV2GCCD", 130, "1IGCCD", "130", "GBU");

				etudiant.getTransferts().setOdf(o);
				etudiant.getTransferts().getOdf().setRne(rneEtabAccueil);

				System.out.println("etudiant.toString()===>" +etudiant.toString()+"<===");

				etudiant.setAccueil(null);

				etudiant = getDomainService().addDemandeTransferts(etudiant);
				listeEtudiantATransferer.add(etudiant);
			}
			compteur++;
		}
		if(envoi)
			this.addTransfertOpiToListeTransfertsAccueilTest(rneEtabAccueil, listeEtudiantATransferer);
	}	

	public void addTransfertOpiToListeTransfertsAccueilTest(String rneEtabAccueil, List<EtudiantRef> listeEtudiants)
	{
		System.out.println("===>public void addTransfertOpiToListeTransfertsAccueilTest(String rneEtabAccueil, List<EtudiantRef> listeEtudiants)<===");
		WsPub p = getDomainService().getWsPubByRneAndAnnee(rneEtabAccueil, getCurrentAnnee());

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

					InfosAccueil ia = new InfosAccueil();

					for(EtudiantRef etu : listeEtudiants)
					{
						TrResultatVdiVetDTO sessionsResultats = getDomainServiceScolarite().getSessionsResultats(etu.getNumeroEtudiant(), "A");
						TrBac bac = getDomainServiceScolarite().getBaccalaureat(etu.getNumeroEtudiant());
						TrInfosAdmEtu trInfosAdmEtu = getDomainServiceScolarite().getInfosAdmEtu(etu.getNumeroEtudiant());
						TrEtablissementDTO trEtablissementDTO = getDomainServiceScolarite().getEtablissementByRne(rneEtabAccueil);

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

						

						Fichier signatureParDefaut = getDomainService().getFichierDefautByAnneeAndFrom(2015, "D");
						if(signatureParDefaut!=null)
						{
							etu.getTransferts().setTemoinOPIWs(1);
							etu.getTransferts().setTemoinTransfertValide(2);
							Avis currentAvis = new Avis();
							currentAvis.setNumeroEtudiant(etu.getNumeroEtudiant());
							currentAvis.setAnnee(2015);
							currentAvis.setDateSaisie(new Date());
							currentAvis.setIdDecisionDossier(2);
							currentAvis.setIdEtatDossier(1);
							currentAvis.setIdLocalisationDossier(1);
							
							getDomainService().addAvis(currentAvis);
							
							etu.getTransferts().setFichier(getDomainService().getFichierDefautByAnneeAndFrom(2015, "D"));
							
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

							monService.addTransfertOpiToListeTransfertsAccueil(etu);
							System.out.println("Envoyé vers : ===>"+p.getLibEtab()+"<===");
						}
						else
						{
							System.out.println("pas Envoyé vers : ===>"+p.getLibEtab()+" car pas de signature par défaut<===");
						}
					}
				} 
				catch (Exception e) 
				{
					for(EtudiantRef etu : listeEtudiants)
					{
						etu.getTransferts().setTemoinOPIWs(2);
						etu.getTransferts().setTemoinTransfertValide(2);
						getDomainService().addDemandeTransferts(etu);
					}
					System.out.println("WebServiceException RNE : " + p.getRne());
					e.printStackTrace();
				}
			}
		}
	}

	public void addTransfertOpiToListeTransfertsAccueilTestOld(String rneEtabAccueil, List<EtudiantRef> listeEtudiants)
	{
		System.out.println("addtransfertOpiToListeTransfertAccueil");
		String cod_etu="20054890";
		WsPub p = getDomainService().getWsPubByRneAndAnnee("0593561A", getCurrentAnnee());
		//		WsPub p = getDomainService().getWsPubByRneAndAnnee("0623957P", getCurrentAnnee());

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

					EtudiantRef etu = new EtudiantRef();
					InfosAccueil ia = new InfosAccueil();

					TrResultatVdiVetDTO sessionsResultats = getDomainServiceScolarite().getSessionsResultats(cod_etu, "A");
					TrBac bac = getDomainServiceScolarite().getBaccalaureat(cod_etu);
					TrInfosAdmEtu trInfosAdmEtu = getDomainServiceScolarite().getInfosAdmEtu(cod_etu);
					TrEtablissementDTO trEtablissementDTO = getDomainServiceScolarite().getEtablissementByRne(rneEtabAccueil);

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

					etu.setAnnee(getCurrentAnnee());
					etu.setNomPatronymique("TEST_"+etu.getNumeroIne());
					etu.setPrenom1("UNITAIRE_"+etu.getNumeroIne());

					etu.getAdresse().setNumeroEtudiant(etu.getNumeroIne());
					etu.getAdresse().setAnnee(getCurrentAnnee());
					etu.getAdresse().setCodeCommune("62065");
					etu.getAdresse().setNomCommune("AVION");
					etu.getAdresse().setCodPay("100");
					etu.getAdresse().setEmail("farid.aitkarra@univ-artois.fr");
					etu.getAdresse().setLibAd1("8 rue du 14 juillet");
					etu.getAdresse().setCodePostal("62210");
					etu.getAdresse().setNumTelPortable("0681083586");
					etu.getTransferts().setAnnee(getCurrentAnnee());
					etu.getTransferts().setDateDemandeTransfert(new Date());

//					Fichier f = new Fichier();
//					f.setFrom("A");
//					f.setMd5("ETABLISSEMENT_PARTENAIRE");
//					f.setAnnee(getCurrentAnnee());
//					f.setNom("ETABLISSEMENT_PARTENAIRE");
//					f.setNomSignataire("ETABLISSEMENT_PARTENAIRE");
//					f.setTaille(12345);
//					etu.getTransferts().setFichier(f);

					etu.getTransferts().setFichier(null);

					etu.getTransferts().setRne(getCurrentRne());
					etu.getTransferts().setTemoinTransfertValide(0);
					etu.getTransferts().setTemoinOPIWs(null);
					etu.getTransferts().setTypeTransfert("T");

					OffreDeFormationsDTO o = getDomainService().getOdfByPK(rneEtabAccueil, 2015, "BV2GCCD", 130, "1IGCCD", "130", "GBU");

					etu.getTransferts().setOdf(o);
					etu.getTransferts().getOdf().setRne(rneEtabAccueil);

					if(etu.getAccueil()!=null)
					{
						System.out.println("etu.getAccueil()===>" +etu.getAccueil().getNumeroEtudiant()+"---"+etu.getAccueil().getAnnee()+"<===");
						System.out.println("etu.getAccueil().getSituationUniversitaire().size()===>" +etu.getAccueil().getSituationUniversitaire().size()+"<===");
					}
					else
						System.out.println("etu.getAccueil()===>null<===");


					System.out.println("etu===>" +etu+"<===");						

					//											getDomainService().addDemandeTransferts(etu);
					for(int i=0;i<10;i++)
					{
						etu.setNumeroIne(rand(11));
						etu.setNumeroEtudiant(etu.getNumeroIne());
						etu.setSource("A");
						etu.getAdresse().setNumeroEtudiant(etu.getNumeroIne());
						etu.getTransferts().setNumeroEtudiant(etu.getNumeroIne());
						ia.setNumeroEtudiant(etu.getNumeroIne());

						List<SituationUniversitaire> lSu = ia.getSituationUniversitaire();
						if(lSu!=null && !lSu.isEmpty())
						{
							for(int j=0;j<lSu.size();j++)
							{
								String timestamp = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
								lSu.get(j).setId(etu.getNumeroIne()+"_"+timestamp+"_P"+j);	
							}
						}
						monService.addTransfertOpiToListeTransfertsAccueil(etu);
					}

					System.out.println("Envoyé vers : ===>"+p.getLibEtab()+"<===");

				} 
				catch (Exception e) 
				{
					System.out.println("WebServiceException RNE : " + p.getRne());
					e.printStackTrace();
				}
			}
		}
	}	

	//@Test
	public void deleteAllDemandesTransfertDepartTest()
	{
		System.out.println("===>public void deleteAllDemandesTransfertDepartTest()<===");
		List<EtudiantRef> lEtuDepart = getDomainService().getAllDemandesTransfertsByAnnee(getCurrentAnnee(), "D");
		for(EtudiantRef etuDepart : lEtuDepart)
			getDomainService().deleteDemandeTransfert(etuDepart, getCurrentAnnee());
	}

	//@Test
	public void deleteAllDemandesTransfertAccueilTest()
	{
		System.out.println("===>public void deleteAllDemandesTransfertAccueilTest()<===");
		List<EtudiantRef> lEtuAccueil = getDomainService().getAllDemandesTransfertsByAnnee(getCurrentAnnee(), "A");
		for(EtudiantRef etuAccueil : lEtuAccueil)
			getDomainService().deleteDemandeTransfert(etuAccueil,getCurrentAnnee());
	}	

	private boolean testUrl(String host) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(host).openConnection();
			conn.setConnectTimeout(100000);
			conn.connect();
			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (MalformedURLException e) {

			System.out.println("MalformedURLException");
			System.out.println("host : " + host);

			e.printStackTrace();
			return false;
		} catch (IOException e) {

			System.out.println("IOException");
			System.out.println("host : " + host);

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

	public String getCurrentRne() {
		return currentRne;
	}

	public void setCurrentRne(String currentRne) {
		this.currentRne = currentRne;
	}
}
