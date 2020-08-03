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

import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Company;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
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
@RequestMapping("/module/ehrbilling/company.form")
public class CompanyFormController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String firstView(@ModelAttribute("company") Company company,
	        @RequestParam(value = "companyId", required = false) Integer id, Model model) {
		if (id != null) {
			company = Context.getService(BillingService.class).getCompanyById(id);
			model.addAttribute(company);
		}
		return "/module/ehrbilling/company/form";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Company company, BindingResult bindingResult, HttpServletRequest request) {
		
		new CompanyValidator().validate(company, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehrbilling/company/form";
		}
		BillingService billingService = Context.getService(BillingService.class);
		if (company.getRetired()) {
			company.setRetiredDate(new Date());
		} else {
			company.setCreatedDate(new Date());
		}
		billingService.saveCompany(company);
		return "redirect:/module/ehrbilling/company.list";
	}
	
}
