/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.web.controller.main;

import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
@Controller
@RequestMapping("/module/ehrbilling/searchDriver.form")
public class SearchDriverController {

	@RequestMapping(method=RequestMethod.POST)	
	public String searchCompany(@RequestParam("searchText") String searchText, Model model){
		
		BillingService billingService = Context.getService(BillingService.class);
		
		model.addAttribute("drivers", billingService.searchDriver(searchText));
		
		model.addAttribute("searchText", searchText);
			
		return "/module/ehrbilling/main/searchDriver";
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String listAll(Model model){
		BillingService billingService = Context.getService(BillingService.class);
		model.addAttribute("drivers", billingService.getAllActiveDriver());
		return "/module/ehrbilling/main/searchDriver";
	}
}
