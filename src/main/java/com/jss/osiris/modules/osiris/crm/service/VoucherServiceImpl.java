package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.crm.repository.VoucherRepository;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.PricingHelper;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    ServiceService serviceService;

    @Override
    public List<Voucher> getVouchers(Boolean isDisplayOnlyActiveVouchers) {
        if (isDisplayOnlyActiveVouchers)
            return voucherRepository.findActiveVouchers();
        else
            return IterableUtils.toList(voucherRepository.findAll());
    }

    @Override
    public Voucher getVoucher(Integer id) {
        Optional<Voucher> voucher = voucherRepository.findById(id);
        if (voucher.isPresent())
            return voucher.get();
        return null;
    }

    @Override
    public Voucher getVoucherByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    @Override
    public Voucher addOrUpdateVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    public void deleteVoucher(Voucher voucher)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        if (!voucher.getCustomerOrders().isEmpty()) {
            for (CustomerOrder order : voucher.getCustomerOrders()) {
                order.setVoucher(null);
                customerOrderService.addOrUpdateCustomerOrder(order, false, false);
            }
        }
        voucherRepository.delete(voucher);
    }

    @Override
    public List<Voucher> getVouchersFromCode(String code) {
        return IterableUtils.toList(voucherRepository.findByCodeContainingIgnoreCase(code));
    }

    @Override
    public Voucher checkVoucherValidity(IQuotation quotation, Voucher voucher)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        Integer voucherUsesPerResponsable = 0;
        Integer voucherUsesTotal = 0;
        Responsable responsableQuotation = null;

        if (quotation.getResponsable() != null
                && quotation.getResponsable().getId() != null) {
            responsableQuotation = quotation.getResponsable();
        } else if (employeeService.getCurrentMyJssUser() != null) {
            responsableQuotation = employeeService.getCurrentMyJssUser();
        }

        if (!voucher.getResponsables().isEmpty()
                && responsableQuotation == null)
            return null;

        if (!voucher.getResponsables().isEmpty() && responsableQuotation != null) {
            boolean found = false;
            for (Responsable responsable : voucher.getResponsables())
                if (responsableQuotation.getId().equals(responsable.getId())) {
                    found = true;
                    break;
                }
            if (!found)
                return null;
        }
        if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(LocalDate.now())
                || (voucher.getEndDate() != null && voucher.getEndDate().isBefore(LocalDate.now())))
            return null;

        if (!quotation.getIsQuotation()) {
            if (responsableQuotation != null)
                voucherUsesPerResponsable = customerOrderService
                        .getCustomerOrdersByVoucherAndResponsable(voucher, responsableQuotation).size();

            voucherUsesTotal = customerOrderService.getCustomerOrdersByVoucherAndResponsable(voucher, null).size();

            if ((voucher.getPerUserLimit() != null && voucherUsesPerResponsable >= voucher.getPerUserLimit() - 1)
                    || (voucher.getTotalLimit() != null && voucherUsesTotal >= voucher.getTotalLimit() - 1))
                return null;
        }
        if (employeeService.getCurrentMyJssUser() != null) {
            quotation.setVoucher(voucher);
            if (quotation.getIsQuotation())
                quotationService.addOrUpdateQuotation((Quotation) quotation);
            else
                customerOrderService.simpleAddOrUpdate((CustomerOrder) quotation);
        }
        return voucher;
    }

    @Override
    public Boolean deleteVoucheredPriceOnIQuotation(IQuotation quotation)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        quotation.setVoucher(null);
        if (quotation.getIsQuotation())
            quotationService.addOrUpdateQuotation((Quotation) quotation);
        else
            customerOrderService.simpleAddOrUpdate((CustomerOrder) quotation);

        for (AssoAffaireOrder assoAffaireOrder : quotation.getAssoAffaireOrders())
            serviceService.populateTransientField(assoAffaireOrder.getServices());
        return true;
    }
}