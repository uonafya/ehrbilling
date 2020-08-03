/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.web.controller.ambulance;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Ambulance;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 */
public class AmbulanceValidator implements Validator {
	
	/**
	 * @see Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {
		return Ambulance.class.equals(clazz);
	}
	
	/**
	 * @see Validator#validate(java.lang.Object, Errors)
	 */
	public void validate(Object command, Errors error) {
		Ambulance ambulance = (Ambulance) command;
		
		if (StringUtils.isBlank(ambulance.getName())) {
			error.reject("billing.name.required");
		}
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		Integer companyId = ambulance.getAmbulanceId();
		if (companyId == null) {
			if (billingService.getAmbulanceByName(ambulance.getName()) != null) {
				error.reject("billing.name.existed");
			}
		} else {
			Ambulance dbStore = billingService.getAmbulanceById(companyId);
			if (!dbStore.getName().equalsIgnoreCase(ambulance.getName())) {
				if (billingService.getAmbulanceByName(ambulance.getName()) != null) {
					error.reject("billing.name.existed");
				}
			}
		}
	}
	
}
