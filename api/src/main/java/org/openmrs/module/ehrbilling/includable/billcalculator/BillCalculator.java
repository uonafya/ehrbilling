/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.includable.billcalculator;

import java.math.BigDecimal;
import java.util.Map;

public interface BillCalculator {
	
	/**
	 * Return the rate to calculate for a particular bill item
	 * 
	 * @param parameters TODO
	 * @return
	 */
	public BigDecimal getRate(Map<String, Object> parameters);
	
	/**
	 * at Determine whether a bill should be free or not
	 * 
	 * @param patarameters TODO
	 * @return
	 */
	//public int isFreeBill(Map<String, Object> parameters);
	public int isFreeBill(Map<String, Object> parameters);
}
