
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.RadiologyService;
import org.openmrs.module.hospitalcore.model.RadiologyDepartment;
import org.openmrs.module.hospitalcore.util.RadiologyConstants;
import org.openmrs.module.hospitalcore.util.RadiologyUtil;
import org.openmrs.module.hospitalcore.util.TestModel;
import org.openmrs.module.radiologyapp.util.RadiologyAppUtil;

import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.OpdTestOrder;

import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by masenohach_5
 */
public class GetAllProcessedRadiologyOrdersController{
    private static Logger logger = LoggerFactory.getLogger(QueueFragmentController.class);
    public void controller(FragmentModel model) {
        RadiologyService radiologyService = (RadiologyService) Context.getService(RadiologyService.class);
        RadiologyDepartment department = radiologyService.getCurrentRadiologyDepartment();
        if(department != null){
            Set<Concept> investigations = department.getInvestigations();
            model.addAttribute("investigations", investigations);
        }
    }

    public List<SimpleObject> getProcessedRadilogyTests(
            @RequestParam("paid") Integer paid,
            @RequestParam(value = "date", required = false) String dateStr,
            @RequestParam(value = "currentPage", required = false) Integer currentPage,
            UiUtils ui) {

        //load billing services
        BillingService billingService = Context.getService(BillingService.class);

        //load billing services
        //imp -> ()

        SimpleDateFormat sdf = new SimpleDateFormat();
        Date date = null;

        try {
            date = sdf.parse(dateStr);

        }catch(ParseException E){
            E.printStackTrace();
        }


        //return rad orders as queue
        SimpleDateFormat dateformat=new SimpleDateFormat("dd/MM/yyyy");
        List<SimpleObject> testRadOrdersInQue = new ArrayList<>();
        try{
            if (currentPage==null)
                currentPage=1;
            // get paid/processed list orders
            List<OpdTestOrder> listOfOrders = billingService.listOfOrder(paid,date);

            List<OpdTestOrder> tests = new ArrayList<OpdTestOrder>();
            for(OpdTestOrder opdTestOrder : listOfOrders){
                tests.add(OpdTestOrder)//load all tests to list
            }

            testRadOrdersInQue=SimpleObject.fromCollection(test,ui,"startDate","patientIdentifier","patientName","gender","age","testName","orderId","status");

        }catch(Exception e){
            e.printStackTrace();
            logger.error("Error when parsing order date! "+e.getMessage());
        }

        return testRadOrdersInQue;
    }
