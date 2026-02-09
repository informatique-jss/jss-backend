package com.jss.osiris.modules.osiris.quotation.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
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
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

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

    @Autowired
    EmployeeService employeeService;

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
    public Map<Integer, List<CustomerOrderComment>> getCommentsFromChatForIQuotations(List<Integer> iQuotationIds)
            throws OsirisException {

        Map<Integer, List<CustomerOrderComment>> commentsForIQuotationsMap = new HashMap<Integer, List<CustomerOrderComment>>();
        List<CustomerOrderComment> commentsFound = new ArrayList<>();

        for (Integer iQuotationId : iQuotationIds) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(iQuotationId);
            if (customerOrder != null) {
                commentsFound = customerOrderCommentService
                        .getCustomerOrderCommentForOrder(customerOrder);
                populateCustomerOrderCommentsTransientFields(customerOrder.getResponsable().getTiers(), iQuotationId,
                        commentsFound);
                commentsForIQuotationsMap.put(iQuotationId,
                        customerOrderCommentService.getCustomerOrderCommentForOrder(customerOrder));
            }
            Quotation quotation = quotationService.getQuotation(iQuotationId);
            if (quotation != null) {
                commentsFound = customerOrderCommentService.getCustomerOrderCommentForQuotation(quotation);
                populateCustomerOrderCommentsTransientFields(quotation.getResponsable().getTiers(), iQuotationId,
                        commentsFound);
                commentsForIQuotationsMap.put(iQuotationId, commentsFound);
            }
        }
        return commentsForIQuotationsMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrderComment> getCommentsListFromChatForIQuotations(List<Integer> iQuotationIds)
            throws OsirisException {
        List<CustomerOrderComment> comments = new ArrayList<>();
        List<CustomerOrderComment> commentsListForIQuotations = new ArrayList<>();

        Tiers tiers = null;
        for (Integer iQuotationId : iQuotationIds) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(iQuotationId);
            if (customerOrder != null) {
                comments = customerOrderCommentService.getCustomerOrderCommentForOrder(customerOrder);
                tiers = customerOrder.getResponsable().getTiers();
            } else {
                Quotation quotation = quotationService.getQuotation(iQuotationId);
                if (quotation != null) {
                    comments = customerOrderCommentService.getCustomerOrderCommentForQuotation(quotation);
                    tiers = quotation.getResponsable().getTiers();
                }
            }

            populateCustomerOrderCommentsTransientFields(tiers, iQuotationId, comments);
            commentsListForIQuotations.addAll(comments);
        }

        commentsListForIQuotations.sort((c1, c2) -> c1.getCreatedDateTime().compareTo(c2.getCreatedDateTime()));
        return commentsListForIQuotations;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrderComment> getUnreadCommentsListFromChatForEmployee() {
        List<CustomerOrderComment> comments = new ArrayList<>();

        Employee currentEmployee = employeeService.getCurrentEmployee();

        List<Employee> employeesThatIBackupAndMe = employeeService.getMyHolidaymaker(currentEmployee);

        for (Employee employee : employeesThatIBackupAndMe) {
            List<CustomerOrderComment> commentsFoundsForEmployee = customerOrderCommentService
                    .getUnreadCustomerOrderCommentForSalesEmployee(employee);
            comments.addAll(commentsFoundsForEmployee);
        }

        for (CustomerOrderComment comment : comments) {
            if (comment.getCustomerOrder() != null) {
                populateCustomerOrderCommentTransientField(comment.getCustomerOrder().getResponsable().getTiers(),
                        comment.getCustomerOrder().getId(), comment);
            } else {
                populateCustomerOrderCommentTransientField(comment.getQuotation().getResponsable().getTiers(),
                        comment.getQuotation().getId(), comment);
            }
        }

        return comments;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrderComment> getUnreadCommentsListFromChatForMyJssCurrentUser() {

        Responsable currentMyJssUser = employeeService.getCurrentMyJssUser();

        List<CustomerOrderComment> commentsFoundsForMyJssUser = customerOrderCommentService
                .getUnreadCustomerOrderCommentForResponsable(currentMyJssUser);

        for (CustomerOrderComment comment : commentsFoundsForMyJssUser) {
            if (comment.getCustomerOrder() != null) {
                populateCustomerOrderCommentTransientField(comment.getCustomerOrder().getResponsable().getTiers(),
                        comment.getCustomerOrder().getId(), comment);
            } else {
                populateCustomerOrderCommentTransientField(comment.getQuotation().getResponsable().getTiers(),
                        comment.getQuotation().getId(), comment);
            }
        }

        return commentsFoundsForMyJssUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrderComment> getUnreadCommentsListFromChatForMyJssCurrentUserByIQuotation(
            Integer iQuotationId) {

        Responsable currentMyJssUser = employeeService.getCurrentMyJssUser();

        List<CustomerOrderComment> commentsFoundsForMyJssUser = customerOrderCommentService
                .getUnreadCustomerOrderCommentForResponsable(currentMyJssUser);

        for (CustomerOrderComment comment : commentsFoundsForMyJssUser) {
            if (comment.getCustomerOrder() != null) {
                populateCustomerOrderCommentTransientField(comment.getCustomerOrder().getResponsable().getTiers(),
                        comment.getCustomerOrder().getId(), comment);
            } else {
                populateCustomerOrderCommentTransientField(comment.getQuotation().getResponsable().getTiers(),
                        comment.getQuotation().getId(), comment);
            }
        }

        return commentsFoundsForMyJssUser.stream().filter(com -> com.getiquotationId().equals(iQuotationId)).toList();
    }

    private void populateCustomerOrderCommentsTransientFields(Tiers tiers, Integer iQuotationId,
            List<CustomerOrderComment> comments) {
        for (CustomerOrderComment comment : comments) {
            populateCustomerOrderCommentTransientField(tiers, iQuotationId, comment);
        }
    }

    private void populateCustomerOrderCommentTransientField(Tiers tiers, Integer iQuotationId,
            CustomerOrderComment comment) {
        comment.setiquotationId(iQuotationId);
        comment.setTiersDenomination(tiers.getDenomination() != null ? tiers.getDenomination()
                : (tiers.getFirstname() + " " + tiers.getLastname()));
        comment.setTiersId(tiers.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderComment addOrUpdateCustomerOrderComment(CustomerOrderComment customerOrderComment) {
        if (customerOrderComment.getCustomerOrder() == null && customerOrderComment.getCustomerOrder() == null
                && customerOrderComment.getQuotation() == null
                && customerOrderComment.getiquotationId() != null) {
            CustomerOrder customerOrder = customerOrderService
                    .getCustomerOrder(customerOrderComment.getiquotationId());

            if (customerOrder != null)
                customerOrderComment.setCustomerOrder(customerOrder);
            else {
                Quotation quotation = quotationService.getQuotation(customerOrderComment.getiquotationId());
                if (quotation != null)
                    customerOrderComment.setQuotation(quotation);
            }
        }
        if (customerOrderComment.getCurrentCustomer() == null
                || customerOrderComment.getCurrentCustomer().getId() == null) {
            Responsable currentUser = null;
            if (customerOrderComment.getCustomerOrder() != null)
                currentUser = customerOrderComment.getCustomerOrder().getResponsable();
            else
                currentUser = customerOrderComment.getQuotation().getResponsable();

            customerOrderComment.setCurrentCustomer(currentUser);
        }
        customerOrderComment = customerOrderCommentService.addOrUpdateCustomerOrderComment(customerOrderComment);
        return customerOrderComment;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer getTiersIdByIQuotationId(Integer iQuotationId) throws OsirisException {

        CustomerOrder customerOrder = customerOrderService.getCustomerOrder(iQuotationId);
        if (customerOrder != null)
            return customerOrder.getResponsable().getTiers().getId();

        Quotation quotation = quotationService.getQuotation(iQuotationId);
        if (quotation != null)
            return quotation.getResponsable().getTiers().getId();

        return -1;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrder> getCustomerOrdersWithUnreadCommentsForMyJssUser() {
        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser == null)
            return new ArrayList<>();

        return customerOrderCommentService.getCustomerOrdersWithUnreadCommentsForResponsable(currentUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Quotation> getQuotationsWithUnreadCommentsForMyJssUser() {
        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser == null)
            return new ArrayList<>();

        return customerOrderCommentService.getQuotationsWithUnreadCommentsForResponsable(currentUser);
    }

    // WARNING : Check in caller if for iQuotationId there is an existing quotation
    // or customerOrder
    @Transactional(rollbackFor = Exception.class)
    public Boolean getIsQuotation(Integer iQuotationId) {
        Quotation quotation = quotationService.getQuotation(iQuotationId);
        if (quotation != null)
            return true;

        return false;
    }
}
