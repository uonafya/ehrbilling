/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ehrbilling.web.controller.driver;

import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Driver;
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
@RequestMapping("/module/ehrbilling/driver.form")
public class DriverFormController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String firstView(@ModelAttribute("driver") Driver driver,
	        @RequestParam(value = "driverId", required = false) Integer id, Model model) {
		if (id != null) {
			driver = Context.getService(BillingService.class).getDriverById(id);
		} else {
			driver = new Driver();
		}
		model.addAttribute(driver);
		return "/module/ehrbilling/driver/form";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Driver driver, BindingResult bindingResult, HttpServletRequest request) {
		
		new DriverValidator().validate(driver, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehrbilling/driver/form";
		}
		BillingService billingService = Context.getService(BillingService.class);
		if (driver.getRetired()) {
			driver.setRetiredDate(new Date());
		} else {
			driver.setCreatedDate(new Date());
		}
		billingService.saveDriver(driver);
		return "redirect:/module/ehrbilling/driver.list";
	}
	
}
