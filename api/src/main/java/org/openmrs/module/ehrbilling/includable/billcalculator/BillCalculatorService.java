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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.hospitalcore.util.GlobalPropertyUtil;
import org.openmrs.module.hospitalcore.util.HospitalCoreConstants;

import java.math.BigDecimal;
import java.util.Map;

public class BillCalculatorService implements BillCalculator {
	
	private Log logger = LogFactory.getLog(getClass());
	
	private BillCalculator calculator = null;
	
	/**
	 * Get the calculator relying on the hospital name. If can't find one, a warning will be thrown
	 * and the default calculator will be used
	 */
	public BillCalculatorService() {
		
		String hospitalName = GlobalPropertyUtil.getString(HospitalCoreConstants.PROPERTY_HOSPITAL_NAME, "");
		if (StringUtils.isBlank(hospitalName)) {
			hospitalName = "common";
			logger.warn("CAN'T FIND THE HOSPITAL NAME. ALL TESTS WILL BE CHARGED 100%");
		}
		
		hospitalName = hospitalName.toLowerCase();
		String qualifiedName = "org.openmrs.module.billing.includable.billcalculator." + hospitalName
		        + ".BillCalculatorImpl";
		try {
			calculator = (BillCalculator) Class.forName(qualifiedName).newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Return the rate to calculate for a particular bill item. If the `calculator` found, it will
	 * be used to calculate Otherwise, it will return 1 which means patient will be charged 100%
	 */
	public BigDecimal getRate(Map<String, Object> parameters) {
		if (calculator != null) {
			return calculator.getRate(parameters);
		} else {
			return new BigDecimal(1);
		}
	}
	
	/**
	 * Determine whether a bill should be free or not. If the `calculator` found, it will be used to
	 * determine. Otherwise, it will return `false` which means the bill is not free.
	 * 
	 * @return
	 */
	//public int isFreeBill(Map<String, Object> parameters) {
	public int isFreeBill(Map<String, Object> parameters) {
		if (calculator != null) {
			return calculator.isFreeBill(parameters);
		}
		return 0;
	}
}
