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
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Ambulance;
import org.openmrs.module.hospitalcore.model.AmbulanceBill;
import org.openmrs.module.hospitalcore.model.AmbulanceBillItem;
import org.openmrs.module.hospitalcore.model.Driver;
import org.openmrs.module.hospitalcore.util.Money;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/module/ehrbilling/addAmbulanceBill.form")
public class AmbulanceBillAddController {
	
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Model model, @RequestParam("driverId") Integer driverId,
	        @RequestParam("ambulanceIds") Integer[] ambulanceIds, HttpServletRequest request, Object command,
	        BindingResult binding) {
		
		validate(ambulanceIds, binding, request);
		if (binding.hasErrors()) {
			model.addAttribute("errors", binding.getAllErrors());
			return "module/ehrbilling/main/ambulanceBillAdd";
		}
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		Driver driver = billingService.getDriverById(driverId);
		AmbulanceBill ambulanceBill = new AmbulanceBill();
		ambulanceBill.setDriver(driver);
		ambulanceBill.setCreatedDate(new Date());
		ambulanceBill.setCreator(Context.getAuthenticatedUser());
		
		Ambulance ambulance = null;
		Money itemAmount;
		Money totalAmount = new Money(BigDecimal.ZERO);
		for (Integer id : ambulanceIds) {
			
			ambulance = billingService.getAmbulanceById(id);
			BigDecimal amount = new BigDecimal(request.getParameter(id + "_amount"));
			itemAmount = new Money(amount);
			totalAmount = totalAmount.plus(itemAmount);
			
			Integer numberOfTrip = Integer.parseInt(request.getParameter(id + "_numOfTrip"));
			
			// New Requirement Additional details in Ambulance Bill
			String patientName = (request.getParameter(id + "_patientName"));
			// feedback of New Requirement Additional details in Ambulance Bill
			String receiptNumber = (request.getParameter(id + "_receiptNumber"));
			String origin = (request.getParameter(id + "_origin"));
			String destination = (request.getParameter(id + "_destination"));
			
			AmbulanceBillItem item = new AmbulanceBillItem();
			item.setName(ambulance.getName());
			item.setCreatedDate(new Date());
			item.setNumberOfTrip(numberOfTrip);
			item.setAmbulance(ambulance);
			item.setAmbulanceBill(ambulanceBill);
			item.setAmount(itemAmount.getAmount());
			
			//New Requirement: Additional details in Ambulance Bill
			item.setPatientName(patientName);
			item.setReceiptNumber(receiptNumber);
			item.setOrigin(origin);
			item.setDestination(destination);
			
			ambulanceBill.addBillItem(item);
		}
		ambulanceBill.setAmount(totalAmount.getAmount());
		ambulanceBill.setReceipt(billingService.createReceipt());
		ambulanceBill = billingService.saveAmbulanceBill(ambulanceBill);
		
		return "redirect:/module/ehrbilling/ambulanceBill.list?driverId=" + driverId + "&ambulanceBillId="
		        + ambulanceBill.getAmbulanceBillId();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String displayForm(@ModelAttribute("command") Object command, @RequestParam("driverId") Integer driverId,
	        HttpServletRequest request, Model model) {
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		List<Ambulance> listAmbulance = billingService.getActiveAmbulances();
		
		if (listAmbulance == null || listAmbulance.size() == 0) {
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "No Ambulance found.");
		} else {
			model.addAttribute("listAmbulance", listAmbulance);
		}
		model.addAttribute("driverId", driverId);
		return "module/ehrbilling/main/ambulanceBillAdd";
	}
	
	private void validate(Integer[] ids, BindingResult binding, HttpServletRequest request) {
		for (int id : ids) {
			try {
				Integer.parseInt(request.getParameter(id + "_numOfTrip"));
			}
			catch (Exception e) {
				binding.reject("billing.bill.quantity.invalid", "Number of trip is invalid");
				return;
			}
			try {
				new BigDecimal(request.getParameter(id + "_amount"));
			}
			catch (Exception e) {
				binding.reject("billing.bill.quantity.invalid", "Amount is invalid");
				return;
			}
			
		}
	}
	
}
