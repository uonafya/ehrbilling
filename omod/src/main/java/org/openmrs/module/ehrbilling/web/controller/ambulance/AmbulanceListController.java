/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.web.controller.ambulance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Ambulance;
import org.openmrs.module.hospitalcore.util.PagingUtil;
import org.openmrs.module.hospitalcore.util.RequestUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/module/ehrbilling/ambulance.list")
public class AmbulanceListController {
	
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.POST)
	public String deleteCompanies(@RequestParam("ids") String[] ids, HttpServletRequest request) {
		
		HttpSession httpSession = request.getSession();
		Integer ambulanceId = null;
		try {
			BillingService billingService = (BillingService) Context.getService(BillingService.class);
			if (ids != null && ids.length > 0) {
				for (String sId : ids) {
					ambulanceId = Integer.parseInt(sId);
					Ambulance ambulance = billingService.getAmbulanceById(ambulanceId);
					if (ambulance != null) {
						billingService.deleteAmbulance(ambulance);
					}
				}
			}
		}
		catch (Exception e) {
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Can not delete ambulance because it has link to bill ");
			log.error(e);
			return "redirect:/module/ehrbilling/ambulance.list";
		}
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Ambulance.deleted");
		
		return "redirect:/module/ehrbilling/ambulance.list";
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String listTender(@RequestParam(value = "pageSize", required = false) Integer pageSize,
	        @RequestParam(value = "currentPage", required = false) Integer currentPage, Map<String, Object> model,
	        HttpServletRequest request) {
		
		BillingService billingService = Context.getService(BillingService.class);
		
		int total = billingService.countListAmbulance();
		
		PagingUtil pagingUtil = new PagingUtil(RequestUtil.getCurrentLink(request), pageSize, currentPage, total);
		
		List<Ambulance> ambulances = billingService.listAmbulance(pagingUtil.getStartPos(), pagingUtil.getPageSize());
		
		model.put("ambulances", ambulances);
		
		model.put("pagingUtil", pagingUtil);
		
		return "/module/ehrbilling/ambulance/list";
	}
}
