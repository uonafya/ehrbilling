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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 *
 */
@Controller
@RequestMapping("/module/ehrbilling/main.form")
public class MainController {

	private Log log = LogFactory.getLog(this.getClass());
	
	@RequestMapping(method=RequestMethod.GET)
	public String main(Model model){
		
		String prefix = Context.getAdministrationService().getGlobalProperty("registration.identifier_prefix");
		model.addAttribute("idPrefix", prefix);
		
//		BillingService billingService = Context.getService(BillingService.class);
//		billingService.updateReceipt();	
		return "/module/ehrbilling/main/mainPage";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String submit(Model model, @RequestParam("identifier") String identifier){
		
		String prefix = Context.getAdministrationService().getGlobalProperty("registration.identifier_prefix");
		if( identifier.contains("-") && !identifier.contains(prefix)){
			identifier = prefix+identifier;
    	}
		List<Patient> patientsList = Context.getPatientService().getPatients( identifier.trim() );
		model.addAttribute("patients", patientsList);
			
		return "/module/ehrbilling/main/mainPage";
	}
}
