/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.web.controller.tender;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Tender;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 */
public class TenderValidator implements Validator {
	
	/**
	 * @see Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {
		return Tender.class.equals(clazz);
	}
	
	/**
	 * @see Validator#validate(java.lang.Object, Errors)
	 */
	public void validate(Object command, Errors error) {
		Tender tender = (Tender) command;
		
		if (StringUtils.isBlank(tender.getName())) {
			error.reject("billing.name.invalid");
		}
		if (tender.getNumber() == 0) {
			error.reject("billing.number.invalid");
		}
		if (tender.getPrice() == null) {
			error.reject("billing.price.invalid");
		}
		if (tender.getOpeningDate() == null) {
			error.reject("billing.openingDate.invalid");
		}
		if (tender.getClosingDate() == null) {
			error.reject("billing.closingDate.invalid");
		}
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		Integer tenderId = tender.getTenderId();
		if (tenderId == null) {
			if (billingService.getTenderByNameAndNumber(tender.getName(), tender.getNumber()) != null) {
				error.reject("billing.nameandnumber.existed");
			}
		} else {
			Tender dbStore = billingService.getTenderById(tenderId);
			if (dbStore != null && !dbStore.getName().equalsIgnoreCase(tender.getName())) {
				if (billingService.getTenderByNameAndNumber(tender.getName(), tender.getNumber()) != null) {
					error.reject("billing.nameandnumber.existed");
				}
			}
		}
		
		if (tender.getOpeningDate() != null
		        && tender.getClosingDate() != null
		        && (tender.getOpeningDate().compareTo(tender.getClosingDate()) > 0 || tender.getOpeningDate().compareTo(
		            tender.getClosingDate()) == 0)) {
			error.reject("billing.closingDate.invalid");
		}
		
	}
	
}
