/**
 * ESUP-Portail example Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain.beans;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The class that represent users.
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "getIndOpiByNneAndCleIneAndAnnee", query = "SELECT indOpi FROM IndOpi indOpi WHERE indOpi.codNneIndOpi = :nne AND indOpi.codCleNneIndOpi = :cleIne AND indOpi.annee = :annee"),
		@NamedQuery(name = "allIndOpiNonSynchroAndSource", query = "SELECT indOpi FROM IndOpi indOpi LEFT JOIN FETCH indOpi.voeux WHERE indOpi.annee = :annee AND indOpi.synchro=0 AND indOpi.source = :source"),
		@NamedQuery(name = "allIndOpiBySynchroAndSource", query = "SELECT indOpi FROM IndOpi indOpi LEFT JOIN FETCH indOpi.voeux  WHERE indOpi.annee = :annee AND indOpi.synchro= :synchro AND indOpi.source= :source"),
		@NamedQuery(name = "allIndOpiBySynchroAndExcluAndSource", query = "SELECT indOpi FROM IndOpi indOpi WHERE indOpi.annee = :annee AND indOpi.synchro!=0 AND indOpi.source= :source"),
		@NamedQuery(name = "updateSynchroOpi", query = "UPDATE IndOpi i SET i.synchro=1 WHERE i.numeroOpi= :numeroOpi"),	
		@NamedQuery(name = "allIndOpiBySource", query = "SELECT indOpi FROM IndOpi indOpi WHERE indOpi.annee = :annee AND indOpi.source = :source"),
		@NamedQuery(name = "allIndOpi", query = "SELECT indOpi FROM IndOpi indOpi WHERE indOpi.annee = :annee"),
		@NamedQuery(name = "allIndOpiByAnnee", query = "SELECT indOpi FROM IndOpi indOpi WHERE indOpi.annee = :annee"),
		@NamedQuery(name = "getIndOpiBySource", query = "SELECT indOpi FROM IndOpi indOpi WHERE indOpi.numeroOpi = :numeroOpi AND indOpi.annee = :annee AND indOpi.source = :source"),
		@NamedQuery(name = "getIndOpiExtractBySource", query = "SELECT DISTINCT RPAD (NVL(io.numeroOpi,' '), 10, ' ')"
				+ "|| RPAD (' ',1,' ')"
				+ "|| RPAD (NVL(io.codPayNat,' '),3,' ')"
				+ "|| RPAD (NVL(io.codEtb,' '),8,' ')"
				+ "|| RPAD (NVL(io.codNneIndOpi,' '),10,' ')"
				+ "|| RPAD (NVL(io.codCleNneIndOpi,' '),1,' ')"
				+ "|| TO_CHAR (io.dateNaiIndOpi, 'DDMMYYYY')"
				+ "|| RPAD (NVL(io.temDateNaiRelOpi,' '),1,' ')"
				+ "|| RPAD (' ',6,' ')"
				+ "|| RPAD (NVL(io.daaEntEtbOpi,' '),4,' ')"
				+ "|| RPAD (NVL(io.libNomPatIndOpi,' '),30,' ')"
				+ "|| RPAD (NVL(io.libNomUsuIndOpi,' '),30,' ')"
				+ "|| RPAD (NVL(io.libPr1IndOpi,' '),20,' ')"
				+ "|| RPAD (NVL(io.libPr2IndOpi,' '),20,' ')"
				+ "|| RPAD (NVL(io.libPr3IndOpi,' '),20,' ')"
				+ "|| RPAD (NVL(io.LibVilNaiEtuOpi,' '),30,' ')"
				+ "|| RPAD (NVL(io.codDepPayNai,' '),3,' ')"
				+ "|| RPAD (NVL(io.codTypDepPayNai,' '),1,' ')"
				+ "|| RPAD (' ',115,' ')"
//				+ "|| RPAD (' ',21,' ')"
				+
				 "|| RPAD (NVL (io.codBac, ' '),4, ' ')" +
				 "|| RPAD (NVL (io.codEtbBac, ' '),8, ' ')" +
				 "|| RPAD (NVL(io.codDep,' '),3,' ')" +
				 "|| RPAD (NVL (io.codMnb, ' '),2, ' ')" +
				 "|| RPAD (NVL(io.daabacObtOba,' '),4,' ')" +
				"|| RPAD (NVL(io.codPay,' '),3,' ')"
				+
				// "|| RPAD (' ',5,' ')" +
				"|| RPAD (NVL(io.codBdi,' '),5,' ')"
				+ "|| RPAD (NVL(io.codCom,' '),5,' ')"
				+ "|| RPAD (NVL(io.libAd1,' '),32,' ')"
				+ "|| RPAD (NVL(io.libAd2,' '),32,' ')"
				+ "|| RPAD (NVL(io.libAd3,' '),32,' ')"
				+ "|| RPAD (NVL(io.libAde,' '),32,' ')"
				+ "|| RPAD (NVL(io.numTel,' '),15,' ')"
				+ "|| RPAD (NVL(io.daaEnsSupOpi,' '),4,' ')"
				+ "|| RPAD (' ',160,' ')"
				+ "|| RPAD (NVL(io.codSexEtuOpi,' '),1,' ')"
				+ "|| RPAD (' ',4,' ')"
				+ "|| RPAD (NVL(io.adrMailOpi,' '),200,' ')"
				+ "|| RPAD (NVL(io.numTelPorOpi,' '),15,' ')"
				+ "|| RPAD (' ',2,' ')"
				+
				// "|| RPAD (NVL(io.codTpe,' '),2,' ')" +
				"|| RPAD (' ',71,' ')"
				+ "|| RPAD (NVL(io.daaEtrSup,' '),4,' ')"
				+ "|| RPAD (' ',219,' ') "
				+ "|| RPAD (' ',3,' ')"
				+ "FROM IndOpi io "
				+ "WHERE io.annee = :annee AND io.source = :source"),
		@NamedQuery(name = "getVoeuxInsExtractBySource", query = "SELECT DISTINCT RPAD (NVL(vi.voeux.numeroOpi,' '), 10, ' ') "
				+ "|| RPAD (NVL(vi.voeux.libNomPatIndOpi,' '),30,' ') "
				+ "|| RPAD (NVL(vi.voeux.libPr1IndOpi,' '),20,' ') "
				+ "|| RPAD (NVL(vi.voeux.codCge,' '),3,' ') "
				+ "|| RPAD (NVL(vi.voeux.codEtp,' '),6,' ') "
				+ "|| RPAD (NVL(vi.voeux.codVrsVet,' '),3,' ') "
				+ "|| RPAD (NVL(vi.voeux.codDip,' '),7,' ') "
				+
				// "|| RPAD (NVL(vi.voeux.codVrsVdi,' '),3,' ') " +
				"|| RPAD (NVL(vi.voeux.codVrsVdi,null),3,' ') "
				+ "|| RPAD (NVL(vi.voeux.codCmp,' '),3,' ') "
				+ "|| RPAD (' ',4,' ') "
				+ "|| RPAD (NVL(vi.voeux.codDemDos,' '),1,' ') "
				+ "|| RPAD (NVL(vi.voeux.codDemDos,' '),1,' ') "
				+ "|| RPAD ('F',1,' ') "
				+ "|| RPAD (NVL(vi.voeux.numCls,' '),2,' ') "
				+ "|| RPAD (' ',268,' ') "
				+ " FROM IndOpi vi "
				+ " WHERE vi.annee = :annee AND vi.source = :source"),
		@NamedQuery(name = "updateLibelleVersionEtapeLocal", query = "UPDATE IndOpi i SET i.voeux.libelleVersionEtape= :libelleVersionEtape WHERE i.numeroOpi= :numeroOpi"),
		@NamedQuery(name = "getPresenceEtudiantOPiByIneAndAnnee", query = "SELECT indOpi FROM IndOpi indOpi WHERE indOpi.codNneIndOpi=:codNneIndOpi AND indOpi.codCleNneIndOpi=:codCleNneIndOpi AND indOpi.annee=:annee"),
		@NamedQuery(name = "getStatistiquesNombreTotalTransfertOPI", query = "select count(indOpi.numeroOpi) from IndOpi indOpi where indOpi.annee=:annee")
})
@Table(name = "IND_OPI")
public class IndOpi implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7421112897404494181L;

	/**
	 * Numero de l'OPI
	 */
	@Id
	@Column(name = "COD_OPI_INT_EPO", nullable = false, length = 10)
	private String numeroOpi;

	/**
	 * Libelle etablissement de depart
	 */
	@Transient
	private String libEtabDepart;

	/**
	 * Etablissement de depart
	 */
	@Column(name = "ETAB_DEPART", length = 8)
	private String etabDepart;

	/**
	 * Annee universitaire
	 */
	@Column(name = "annee")
	private Integer annee;

	/**
	 * Code pays de nationalite
	 */
	@Column(name = "COD_PAY_NAT", length = 3)
	private String codPayNat;

	/**
	 * Code de l'etablissement de premiere inscription universite en France
	 */
	@Column(name = "COD_ETB", length = 8)
	private String codEtb;

	/**
	 * Numero national etudiant
	 */
	@Column(name = "COD_NNE_IND_OPI", length = 10)
	private String codNneIndOpi;

	/**
	 * Cle du numero national etudiant
	 */
	@Column(name = "COD_CLE_NNE_IND_OPI", length = 1)
	private String codCleNneIndOpi;

	/**
	 * Date de naissance de l'etudiant
	 */
	@Column(name = "DATE_NAI_IND_OPI", nullable = false, length = 8)
	@Temporal(TemporalType.DATE)
	private Date dateNaiIndOpi;

	/**
	 * Temoin de date de naissance estimee
	 */
	@Column(name = "TEM_DATE_NAI_REL_OPI", nullable = false, length = 1)
	private String temDateNaiRelOpi;

	/**
	 * Annee de premiere inscription en Universite francaise
	 */
	@Column(name = "DAA_ENT_ETB_OPI")
	private String daaEntEtbOpi;

	/**
	 * Nom patronymique de l'etudiant
	 */
	@Column(name = "LIB_NOM_PAT_IND_OPI", nullable = false, length = 30)
	private String libNomPatIndOpi;

	/**
	 * Nom usuel de l'etudiant
	 */
	@Column(name = "LIB_NOM_USU_IND_OPI", length = 30)
	private String libNomUsuIndOpi;

	/**
	 * Premier prenom de l'etudiant
	 */
	@Column(name = "LIB_PR1_IND_OPI", nullable = false, length = 20)
	private String libPr1IndOpi;

	/**
	 * Second prenom de l'etudiant
	 */
	@Column(name = "LIB_PR2_IND_OPI", length = 20)
	private String libPr2IndOpi;

	/**
	 * Troisieme prenom de l'etudiant
	 */
	@Column(name = "LIB_PR3_IND_OPI", length = 20)
	private String libPr3IndOpi;

	/**
	 * Ville de naissance de l'etudiant
	 */
	@Column(name = "LIB_VIL_NAI_ETU_OPI", length = 30)
	private String LibVilNaiEtuOpi;

	/**
	 * Code du pays ou du departement de naissance de l'etudiant
	 */
	@Column(name = "COD_DEP_PAY_NAI", length = 3)
	private String codDepPayNai;

	/**
	 * Type precisant s'il s'agit d'un pays ou d'un departement
	 */
	@Column(name = "COD_TYP_DEP_PAY_NAI", length = 1)
	private String codTypDepPayNai;

	/**
	 * Annee de la premiere inscription dans l'enseignement superieure
	 */
	@Column(name = "DAA_ENS_SUP_OPI", length = 4)
	private String daaEnsSupOpi;

	/**
	 * Adresse electronique personnel de l'individu
	 */
	@Column(name = "ADR_MAIL_OPI", length = 200)
	private String adrMailOpi;

	/**
	 * Numero de telephone portable de l'individu
	 */
	@Column(name = "NUM_TEL_POR_OPI", length = 15)
	private String numTelPorOpi;

	/**
	 * Annee universitaire de premiere inscription en enseignement superieur
	 * à l'etranger
	 */
	@Column(name = "DAA_ETR_SUP", length = 4)
	private String daaEtrSup;

	/**
	 * Sexe de l'etudiant
	 */
	@Column(name = "COD_SEX_ETU_OPI", length = 1)
	private String codSexEtuOpi;

	/**
	 * Code de l'etablissement du baccalaureat ou de l'equivalence
	 */
	@Column(name = "COD_ETB_BAC", length = 8)
	private String codEtbBac;

	/**
	 * Code du baccalaureat ou de l'equivalence de l'etudiant
	 */
	@Column(name = "COD_BAC", length = 4)
	private String codBac;

	/**
	 * Departement d'obtention du baccalaureat ou de l'equivalence
	 */
	@Column(name = "COD_DEP", length = 3)
	private String codDep;

	/**
	 * Code de la mention obtenue baccalaureat
	 */
	@Column(name = "COD_MNB", length = 2)
	private String codMnb;

	/**
	 * Annee d'obtention du baccalaureat ou de l'equivalence
	 */
	@Column(name = "DAA_BAC_OBT_OBA", length = 4)
	private String daabacObtOba;

	/**
	 * Type de l'etablissement d'obtention du Bac ou de l'equivalence
	 */
	@Column(name = "COD_TPE", length = 2)
	private String codTpe;

	/**
	 * Numero de telephone temporaire de l'etudiant
	 */
	@Column(name = "NUM_TEL", length = 15)
	private String numTel;

	/**
	 * Code pays INSEE de l'adresse fixe de l'etudiant
	 */
	@Column(name = "COD_PAY", length = 3)
	private String codPay;

	/**
	 * Code du bureau distributeur de l'adresse fixe de l'etudiant
	 */
	@Column(name = "COD_BDI", length = 5)
	private String codBdi;

	/**
	 * Code de la commune de l'adresse fixe de l'etudiant
	 */
	@Column(name = "COD_COM", length = 5)
	private String codCom;

	/**
	 * Premiere ligne de l'adresse fixe de l'etudiant
	 */
	@Column(name = "LIB_AD1", length = 32)
	private String libAd1;

	/**
	 * Seconde ligne de l'adresse fixe de l'etudiant
	 */
	@Column(name = "LIB_AD2", length = 32)
	private String libAd2;

	/**
	 * Troisieme ligne de l'adresse fixe de l'etudiant
	 */
	@Column(name = "LIB_AD3", length = 32)
	private String libAd3;

	/**
	 * Code postal et ville etrangere de l'adresse fixe de l'etudiant
	 */
	@Column(name = "LIB_ADE", length = 32)
	private String libAde;

	/**
	 * Temoin de retour du transfert accueil
	 * 0 Pas de synchro
	 * 1 Synchro OK
	 * 2 Synchro NOK
	 * 3 VAP en cours
	 * 4 APB PostBac
	 */
	@Column(name = "SYNCHRO", nullable=false, columnDefinition = "INTEGER default 0")
	private Integer synchro;

	@Column(name = "SOURCE", nullable=false)
	private String source;		
	
	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "COD_OPI_INT_EPO", referencedColumnName = "COD_OPI_INT_EPO")
	private VoeuxIns voeux = new VoeuxIns();

	@Column(name = "COD_IND")
	private Integer codInd;
	
	@Column(name = "COD_ETU_LPA")
	private Integer codEtuLpa;

	@Column(name = "COD_IND_OPI")
	private Integer codIndOpi;	
	
