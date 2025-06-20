package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.crm.repository.VoucherRepository;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
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
    public List<Voucher> getVouchers() {
        return IterableUtils.toList(voucherRepository.findAll());
    }

    @Override
    public List<Voucher> getActiveVouchers() {
        return IterableUtils.toList(voucherRepository.findActiveVouchers());
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
    public void deleteVoucher(Voucher voucher) {
        voucherRepository.delete(voucher);
    }

    @Override
    public List<Voucher> getVouchersFromCode(String code) {
        return IterableUtils.toList(voucherRepository.findByCodeContainingIgnoreCase(code));
    }

    @Override
    public Voucher checkVoucherMyJssValidity(String voucherCode) {
        Voucher voucher = null;
        Integer voucherUsesPerResponsable = null;
        Integer voucherUsesTotal = null;
        Responsable responsable = null;

        if (voucherCode != null) {
            voucher = getVoucherByCode(voucherCode);
            responsable = employeeService.getCurrentMyJssUser();
            if (responsable != null && voucher != null) {
                voucherUsesPerResponsable = customerOrderService.getCustomerOrdersByVoucherAndResponsable(voucher,
                        responsable).size();
                voucherUsesTotal = customerOrderService.getCustomerOrdersByVoucher(voucher).size();

                if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(LocalDate.now())
                        || (voucher.getEndDate() != null && voucher.getEndDate().isBefore(LocalDate.now()))
                        || (voucher.getPerUserLimit() != null && voucherUsesPerResponsable >= voucher.getPerUserLimit())
                        || (voucher.getTotalLimit() != null && voucherUsesTotal >= voucher.getTotalLimit()))
                    return null;
                return voucher;
            }
        }
        return null;
    }

    @Override
    public Voucher checkVoucherValidity(CustomerOrder customerOrder, Voucher voucher) {
        Integer voucherUsesPerResponsable = null;
        Integer voucherUsesTotal = null;
        if (customerOrder.getResponsable() != null) {
            voucherUsesPerResponsable = customerOrderService
                    .getCustomerOrdersByVoucherAndResponsable(voucher, customerOrder.getResponsable()).size();
            voucherUsesTotal = customerOrderService.getCustomerOrdersByVoucher(voucher).size();

            if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(LocalDate.now())
                    || (voucher.getEndDate() != null && voucher.getEndDate().isBefore(LocalDate.now()))
                    || (voucher.getPerUserLimit() != null && voucherUsesPerResponsable >= voucher.getPerUserLimit())
                    || (voucher.getTotalLimit() != null && voucherUsesTotal >= voucher.getTotalLimit()))
                return null;
            return voucher;
        }
        return null;
    }

    // TODO après validation myjss order en cours, lié responsable au voucher
    private void applyVoucher(CustomerOrder order) {
        if (order.getVoucher() != null) {

        }
    }

}