package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.repository.CustomerOrderCommentRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class CustomerOrderCommentServiceImpl implements CustomerOrderCommentService {

    @Autowired
    CustomerOrderCommentRepository customerOrderCommentRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    ConstantService constantService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Override
    public List<CustomerOrderComment> getCustomerOrderComments() {
        return IterableUtils.toList(customerOrderCommentRepository.findAll());
    }

    @Override
    public List<CustomerOrderComment> getCustomerOrderCommentForOrder(CustomerOrder customerOrder) {
        return customerOrderCommentRepository.findByCustomerOrder(customerOrder);
    }

    @Override
    public List<CustomerOrderComment> getCustomerOrderCommentForQuotation(Quotation quotation) {
        return customerOrderCommentRepository.findByQuotation(quotation);
    }

    @Override
    public List<CustomerOrderComment> getUnreadCustomerOrderCommentForSalesEmployee(Employee employee) {
        return customerOrderCommentRepository.findUnreadCommmentsForSalesEmployee(employee);
    }

    @Override
    public List<CustomerOrderComment> getUnreadCustomerOrderCommentForResponsable(Responsable responsable) {
        return customerOrderCommentRepository.findUnreadCommmentsForResponsable(responsable.getId());
    }

    @Override
    public List<CustomerOrder> getCustomerOrdersWithUnreadCommentsForResponsable(Responsable responsable) {
        List<CustomerOrderComment> comments = customerOrderCommentRepository
                .findUnreadCommmentsForResponsable(responsable.getId());

        return comments.stream().filter(comment -> comment.getCustomerOrder() != null)
                .map(CustomerOrderComment::getCustomerOrder).distinct().toList();
    }

    @Override
    public List<Quotation> getQuotationsWithUnreadCommentsForResponsable(Responsable responsable) {
        List<CustomerOrderComment> comments = customerOrderCommentRepository
                .findUnreadCommmentsForResponsable(responsable.getId());

        return comments.stream().filter(comment -> comment.getQuotation() != null)
                .map(CustomerOrderComment::getQuotation).distinct().toList();
    }

    @Override
    public List<CustomerOrderComment> getCustomerOrderCommentForProvision(Provision provision) {
        return customerOrderCommentRepository.findByProvision(provision);
    }

    @Override
    public CustomerOrderComment getCustomerOrderComment(Integer id) {
        Optional<CustomerOrderComment> customerOrderComment = customerOrderCommentRepository.findById(id);
        if (customerOrderComment.isPresent())
            return customerOrderComment.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderComment addOrUpdateCustomerOrderComment(
            CustomerOrderComment customerOrderComment) {
        if (customerOrderComment.getIsRead() == null)
            customerOrderComment.setIsRead(false);
        if (customerOrderComment.getIsReadByCustomer() == null)
            customerOrderComment.setIsReadByCustomer(false);
        if (customerOrderComment.getCreatedDateTime() == null)
            customerOrderComment.setCreatedDateTime(LocalDateTime.now());
        return customerOrderCommentRepository.save(customerOrderComment);
    }

    @Override
    public CustomerOrderComment createCustomerOrderComment(CustomerOrder customerOrder, String contentComment,
            Boolean doNotNotify, Boolean isToDisplayToCustomer, Boolean isFromChat)
            throws OsirisException {
        CustomerOrderComment customerOrderComment = new CustomerOrderComment();
        customerOrderComment.setCustomerOrder(customerOrder);
        customerOrderComment.setComment(contentComment);

        Employee employee = employeeService.getCurrentEmployee();
        if (employee != null)
            customerOrderComment.setEmployee(employee);
        else if (!doNotNotify) {
            customerOrderComment.setCurrentCustomer(employeeService.getCurrentMyJssUser());
            customerOrderComment.setActiveDirectoryGroups(new ArrayList<ActiveDirectoryGroup>());
            customerOrderComment.getActiveDirectoryGroups().add(constantService.getActiveDirectoryGroupSales());
            customerOrderComment.setIsToDisplayToCustomer(isToDisplayToCustomer);
            notificationService.notifyCommentFromMyJssAddToCustomerOrder(customerOrder);
        }
        customerOrderComment.setCreatedDateTime(LocalDateTime.now());
        customerOrderComment.setIsRead(false);
        customerOrderComment.setIsReadByCustomer(false);
        customerOrderComment.setIsFromChat(isFromChat);

        return addOrUpdateCustomerOrderComment(customerOrderComment);
    }

    @Override
    public CustomerOrderComment tagActiveDirectoryGroupOnCustomerOrderComment(CustomerOrderComment customerOrderComment,
            ActiveDirectoryGroup activeDirectoryGroup) {
        if (customerOrderComment != null && activeDirectoryGroup != null) {
            if (customerOrderComment.getActiveDirectoryGroups() == null
                    || customerOrderComment.getActiveDirectoryGroups().size() == 0)
                customerOrderComment.setActiveDirectoryGroups(new ArrayList<ActiveDirectoryGroup>());
            customerOrderComment.getActiveDirectoryGroups().add(activeDirectoryGroup);
        }
        return addOrUpdateCustomerOrderComment(customerOrderComment);
    }

    @Override
    public List<CustomerOrderComment> getCommentsFromChatForOrder(CustomerOrder customerOrder) {
        return customerOrderCommentRepository
                .findByCustomerOrderAndIsFromChat(customerOrder, true);

    }
}
