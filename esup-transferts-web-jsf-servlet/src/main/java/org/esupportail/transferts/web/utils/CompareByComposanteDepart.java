/**
 * 
 */
package org.esupportail.transferts.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Comparator;

import org.esupportail.transferts.domain.beans.EtudiantRef;

/**
 * @author Farid AITKARRA
 * copyleft
 */
public class CompareByComposanteDepart implements Comparator<EtudiantRef>{

	public int compare(EtudiantRef etu1, EtudiantRef etu2) 
	{
        if (etu1.getComposante().compareTo(etu2.getComposante()) > 0) {
            return 1;
        }
        if (etu1.getComposante().compareTo(etu2.getComposante()) < 0) {
            return -1;
        }
        // at this point all a.b,c,d are equal... so return "equal"
        return 0;
	}
	
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
 
}
