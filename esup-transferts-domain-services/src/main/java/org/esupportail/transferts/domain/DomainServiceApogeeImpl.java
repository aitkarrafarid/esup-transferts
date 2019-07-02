/**
 * ESUP-Portail Blank Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain;

import com.googlecode.ehcache.annotations.Cacheable;
import gouv.education.apogee.commun.client.utils.WSUtils;
import gouv.education.apogee.commun.client.ws.administratifmetier.AdministratifMetierServiceInterfaceProxy;
import gouv.education.apogee.commun.client.ws.etablissementmetier.EtablissementMetierServiceInterfaceProxy;
import gouv.education.apogee.commun.client.ws.etudiantmetier.EtudiantMetierServiceInterfaceProxy;
import gouv.education.apogee.commun.client.ws.geographiemetier.GeographieMetierServiceInterfaceProxy;
import gouv.education.apogee.commun.client.ws.offreformationmetier.OffreFormationMetierServiceInterfaceProxy;
import gouv.education.apogee.commun.client.ws.opimetier.OpiMetierSoapBindingStub;
import gouv.education.apogee.commun.client.ws.pedagogiquemetier.PedagogiqueMetierServiceInterfaceProxy;
import gouv.education.apogee.commun.client.ws.referentielmetier.ReferentielMetierServiceInterfaceProxy;
import gouv.education.apogee.commun.client.ws.scolaritemetier.ScolariteMetierServiceInterfaceProxy;
import gouv.education.apogee.commun.transverse.dto.administratif.InsAdmEtpDTO2;
import gouv.education.apogee.commun.transverse.dto.etablissement.EtablissementCompletDTO2;
import gouv.education.apogee.commun.transverse.dto.etudiant.*;
import gouv.education.apogee.commun.transverse.dto.etudiant.PaysDTO;
import gouv.education.apogee.commun.transverse.dto.geographie.DepartementDTO;
import gouv.education.apogee.commun.transverse.dto.offreformation.recupererse.*;
import gouv.education.apogee.commun.transverse.dto.opi.*;
import gouv.education.apogee.commun.transverse.dto.pedagogique.*;
import gouv.education.apogee.commun.transverse.dto.scolarite.BacOuEquDTO;
import gouv.education.apogee.commun.transverse.exception.WebBaseException;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import gouv.education.apogee.commun.transverse.dto.geographie.DepartementDTO;

/**
 * @author Farid AIT KARRA (Universite d'Artois) - 2012
 *
 */
public class DomainServiceApogeeImpl implements DomainServiceScolarite {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String codeComposanteInconnue = "N.A";
	private List<String> forcerBlocageListSplit = new ArrayList<String>();
	private String user;
	private String password;

	/**
	 * For Logging.
	 */
	private static final Logger logger = new LoggerImpl(DomainServiceApogeeImpl.class);

	/**
	 * Constructor.
	 */
	public DomainServiceApogeeImpl() {
		super();
	}

	public DomainServiceApogeeImpl(List<String> forcerBlocageListSplit) {
		this.forcerBlocageListSplit=forcerBlocageListSplit;
	}

	public DomainServiceApogeeImpl(List<String> forcerBlocageListSplit, String user, String password) {
		this.forcerBlocageListSplit=forcerBlocageListSplit;
		this.user=user;
		this.password=password;
	}

