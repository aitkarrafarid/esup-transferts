/**
 * 
 */
package org.esupportail.transferts.web.comparator;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.esupportail.transferts.domain.beans.AccueilDecision;

/**
 * @author cleprous
 *
 */
public class ComparatorDateTime implements Comparator<AccueilDecision>, Serializable {

	/**
	 * The serialization id. 
	 */
	private static final long serialVersionUID = 1545052575014067760L;

	/**
	 * Constructor.
	 */
	public ComparatorDateTime() {
		super();
	}

	public int compare(AccueilDecision p, AccueilDecision q) {
//		Date Pdate = p.getDateSaisie();
//		Date Qdate =q.getDateSaisie();
//		return Pdate.compareTo(Qdate) > 0 ? 0 : 1;
		return p.getDateSaisie().compareTo(q.getDateSaisie())*-1;
	}

}