//	@GeneratedValue(strategy=GenerationType.TABLE, generator="OPI_SEQ")
//	@SequenceGenerator(name="OPI_SEQ", sequenceName="OPI_SEQ", allocationSize=1)	
//	private long sequenceIndOpi;	
	
	/**
	 * Bean constructor.
	 */
	public IndOpi() {
		super();
	}

	@Override
	public String toString() {
		return "IndOpi [numeroOpi=" + numeroOpi + ", libEtabDepart=" + libEtabDepart + ", etabDepart=" + etabDepart
				+ ", annee=" + annee + ", codPayNat=" + codPayNat + ", codEtb=" + codEtb + ", codNneIndOpi="
				+ codNneIndOpi + ", codCleNneIndOpi=" + codCleNneIndOpi + ", dateNaiIndOpi=" + dateNaiIndOpi
				+ ", temDateNaiRelOpi=" + temDateNaiRelOpi + ", daaEntEtbOpi=" + daaEntEtbOpi + ", libNomPatIndOpi="
				+ libNomPatIndOpi + ", libNomUsuIndOpi=" + libNomUsuIndOpi + ", libPr1IndOpi=" + libPr1IndOpi
				+ ", libPr2IndOpi=" + libPr2IndOpi + ", libPr3IndOpi=" + libPr3IndOpi + ", LibVilNaiEtuOpi="
				+ LibVilNaiEtuOpi + ", codDepPayNai=" + codDepPayNai + ", codTypDepPayNai=" + codTypDepPayNai
				+ ", daaEnsSupOpi=" + daaEnsSupOpi + ", adrMailOpi=" + adrMailOpi + ", numTelPorOpi=" + numTelPorOpi
				+ ", daaEtrSup=" + daaEtrSup + ", codSexEtuOpi=" + codSexEtuOpi + ", codEtbBac=" + codEtbBac
				+ ", codBac=" + codBac + ", codDep=" + codDep + ", codMnb=" + codMnb + ", daabacObtOba=" + daabacObtOba
				+ ", codTpe=" + codTpe + ", numTel=" + numTel + ", codPay=" + codPay + ", codBdi=" + codBdi
				+ ", codCom=" + codCom + ", libAd1=" + libAd1 + ", libAd2=" + libAd2 + ", libAd3=" + libAd3
				+ ", libAde=" + libAde + ", synchro=" + synchro + ", source=" + source + ", voeux=" + voeux
				+ ", codInd=" + codInd + ", codEtuLpa=" + codEtuLpa + ", codIndOpi=" + codIndOpi + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}

	public String getNumeroOpi() {
		return numeroOpi;
	}

	public void setNumeroOpi(String numeroOpi) {
		this.numeroOpi = numeroOpi;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setVoeux(VoeuxIns voeux) {
		this.voeux = voeux;
	}

	public VoeuxIns getVoeux() {
		return voeux;
	}

	public void setCodPayNat(String codPayNat) {
		this.codPayNat = codPayNat;
	}

	public String getCodPayNat() {
		return codPayNat;
	}

	public void setCodEtb(String codEtb) {
		this.codEtb = codEtb;
	}

	public String getCodEtb() {
		return codEtb;
	}

	public void setCodNneIndOpi(String codNneIndOpi) {
		this.codNneIndOpi = codNneIndOpi;
	}

	public String getCodNneIndOpi() {
		return codNneIndOpi;
	}

	public void setCodCleNneIndOpi(String codCleNneIndOpi) {
		this.codCleNneIndOpi = codCleNneIndOpi;
	}

	public String getCodCleNneIndOpi() {
		return codCleNneIndOpi;
	}

	public void setDateNaiIndOpi(Date dateNaiIndOpi) {
		this.dateNaiIndOpi = dateNaiIndOpi;
	}

	public Date getDateNaiIndOpi() {
		return dateNaiIndOpi;
	}

	public void setTemDateNaiRelOpi(String temDateNaiRelOpi) {
		this.temDateNaiRelOpi = temDateNaiRelOpi;
	}

	public String getTemDateNaiRelOpi() {
		return temDateNaiRelOpi;
	}

	public void setDaaEntEtbOpi(String daaEntEtbOpi) {
		this.daaEntEtbOpi = daaEntEtbOpi;
	}

	public String getDaaEntEtbOpi() {
		return daaEntEtbOpi;
	}

	public void setLibNomPatIndOpi(String libNomPatIndOpi) {
		this.libNomPatIndOpi = libNomPatIndOpi;
	}

	public String getLibNomPatIndOpi() {
		return libNomPatIndOpi;
	}

	public void setLibNomUsuIndOpi(String libNomUsuIndOpi) {
		this.libNomUsuIndOpi = libNomUsuIndOpi;
	}

	public String getLibNomUsuIndOpi() {
		return libNomUsuIndOpi;
	}

	public void setLibPr1IndOpi(String libPr1IndOpi) {
		this.libPr1IndOpi = libPr1IndOpi;
	}

	public String getLibPr1IndOpi() {
		return libPr1IndOpi;
	}

	public void setLibPr2IndOpi(String libPr2IndOpi) {
		this.libPr2IndOpi = libPr2IndOpi;
	}

	public String getLibPr2IndOpi() {
		return libPr2IndOpi;
	}

	public void setLibPr3IndOpi(String libPr3IndOpi) {
		this.libPr3IndOpi = libPr3IndOpi;
	}

	public String getLibPr3IndOpi() {
		return libPr3IndOpi;
	}

	public void setLibVilNaiEtuOpi(String libVilNaiEtuOpi) {
		LibVilNaiEtuOpi = libVilNaiEtuOpi;
	}

	public String getLibVilNaiEtuOpi() {
		return LibVilNaiEtuOpi;
	}

	public void setCodDepPayNai(String codDepPayNai) {
		this.codDepPayNai = codDepPayNai;
	}

	public String getCodDepPayNai() {
		return codDepPayNai;
	}

	public void setCodTypDepPayNai(String codTypDepPayNai) {
		this.codTypDepPayNai = codTypDepPayNai;
	}

	public String getCodTypDepPayNai() {
		return codTypDepPayNai;
	}

	public void setDaaEnsSupOpi(String daaEnsSupOpi) {
		this.daaEnsSupOpi = daaEnsSupOpi;
	}

	public String getDaaEnsSupOpi() {
		return daaEnsSupOpi;
	}

	public void setAdrMailOpi(String adrMailOpi) {
		this.adrMailOpi = adrMailOpi;
	}

	public String getAdrMailOpi() {
		return adrMailOpi;
	}

	public void setNumTelPorOpi(String numTelPorOpi) {
		this.numTelPorOpi = numTelPorOpi;
	}

	public String getNumTelPorOpi() {
		return numTelPorOpi;
	}

	public void setCodSexEtuOpi(String codSexEtuOpi) {
		this.codSexEtuOpi = codSexEtuOpi;
	}

	public String getCodSexEtuOpi() {
		return codSexEtuOpi;
	}

	public void setDaaEtrSup(String daaEtrSup) {
		this.daaEtrSup = daaEtrSup;
	}

	public String getDaaEtrSup() {
		return daaEtrSup;
	}

	public void setCodEtbBac(String codEtbBac) {
		this.codEtbBac = codEtbBac;
	}

	public String getCodEtbBac() {
		return codEtbBac;
	}

	public void setCodBac(String codBac) {
		this.codBac = codBac;
	}

	public String getCodBac() {
		return codBac;
	}

	public void setCodDep(String codDep) {
		this.codDep = codDep;
	}

	public String getCodDep() {
		return codDep;
	}

	public void setCodMnb(String codMnb) {
		this.codMnb = codMnb;
	}

	public String getCodMnb() {
		return codMnb;
	}

	public void setDaabacObtOba(String daabacObtOba) {
		this.daabacObtOba = daabacObtOba;
	}

	public String getDaabacObtOba() {
		return daabacObtOba;
	}

	public void setCodTpe(String codTpe) {
		this.codTpe = codTpe;
	}

	public String getCodTpe() {
		return codTpe;
	}

	public void setNumTel(String numTel) {
		this.numTel = numTel;
	}

	public String getNumTel() {
		return numTel;
	}

	public void setCodPay(String codPay) {
		this.codPay = codPay;
	}

	public String getCodPay() {
		return codPay;
	}

	public void setCodBdi(String codBdi) {
		this.codBdi = codBdi;
	}

	public String getCodBdi() {
		return codBdi;
	}

	public void setCodCom(String codCom) {
		this.codCom = codCom;
	}

	public String getCodCom() {
		return codCom;
	}

	public void setLibAd1(String libAd1) {
		this.libAd1 = libAd1;
	}

	public String getLibAd1() {
		return libAd1;
	}

	public void setLibAd2(String libAd2) {
		this.libAd2 = libAd2;
	}

	public String getLibAd2() {
		return libAd2;
	}

	public void setLibAd3(String libAd3) {
		this.libAd3 = libAd3;
	}

	public String getLibAd3() {
		return libAd3;
	}

	public void setLibAde(String libAde) {
		this.libAde = libAde;
	}

	public String getLibAde() {
		return libAde;
	}

	public void setEtabDepart(String etabDepart) {
		this.etabDepart = etabDepart;
	}

	public String getEtabDepart() {
		return etabDepart;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setLibEtabDepart(String libEtabDepart) {
		this.libEtabDepart = libEtabDepart;
	}

	public String getLibEtabDepart() {
		return libEtabDepart;
	}

	public Integer getSynchro() {
		return synchro;
	}

	public void setSynchro(Integer synchro) {
		this.synchro = synchro;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getCodIndOpi() {
		return codIndOpi;
	}

	public void setCodIndOpi(Integer codIndOpi) {
		this.codIndOpi = codIndOpi;
	}

	public Integer getCodEtuLpa() {
		return codEtuLpa;
	}

	public void setCodEtuLpa(Integer codEtuLpa) {
		this.codEtuLpa = codEtuLpa;
	}

	public Integer getCodInd() {
		return codInd;
	}

	public void setCodInd(Integer codInd) {
		this.codInd = codInd;
	}
}