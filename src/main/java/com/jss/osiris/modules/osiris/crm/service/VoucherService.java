package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;

public interface VoucherService {
        public List<Voucher> getVouchers(Boolean isDisplayOnlyActiveVouchers);

        public Voucher getVoucher(Integer id);

        public Voucher getVoucherByCode(String code);

        public Voucher addOrUpdateVoucher(Voucher voucher);

        public void deleteVoucher(Voucher voucher)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException,
                        OsirisException;

        public Voucher checkVoucherValidity(IQuotation quotation, Voucher voucher)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisException;

        public List<Voucher> getVouchersFromCode(String code);

        public Boolean deleteVoucheredPriceOnIQuotation(IQuotation quotation)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisException;
}
