package org.esupportail.transferts.domain.beans;

import javax.persistence.Column;

public class DatasExternePK implements java.io.Serializable {

	private static final long serialVersionUID = 7427732897412345181L;

	@Column(name = "IDENTIFIANT", nullable=false)
	private String identifiant;

	@Column(name = "CODE", nullable=false)
	private String code;	
		
	public DatasExternePK() {}

	public DatasExternePK(String identifiant, String code) 
	{
		this.identifiant=identifiant;
		this.code=code;
	}

	public boolean equals(Object obj) {
		boolean resultat = false;

		if (obj == this) {
			resultat = true;
		} else {
			if (!(obj instanceof DatasExternePK))
			{
				resultat = false;
			}
			else 
			{
				DatasExternePK autre = (DatasExternePK) obj;
				if (identifiant==autre.identifiant 
					&& code==autre.code) 
					resultat = true;
				else 
					resultat = false;
			}
		}
		return resultat;
	}

	public int hashCode() {
		return (identifiant + code).hashCode();
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


}