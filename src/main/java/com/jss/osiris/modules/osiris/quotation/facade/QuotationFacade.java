package com.jss.osiris.modules.osiris.quotation.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.dto.CustomerOrderDto;
import com.jss.osiris.modules.osiris.quotation.dto.QuotationDto;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearch;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearch;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.infoGreffe.InfogreffeKbisService;

@Service
public class QuotationFacade {

    @Autowired
    InfogreffeKbisService infogreffeKbisService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationDtoHelper quotationDtoHelper;

    @Transactional(rollbackFor = Exception.class)
    public KbisRequest orderNewKbisForSiret(String siret, Integer provisionId) throws OsirisException {
        Provision provision = provisionService.getProvision(provisionId);
        return infogreffeKbisService.orderNewKbisForSiret(siret, provision);
    }

    @Transactional(rollbackFor = Exception.class)
    public Attachment getUpToDateKbisForSiret(String siret) throws OsirisException {
        return infogreffeKbisService.getUpToDateKbisForSiret(siret);
    }

    @Transactional
    public List<QuotationDto> searchQuotations(QuotationSearch quotationSearch) throws OsirisException {

        List<Quotation> quotationsFound = quotationService.searchForQuotations(quotationSearch);
        return quotationDtoHelper.mapQuotations(quotationsFound);
    }

    @Transactional
    public List<CustomerOrderDto> searchCustomerOrders(OrderingSearch customerOrderSearch) throws OsirisException {

        List<CustomerOrder> customerOrderFound = customerOrderService.searchForCustomerOrders(customerOrderSearch);
        return quotationDtoHelper.mapCustomerOrders(customerOrderFound);
    }

}
