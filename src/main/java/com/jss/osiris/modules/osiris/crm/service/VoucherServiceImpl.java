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
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    EmployeeService employeeService;

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
    public Voucher checkVoucherValidity(IQuotation quotation) {
        Integer voucherUsesPerResponsable = null;
        Integer voucherUsesTotal = null;
        Responsable responsable = null;
        Voucher voucher = getVoucherByCode(quotation.getVoucher().getCode());
        if (quotation.getResponsable() != null)
            responsable = quotation.getResponsable();
        if (responsable != null) {
            if (!quotation.getIsQuotation()) {
                voucherUsesPerResponsable = customerOrderService
                        .getCustomerOrdersByVoucherAndResponsable(voucher, quotation.getResponsable()).size();
                voucherUsesTotal = customerOrderService.getCustomerOrdersByVoucherAndResponsable(voucher, null).size();
            }
            if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(LocalDate.now())
                    || (voucher.getEndDate() != null && voucher.getEndDate().isBefore(LocalDate.now()))
                    || (voucher.getPerUserLimit() != null && voucherUsesPerResponsable >= voucher.getPerUserLimit())
                    || (voucher.getTotalLimit() != null && voucherUsesTotal >= voucher.getTotalLimit()))
                return null;

            if (voucher.getResponsables() != null)
                voucher.getResponsables().add(responsable);
            else
                voucher.setResponsables(List.of(responsable));
            return voucher;
        }
        return null;
    }
}