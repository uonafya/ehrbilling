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

import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.MiscellaneousService;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 *
 */
@Controller
@RequestMapping("/module/ehrbilling/miscellaneousService.form")
public class MiscellaneousServiceFormController {
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String firstView(@ModelAttribute("miscellaneousService") MiscellaneousService miscellaneousService, @RequestParam(value="id",required=false) Integer id, Model model) {
		if( id != null ){
			miscellaneousService = Context.getService(BillingService.class).getMiscellaneousServiceById(id);
			model.addAttribute(miscellaneousService);
		}
		return "/module/ehrbilling/miscellaneousService/form";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true));
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true",
		        "false", true));
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(MiscellaneousService miscellaneousService, BindingResult bindingResult, HttpServletRequest request) {
		new MiscellaneousServiceValidator().validate(miscellaneousService, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehrbilling/miscellaneousService/form";
		}
		BillingService billingService = Context.getService(BillingService.class);
		if( miscellaneousService.getRetired()) {
			miscellaneousService.setRetiredDate(new Date());
		}		
		miscellaneousService.setCreatedDate(new Date());
		billingService.saveMiscellaneousService(miscellaneousService);
		return "redirect:/module/ehrbilling/miscellaneousService.list";
	}
	
}
