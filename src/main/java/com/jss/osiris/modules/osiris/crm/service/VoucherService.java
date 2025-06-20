package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;

public interface VoucherService {
    public List<Voucher> getVouchers();

    public List<Voucher> getActiveVouchers();

    public Voucher getVoucher(Integer id);

    public Voucher getVoucherByCode(String code);

    public Voucher addOrUpdateVoucher(Voucher voucher);

    public void deleteVoucher(Voucher voucher);

    public Voucher checkVoucherMyJssValidity(String voucherCode);

    public Voucher checkVoucherValidity(CustomerOrder customerOrder, Voucher voucher);

    public List<Voucher> getVouchersFromCode(String code);
}
