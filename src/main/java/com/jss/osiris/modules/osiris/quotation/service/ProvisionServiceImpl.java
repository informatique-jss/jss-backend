package com.jss.osiris.modules.osiris.quotation.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.osiris.quotation.repository.AnnouncementStatusRepository;
import com.jss.osiris.modules.osiris.quotation.repository.ProvisionRepository;

@Service
public class ProvisionServiceImpl implements ProvisionService {

    @Autowired
    ProvisionRepository provisionRepository;

    @Autowired
    AnnouncementStatusRepository announcementStatusRepository;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    AttachmentService attachmentService;

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
    @Transactional
    public Boolean deleteProvision(Provision provision) {
        if (provision.getAttachments() != null && provision.getAttachments().size() > 0) {
            for (Attachment attachment : provision.getAttachments()) {
                attachmentService.cleanAttachmentForDelete(attachment);
            }
        }
        provisionRepository.delete(provision);
        return true;
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

    @Override
    public File getRegistrationActPdf(Provision provision) throws OsirisException {
        return generatePdfDelegate.generateRegistrationActPdf(provision);
    }

    @Override
    public File getTrackingSheetPdf(Provision provision) throws OsirisException {
        return generatePdfDelegate.generateTrackingSheetPdf(provision);
    }
}
