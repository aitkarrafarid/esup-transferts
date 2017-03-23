package org.esupportail.transferts.domain.beans;

import javax.persistence.Column;
import javax.persistence.Id;

public class OffreDeFormationPK implements java.io.Serializable {

	private static final long serialVersionUID = 2327732897404494181L;

	@Column(name = "COD_RNE", nullable=false, length=8)
	private String rne;
	
	@Column(name = "COD_ANU", nullable=false, length=4)	
	private Integer annee;		
	
	@Column(name = "COD_DIP", nullable=false, length=7)
	private String codeDiplome;

	@Column(name = "COD_VRS_VDI", nullable=false, length=10)
	private Integer codeVersionDiplome;		

	@Column(name = "COD_ETP", nullable=false, length=6)
	private String codeEtape;		

	@Column(name = "COD_VET", nullable=false, length=6)
	private String codeVersionEtape;		
	
	@Column(name = "COD_CGE", nullable=true, length=9)
	private String codeCentreGestion;		

	public OffreDeFormationPK() {}

	public OffreDeFormationPK(String rne, Integer annee, String codeDiplome, Integer codeVersionDiplome, String codeEtape, String codeVersionEtape, String codeCentreGestion) 
	{
		this.rne=rne;
		this.annee=annee;
		this.codeDiplome=codeDiplome;
		this.codeVersionDiplome = codeVersionDiplome;
		this.codeEtape=codeEtape;
		this.codeVersionEtape = codeVersionEtape;		
		this.codeCentreGestion=codeCentreGestion;
	}

	public boolean equals(Object obj) {
		boolean resultat = false;

		if (obj == this) {
			resultat = true;
		} else {
			if (!(obj instanceof OffreDeFormationPK))
			{
				resultat = false;
			}
			else 
			{
				OffreDeFormationPK autre = (OffreDeFormationPK) obj;
				if (rne==autre.rne 
					&& annee==autre.annee 
					&& codeDiplome==autre.codeDiplome 
					&& codeVersionDiplome==autre.codeVersionDiplome
					&& codeEtape==autre.codeEtape
					&& codeVersionEtape==autre.codeVersionEtape
					&& codeCentreGestion==autre.codeCentreGestion
					) 
					resultat = true;
				else 
					resultat = false;
			}
		}
		return resultat;
	}

	public int hashCode() {
		return (rne + annee.toString() + codeDiplome + codeVersionDiplome + codeEtape + codeVersionEtape + codeCentreGestion).hashCode();
	}

	public String getCodeDiplome() {
		return codeDiplome;
	}

	public void setCodeDiplome(String codeDiplome) {
		this.codeDiplome = codeDiplome;
	}

	public Integer getCodeVersionDiplome() {
		return codeVersionDiplome;
	}

	public void setCodeVersionDiplome(Integer codeVersionDiplome) {
		this.codeVersionDiplome = codeVersionDiplome;
	}

	public String getCodeEtape() {
		return codeEtape;
	}

	public void setCodeEtape(String codeEtape) {
		this.codeEtape = codeEtape;
	}

	public String getCodeVersionEtape() {
		return codeVersionEtape;
	}

	public void setCodeVersionEtape(String codeVersionEtape) {
		this.codeVersionEtape = codeVersionEtape;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public String getRne() {
		return rne;
	}

	public void setRne(String rne) {
		this.rne = rne;
	}

	public String getCodeCentreGestion() {
		return codeCentreGestion;
	}

	public void setCodeCentreGestion(String codeCentreGestion) {
		this.codeCentreGestion = codeCentreGestion;
	}



}