package com.jss.osiris.modules.osiris.quotation.facade;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.dto.CustomerOrderDto;
import com.jss.osiris.modules.osiris.quotation.dto.GuichetUniqueDepositInfoDto;
import com.jss.osiris.modules.osiris.quotation.dto.ProvisionDto;
import com.jss.osiris.modules.osiris.quotation.dto.QuotationDto;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearch;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionSearch;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearch;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.ValidationRequest;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.FormaliteGuichetUniqueService;
import com.jss.osiris.modules.osiris.quotation.service.infoGreffe.InfogreffeKbisService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

@org.springframework.stereotype.Service
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

    @Autowired
    ServiceService serviceService;

    @Autowired
    FormaliteGuichetUniqueService formaliteGuichetUniqueService;

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
                        .getCustomerOrderCommentForOrder(customerOrder, true);
                populateCustomerOrderCommentsTransientFields(customerOrder.getResponsable().getTiers(), iQuotationId,
                        commentsFound);
                commentsForIQuotationsMap.put(iQuotationId,
                        customerOrderCommentService.getCustomerOrderCommentForOrder(customerOrder, true));
            }
            Quotation quotation = quotationService.getQuotation(iQuotationId);
            if (quotation != null) {
                commentsFound = customerOrderCommentService.getCustomerOrderCommentForQuotation(quotation, true);
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
                comments = customerOrderCommentService.getCustomerOrderCommentForOrder(customerOrder, true);
                tiers = customerOrder.getResponsable().getTiers();
            } else {
                Quotation quotation = quotationService.getQuotation(iQuotationId);
                if (quotation != null) {
                    comments = customerOrderCommentService.getCustomerOrderCommentForQuotation(quotation, true);
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
    public void markAllCommentsAsReadForIQuotation(Integer quotationId, Boolean setReadByCustomer,
            Boolean setReadByEmployee) {
        CustomerOrder customerOrder = customerOrderService.getCustomerOrder(quotationId);
        Quotation quotation = quotationService.getQuotation(quotationId);

        List<CustomerOrderComment> comments = new ArrayList<CustomerOrderComment>();
        if (customerOrder != null) {
            comments = customerOrderCommentService.getCustomerOrderCommentForOrder(customerOrder, true);
        } else if (quotation != null) {
            comments = customerOrderCommentService.getCustomerOrderCommentForQuotation(quotation, true);
        }

        for (CustomerOrderComment comment : comments) {
            if (setReadByCustomer && !Boolean.TRUE.equals(comment.getIsReadByCustomer())) {
                comment.setIsReadByCustomer(true);
            }
            if (setReadByEmployee && !Boolean.TRUE.equals(comment.getIsRead())) {
                comment.setIsRead(true);
            }
            customerOrderCommentService.addOrUpdateCustomerOrderComment(comment);
        }
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
    public CustomerOrderComment addOrUpdateCustomerOrderComment(String customerOrderComment, Integer quotationId,
            Boolean isFromCustomer)
            throws OsirisException {

        CustomerOrder customerOrder = customerOrderService.getCustomerOrder(quotationId);
        Quotation quotation = quotationService.getQuotation(quotationId);

        return customerOrderCommentService.createCustomerOrderComment(customerOrder, quotation, customerOrderComment,
                isFromCustomer, true);

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

    @Transactional(rollbackFor = Exception.class)
    public List<GuichetUniqueDepositInfoDto> getGuichetUniqueDatesDtosForService(Integer serviceId)
            throws OsirisException {

        List<GuichetUniqueDepositInfoDto> guichetUniqueDepositInfoDtos = new ArrayList<>();
        Service service = serviceService.getService(serviceId);

        for (Provision provision : service.getProvisions())
            if (provision.getFormalite() != null)
                if (provision.getFormalite().getFormalitesGuichetUnique() != null)
                    for (FormaliteGuichetUnique formaliteGuichetUnique : provision.getFormalite()
                            .getFormalitesGuichetUnique()) {
                        formaliteGuichetUnique = formaliteGuichetUniqueService
                                .getFormaliteGuichetUnique(formaliteGuichetUnique.getId());
                        if (formaliteGuichetUnique != null && !isFormaliteGuCancelled(formaliteGuichetUnique)) {
                            GuichetUniqueDepositInfoDto currentGuInfoDto = new GuichetUniqueDepositInfoDto();
                            // Deposit date
                            currentGuInfoDto.setDepositDate(
                                    OffsetDateTime.parse(formaliteGuichetUnique.getCreated()).toLocalDate());

                            // Waiting for validation
                            if (formaliteGuichetUnique.getValidationsRequests() != null
                                    && formaliteGuichetUnique.getValidationsRequests().size() > 0) {
                                LocalDate lastValidationRequestDate = formaliteGuichetUnique
                                        .getValidationsRequests().stream()
                                        .map(val -> OffsetDateTime.parse(val.getCreated()).toLocalDate())
                                        .max(Comparator.naturalOrder()).get();

                                currentGuInfoDto.setWaitingForValidationFromDate(lastValidationRequestDate);

                                String partnerCenterName = "";
                                for (ValidationRequest validationRequest : formaliteGuichetUnique
                                        .getValidationsRequests()) {
                                    if (validationRequest.getPartnerCenter() != null && !partnerCenterName
                                            .contains(validationRequest.getPartnerCenter().getName())) {
                                        partnerCenterName = partnerCenterName
                                                + validationRequest.getPartnerCenter().getName() + ", ";
                                    }
                                }
                                currentGuInfoDto.setWaitingForValidationPartnerCenterName(partnerCenterName);
                            }

                            // If validated
                            if (formaliteGuichetUnique.getStatus().getCode()
                                    .equals(FormaliteGuichetUniqueStatus.VALIDATED))
                                currentGuInfoDto
                                        .setValidationDate(OffsetDateTime.parse(formaliteGuichetUnique.getStatusDate())
                                                .toLocalDate());

                            guichetUniqueDepositInfoDtos.add(currentGuInfoDto);
                        }
                    }

        addMissingAttachmentQueryToGuDepositInfoDtos(service, guichetUniqueDepositInfoDtos);

        return guichetUniqueDepositInfoDtos;
    }

    private boolean isFormaliteGuCancelled(FormaliteGuichetUnique formaliteGuichetUnique) {
        return FormaliteGuichetUniqueStatus.ERROR
                .equals(formaliteGuichetUnique.getStatus().getCode())
                || FormaliteGuichetUniqueStatus.EXPIRED
                        .equals(formaliteGuichetUnique.getStatus().getCode())
                || FormaliteGuichetUniqueStatus.REJECTED
                        .equals(formaliteGuichetUnique.getStatus().getCode());
    }

    private void addMissingAttachmentQueryToGuDepositInfoDtos(Service service,
            List<GuichetUniqueDepositInfoDto> guichetUniqueDepositInfoDtos) {

        List<MissingAttachmentQuery> missingAttachmentsQueries = service.getMissingAttachmentQueries();
        missingAttachmentsQueries = missingAttachmentsQueries.stream()
                .sorted((o1, o2) -> o1.getCreatedDateTime().compareTo(o2.getCreatedDateTime())).toList();

        if (missingAttachmentsQueries != null && !missingAttachmentsQueries.isEmpty()) {
            for (GuichetUniqueDepositInfoDto guichetUniqueDepositInfoDto : guichetUniqueDepositInfoDtos) {
                List<LocalDate> askingMissingDocumentDates = new ArrayList<>();
                for (MissingAttachmentQuery missingAttachmentQuery : missingAttachmentsQueries) {
                    if (missingAttachmentQuery.getCreatedDateTime().toLocalDate()
                            .isAfter(guichetUniqueDepositInfoDto.getDepositDate())
                            && missingAttachmentQuery.getCreatedDateTime().toLocalDate()
                                    .isBefore(guichetUniqueDepositInfoDto.getValidationDate() != null
                                            ? guichetUniqueDepositInfoDto.getValidationDate()
                                            : LocalDate.MAX)) {
                        askingMissingDocumentDates.add(missingAttachmentQuery.getCreatedDateTime().toLocalDate());
                    }
                }
                guichetUniqueDepositInfoDto.setAskingMissingDocumentDates(askingMissingDocumentDates);
            }

        }
    }
}
