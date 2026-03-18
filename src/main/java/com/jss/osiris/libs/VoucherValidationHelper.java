package com.jss.osiris.libs;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.crm.service.VoucherService;

@Service
public class VoucherValidationHelper {
    @Autowired
    private ValidationHelper validationHelper;

    @Autowired
    VoucherService voucherService;

    public Boolean validateVoucher(Voucher voucher) throws OsirisException {

        if (voucher == null || (voucher != null
                && (voucher.getCode() == null || voucher.getStartDate() == null || voucher.getEndDate() == null
                        || (voucher.getDiscountRate() == null && voucher.getDiscountAmount() == null))))
            throw new OsirisValidationException("voucher");

        if (voucher.getId() != null)
            validationHelper.validateReferential(voucher, true, "Voucher");

        validationHelper.validateDate(voucher.getStartDate(), true, "startDate");
        validationHelper.validateDate(voucher.getEndDate(), true, "endDate");

        if (!voucher.getStartDate().isBefore(voucher.getEndDate()))
            throw new OsirisValidationException("date");

        validationHelper.validateString(voucher.getCode(), true, "code");

        if (voucher.getDiscountAmount() != null)
            validationHelper.validateBigDecimal(voucher.getDiscountAmount(), false, "discountAmount");
        if (voucher.getDiscountRate() != null)
            validationHelper.validateBigDecimal(voucher.getDiscountRate(), false, "discountRate");

        if (voucher.getCode() != null && voucherService.getVoucherByCode(voucher.getCode()) != null)
            throw new OsirisValidationException("codeVoucher");

        if (voucher.getDiscountRate() != null && voucher.getDiscountRate().compareTo(BigDecimal.ZERO) < 0
                && voucher.getDiscountRate().compareTo(BigDecimal.valueOf(100)) > 0)
            throw new OsirisValidationException("codeVoucher");

        if (voucher.getTotalLimit() != null && voucher.getPerUserLimit() != null
                && voucher.getPerUserLimit() > voucher.getTotalLimit())
            throw new OsirisClientMessageException(
                    "Nombre total d'utilisations trop faible");

        if (voucher.getDiscountAmount() != null && voucher.getDiscountRate() != null)
            throw new OsirisClientMessageException(
                    "Impossible de renseigner un montant et un taux de réduction à la fois");
        return true;
    }

}
