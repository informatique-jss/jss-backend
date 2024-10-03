package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;

public interface CustomerOrderCommentService {
    public List<CustomerOrderComment> getCustomerOrderComments();

    public CustomerOrderComment getCustomerOrderComment(Integer id);

    public CustomerOrderComment addOrUpdateCustomerOrderComment(CustomerOrderComment customerOrderComment);

    public CustomerOrderComment createCustomerOrderComment(CustomerOrder customerOrder, String contentComment);

    public CustomerOrderComment tagActiveDirectoryGroupOnCustomerOrderComment(CustomerOrderComment customerOrderComment,
            ActiveDirectoryGroup activeDirectoryGroup);
}
