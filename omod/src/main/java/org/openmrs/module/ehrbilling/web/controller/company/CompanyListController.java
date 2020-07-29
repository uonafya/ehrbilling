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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.Company;
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


/**
 *
 */
@Controller
@RequestMapping("/module/ehrbilling/company.list")
public class CompanyListController {
	Log log = LogFactory.getLog(getClass());
    
    @RequestMapping(method=RequestMethod.POST)
    public String deleteCompanies(@RequestParam("ids") String[] ids,HttpServletRequest request){
    	
    	HttpSession httpSession = request.getSession();
		Integer companyId  = null;
		try{
			BillingService billingService = (BillingService)Context.getService(BillingService.class);
			if( ids != null && ids.length > 0 ){
				for(String sId : ids )
				{
					companyId = Integer.parseInt(sId);
					Company company = billingService.getCompanyById(companyId);
					if( company!= null )
					{
						billingService.deleteCompany(company);
					}
				}
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
		"ehrbilling.company.delete.done");
			}
		}catch (Exception e) {
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
			"Can not delete company ");
			log.error(e);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
		"ehrbilling.company.delete.fail");
		}
		
    	
    	return "redirect:/module/ehrbilling/company.list";
    }
	
    @RequestMapping(method=RequestMethod.GET)
	public String listTender(@RequestParam(value="pageSize",required=false)  Integer pageSize, 
	                         @RequestParam(value="currentPage",required=false)  Integer currentPage,
	                         Map<String, Object> model, HttpServletRequest request){
		
		BillingService billingService = Context.getService(BillingService.class);
		
		int total = billingService.countListCompany();
		
		PagingUtil pagingUtil = new PagingUtil( RequestUtil.getCurrentLink(request) , pageSize, currentPage, total );
		
		List<Company> companies = billingService.listCompany(pagingUtil.getStartPos(), pagingUtil.getPageSize());
		
		model.put("companies", companies );
		
		model.put("pagingUtil", pagingUtil);
		
		return "/module/ehrbilling/company/list";
	}
}
