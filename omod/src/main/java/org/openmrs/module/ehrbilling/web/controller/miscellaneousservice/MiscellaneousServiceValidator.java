/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.web.controller.miscellaneousservice;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.MiscellaneousService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 */
public class MiscellaneousServiceValidator implements Validator {
	
	/**
	 * @see Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {
		return MiscellaneousService.class.equals(clazz);
	}
	
	/**
	 * @see Validator#validate(java.lang.Object, Errors)
	 */
	public void validate(Object command, Errors error) {
		MiscellaneousService miscellaneousService = (MiscellaneousService) command;
		
		if (StringUtils.isBlank(miscellaneousService.getName())) {
			error.reject("billing.name.required");
		}
		if (miscellaneousService.getPrice() == null) {
			error.reject("billing.price.required");
		}
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		Integer miscellaneousServiceId = miscellaneousService.getId();
		if (miscellaneousServiceId == null) {
			if (billingService.getMiscellaneousServiceByName(miscellaneousService.getName()) != null) {
				error.reject("billing.name.existed");
			}
		} else {
			MiscellaneousService dbStore = billingService.getMiscellaneousServiceById(miscellaneousServiceId);
			if (dbStore != null && !dbStore.getName().equalsIgnoreCase(miscellaneousService.getName())) {
				if (billingService.getMiscellaneousServiceByName(miscellaneousService.getName()) != null) {
					error.reject("billing.name.existed");
				}
			}
		}
		
	}
	
}
