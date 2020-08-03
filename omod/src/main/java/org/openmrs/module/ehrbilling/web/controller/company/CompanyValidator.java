/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.web.controller.company;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Company;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 */
public class CompanyValidator implements Validator {
	
	/**
	 * @see Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {
		return Company.class.equals(clazz);
	}
	
	/**
	 * @see Validator#validate(java.lang.Object, Errors)
	 */
	public void validate(Object command, Errors error) {
		Company company = (Company) command;
		
		if (StringUtils.isBlank(company.getName())) {
			error.reject("billing.name.required");
		}
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		Integer companyId = company.getCompanyId();
		if (companyId == null) {
			if (billingService.getCompanyByName(company.getName()) != null) {
				error.reject("billing.name.existed");
			}
		} else {
			Company dbStore = billingService.getCompanyById(companyId);
			if (!dbStore.getName().equalsIgnoreCase(company.getName())) {
				if (billingService.getCompanyByName(company.getName()) != null) {
					error.reject("billing.name.existed");
				}
			}
		}
	}
	
}
