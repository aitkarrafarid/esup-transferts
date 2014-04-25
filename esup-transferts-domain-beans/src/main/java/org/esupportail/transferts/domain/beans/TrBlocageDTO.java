/**
 * ESUP-Portail example Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.transferts.domain.beans;

import java.io.Serializable;
/**
 * The class that represent users.
 */

public class TrBlocageDTO implements Serializable {

	/**
	 * For serialize.
	 */
	private static final long serialVersionUID = 7427732897404494181L;

	/**
	 * Code du blocage
	 */
	private String codeBlocage;

	/**
	 * Libellé du blocage
	 */
	private String libBlocage;			

	/**
	 * Bean constructor.
	 */
	public TrBlocageDTO() {
		super();
	}

	/**
	 * Bean constructor.
	 */
	public TrBlocageDTO(String codeBlocage, String libBlocage) {
		super();
		this.codeBlocage=codeBlocage;
		this.libBlocage=libBlocage;
	}	
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "TrBlocageDTO#" + hashCode() + "[codeBlocage=[" + codeBlocage + "],  libBlocage=["+ libBlocage +"]";
	}

	public void setCodeBlocage(String codeBlocage) {
		this.codeBlocage = codeBlocage;
	}

	public String getCodeBlocage() {
		return codeBlocage;
	}

	public void setLibBlocage(String libBlocage) {
		this.libBlocage = libBlocage;
	}

	public String getLibBlocage() {
		return libBlocage;
	}

}