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

import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingConstants;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Tender;
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
@RequestMapping("/module/ehrbilling/tender.form")
public class TenderFormController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String firstView(@ModelAttribute("tender") Tender tender,
	        @RequestParam(value = "tenderId", required = false) Integer id, Model model) {
		if (id != null) {
			tender = Context.getService(BillingService.class).getTenderById(id);
			model.addAttribute(tender);
		}
		return "/module/ehrbilling/tender/form";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true));
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor(BillingConstants.TRUE,
		        BillingConstants.FALSE, true));
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Tender tender, BindingResult bindingResult, HttpServletRequest request) {
		
		new TenderValidator().validate(tender, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehrbilling/tender/form";
		}
		BillingService billingService = Context.getService(BillingService.class);
		if (tender.getRetired()) {
			tender.setRetiredDate(new Date());
		}
		billingService.saveTender(tender);
		return "redirect:/module/ehrbilling/tender.list";
	}
	
}
