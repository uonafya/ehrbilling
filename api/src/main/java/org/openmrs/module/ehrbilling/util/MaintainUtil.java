/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.util;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.util.GlobalPropertyUtil;

public class MaintainUtil {
	
	/**
	 * Reset service order concept id
	 */
	public static void resetServiceOrderConceptId() {
		System.out.println("=== resetServiceOrderId ===");
		Concept concept = Context.getConceptService().getConcept(BillingConstants.SERVICE_ORDER_CONCEPT_NAME);
		if(concept!=null){
			GlobalPropertyUtil.setString(BillingConstants.PROPERTY_ROOT_SERVICE_CONCEPT_ID, concept.getConceptId().toString());
		} else {
			System.out.println("CAN'T FOUND SERVICE ORDER CONCEPT");
		}		
	}
}
