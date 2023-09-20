package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.quotation.repository.AnnouncementStatusRepository;
import com.jss.osiris.modules.quotation.repository.BodaccStatusRepository;
import com.jss.osiris.modules.quotation.repository.ProvisionRepository;

@Service
public class ProvisionServiceImpl implements ProvisionService {

    @Autowired
    ProvisionRepository provisionRepository;

    @Autowired
    BodaccStatusRepository bodaccStatusRepository;

    @Autowired
    AnnouncementStatusRepository announcementStatusRepository;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Override
    public Provision getProvision(Integer id) {
        Optional<Provision> provision = provisionRepository.findById(id);
        if (provision.isPresent())
            return provision.get();
        return null;
    }

    @Override
    public Provision addOrUpdateProvision(Provision provision) {
        return provisionRepository.save(provision);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForProvision(Provision provision, Employee employee) {
        provision.setAssignedTo(employee);
        provisionRepository.save(provision);
    }

    @Override
    public List<ProvisionBoardResult> getDashboardEmployee(List<Employee> employees) {
        List<Integer> employeeIds = new ArrayList<Integer>();
        for (Employee employee : employees)
            employeeIds.add(employee.getId());

        return provisionRepository.getDashboardEmployee(employeeIds,
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED).getId(),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.WAITING_DEPOSIT).getId(),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.OPEN).getId());
    }

}
