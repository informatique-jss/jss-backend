package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderComment;

public interface CustomerOrderCommentService {
    public List<CustomerOrderComment> getCustomerOrderComments();

    public CustomerOrderComment getCustomerOrderComment(Integer id);

    public CustomerOrderComment addOrUpdateCustomerOrderComment(CustomerOrderComment customerOrderComment);

    public void createCustomerOrderComment(CustomerOrder customerOrder, String contentComment);
}