	@Override
	public EtudiantRef getCurrentEtudiant(String supannEtuId){
		EtudiantRef etudiant = new EtudiantRef();
		AdresseRef adresse = new AdresseRef();
		Transferts transfert = new Transferts();
		InfosAccueil infosAccueil = new InfosAccueil();
		if (logger.isDebugEnabled())
			logger.debug("Je suis dans le WS AMUE");
		etudiant.setFrom("WSAMUE");
		//etudiant.setEduPersonAffiliation(eduPersonAffiliation);
		etudiant.setNumeroEtudiant(supannEtuId);
		EtudiantMetierServiceInterfaceProxy etudiantMetierService = new EtudiantMetierServiceInterfaceProxy();
		// Recuperation des infos de l'etudiant dans Apogee	
		InfoAdmEtuDTO2 infoAdmEtuDTO;
		try {
			if (logger.isDebugEnabled())
				logger.debug("Numero etudiant -->"+etudiant.getNumeroEtudiant());

			infoAdmEtuDTO = etudiantMetierService.recupererInfosAdmEtu_v2(etudiant.getNumeroEtudiant());
			BlocageDTO[] listeBlocagesDTO = infoAdmEtuDTO.getListeBlocages();
			if (logger.isDebugEnabled())
				if(listeBlocagesDTO!=null)
					logger.debug("listeBlocagesDTO -->"+listeBlocagesDTO.length);

			CoordonneesDTO2 coordonneesDTO = etudiantMetierService.recupererAdressesEtudiant_v2(etudiant.getNumeroEtudiant(), null, null);
			if (logger.isDebugEnabled())
				logger.debug("coordonneesDTO -->"+coordonneesDTO.toString());

			AdresseDTO2 adresseFixe = coordonneesDTO.getAdresseFixe();
			if (logger.isDebugEnabled())
				logger.debug("adresseFixe -->"+adresseFixe.toString());

			CommuneDTO2 communeDTO = adresseFixe.getCommune();
			if (logger.isDebugEnabled() && communeDTO!=null)
				logger.debug("communeDTO -->"+communeDTO.toString());
			else if(logger.isDebugEnabled() && communeDTO==null)
				logger.debug("communeDTO --> la commune est null");

			PaysDTO paysDTO = adresseFixe.getPays();
			if (logger.isDebugEnabled())
				logger.debug("paysDTO -->"+paysDTO.toString());

			NationaliteDTO nationaliteDTO = infoAdmEtuDTO.getNationaliteDTO();
			if (logger.isDebugEnabled())
				logger.debug("nationaliteDTO -->"+nationaliteDTO.toString());

			IndBacDTO[] indBac = infoAdmEtuDTO.getListeBacs();
			if (logger.isDebugEnabled())
				if(indBac!=null)
					logger.debug("indBac -->"+indBac.length);

			infosAccueil.setAnneeBac(indBac[0].getAnneeObtentionBac());
			infosAccueil.setCodeBac(indBac[0].getCodBac());
			infosAccueil.setCodePaysNat(nationaliteDTO.getCodeNationalite());


			if(listeBlocagesDTO != null)
			{
				if(listeBlocagesDTO != null && listeBlocagesDTO.length==0)
				{
					etudiant.setInterdit(false);
					if (logger.isDebugEnabled()) {
						logger.debug("Interdit a FALSE");
					}
				}
				else if(listeBlocagesDTO.length>0)
				{
					if (logger.isDebugEnabled()) {
						logger.debug("Interdit a TRUE");
						if(this.forcerBlocageListSplit!=null && !this.forcerBlocageListSplit.isEmpty())
						{
							for(int i=0;i<this.forcerBlocageListSplit.size();i++)
								logger.debug("this.forcerBlocageListSplit.get(i) --> "+this.forcerBlocageListSplit.get(i));
						}
					}
					int forcerInterdit = 0;
					//etudiant.setListeBlocagesDTO(listeBlocagesDTO);
					for(BlocageDTO b : listeBlocagesDTO)
					{
						boolean forcer=false;
						if(this.forcerBlocageListSplit!=null && !this.forcerBlocageListSplit.isEmpty())
						{
							for(int i=0;i<this.forcerBlocageListSplit.size();i++)
							{
								if(b.getCodBlocage().equalsIgnoreCase(this.forcerBlocageListSplit.get(i)))
								{
									forcer=true;
									forcerInterdit=forcerInterdit+1;
									break;
								}
							}
						}
						if(!forcer)
							etudiant.getListeBlocagesDTO().add(new TrBlocageDTO(b.getCodBlocage(), b.getLibBlocage()));
					}

					if (logger.isDebugEnabled()) {
						logger.debug("forcerInterdit -->" + forcerInterdit);
						logger.debug("listeBlocagesDTO.length -->" + listeBlocagesDTO.length);
					}

					if(forcerInterdit==listeBlocagesDTO.length)
						etudiant.setInterdit(false);
					else
						etudiant.setInterdit(true);
				}
			}
			else
			{
				etudiant.setInterdit(false);
				if (logger.isDebugEnabled()) {
					logger.debug("Interdit a FALSE");
				}
			}

			//Etat civil
			etudiant.setNumeroIne(infoAdmEtuDTO.getNumeroINE());
			etudiant.setNomPatronymique(infoAdmEtuDTO.getNomPatronymique());
			etudiant.setNomUsuel(infoAdmEtuDTO.getNomUsuel());
			etudiant.setPrenom1(infoAdmEtuDTO.getPrenom1());
			etudiant.setPrenom2(infoAdmEtuDTO.getPrenom2());
			etudiant.setDateNaissance(infoAdmEtuDTO.getDateNaissance().getTime());
			etudiant.setLibNationalite(nationaliteDTO.getLibNationalite());

			//Adresse
			adresse.setNumeroEtudiant(etudiant.getNumeroEtudiant());
			adresse.setLibAd1(adresseFixe.getLibAd1());
			adresse.setLibAd2(adresseFixe.getLibAd2());
			adresse.setLibAd3(adresseFixe.getLibAd3());
			adresse.setNumTelPortable(coordonneesDTO.getNumTelPortable());
			adresse.setNumTel(adresseFixe.getNumTel());
			adresse.setEmail(coordonneesDTO.getEmail());
			if("100".equals(paysDTO.getCodPay()))
			{
				adresse.setCodePostal(communeDTO.getCodePostal());
				adresse.setCodeCommune(communeDTO.getCodeInsee());
				adresse.setCodPay(paysDTO.getCodPay());
				adresse.setLibPay(paysDTO.getLibPay());
			}
			else
			{
				adresse.setCodPay(paysDTO.getCodPay());
				adresse.setLibPay(paysDTO.getLibPay());
				adresse.setCodePostal("");
				adresse.setCodeCommune("");
			}
			transfert.setNumeroEtudiant(etudiant.getNumeroEtudiant());
			etudiant.setAdresse(adresse);
			etudiant.setTransferts(transfert);
			etudiant.setAccueil(infosAccueil);
			return etudiant;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public EtudiantRef getCurrentEtudiantIne(String ine, Date dateNaissance){
		EtudiantRef etudiant = new EtudiantRef();
		AdresseRef adresse = new AdresseRef();
		Transferts transfert = new Transferts();
		InfosAccueil infosAccueil = new InfosAccueil();
		// appel au WS AMUE
		if (logger.isDebugEnabled()) {
			logger.debug("Je suis dans le WS AMUE - AUTH Apogee");
		}
		etudiant.setFrom("WSAMUE");

		String ineSansCle = ine.substring(0, ine.length()-1);
		String cleIne = ine.substring(ine.length()-1, ine.length());

		if (logger.isDebugEnabled()) {
			logger.debug("===>public EtudiantRef getCurrentEtudiantIne(String ine, Date dateNaissance)<===");
			logger.debug("ine===>"+ine+"<===");
			logger.debug("dateNaissance ===>"+dateNaissance+"<===");
			logger.debug("===>----------------------------------------------------<===");
			logger.debug("ineSansCle===>"+ineSansCle+"<===");
			logger.debug("cleIne ===>"+cleIne+"<===");
		}
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		EtudiantMetierServiceInterfaceProxy etudiantMetierService = new EtudiantMetierServiceInterfaceProxy();
		// Recuperation des infos de l'etudiant dans Apogee	
		InfoAdmEtuDTO2 infoAdmEtuDTO;
		try {
			if (logger.isDebugEnabled())
				logger.debug("ine===>" + ine + "<===");
			IdentifiantsEtudiantDTO2 identifiantEtudiant =  etudiantMetierService.recupererIdentifiantsEtudiant_v2(null, null, ine, null, null, null, null, null, null);

			infoAdmEtuDTO = etudiantMetierService.recupererInfosAdmEtu_v2(identifiantEtudiant.getCodEtu().toString());

			if(identifiantEtudiant!=null && infoAdmEtuDTO!=null)
				if (logger.isDebugEnabled())
				{
					logger.debug("===>if(identifiantEtudiant!=null && infoAdmEtuDTO!=null)<===");
					logger.debug("Numero etudiant dans la bdd scol===>"+identifiantEtudiant.getCodEtu().toString()+"<===");
					logger.debug("Date de naissance dans la bdd scol ===>"+infoAdmEtuDTO.getDateNaissance().getTime()+"<===");
				}

			if(dateFormat.format(dateNaissance).equals(dateFormat.format(infoAdmEtuDTO.getDateNaissance().getTime())))
			{
				if (logger.isDebugEnabled())
					logger.debug("===>Compare date OK<===");

				BlocageDTO[] listeBlocagesDTO = infoAdmEtuDTO.getListeBlocages();
				if (logger.isDebugEnabled())
					if(listeBlocagesDTO!=null)
						logger.debug("listeBlocagesDTO -->"+listeBlocagesDTO.length);

				CoordonneesDTO2 coordonneesDTO = etudiantMetierService.recupererAdressesEtudiant_v2(infoAdmEtuDTO.getNumEtu().toString(), null, "N");
				if (logger.isDebugEnabled())
					logger.debug("coordonneesDTO -->"+coordonneesDTO.toString());

				AdresseDTO2 adresseFixe = coordonneesDTO.getAdresseFixe();
				if (logger.isDebugEnabled())
					logger.debug("adresseFixe -->"+adresseFixe.toString());

				CommuneDTO2 communeDTO = adresseFixe.getCommune();
				if (logger.isDebugEnabled())
					logger.debug("communeDTO -->"+communeDTO.toString());

				PaysDTO paysDTO = adresseFixe.getPays();
				if (logger.isDebugEnabled())
					logger.debug("paysDTO -->"+paysDTO.toString());

				NationaliteDTO nationaliteDTO = infoAdmEtuDTO.getNationaliteDTO();
				if (logger.isDebugEnabled())
					logger.debug("nationaliteDTO -->"+nationaliteDTO.toString());

				IndBacDTO[] indBac = infoAdmEtuDTO.getListeBacs();
				if (logger.isDebugEnabled())
					if(indBac!=null)
						logger.debug("indBac -->"+indBac.length);

				infosAccueil.setAnneeBac(indBac[0].getAnneeObtentionBac());
				infosAccueil.setCodeBac(indBac[0].getCodBac());
				infosAccueil.setCodePaysNat(nationaliteDTO.getCodeNationalite());

				if(listeBlocagesDTO != null)
				{
					if(listeBlocagesDTO != null && listeBlocagesDTO.length==0)
					{
						etudiant.setInterdit(false);
						if (logger.isDebugEnabled()) {
							logger.debug("Interdit a FALSE");
						}
					}
					else if(listeBlocagesDTO.length>0)
					{
						if (logger.isDebugEnabled()) {
							logger.debug("Interdit a TRUE");
							if(this.forcerBlocageListSplit!=null && !this.forcerBlocageListSplit.isEmpty())
							{
								for(int i=0;i<this.forcerBlocageListSplit.size();i++)
									logger.debug("this.forcerBlocageListSplit.get(i) --> "+this.forcerBlocageListSplit.get(i));
							}
						}
						int forcerInterdit = 0;
						//etudiant.setListeBlocagesDTO(listeBlocagesDTO);
						for(BlocageDTO b : listeBlocagesDTO)
						{
							boolean forcer=false;
							if(this.forcerBlocageListSplit!=null && !this.forcerBlocageListSplit.isEmpty())
							{
								for(int i=0;i<this.forcerBlocageListSplit.size();i++)
								{
									if(b.getCodBlocage().equalsIgnoreCase(this.forcerBlocageListSplit.get(i)))
									{
										forcer=true;
										forcerInterdit=forcerInterdit+1;
										break;
									}
								}
							}
							if(!forcer)
								etudiant.getListeBlocagesDTO().add(new TrBlocageDTO(b.getCodBlocage(), b.getLibBlocage()));
						}

						if (logger.isDebugEnabled()) {
							logger.debug("forcerInterdit -->" + forcerInterdit);
							logger.debug("listeBlocagesDTO.length -->" + listeBlocagesDTO.length);
						}

						if(forcerInterdit==listeBlocagesDTO.length)
							etudiant.setInterdit(false);
						else
							etudiant.setInterdit(true);
					}
				}
				else
				{
					etudiant.setInterdit(false);
					if (logger.isDebugEnabled()) {
						logger.debug("Interdit a FALSE");
					}
				}

				//Etat civil
				etudiant.setNumeroEtudiant(infoAdmEtuDTO.getNumEtu().toString());
				etudiant.setNumeroIne(infoAdmEtuDTO.getNumeroINE());
				etudiant.setNomPatronymique(infoAdmEtuDTO.getNomPatronymique());
				etudiant.setNomUsuel(infoAdmEtuDTO.getNomUsuel());
				etudiant.setPrenom1(infoAdmEtuDTO.getPrenom1());
				etudiant.setPrenom2(infoAdmEtuDTO.getPrenom2());
				etudiant.setDateNaissance(infoAdmEtuDTO.getDateNaissance().getTime());
				etudiant.setLibNationalite(nationaliteDTO.getLibNationalite());

				//Adresse
				adresse.setNumeroEtudiant(etudiant.getNumeroEtudiant());
				adresse.setLibAd1(adresseFixe.getLibAd1());
				adresse.setLibAd2(adresseFixe.getLibAd2());
				adresse.setLibAd3(adresseFixe.getLibAd3());
				adresse.setNumTelPortable(coordonneesDTO.getNumTelPortable());
				adresse.setNumTel(adresseFixe.getNumTel());
				adresse.setEmail(coordonneesDTO.getEmail());
				if("100".equals(paysDTO.getCodPay()))
				{
					adresse.setCodePostal(communeDTO.getCodePostal());
					adresse.setCodeCommune(communeDTO.getCodeInsee());
					adresse.setCodPay(paysDTO.getCodPay());
					adresse.setLibPay(paysDTO.getLibPay());
				}
				else
				{
					adresse.setCodePostal("");
					adresse.setCodeCommune("");
					adresse.setCodPay(paysDTO.getCodPay());
					adresse.setLibPay(paysDTO.getLibPay());
				}
				//Transferts
				transfert.setNumeroEtudiant(etudiant.getNumeroEtudiant());
				etudiant.setAdresse(adresse);
				etudiant.setTransferts(transfert);
				etudiant.setAccueil(infosAccueil);
				return etudiant;
			}
			else
			{
				if (logger.isDebugEnabled()) {
					logger.debug("Compare date FAUX !!!");
				}
				etudiant = null;
			}
			return etudiant;

		} catch (WebBaseException e) {
			logger.error(e);
			return null;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	//	public Integer getCleIndByCodAndCle(String codNneIndOpi, String codCleNneIndOpi) 
	public IdentifiantEtudiant getIdentifiantEtudiantByIne(String codNneIndOpi, String codCleNneIndOpi)
	{
		// appel au WS AMUE
		if (logger.isDebugEnabled())
			logger.debug("Je suis dans le WS AMUE - AUTH Apogee");

		if (logger.isDebugEnabled()) {
			logger.debug("public Integer getCleIndByCodAndCle(String codNneIndOpi, String codCleNneIndOpi) ");
			logger.debug("codNneIndOpi --> " + codNneIndOpi);
			logger.debug("codCleNneIndOpi --> " + codCleNneIndOpi);
		}
		//TODO Modif Flo pour le nouvel INE A VERIFIER !
//		String ine = codNneIndOpi+codCleNneIndOpi;
//		String ine = codNneIndOpi.substring(0, codNneIndOpi.length()-codCleNneIndOpi.length());
		String ine = codNneIndOpi;
		if (logger.isDebugEnabled()) {
			logger.debug("ine --> " + ine);
		}

		EtudiantMetierServiceInterfaceProxy etudiantMetierService = new EtudiantMetierServiceInterfaceProxy();
		// Recuperation des infos de l'etudiant dans Apogee
		try {
			IdentifiantsEtudiantDTO2 identifiantEtudiant =  etudiantMetierService.recupererIdentifiantsEtudiant_v2(null, null, ine, null, null, null, null, null, null);
			IdentifiantEtudiant ie = new IdentifiantEtudiant(identifiantEtudiant.getCodEtu(), identifiantEtudiant.getCodInd(), identifiantEtudiant.getNumeroINE());
			return ie;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public List<TrCommuneDTO> getCommunes(String codePostal){
		// appel au WS AMUE
		List<TrCommuneDTO> listTrCommuneDTO = null;
		GeographieMetierServiceInterfaceProxy geographieMetierServiceInterface = new GeographieMetierServiceInterfaceProxy();
		gouv.education.apogee.commun.transverse.dto.geographie.CommuneDTO[] listeCommunes;
		try{
			listeCommunes = geographieMetierServiceInterface.recupererCommune(codePostal, null, null);
			if(listeCommunes.length>0)
			{
				listTrCommuneDTO = new ArrayList<TrCommuneDTO>();
				for(int i=0;i<listeCommunes.length;i++)
					listTrCommuneDTO.add(new TrCommuneDTO(listeCommunes[i].getCodeCommune(), listeCommunes[i].getLibCommune()));
			}
		}
		catch (Exception e)
		{
			logger.error(e);
			listTrCommuneDTO = null;
		}
		return listTrCommuneDTO;
	}

	@Override
	public List<TrPaysDTO> getListePays()
	{
		// appel au WS AMUE
		List<TrPaysDTO> listTrPaysDTO = null;
		GeographieMetierServiceInterfaceProxy geographieMetierServiceInterface = new GeographieMetierServiceInterfaceProxy();
		gouv.education.apogee.commun.transverse.dto.geographie.PaysDTO[] listePays;
		try{
			listePays = geographieMetierServiceInterface.recupererPays(null, null);
			if(listePays.length>0)
			{
				listTrPaysDTO = new ArrayList<TrPaysDTO>();
				for(int i=0;i<listePays.length;i++)
					listTrPaysDTO.add(new TrPaysDTO(listePays[i].getCodePay(), listePays[i].getLibPay(), listePays[i].getLibNat()));
			}
		}
		catch (Exception e)
		{
			logger.error(e);
			listTrPaysDTO = null;
		}
		return listTrPaysDTO;
	}

	@Override
	public TrPaysDTO getPaysByCodePays(String codePays)
	{
		// appel au WS AMUE
		TrPaysDTO trPaysDTO = null;
		GeographieMetierServiceInterfaceProxy geographieMetierServiceInterface = new GeographieMetierServiceInterfaceProxy();
		gouv.education.apogee.commun.transverse.dto.geographie.PaysDTO[] pays;
		try
		{
			pays = geographieMetierServiceInterface.recupererPays(codePays, null);
			trPaysDTO = new TrPaysDTO(pays[0].getCodePay(), pays[0].getLibPay(), pays[0].getLibNat());
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		return trPaysDTO;
	}

	public List<TrDepartementDTO> getListeDepartements()
	{
		// appel au WS AMUE
		List<TrDepartementDTO> listTrDepartementDTO = null;
		GeographieMetierServiceInterfaceProxy geographieMetierServiceInterface = new GeographieMetierServiceInterfaceProxy();
		DepartementDTO[] listeDepartements;
		try{
			listeDepartements = geographieMetierServiceInterface.recupererDepartement(null, null);
			if(listeDepartements.length>0)
			{
				listTrDepartementDTO = new ArrayList<TrDepartementDTO>();
				for(int i=0;i<listeDepartements.length;i++)
					listTrDepartementDTO.add(new TrDepartementDTO(listeDepartements[i].getCodeDept(), listeDepartements[i].getLibDept()));
			}
		}
		catch (Exception e)
		{
			logger.error(e);
			listTrDepartementDTO = null;
		}
		return listTrDepartementDTO;
	}

	public List<TrEtablissementDTO> getListeEtablissements(String typeEtablissement, String dept) {
		if (logger.isDebugEnabled()) {
			logger.info("===>Appel au WS AMUE<===");
			logger.info("public List<TrEtablissementDTO> getListeEtablissements(String typeEtablissement, String dept)===>" + typeEtablissement + "/" + dept + "<===");
		}
		List<TrEtablissementDTO> listTrEtablissementDTO = null;
		EtablissementMetierServiceInterfaceProxy etablissementMetierServiceInterface = new EtablissementMetierServiceInterfaceProxy();
		EtablissementCompletDTO2[] listeEtablissements;
		try{
			listeEtablissements = etablissementMetierServiceInterface.recupererEtablissementWS_v2(typeEtablissement, null, null, dept, null, null, null);

			if(listeEtablissements.length>0)
			{
				listTrEtablissementDTO = new ArrayList<TrEtablissementDTO>();
				for(int i=0;i<listeEtablissements.length;i++)
					listTrEtablissementDTO.add(new TrEtablissementDTO(listeEtablissements[i].getCodeEtb(), listeEtablissements[i].getLibEtb()));
			}
		}
		catch (Exception e)
		{
			logger.error(e);
			listTrEtablissementDTO = null;
		}
		return listTrEtablissementDTO;

	}

	public TrEtablissementDTO getEtablissementByRne(String rne) {
		// appel au WS AMUE
		TrEtablissementDTO trEtablissement = null;
		EtablissementMetierServiceInterfaceProxy etablissementMetierServiceInterface = new EtablissementMetierServiceInterfaceProxy();
		EtablissementCompletDTO2[] typeEtablissementDTO;
		try{
			typeEtablissementDTO = etablissementMetierServiceInterface.recupererEtablissementWS_v2(null, rne, null, null, null, null, null);
			if(typeEtablissementDTO != null && typeEtablissementDTO.length>0)
			{
				trEtablissement = new TrEtablissementDTO(typeEtablissementDTO[0].getCodeEtb(),
						typeEtablissementDTO[0].getLibEtb(),
						typeEtablissementDTO[0].getDepartement().getCodeDept(),
						typeEtablissementDTO[0].getDepartement().getLibDept(),
						typeEtablissementDTO[0].getAcademie().getLibAcd(),
						typeEtablissementDTO[0].getLibOffEtb(),
						typeEtablissementDTO[0].getAdresse().getLibAd1Etb(),
						typeEtablissementDTO[0].getAdresse().getLibAd2Etb(),
						typeEtablissementDTO[0].getAdresse().getLibAd3Etb(),
						typeEtablissementDTO[0].getAdresse().getCodPosAdrEtb(),
						typeEtablissementDTO[0].getAdresse().getLibAchAdrEtb()
						);
			}
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		return trEtablissement;
	}

	public TrEtablissementDTO getEtablissementByDepartement(String dep) {
		// appel au WS AMUE
		TrEtablissementDTO trEtablissement = null;
		EtablissementMetierServiceInterfaceProxy etablissementMetierServiceInterface = new EtablissementMetierServiceInterfaceProxy();
		EtablissementCompletDTO2[] typeEtablissementDTO;
		try{
			typeEtablissementDTO = etablissementMetierServiceInterface.recupererEtablissementWS_v2(null, null, null, dep, null, null, null);
			if(typeEtablissementDTO.length>0)
			{
				trEtablissement = new TrEtablissementDTO(typeEtablissementDTO[0].getCodeEtb(),
						typeEtablissementDTO[0].getLibEtb(),
						typeEtablissementDTO[0].getDepartement().getCodeDept(),
						typeEtablissementDTO[0].getDepartement().getLibDept(),
						typeEtablissementDTO[0].getAcademie().getLibAcd(),
						typeEtablissementDTO[0].getLibOffEtb(),
						typeEtablissementDTO[0].getAdresse().getLibAd1Etb(),
						typeEtablissementDTO[0].getAdresse().getLibAd2Etb(),
						typeEtablissementDTO[0].getAdresse().getLibAd3Etb(),
						typeEtablissementDTO[0].getAdresse().getCodPosAdrEtb(),
						typeEtablissementDTO[0].getAdresse().getLibAchAdrEtb()
						);
			}
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		return trEtablissement;
	}

	@Override
	public TrBac getBaccalaureat(String supannEtuId){
		EtudiantMetierServiceInterfaceProxy etudiantMetierService = new EtudiantMetierServiceInterfaceProxy();
		// Recuperation des infos de l'etudiant dans Apogee	
		InfoAdmEtuDTO2 infoAdmEtuDTO;
		IndBacDTO[] indBacDTO;
		try {
			infoAdmEtuDTO = etudiantMetierService.recupererInfosAdmEtu_v2(supannEtuId);
			indBacDTO = infoAdmEtuDTO.getListeBacs();

			for(int i=0; i<indBacDTO.length;i++)
			{
				if (logger.isDebugEnabled()) {
					logger.debug("indBacDTO.length --> "+indBacDTO.length);
					logger.debug("indBacDTO[i].getLibelleBac() --> "+indBacDTO[i].getLibelleBac());
					logger.debug("indBacDTO[i].getDepartementBac().getLibDept() --> "+indBacDTO[i].getDepartementBac().getLibDept());
					logger.debug("indBacDTO[i].getEtbBac().getLibEtb() --> "+indBacDTO[i].getEtbBac().getLibEtb());
					logger.debug("indBacDTO[i].getAnneeObtentionBac() --> "+indBacDTO[i].getAnneeObtentionBac());
					//logger.debug("this.getEtablissementByDepartement(indBacDTO[i].getDepartementBac().getCodeDept()).getLibAcademie()) --> "+this.getEtablissementByDepartement(indBacDTO[i].getDepartementBac().getCodeDept()).getLibAcademie());
				}
				indBacDTO[i].getLibelleBac();
			}

			String etabBac = "ETRANGER";

			if(indBacDTO[0]!=null && !"ETRANGER".equals(indBacDTO[0].getDepartementBac().getLibDept()))
				etabBac = this.getEtablissementByDepartement(indBacDTO[0].getDepartementBac().getCodeDept()).getLibAcademie();

			TrBac infosBac = new TrBac(indBacDTO[0].getCodBac(),
					indBacDTO[0].getLibelleBac(),
					indBacDTO[0].getDepartementBac().getLibDept(),
					indBacDTO[0].getEtbBac().getLibEtb(),
					indBacDTO[0].getAnneeObtentionBac(),
					etabBac);
			return infosBac;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public TrInfosAdmEtu getInfosAdmEtu(String supannEtuId){
		EtudiantMetierServiceInterfaceProxy etudiantMetierService = new EtudiantMetierServiceInterfaceProxy();
		// Recuperation des infos de l'etudiant dans Apogee	
		InfoAdmEtuDTO2 infoAdmEtuDTO;
		try {
			infoAdmEtuDTO = etudiantMetierService.recupererInfosAdmEtu_v2(supannEtuId);
			NationaliteDTO nationaliteDTO = infoAdmEtuDTO.getNationaliteDTO();
			TrInfosAdmEtu trInfosAdmEtu = new TrInfosAdmEtu(nationaliteDTO.getCodeNationalite(), nationaliteDTO.getLibNationalite());
			return trInfosAdmEtu;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public String getComposante(String supannEtuId){
		if (logger.isDebugEnabled()) {
			logger.debug("String getComposante(String supannEtuId)");
			logger.debug("supannEtuId --> "+supannEtuId);
		}
		AdministratifMetierServiceInterfaceProxy administratifMetierServiceInterface = new AdministratifMetierServiceInterfaceProxy();
		InsAdmEtpDTO2[] insAdmEtpDTO;
		try {
			String ret="";
			insAdmEtpDTO = administratifMetierServiceInterface.recupererIAEtapes_v2(supannEtuId, null, "ARE", "ARE");
			for(int i=0; i<insAdmEtpDTO.length;i++)
			{
				if("oui".equals(insAdmEtpDTO[i].getEtapePremiere()))
					//					ret=insAdmEtpDTO[i].getComposante().getLibComposante();
					ret=insAdmEtpDTO[i].getComposante().getCodComposante();

				if (logger.isDebugEnabled()) {
					logger.debug("----------------------> "+insAdmEtpDTO[i].getComposante().getLibComposante());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getTemoinPI());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getTemoinVae());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtatIae().getCodeEtatIAE());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtatIae().getLibEtatIAE());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtatIaa().getCodeEtatIAA());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtatIaa().getLibEtatIAA());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtapePremiere());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getCge().getCodeCGE());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getCge().getLibCGE());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getComposante().getCodComposante());
					logger.debug("############################################################################################");
				}
			}
			return ret;
		} catch (Exception e) {
			logger.error(e);
			return codeComposanteInconnue;
		}
	}

	@Override
	public Map<String,String> getEtapePremiereAndCodeCgeAndLibCge(String supannEtuId){
		if (logger.isDebugEnabled()) {
			logger.debug("public Map<String,String> getEtapePremiereAndCodeCgeAndLibCge(String supannEtuId)");
			logger.debug("supannEtuId --> "+supannEtuId);
		}
		AdministratifMetierServiceInterfaceProxy administratifMetierServiceInterface = new AdministratifMetierServiceInterfaceProxy();
		InsAdmEtpDTO2[] insAdmEtpDTO;
		try {
			insAdmEtpDTO = administratifMetierServiceInterface.recupererIAEtapes_v2(supannEtuId, null, "ARE", "ARE");
			Map<String, String> map = new HashMap<String, String>();
			for(int i=0; i<insAdmEtpDTO.length;i++)
			{
				if("oui".equals(insAdmEtpDTO[i].getEtapePremiere()))
				{
					map.put("libWebVet", insAdmEtpDTO[i].getEtape().getLibWebVet());
					map.put("codeCGE", insAdmEtpDTO[i].getCge().getCodeCGE());
					map.put("libCGE", insAdmEtpDTO[i].getCge().getLibCGE());
					map.put("codeComposante", insAdmEtpDTO[i].getComposante().getCodComposante());
					map.put("libComposante", insAdmEtpDTO[i].getComposante().getLibComposante());
				}
				if (logger.isDebugEnabled()) {
					logger.debug("----------------------> "+insAdmEtpDTO[i].getComposante().getLibComposante());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getTemoinPI());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getTemoinVae());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtatIae().getCodeEtatIAE());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtatIae().getLibEtatIAE());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtatIaa().getCodeEtatIAA());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtatIaa().getLibEtatIAA());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtapePremiere());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getCge().getCodeCGE());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getCge().getLibCGE());
					logger.debug("----------------------> "+insAdmEtpDTO[i].getEtape().getLibWebVet());
				}
			}
			return map;
		} catch (Exception e) {
			logger.error(e);
			Map<String, String> map = new HashMap<String, String>();
			map.put("libWebVet", "Inconnue");
			map.put("codeCGE", "Inconnue");
			map.put("libCGE", "Inconnue");
			map.put("codeComposante", "Inconnue");
			map.put("libComposante", "Inconnue");
			return map;
		}
	}

	@Override
	public TrResultatVdiVetDTO getSessionsResultats(String supannEtuId, String source)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getSessionsResultats ----- supannEtuId===>" + supannEtuId+"<===");
			logger.debug("getSessionsResultats ----- source===>" + source+"<===");
		}

		int max=MAX_SESSIONS_RESULTAT_DEPART;
		if("A".equals(source))
			max=MAX_SESSIONS_RESULTAT_ACCUEIL;

		PedagogiqueMetierServiceInterfaceProxy pedagogiqueMetierServiceInterface = new PedagogiqueMetierServiceInterfaceProxy();
		ContratPedagogiqueResultatVdiVetDTO2[] contratPedagogiqueResultatVdiVetDTO;
		try
		{
//			contratPedagogiqueResultatVdiVetDTO = pedagogiqueMetierServiceInterface.recupererContratPedagogiqueResultatVdiVet(supannEtuId, "toutes", "Apogee", "AET", "toutes", null);
			contratPedagogiqueResultatVdiVetDTO = pedagogiqueMetierServiceInterface.recupererContratPedagogiqueResultatVdiVet_v2(supannEtuId, "toutes", "Apogee", "AET", "toutes", null, "E");

			ContratPedagogiqueResultatVdiVetDTO2[] tab = new ContratPedagogiqueResultatVdiVetDTO2[9];

			if (logger.isDebugEnabled())
				logger.debug("contratPedagogiqueResultatVdiVetDTO.length -----> " + contratPedagogiqueResultatVdiVetDTO.length);

			if(contratPedagogiqueResultatVdiVetDTO!=null)
			{
				if("A".equals(source) && contratPedagogiqueResultatVdiVetDTO.length>max)
				{
					int compteur=max-1;
					int diff=contratPedagogiqueResultatVdiVetDTO.length-max;
					for(int i=contratPedagogiqueResultatVdiVetDTO.length-1;i>=0;i--)
					{
						if(diff<=i)
						{
							if (logger.isDebugEnabled())
								logger.debug("########## i ########### ----->"+i);
							tab[compteur]=contratPedagogiqueResultatVdiVetDTO[i];
						}
						if (logger.isDebugEnabled())
							logger.debug("########## compteur ########### ----->"+compteur);
						compteur--;
					}
					contratPedagogiqueResultatVdiVetDTO=tab;
				}
			}
		}
		catch (WebBaseException a) {
			logger.error(a);
			if (logger.isDebugEnabled())
				logger.debug("contratPedagogiqueResultatVdiVetDTO.length -----> NULL (Erreur !!!)");
			contratPedagogiqueResultatVdiVetDTO=null;
		}
		catch (Exception e) {
			logger.error(e);
			contratPedagogiqueResultatVdiVetDTO=null;

		}
		TrResultatVdiVetDTO trResultatVdiVetDTO;
		List<ResultatEtape> listResultatEtape = new ArrayList<ResultatEtape>();
		List<ResultatSession> listResultatSession;
		ResultatSession r;

		int nb = 0;

		if(contratPedagogiqueResultatVdiVetDTO!=null)
		{
			for(int i=contratPedagogiqueResultatVdiVetDTO.length-1;i>=0;i--)
			{
				EtapeResVdiVetDTO2[] etapeResVdiVetDTO = contratPedagogiqueResultatVdiVetDTO[i].getEtapes();
				ResultatVdiDTO[] resultatVdiDTO = contratPedagogiqueResultatVdiVetDTO[i].getResultatVdi();

				if (logger.isDebugEnabled()) {
					logger.debug("Diplome --> " + resultatVdiDTO);
					logger.debug("etapeResVdiVetDTO.length -----> " + etapeResVdiVetDTO.length);
				}

				for(int j=0;j<etapeResVdiVetDTO.length;j++)
				{
					if (logger.isDebugEnabled())
					{
						logger.debug("année --> " + etapeResVdiVetDTO[j].getCodAnu() + "/" + (Integer.parseInt(etapeResVdiVetDTO[j].getCodAnu())+1));
						logger.debug("etape --> " + etapeResVdiVetDTO[j].getEtape().getLibEtp());
						logger.debug("etapeResVdiVetDTO[j].getCodTypIpe() --> " + etapeResVdiVetDTO[j].getCodTypIpe());
					}

					ResultatVetDTO[] resultatVetDTO = etapeResVdiVetDTO[j].getResultatVet();
					listResultatSession = new ArrayList<ResultatSession>();

					/*Modification apporté par le apo-webservicesclient470lba (Attribut supplémentaire "E" comme en cours)
					* 			contratPedagogiqueResultatVdiVetDTO = pedagogiqueMetierServiceInterface.recupererContratPedagogiqueResultatVdiVet_v2(supannEtuId, "toutes", "Apogee", "AET", "toutes", null, "E");*/
					if(resultatVetDTO != null && nb<=max)
					{
						if (logger.isDebugEnabled())
							logger.debug("Etape non diplomante");

						for(int k=0;k<resultatVetDTO.length;k++)
						{
							nb++;
							r = new ResultatSession();

							if(resultatVetDTO[k].getSession() != null)
							{
								if (logger.isDebugEnabled()) {
									logger.debug("session --> " + resultatVetDTO[k].getSession().getLibSes());
									logger.debug("Note au version etape --> " + resultatVetDTO[k].getNotVet());
								}
								r.setLibSession(resultatVetDTO[k].getSession().getLibSes());
							}
							else
								r.setLibSession("");

							if(resultatVetDTO[k].getTypResultat() != null && "T".equals(resultatVetDTO[k].getEtatDelib().getCodEtaAvc()))
							{
								if (logger.isDebugEnabled()) {
									logger.debug("resultat --> " + resultatVetDTO[k].getTypResultat().getLibTre());
									logger.debug("Etat deliberation TTTT --> " + resultatVetDTO[k].getEtatDelib().getCodEtaAvc());
									logger.debug("Nature Resultat TTTTT --> " + resultatVetDTO[k].getNatureResultat().getCodAdm());
								}
								r.setResultat(resultatVetDTO[k].getTypResultat().getLibTre());
							}
							else if(resultatVetDTO[k].getTypResultat() != null && !"T".equals(resultatVetDTO[k].getEtatDelib().getCodEtaAvc()))
							{
								if (logger.isDebugEnabled()) {
									logger.debug("resultat --> " + resultatVetDTO[k].getTypResultat().getLibTre());
									logger.debug("Etat deliberation --> " + resultatVetDTO[k].getEtatDelib().getCodEtaAvc());
									logger.debug("Nature Resultat --> " + resultatVetDTO[k].getNatureResultat().getCodAdm());
									logger.debug("En attente de resultat ETAPE --> " +resultatVetDTO[k].getSession().getLibSes());
								}
								r.setResultat("Non dispo");
							}
							else
								r.setResultat("");

							if(resultatVetDTO[k].getMention() != null && "T".equals(resultatVetDTO[k].getEtatDelib().getCodEtaAvc()))
							{
								if (logger.isDebugEnabled()) {
									logger.debug("mention --> " + resultatVetDTO[k].getMention().getCodMen());
									logger.debug("mention --> " + resultatVetDTO[k].getMention().getLibMen());
								}
								r.setMention(resultatVetDTO[k].getMention().getCodMen());
								//r.setMention(resultatVetDTO[k].getMention().getLibMen());
							}
							else
								r.setMention("");

							listResultatSession.add(r);
						}
						if(resultatVetDTO==null ||resultatVetDTO.length!=2)
						{
							ResultatSession tmp = new ResultatSession();

							tmp.setLibSession("");
							tmp.setResultat("");
							listResultatSession.add(tmp);
						}
					}
					else if(resultatVdiDTO != null && "O".equals(etapeResVdiVetDTO[j].getEtape().getTemDipVet()) && nb<=max)
					{
						r = new ResultatSession();
						if (logger.isDebugEnabled()) {
							logger.debug("Resulats du diplome");
						}
						for(int l=0;l<resultatVdiDTO.length;l++)
						{
							nb++;
							if(resultatVdiDTO[l].getSession() != null)
							{
								if (logger.isDebugEnabled()) {
									logger.debug("session --> " + resultatVdiDTO[l].getSession().getLibSes());
									logger.debug("Note au version de diplome --> " + resultatVdiDTO[l].getNotVdi());
								}
								//r.setCodeSession(Integer.parseInt(resultatVdiDTO[l].getSession().getCodSes()));
								r.setLibSession(resultatVdiDTO[l].getSession().getLibSes());
							}
							else
								r.setLibSession("");

							if(resultatVdiDTO[l].getTypResultat() != null && "T".equals(resultatVdiDTO[l].getEtatDelib().getCodEtaAvc()))
							{
								if (logger.isDebugEnabled()) {
									logger.debug("resultat --> " + resultatVdiDTO[l].getTypResultat().getLibTre());
									logger.debug("Etat délibération TTTTT --> " + resultatVdiDTO[l].getEtatDelib().getCodEtaAvc());
									logger.debug("Nature Résultat TTTTT --> " + resultatVdiDTO[l].getNatureResultat().getCodAdm());
								}
								r.setResultat(resultatVdiDTO[l].getTypResultat().getLibTre());
							}
							else if(resultatVdiDTO[l].getTypResultat() != null && !"T".equals(resultatVdiDTO[l].getEtatDelib().getCodEtaAvc()))
							{
								if (logger.isDebugEnabled()) {
									logger.debug("resultat --> " + resultatVdiDTO[l].getTypResultat().getLibTre());
									logger.debug("Etat délibération --> " + resultatVdiDTO[l].getEtatDelib().getCodEtaAvc());
									logger.debug("Nature Résultat --> " + resultatVdiDTO[l].getNatureResultat().getCodAdm());
									logger.debug("En attente de résultat DIPLOME --> " + resultatVdiDTO[l].getSession().getLibSes());
								}
								r.setResultat("Non dispo");
							}
							else
								r.setResultat("");

							if(resultatVdiDTO[l].getMention() != null && "T".equals(resultatVdiDTO[l].getEtatDelib().getCodEtaAvc()))
							{
								if (logger.isDebugEnabled()) {
									logger.debug("mention --> " + resultatVdiDTO[l].getMention().getCodMen());
									logger.debug("mention --> " + resultatVdiDTO[l].getMention().getLibMen());
								}
								r.setMention(resultatVdiDTO[l].getMention().getCodMen());
							}
							else
								r.setMention("");

							listResultatSession.add(r);
						}
						if(resultatVdiDTO==null || resultatVdiDTO.length!=2)
						{
							ResultatSession tmp = new ResultatSession();
							tmp.setLibSession("");
							tmp.setResultat("");
							listResultatSession.add(tmp);
						}
					}
					else
					{
						if (logger.isDebugEnabled()) {
							logger.debug("Aucun resultat ou aucun diplome !!!");
						}
						r = new ResultatSession();
						//r.setCodeSession(0);
						r.setLibSession("");
						r.setResultat("");
						r.setMention("");
						listResultatSession.add(r);
						r = new ResultatSession();
						r.setLibSession("");
						r.setResultat("");
						r.setMention("");
						listResultatSession.add(r);
					}
//					if(etapeResVdiVetDTO[j].getCodEtaIae()!=null && etapeResVdiVetDTO[j].getCodEtaIae().equals("E") && nb<=max)
					if(nb<=max)
					{
						listResultatEtape.add(new ResultatEtape(etapeResVdiVetDTO[j].getCodAnu() + "/" + (Integer.parseInt(etapeResVdiVetDTO[j].getCodAnu())+1),
								etapeResVdiVetDTO[j].getEtape().getLibEtp(),
								listResultatSession));
					}

				}

			}
			trResultatVdiVetDTO = new TrResultatVdiVetDTO();
			trResultatVdiVetDTO.setEtapes(listResultatEtape);
			return trResultatVdiVetDTO;
		}
		else
		{
			trResultatVdiVetDTO = new TrResultatVdiVetDTO();
			trResultatVdiVetDTO.setEtapes(listResultatEtape);
			return trResultatVdiVetDTO;
		}
	}

	@Override
	public IndOpi getInfosOpi(String numeroEtudiant) {
		if (logger.isDebugEnabled()) {
			logger.debug("getInfosOpi(String numeroEtudiant)");
		}
		VoeuxIns voeuxIns = new VoeuxIns();
		IndOpi indOpi = new IndOpi();
		EtudiantMetierServiceInterfaceProxy etudiantMetierService = new EtudiantMetierServiceInterfaceProxy();
		// Recuperation des infos de l'etudiant dans Apogee	
		InfoAdmEtuDTO2 infoAdmEtuDTO;
		try {
			infoAdmEtuDTO = etudiantMetierService.recupererInfosAdmEtu_v2(numeroEtudiant);
			/*OPI*/
			/*IND_OPI*/
			indOpi.setCodPayNat(infoAdmEtuDTO.getNationaliteDTO().getCodeNationalite());
			indOpi.setCodEtb(infoAdmEtuDTO.getEtbPremiereInscUniv().getCodeEtb());
			indOpi.setCodNneIndOpi(infoAdmEtuDTO.getNumeroINE());
			indOpi.setCodCleNneIndOpi("");
			indOpi.setDateNaiIndOpi(infoAdmEtuDTO.getDateNaissance().getTime());
			indOpi.setTemDateNaiRelOpi(infoAdmEtuDTO.getTemoinDateNaissEstimee());
			indOpi.setDaaEntEtbOpi(infoAdmEtuDTO.getAnneePremiereInscUniv());
			indOpi.setLibNomPatIndOpi(infoAdmEtuDTO.getNomPatronymique());
			indOpi.setLibNomUsuIndOpi(infoAdmEtuDTO.getNomUsuel());
			indOpi.setLibPr1IndOpi(infoAdmEtuDTO.getPrenom1());
			indOpi.setLibPr2IndOpi(infoAdmEtuDTO.getPrenom2());
			indOpi.setLibPr3IndOpi("");
			indOpi.setLibVilNaiEtuOpi(infoAdmEtuDTO.getLibVilleNaissance());
			/*Code pays ou département de naissance*/
			if(infoAdmEtuDTO.getPaysNaissance().getCodPay()!= null && !"100".equals(infoAdmEtuDTO.getPaysNaissance().getCodPay()))
			{
				indOpi.setCodDepPayNai(infoAdmEtuDTO.getPaysNaissance().getCodPay());
				indOpi.setCodTypDepPayNai("P");
			}
			else if(infoAdmEtuDTO.getDepartementNaissance().getCodeDept()!=null)
			{
				indOpi.setCodDepPayNai(infoAdmEtuDTO.getDepartementNaissance().getCodeDept());
				indOpi.setCodTypDepPayNai("D");
			}
			else
			{
				indOpi.setCodDepPayNai("");
				indOpi.setCodTypDepPayNai("");
			}
			indOpi.setDaaEnsSupOpi(infoAdmEtuDTO.getAnneePremiereInscEnsSup());
			indOpi.setCodSexEtuOpi(infoAdmEtuDTO.getSexe());
			indOpi.setDaaEtrSup(infoAdmEtuDTO.getAnneePremiereInscEtr());

			/*OPI_BAC*/
			IndBacDTO[] listeBacs = infoAdmEtuDTO.getListeBacs();
			indOpi.setCodBac(listeBacs[0].getCodBac());
			indOpi.setCodEtbBac(listeBacs[0].getEtbBac().getCodeEtb());
			indOpi.setCodDep(listeBacs[0].getDepartementBac().getCodeDept());
			indOpi.setCodMnb(listeBacs[0].getMentionBac().getCodMention());
			indOpi.setDaabacObtOba(listeBacs[0].getAnneeObtentionBac());
			indOpi.setCodTpe(listeBacs[0].getTypeEtbBac().getCodTypeEtb());
			indOpi.setVoeux(voeuxIns);
			return indOpi;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public List<String> getForcerBlocageListSplit() {
		return forcerBlocageListSplit;
	}

	public void setForcerBlocageListSplit(List<String> forcerBlocageListSplit) {
		this.forcerBlocageListSplit = forcerBlocageListSplit;
	}

	@Cacheable(cacheName = "getOffreDeFormation")
	public List<OffreDeFormationsDTO> getOffreDeFormation(String rne, Integer annee) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("public List<OffreDeFormationsDTO> getOffreDeFormation(String rne, Integer annee)-->"+rne+"-----"+annee);
		}
		List<OffreDeFormationsDTO> odfs = new ArrayList<OffreDeFormationsDTO>();
		OffreFormationMetierServiceInterfaceProxy offreFormation = new OffreFormationMetierServiceInterfaceProxy();
		// Recuperation des infos de l'etudiant dans Apogee	
		DiplomeDTO3[] diplomeDTO3;
		VersionEtapeDTO2[] versionEtapeDTO2;
		try
		{
			SECritereDTO2 param = new SECritereDTO2();
			param.setCodAnu(annee.toString());
			param.setTemOuvertRecrutement("O");
			param.setCodEtp("tous");
			param.setCodVrsVet("tous");
			param.setCodDip("tous");
			param.setCodVrsVdi("tous");
			param.setCodElp("aucun");
			diplomeDTO3 = offreFormation.recupererSE_v3(param);

//			offreFormation.recupererSE_v3(arg0)

			for(DiplomeDTO3 ld : diplomeDTO3)
			{
				VersionDiplomeDTO3[] versionDiplomeDTO3 =ld.getListVersionDiplome();
				if (logger.isDebugEnabled()) {
					logger.debug("CodTypDip --> "+ ld.getTypeDiplome().getCodTypDip());
					logger.debug("LibTypDip --> "+ ld.getTypeDiplome().getLibTypDip());
					logger.debug("LibDip --> "+ ld.getLibDip());
					logger.debug("Type diplome --> "+ ld.getTypeDiplome().getLibTypDip());
					logger.debug("Cycle --> "+ ld.getCycle().getLibCycle());
					logger.debug("Nature --> "+ ld.getNatureDiplome().getLibNatureDip());
					logger.debug("ld.getTypeDiplome().getLibTypDip() --> "+ ld.getTypeDiplome().getLibTypDip());
				}

				for(VersionDiplomeDTO3 lvd : versionDiplomeDTO3)
				{
					if (logger.isDebugEnabled()) {
						logger.debug("lvd.getCodCursusLmd --> "+lvd.getCodCursusLmd());
						logger.debug("lvd.getLibWebVdi() --> "+lvd.getLibWebVdi());
					}
					EtapeDTO3[] etapeDTO3 = lvd.getOffreFormation().getListEtape();
					for(EtapeDTO3 le : etapeDTO3)
					{
						ComposanteCentreGestionDTO[] ccgOri = le.getListComposanteCentreGestion();

						for(int i=0;i<ccgOri.length;i++)
						{
							if (logger.isDebugEnabled()) {
								logger.debug("###################################################### DEBUT #####################################################################################");
								logger.debug("Alban --> le.getListComposanteCentreGestion()[i].getCodCentreGestion()-->"+ccgOri[i].getCodCentreGestion());
								logger.debug("Alban --> ccgOri[i].getLibCentreGestion()-->"+ccgOri[i].getLibCentreGestion());
							}
							VersionEtapeDTO3[] versionEtapeDTO21=le.getListVersionEtape();

							for(VersionEtapeDTO3 ve : versionEtapeDTO21)
							{
								if(ccgOri[i].getCodComposante().equals(ve.getComposante().getCodComposante()))
								{
									if (logger.isDebugEnabled())
										logger.debug("ve.getLibWebVet() --> "+ve.getLibWebVet());

									Integer codeNiveau = ve.getCodSisDaaMin();
									String libNiveau;

									if(codeNiveau==1)
										libNiveau=codeNiveau+"er année";
									else
										libNiveau=codeNiveau+"ème année";

									String codComp = ve.getComposante().getCodComposante();
									String libComp = ve.getComposante().getLibComposante();

									if (logger.isDebugEnabled()) {
										logger.debug("Alban --> codComp-->"+codComp);
										logger.debug("Alban --> libComp-->"+libComp);
									}
									odfs.add(new OffreDeFormationsDTO(rne,
											annee,
											ld.getTypeDiplome().getCodTypDip(),
											ld.getTypeDiplome().getLibTypDip(),
											ld.getCodDip(),
											lvd.getCodVrsVdi(),
											le.getCodEtp(),
											ve.getCodVrsVet().toString(),
											lvd.getLibWebVdi(),
											ve.getLibWebVet(),
											codComp,
											libComp,
											ccgOri[i].getCodCentreGestion(),
											ccgOri[i].getLibCentreGestion(),
											codeNiveau,
											libNiveau,
											"oui",
											"oui"));

								}
							}
						}
						if (logger.isDebugEnabled())
							logger.debug("################################################################# FIN ##########################################################################");
					}
				}
			}
			return odfs;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public List<OffreDeFormationsDTO> getOffreDeFormation2(Integer annee) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled())
			logger.debug("getOffreDeFormation2(Integer annee)");
		return null;
	}

	@Override
	public List<IndOpi> synchroOpi(List<IndOpi> listeOpis) // APPEL DU WS EN MODE AUTHENTIFIE
	{
		List<IndOpi> listeErreurs = new ArrayList<IndOpi>();
		if (logger.isDebugEnabled()) {
			logger.debug("public void synchroOpi(List<IndOpi> listeOpis)");
			logger.debug("########################### APPEL DU WS EN MODE AUTHENTIFIE ##################################");
			logger.debug("#########################################\n");
			logger.debug("-->this.user -->"+this.user+"<--");
			logger.debug("-->this.password -->"+this.password+"<--");
			logger.debug("############################################################################################\n");
		}

		//		OpiMetierSoapBindingStub opiMetierServiceInterface2 = (OpiMetierSoapBindingStub) WSUtils.getService(WSUtils.OPI_SERVICE_NAME, this.user, this.password);
		OpiMetierSoapBindingStub opiMetierServiceInterface3 = (OpiMetierSoapBindingStub) WSUtils.getService(WSUtils.OPI_SERVICE_NAME, this.user, this.password);

		for(IndOpi opi : listeOpis)
		{
			//OpiMetierServiceInterface opiMetierServiceInterface = new OpiMetierServiceInterfaceProxy();
			//			DonneesOpiDTO2 donneesOpiDTO = new DonneesOpiDTO2();
//			DonneesOpiDTO5 donneesOpiDTO = new DonneesOpiDTO5();
			DonneesOpiDTO9 donneesOpiDTO = new DonneesOpiDTO9();

			/*Initialisation de l'objet DonneesOpiDTO2 d'apogee a partir de l'objet OPI de esup-transferts*/

			/*#################################################*/ 
			/* MAJOpiAdresseDTO - Adresse Fixe*/
			/*#################################################*/
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJOpiAdresseDTO - Adresse Fixe #################################################");
				logger.debug("opi.getCodBdi() --> "+opi.getCodBdi());
				logger.debug("opi.getCodCom() --> "+opi.getCodCom());
				logger.debug("opi.getCodPay() --> "+opi.getCodPay());
				logger.debug("opi.getLibAd1() --> "+opi.getLibAd1());
				logger.debug("opi.getLibAd2() --> "+opi.getLibAd2());
				logger.debug("opi.getLibAd3() --> "+opi.getLibAd3());
				logger.debug("opi.getLibAde() --> "+opi.getLibAde());
				logger.debug("opi.getNumTel() --> "+opi.getNumTel());
				logger.debug("#######################################################################################################################");
			}
			MAJOpiAdresseDTO opiAdresseFixeDTO = new MAJOpiAdresseDTO();
			opiAdresseFixeDTO.setCodBdi(opi.getCodBdi());
			opiAdresseFixeDTO.setCodCom(opi.getCodCom());
			opiAdresseFixeDTO.setCodPay(opi.getCodPay());
			opiAdresseFixeDTO.setLib1(opi.getLibAd1());
			opiAdresseFixeDTO.setLib2(opi.getLibAd2());
			opiAdresseFixeDTO.setLib3(opi.getLibAd3());
			opiAdresseFixeDTO.setLibAde(opi.getLibAde());
			opiAdresseFixeDTO.setNumTel(opi.getNumTel());
			donneesOpiDTO.setAdresseFixe(opiAdresseFixeDTO);

			/*#################################################*/ 
			/* MAJOpiAdresseDTO - Adresse Annuelle*/
			/*#################################################*/
			MAJOpiAdresseDTO opiAdresseAnnuelleDTO = new MAJOpiAdresseDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJOpiAdresseDTO - Adresse Annuelle #############################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			donneesOpiDTO.setAdresseAnnuelle(opiAdresseAnnuelleDTO);

			/*#################################################*/ 
			/* MAJOpiIndDTO*/
			/*#################################################*/
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJOpiIndDTO #################################################");
				logger.debug("##################################### MAJEtatCivilDTO #################################################");
				logger.debug("!!! OBLIGATOIRE !!! opi.getNumeroOpi() --> "+opi.getNumeroOpi());
				logger.debug("opi.getCodCleNneIndOpi() --> "+opi.getCodCleNneIndOpi());
				logger.debug("opi.getCodNneIndOpi() --> "+opi.getCodNneIndOpi());
				logger.debug("opi.getCodSexEtuOpi() --> "+opi.getCodSexEtuOpi());
				logger.debug("!!! OBLIGATOIRE !!! opi.getLibNomPatIndOpi() --> "+opi.getLibNomPatIndOpi());
				logger.debug("opi.getLibNomUsuIndOpi() --> "+opi.getLibNomUsuIndOpi());
				logger.debug("!!! OBLIGATOIRE !!! opi.getLibPr1IndOpi() --> "+opi.getLibPr1IndOpi());
				logger.debug("opi.getLibPr2IndOpi() --> "+opi.getLibPr2IndOpi());
				logger.debug("opi.getLibPr3IndOpi() --> "+opi.getLibPr3IndOpi());
				logger.debug("##################################### MAJEtatCivilDTO #################################################");
				logger.debug("opi.getCodDepPayNai() --> "+opi.getCodDepPayNai());
				logger.debug("opi.getCodPayNat() --> "+opi.getCodPayNat());
				logger.debug("opi.getCodTypDepPayNai() --> "+opi.getCodTypDepPayNai());
				logger.debug("!!! OBLIGATOIRE !!! opi.getDateNaiIndOpi().getTime() --> "+dateFormat.format(opi.getDateNaiIndOpi().getTime()));
				logger.debug("opi.getLibVilNaiEtuOpi() --> "+opi.getLibVilNaiEtuOpi());
				logger.debug("!!! OBLIGATOIRE !!! opi.getTemDateNaiRelOpi() --> "+opi.getTemDateNaiRelOpi());
				logger.debug("####################################################################################################");
			}
			//			MAJOpiIndDTO2 indDTO = new MAJOpiIndDTO2();
			MAJOpiIndDTO6 indDTO = new MAJOpiIndDTO6();

			indDTO.setCodEtuOpi(opi.getCodEtuLpa());
			indDTO.setCodOpiIntEpo(opi.getNumeroOpi()); // !!! OBLIGATOIRE !!!
			/*#################################################*/ 
			/* MAJEtatCivilDTO */
			/*#################################################*/
			MAJEtatCivilDTO2 etatCivilDTO = new MAJEtatCivilDTO2();
