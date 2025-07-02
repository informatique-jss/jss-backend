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
        if (voucher.getIsCancelled() == null)
            voucher.setIsCancelled(false);
        return voucherRepository.save(voucher);
    }

    @Override
    public void deleteVoucher(Voucher voucher)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        voucher.setIsCancelled(true);
        addOrUpdateVoucher(voucher);
    }

    @Override
    public List<Voucher> getVouchersFromCode(String code) {
        return IterableUtils.toList(voucherRepository.findByCodeContainingIgnoreCaseAndIsCancelled(code, false));
    }

    @Override
    public Voucher checkVoucherValidity(IQuotation quotation, Voucher voucher)
            throws OsirisClientMessageException, OsirisValidationException, OsirisException {
        Integer voucherUsesPerResponsable = 0;
        Integer voucherUsesTotal = 0;
        Responsable responsableQuotation = null;

        if (voucher.getIsCancelled())
            return null;

        if (employeeService.getCurrentMyJssUser() != null) {
            responsableQuotation = employeeService.getCurrentMyJssUser();
        }

        if (!voucher.getResponsables().isEmpty()) {
            if (responsableQuotation == null)
                return null;

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

        if (responsableQuotation != null)
            voucherUsesPerResponsable = customerOrderService
                    .getCustomerOrdersByVoucherAndResponsable(voucher, responsableQuotation).size();

        voucherUsesTotal = customerOrderService.getCustomerOrdersByVoucherAndResponsable(voucher, null).size();

        if ((voucher.getPerUserLimit() != null && voucherUsesPerResponsable >= voucher.getPerUserLimit() - 1)
                || (voucher.getTotalLimit() != null && voucherUsesTotal >= voucher.getTotalLimit() - 1))
            return null;

        quotation.setVoucher(voucher);

        if (quotation.getId() != null) {
            if (quotation.getIsQuotation())
                quotationService.addOrUpdateQuotation((Quotation) quotation);
            else
                customerOrderService.addOrUpdateCustomerOrder((CustomerOrder) quotation, true, false);
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
            customerOrderService.addOrUpdateCustomerOrder((CustomerOrder) quotation, true, false);

        return true;
    }
}