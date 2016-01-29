/**
 * 
 */
package org.esupportail.transferts.web.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.esupportail.transferts.domain.beans.Correspondance;

/**
 * @author cleprous
 *
 */
public class ComparatorDateTimeCorrespondance implements Comparator<Correspondance>, Serializable {

	/**
	 * The serialization id. 
	 */
	private static final long serialVersionUID = 1545052575014067760L;

	/**
	 * Constructor.
	 */
	public ComparatorDateTimeCorrespondance() {
		super();
	}

	public int compare(Correspondance p, Correspondance q) {
		Date Pdate = p.getDateSaisie();
		Date Qdate =q.getDateSaisie();
//		return Pdate.compareTo(Qdate) > 0 ? 0 : 1;
//		return p.getDateSaisie().compareTo(q.getDateSaisie())*-1;
		if (Pdate.compareTo(Qdate) < 0)
		{
//			System.out.println("date1 is before date2");
			return 1;
		}
		else if (Pdate.compareTo(Qdate) > 0)
		{
//			System.out.println("date1 is after date2");
			return -1;
		}
		else
		{
//			System.out.println("date1 is equal to date2");
			return 0;
		}
	}

}