//			etatCivilDTO.setCodCleNneIndOpi(opi.getCodCleNneIndOpi());
			etatCivilDTO.setCodNneIndOpi(opi.getCodNneIndOpi());
			etatCivilDTO.setCodSexEtuOpi(opi.getCodSexEtuOpi());
			etatCivilDTO.setLibNomPatIndOpi(opi.getLibNomPatIndOpi()); // !!! OBLIGATOIRE !!!
			etatCivilDTO.setLibNomUsuIndOpi(opi.getLibNomUsuIndOpi());
			etatCivilDTO.setLibPr1IndOpi(opi.getLibPr1IndOpi()); // !!! OBLIGATOIRE !!!
			etatCivilDTO.setLibPr2IndOpi(opi.getLibPr2IndOpi());
			etatCivilDTO.setLibPr3IndOpi(opi.getLibPr3IndOpi());
			indDTO.setEtatCivil(etatCivilDTO);
			/*#################################################*/ 
			/* MAJDonneesNaissanceDTO */
			/*#################################################*/
			MAJDonneesNaissanceDTO2 donneesNaissanceDTO = new MAJDonneesNaissanceDTO2();
			donneesNaissanceDTO.setCodDepPayNai(opi.getCodDepPayNai());
			donneesNaissanceDTO.setCodPayNat(opi.getCodPayNat());
			donneesNaissanceDTO.setCodTypDepPayNai(opi.getCodTypDepPayNai());
			/*Depuis le module gestionnaire via les WS*/
			String str[]=dateFormat.format(opi.getDateNaiIndOpi().getTime()).split("-");
			/*Si date format dd-mm-yyyy*/
			String date = str[0]+str[1]+str[2];
			donneesNaissanceDTO.setDateNaiIndOpi(date); // !!! OBLIGATOIRE !!!
			donneesNaissanceDTO.setLibVilNaiEtuOpi(opi.getLibVilNaiEtuOpi());
			donneesNaissanceDTO.setTemDateNaiRelOpi(opi.getTemDateNaiRelOpi());	// !!! OBLIGATOIRE !!!				
			indDTO.setDonneesNaissance(donneesNaissanceDTO);

			/*#################################################*/ 
			/* MAJPremiereInscriptionDTO */
			/*#################################################*/
			MAJPremiereInscriptionDTO premiereInscriptionDTO = new MAJPremiereInscriptionDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJPremiereInscriptionDTO #################################################");
				logger.debug("opi.getCodEtb() --> "+opi.getCodEtb());
				logger.debug("opi.getDaaEnsSupOpi() --> "+opi.getDaaEnsSupOpi());
				logger.debug("opi.getDaaEntEtbOpi() --> "+opi.getDaaEntEtbOpi());
				logger.debug("opi.getDaaEtrSup() --> "+opi.getDaaEtrSup());
				logger.debug("#######################################################################################################################");
			}
			premiereInscriptionDTO.setCodEtb(opi.getCodEtb());
			premiereInscriptionDTO.setDaaEnsSupOpi(opi.getDaaEnsSupOpi());
			premiereInscriptionDTO.setDaaEntEtbOpi(opi.getDaaEntEtbOpi());
			//				premiereInscriptionDTO.setDaaEtbOpi(arg0); // !!! PAS RECUPERE !!!
			premiereInscriptionDTO.setDaaEtrSup(opi.getDaaEtrSup());
			indDTO.setPremiereInscription(premiereInscriptionDTO);		

			/*#################################################*/ 
			/* MAJDonneesPersonnellesDTO2 */
			/*#################################################*/
			//			MAJDonneesPersonnellesDTO2 donneesPersonnellesDTO = new MAJDonneesPersonnellesDTO2();
			MAJDonneesPersonnellesDTO3 donneesPersonnellesDTO = new MAJDonneesPersonnellesDTO3();
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJDonneesPersonnellesDTO2 #################################################");
				logger.debug("opi.getAdrMailOpi() --> "+opi.getAdrMailOpi());
				logger.debug("opi.getNumTelPorOpi() --> "+opi.getNumTelPorOpi());
				logger.debug("#######################################################################################################################");
			}
			donneesPersonnellesDTO.setAdrMailOpi(opi.getAdrMailOpi());
			//					donneesPersonnellesDTO.setCodFam(arg0); // !!! PAS RECUPERE !!!
			//					donneesPersonnellesDTO.setCodFam(arg0); // !!! PAS RECUPERE !!!
			//					donneesPersonnellesDTO.setCodSim(arg0); // !!! PAS RECUPERE !!!
			//					donneesPersonnellesDTO.setCodThpOpi(arg0); // !!! PAS RECUPERE !!!
			//					donneesPersonnellesDTO.setCodThbOpi(arg0); // !!! PAS RECUPERE !!!
			//					donneesPersonnellesDTO.setDaaLbtIndOpi(arg0); // !!! PAS RECUPERE !!!
			//					donneesPersonnellesDTO.setDmmLbtIndOpi(arg0); // !!! PAS RECUPERE !!!
			donneesPersonnellesDTO.setNumTelPorOpi(opi.getNumTelPorOpi());
			indDTO.setDonneesPersonnelles(donneesPersonnellesDTO);

			/*#################################################*/ 
			/* MAJPrgEchangeDTO */
			/*#################################################*/
			MAJPrgEchangeDTO prgEchangeDTO = new MAJPrgEchangeDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJPrgEchangeDTO #################################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			indDTO.setPrgEchange(prgEchangeDTO);

			/*#################################################*/ 
			/* MAJDernierEtbFrequenteDTO */
			/*#################################################*/
			MAJDernierEtbFrequenteDTO dernierEtbFrequenteDTO = new MAJDernierEtbFrequenteDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJDernierEtbFrequenteDTO #######################################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			indDTO.setDernierEtbFrequente(dernierEtbFrequenteDTO);		

			/*#################################################*/ 
			/* MAJSituationAnnPreDTO */
			/*#################################################*/
			MAJSituationAnnPreDTO situationAnnPreDTO = new MAJSituationAnnPreDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJSituationAnnPreDTO ###########################################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			indDTO.setSituationAnnPre(situationAnnPreDTO);				

			/*#################################################*/ 
			/* MAJDernierDiplObtDTO */
			/*#################################################*/
			MAJDernierDiplObtDTO dernierDiplObtDTO = new MAJDernierDiplObtDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJDernierDiplObtDTO ############################################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			indDTO.setDernierDiplObt(dernierDiplObtDTO);		

			/*#################################################*/ 
			/* MAJInscriptionParalleleDTO */
			/*#################################################*/
			MAJInscriptionParalleleDTO2 inscriptionParalleleDTO = new MAJInscriptionParalleleDTO2();
			if (logger.isDebugEnabled()) {
				logger.debug("##################################### MAJInscriptionParalleleDTO ######################################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			indDTO.setInscriptionParallele(inscriptionParalleleDTO);
			donneesOpiDTO.setIndividu(indDTO);

			/*#################################################*/ 
			/* MAJOpiDacDTO */
			/*#################################################*/
			MAJOpiDacDTO opiDacDTO = new MAJOpiDacDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("################################################### MAJOpiDacDTO ######################################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			donneesOpiDTO.setDac(opiDacDTO);					

			/*#################################################*/ 
			/* MAJOpiBacDTO */
			/*#################################################*/
			MAJOpiBacDTO opiBacDTO = new MAJOpiBacDTO();
			opiBacDTO.setCodBac(opi.getCodBac());
			opiBacDTO.setCodEtb(opi.getCodEtbBac());
			opiBacDTO.setCodDep(opi.getCodDep());
			opiBacDTO.setCodMention(opi.getCodMnb());
			opiBacDTO.setDaaObtBacOba(opi.getDaabacObtOba());
			if (logger.isDebugEnabled()) {
				logger.debug("################################################### MAJOpiBacDTO ######################################################");
				logger.debug("OBJET VIDE");
				//				logger.debug("!!! OBLIGATOIRE !!! opi.getCodBac() --> "+opi.getCodBac());
				logger.debug("#######################################################################################################################");
			}
			donneesOpiDTO.setBac(opiBacDTO);

			/*#################################################*/ 
			/* MAJOpiVoeuDTO */
			/*#################################################*/
			MAJOpiVoeuDTO3 tabVoeux[] = new MAJOpiVoeuDTO3[1];
			//			MAJOpiVoeuDTO tabVoeux[] = new MAJOpiV
			MAJOpiVoeuDTO3 opiVoeuDTO = new MAJOpiVoeuDTO3();
			if (logger.isDebugEnabled()) {
				logger.debug("################################################### MAJOpiVoeuDTO #####################################################");
				logger.debug("!!! OBLIGATOIRE !!! opi.getVoeux().getCodCge() --> "+opi.getVoeux().getCodCge());
				logger.debug("opi.getVoeux().getCodCmp() --> "+opi.getVoeux().getCodCmp());
				logger.debug("!!! OBLIGATOIRE !!! opi.getVoeux().getCodDemDos() --> "+opi.getVoeux().getCodDemDos());
				logger.debug("!!! OBLIGATOIRE !!! opi.getVoeux().getCodDecVeu() --> "+opi.getVoeux().getCodDecVeu());
				logger.debug("opi.getVoeux().getCodDip() --> "+opi.getVoeux().getCodDip());
				logger.debug("!!! OBLIGATOIRE !!! opi.getVoeux().getCodEtp() --> "+opi.getVoeux().getCodEtp());
				logger.debug("!!! OBLIGATOIRE !!! Integer.parseInt(opi.getVoeux().getCodVrsVet()) --> "+Integer.parseInt(opi.getVoeux().getCodVrsVet()));
				logger.debug("opi.getVoeux().getCodVrsVdi() --> "+opi.getVoeux().getCodVrsVdi());
				logger.debug("!!! OBLIGATOIRE !!! Integer.parseInt(opi.getVoeux().getNumCls()) --> "+Integer.parseInt(opi.getVoeux().getNumCls()));
				logger.debug("#######################################################################################################################");
			}
			//				opiVoeuDTO.setCodAttDec(arg0); // !!! PAS RECUPERE !!!
			opiVoeuDTO.setCodCge(opi.getVoeux().getCodCge()); // !!! OBLIGATOIRE !!!
			opiVoeuDTO.setCodCmp(opi.getVoeux().getCodCmp());
			//				opiVoeuDTO.setCodDecVeu(arg0); // !!! PAS RECUPERE !!!
			opiVoeuDTO.setCodDemDos(opi.getVoeux().getCodDemDos()); // !!! OBLIGATOIRE !!!
			opiVoeuDTO.setCodDecVeu(opi.getVoeux().getCodDecVeu());
//			opiVoeuDTO.setCodDecVeu("D");
			opiVoeuDTO.setCodDip(opi.getVoeux().getCodDip());
			opiVoeuDTO.setCodEtp(opi.getVoeux().getCodEtp()); // !!! OBLIGATOIRE !!!
			//				opiVoeuDTO.setCodMfo(arg0); // !!! PAS RECUPERE !!!
			//				opiVoeuDTO.setCodSpe1Opi(arg0); // !!! PAS RECUPERE !!!
			//				opiVoeuDTO.setCodSpe2Opi(arg0); // !!! PAS RECUPERE !!!
			//				opiVoeuDTO.setCodSpe3Opi(arg0); // !!! PAS RECUPERE !!!
			//				opiVoeuDTO.setCodTyd(arg0); // !!! PAS RECUPERE !!!
			opiVoeuDTO.setCodVrsVet(Integer.parseInt(opi.getVoeux().getCodVrsVet())); // !!! OBLIGATOIRE !!!
			opiVoeuDTO.setCodVrsVdi(opi.getVoeux().getCodVrsVdi());
			opiVoeuDTO.setNumCls(Integer.parseInt(opi.getVoeux().getNumCls())); // !!! OBLIGATOIRE !!!


			/*#################################################*/
			/* MAJTitreAccesExterneDTO */
			/*#################################################*/
			MAJTitreAccesExterneDTO titreAccesExterneDTO = new MAJTitreAccesExterneDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("################################################### MAJTitreAccesExterneDTO ###########################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			opiVoeuDTO.setTitreAccesExterne(titreAccesExterneDTO);
			opiVoeuDTO.setLibCmtJur("TRANSFERTS");

			/*#################################################*/ 
			/* MAJConvocationDTO */
			/*#################################################*/
			MAJConvocationDTO convocationDTO = new MAJConvocationDTO();
			if (logger.isDebugEnabled()) {
				logger.debug("################################################### MAJConvocationDTO ###########################################");
				logger.debug("OBJET VIDE");
				logger.debug("#######################################################################################################################");
			}
			opiVoeuDTO.setConvocation(convocationDTO);
			tabVoeux[0]=opiVoeuDTO;
			donneesOpiDTO.setVoeux(tabVoeux);

			MAJOpiDacDTO majOpiDacDTO = new MAJOpiDacDTO();
			majOpiDacDTO.setCodNif(1);
			donneesOpiDTO.setDac(majOpiDacDTO);

//			logger.info("donneOpiDTO=" + donneesOpiDTO);
			//logger.info("tabVoeux=" + tabVoeux);

			/*APPEL DE LA METHODE DU WS APOGEE*/
			try {
				opiMetierServiceInterface3.mettreajourDonneesOpi_v9(donneesOpiDTO);
				// Traitement des exceptions
			}catch (WebBaseException _ex) {
				listeErreurs.add(opi);
				System.err.println("ApoWeb Exception levee de type " + _ex);
				System.err.println(_ex.getLastErrorMsg());
				logger.error(_ex);
			} catch (Exception _ex) {
				listeErreurs.add(opi);
				System.err.println("Java Exception levee de type " + _ex);
				logger.error(_ex);
			}
		}
		return listeErreurs;
	}

	public List<TrBac> recupererBacOuEquWS(String codeBac)
	{
		//		OffreFormationMetierServiceInterface offreFormation = new OffreFormationMetierServiceInterfaceProxy();	
		//		ScolariteMetierServiceInterface scolariteMetierService = (ScolariteMetierServiceInterface) new ScolariteMetierServiceInterfaceProxy();
		ScolariteMetierServiceInterfaceProxy scolariteMetierService = new ScolariteMetierServiceInterfaceProxy();
		try {
			List<TrBac> lBac = new ArrayList<TrBac>();
			BacOuEquDTO[] tabBacouEquiv = scolariteMetierService.recupererBacOuEquWS(codeBac, null);

			for(int i=0;i<tabBacouEquiv.length;i++)
				lBac.add(new TrBac(tabBacouEquiv[i].getCodBac(), tabBacouEquiv[i].getLibBac()));

			return lBac;
		} catch (WebBaseException e) {
			logger.error(e);
			return null;
		}
	}

	public List<PersonnelComposante> recupererComposante(String uid, String diplayName, String mail, String source, Integer annee)
	{
		ReferentielMetierServiceInterfaceProxy referentielMetierService = new ReferentielMetierServiceInterfaceProxy();
		ComposanteDTO3[] comp = referentielMetierService.recupererComposante_v2(null, null);

		if(comp.length!=0)
		{
			List<PersonnelComposante> pc = new ArrayList<PersonnelComposante>();
			pc.add(new PersonnelComposante(uid, codeComposanteInconnue, source, annee, diplayName, mail, "Inconnue",0,"oui","oui","oui","oui","oui","oui","non","non"));
			for(int i=0;i<comp.length;i++)
			{
				pc.add(new PersonnelComposante(uid, comp[i].getCodCmp(), source, annee, diplayName, mail, comp[i].getLibCmp(),0,"oui","oui","oui","oui","oui","oui","non","non"));
			}
			return pc;
		}
		else
			return null;
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

	@Override
	public Integer getAuthEtu(String ine, Date dateNaissance) {
		// appel au WS AMUE
		Integer retour;
		if (logger.isDebugEnabled()) {
			logger.debug("Je suis dans le WS AMUE - AUTH Apogee");
		}
		String ineSansCle = ine.substring(0, ine.length()-1);
		String cleIne = ine.substring(ine.length()-1, ine.length());

		if (logger.isDebugEnabled()) {
			logger.debug("ineSansCle --> "+ ineSansCle);
			logger.debug("cleIne --> "+ cleIne);
			logger.debug("dateNaissance --> "+ dateNaissance);
		}
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		EtudiantMetierServiceInterfaceProxy etudiantMetierService = new EtudiantMetierServiceInterfaceProxy();
		// Recuperation des infos de l'etudiant dans Apogee	
		InfoAdmEtuDTO2 infoAdmEtuDTO;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("ine --> " + ine);
			}
			IdentifiantsEtudiantDTO2 identifiantEtudiant =  etudiantMetierService.recupererIdentifiantsEtudiant_v2(null, null, ine, null, null, null, null, null, null);
			infoAdmEtuDTO = etudiantMetierService.recupererInfosAdmEtu_v2(identifiantEtudiant.getCodEtu().toString());

			if(dateFormat.format(dateNaissance).equals(dateFormat.format(infoAdmEtuDTO.getDateNaissance().getTime())))
			{
				if (logger.isDebugEnabled()) {
					logger.debug("Compare date OK");
				}
				retour=0;
			}
			else
			{
				if (logger.isDebugEnabled()) {
					logger.debug("Compare date FAUX !!!");
				}
				retour=1;
			}
			return retour;

		} catch (Exception e) {
			logger.error(e);
			retour=2;
			return retour;
		}
	}

	@Override
	public List<Composante> recupererListeComposantes(Integer annee, String source) {
		if (logger.isDebugEnabled())
			logger.debug("public List<Composante> recupererListeComposantes(Integer annee, String source)-->"+annee+"-----"+source);
		ReferentielMetierServiceInterfaceProxy referentielMetierService = new ReferentielMetierServiceInterfaceProxy();
		ComposanteDTO3[] comp = referentielMetierService.recupererComposante_v2(null, null);

		if(comp.length!=0)
		{
			List<Composante> pc = new ArrayList<Composante>();
			pc.add(new Composante(codeComposanteInconnue, source, annee, "Inconnue", "non"));
			for(int i=0;i<comp.length;i++)
			{
				pc.add(new Composante(comp[i].getCodCmp(), source, annee, comp[i].getLibCmp(),"non"));
			}
			return pc;
		}
		else
			return null;
	}

	@Override
	public List<CGE> recupererListeCGE(Integer annee, String source) {
		if (logger.isDebugEnabled())
			logger.debug("public List<CGE> recupererListeCGE(Integer annee, String source)-->"+annee+"-----"+source);
		ReferentielMetierServiceInterfaceProxy referentielMetierService = new ReferentielMetierServiceInterfaceProxy();
		CentreGestionDTO2[] cge = referentielMetierService.recupererCGE(null, null);

		if(cge.length!=0)
		{
			List<CGE> lCGE = new ArrayList<CGE>();
			lCGE.add(new CGE(codeComposanteInconnue, source, annee, "Inconnue", "non"));
			for(int i=0;i<cge.length;i++)
			{
				if (logger.isDebugEnabled())
					logger.debug("cge[i].getCodCge() ----- cge[i].getLibCge()-->"+cge[i].getCodCge()+"-----"+cge[i].getLibCge());
				lCGE.add(new CGE(cge[i].getCodCge(), source, annee, cge[i].getLibCge(),"non"));
			}
			return lCGE;
		}
		else
			return null;
	}

	@Override
	public List<EtudiantRef> recupererListeEtudiants(String myAnnee, String codeDiplome, String versionDiplome, String codeEtape, String versionEtape)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("===>Je suis dans le WS AMUE<===");
			logger.debug("public EtudiantRef recupererListeEtudiants(String myAnnee, String codeDiplome, String versionDiplome, String codeEtape, String versionEtape)===>"+myAnnee+"---"+codeDiplome+"---"+versionDiplome+"---"+codeEtape+"---"+versionEtape+"<===");
		}

		List<EtudiantRef> listeEtu = new ArrayList<EtudiantRef>();

		try{
			EtudiantMetierServiceInterfaceProxy etudiantMetierService = new EtudiantMetierServiceInterfaceProxy();

			EtudiantCritereDTO etuCritere  = new EtudiantCritereDTO();
			etuCritere.setAnnee(myAnnee);

			EtudiantCritereListeDTO[] etuCritereListeDiplome = new EtudiantCritereListeDTO[1];
			EtudiantCritereListeDTO[] etuCritereListeEtape = new EtudiantCritereListeDTO[1];

			EtudiantCritereListeDTO diplome = new EtudiantCritereListeDTO();
			diplome.setCode(codeDiplome);
			diplome.setListVersion(new String[]{versionDiplome});

			EtudiantCritereListeDTO etape = new EtudiantCritereListeDTO();
			etape.setCode(codeEtape);
			etape.setListVersion(new String[]{versionEtape});

			etuCritereListeDiplome[0]=diplome;
			etuCritereListeEtape[0]=etape;

			etuCritere.setListDiplomes(etuCritereListeDiplome);
			etuCritere.setListEtapes(etuCritereListeEtape);

			EtudiantDTO2[] listeEtuDto2 = etudiantMetierService.recupererListeEtudiants(etuCritere);

			for(EtudiantDTO2 etuDto2 : listeEtuDto2)
			{
				EtudiantRef etu = new EtudiantRef();
				etu.setNumeroEtudiant(etuDto2.getCodEtu());
				listeEtu.add(etu);
			}

		}catch (WebBaseException w)
		{
			logger.error(w);
			return null;
		}
		catch (Exception e)
		{
			logger.error(e);
			return null;
		}
		return listeEtu;
	}
}