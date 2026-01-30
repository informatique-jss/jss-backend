package com.jss.osiris.modules.osiris.quotation.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.dto.CustomerOrderDto;
import com.jss.osiris.modules.osiris.quotation.dto.ProvisionDto;
import com.jss.osiris.modules.osiris.quotation.dto.QuotationDto;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearch;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionSearch;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearch;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.infoGreffe.InfogreffeKbisService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

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

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

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

    @Transactional
    public List<ProvisionDto> searchProvisions(ProvisionSearch provisionSearch) throws OsirisException {

        List<Provision> provisionsFound = provisionService.searchForProvisions(provisionSearch);
        return quotationDtoHelper.mapProvisions(provisionsFound);
    }

    /***************************** CHAT *************************/
    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrderComment> getCommentsFromChatForIQuotation(Integer iQuotationId)
            throws OsirisException {

        CustomerOrder customerOrder = customerOrderService.getCustomerOrder(iQuotationId);
        if (customerOrder != null)
            return customerOrderCommentService.getCommentsFromChatForOrder(customerOrder);

        Quotation quotation = quotationService.getQuotation(iQuotationId);
        return customerOrderCommentService.getCustomerOrderCommentForQuotation(quotation);
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderComment addOrUpdateCustomerOrderComment(CustomerOrderComment customerOrderComment) {
        if (customerOrderComment.getCustomerOrder() == null && customerOrderComment.getCustomerOrderId() != null) {
            CustomerOrder customerOrder = customerOrderService
                    .getCustomerOrder(customerOrderComment.getCustomerOrderId());
            customerOrderComment.setCustomerOrder(customerOrder);
        }
        if (customerOrderComment.getCurrentCustomer() == null
                || customerOrderComment.getCurrentCustomer().getId() == null) {
            Responsable currentUser = customerOrderComment.getCustomerOrder().getResponsable();
            customerOrderComment.setCurrentCustomer(currentUser);
        }
        customerOrderComment = customerOrderCommentService.addOrUpdateCustomerOrderComment(customerOrderComment);
        return customerOrderComment;
    }
}
