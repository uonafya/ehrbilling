/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.includable.billcalculator.ddu;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.ehrbilling.includable.billcalculator.BillCalculator;

import java.math.BigDecimal;
import java.util.Map;

public class BillCalculatorImpl implements BillCalculator {
	
	/**
	 * Get the percentage of price to pay If patient category is RSBY or BPL, the bill should be
	 * 100% free
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BigDecimal getRate(Map<String, Object> parameters) {
		BigDecimal ratio = new BigDecimal(1);
		Map<String, String> attributes = (Map<String, String>) parameters.get("attributes");
		String patientCategory = attributes.get("Patient Category");
		String bplNumber = attributes.get("BPL Number");
		String rsbyNumber = attributes.get("RSBY Number");
		
		if (!StringUtils.isBlank(patientCategory)) {
			if (patientCategory.contains("RSBY")) {
				if (!StringUtils.isBlank(rsbyNumber)) {
					ratio = new BigDecimal(0);
				}
			} else if (patientCategory.contains("BPL")) {
				if (!StringUtils.isBlank(bplNumber)) {
					ratio = new BigDecimal(0);
				}
			}
		}
		
		return ratio;
	}
	
	/**
	 * Determine whether a bill should be free or not. If patient category is RSBY or BPL, the bill
	 * should be treated as the free bill
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isFreeBill(Map<String, Object> parameters) {
		Map<String, String> attributes = (Map<String, String>) parameters.get("attributes");
		String patientCategory = attributes.get("Patient Category");
		String bplNumber = attributes.get("BPL Number");
		String rsbyNumber = attributes.get("RSBY Number");
		
		if (!StringUtils.isBlank(patientCategory)) {
			if (patientCategory.contains("RSBY")) {
				if (!StringUtils.isBlank(rsbyNumber)) {
					return true;
				}
			} else if (patientCategory.contains("BPL")) {
				if (!StringUtils.isBlank(bplNumber)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
