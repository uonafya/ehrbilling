/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 **/

package org.openmrs.module.ehrbilling.includable.billcalculator.common;

import org.openmrs.module.ehrbilling.includable.billcalculator.BillCalculator;

import java.math.BigDecimal;
import java.util.Map;

public class BillCalculatorImpl implements BillCalculator {
	
	/**
	 * Return 100%
	 */
	public BigDecimal getRate(Map<String, Object> parameters) {
		return new BigDecimal(1);
	}
	
	//public int isFreeBill(Map<String, Object> parameters) {
	public int isFreeBill(Map<String, Object> parameters) {
		return 0;
	}
}
