package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderAssignation;
import com.jss.osiris.modules.osiris.quotation.model.ICustomerOrderAssignationStatistics;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface CustomerOrderAssignationService {

        public CustomerOrderAssignation addOrUpdateCustomerOrderAssignation(CustomerOrderAssignation actType);

        public CustomerOrderAssignation getCustomerOrderAssignation(Integer idCustomerOrderAssignation);

        public CustomerOrderAssignation addOrUpdateCustomerOrderAssignation(
                        CustomerOrderAssignation customerOrderAssignation,
                        Employee employee);

        public void completeAssignationForCustomerOrder(CustomerOrder customerOrder) throws OsirisException;

        public void assignNewProvisionToUser(Provision provision) throws OsirisException;

        public Integer getNextOrderForFond(boolean isPriority, Integer complexity, Boolean byPassAssignation)
                        throws OsirisException;

        public Integer getNextOrderForCommon(boolean isPriority, Integer complexity, Boolean byPassAssignation)
                        throws OsirisException;

        public String getFondTypeToUse(Integer complexity) throws OsirisException;

        public void assignImmediatlyOrder(CustomerOrder customerOrder) throws OsirisException;

        public List<CustomerOrder> getOrdersToAssignForFond(Employee employee) throws OsirisException;

        public boolean isPriorityOrder(CustomerOrder customerOrder) throws OsirisException;

        public List<ICustomerOrderAssignationStatistics> getCustomerOrderAssignationStatisticsForFormalistes()
                        throws OsirisException;

        public List<ICustomerOrderAssignationStatistics> getCustomerOrderAssignationStatisticsForInsertions()
                        throws OsirisException;
}
