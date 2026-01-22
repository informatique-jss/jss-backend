package com.jss.osiris.modules.osiris.invoicing.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.model.PaymentDto;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentHelper;

@Service
public class InvoicingDtoHelper {

    @Autowired
    private PaymentHelper paymentHelper;

    public List<PaymentDto> mapPayments(List<Payment> payments) {
        List<PaymentDto> outPayment = new ArrayList<PaymentDto>();
        if (payments != null) {
            for (Payment payment : payments) {
                outPayment.add(mapPaymentToPaymentDto(payment));
            }
        }
        return outPayment;
    }

    public Page<PaymentDto> mapPayments(Page<Payment> payments) {
        List<PaymentDto> outPayment = new ArrayList<PaymentDto>();
        if (payments != null) {
            for (Payment payment : payments) {
                outPayment.add(mapPaymentToPaymentDto(payment));
            }
            return new PageImpl<>(outPayment, payments.getPageable(), payments.getTotalElements());
        }
        return new PageImpl<>(outPayment);
    }

    public PaymentDto mapPayment(Payment payment) {
        PaymentDto outPayment = new PaymentDto();
        if (payment != null) {
            outPayment = mapPaymentToPaymentDto(payment);
            mapPaymentDetailsToPaymentDto(outPayment, payment);
        }
        return outPayment;
    }

    private PaymentDto mapPaymentToPaymentDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setOriginPayment(payment.getOriginPayment() != null ? payment.getOriginPayment().getId() : null);
        paymentDto.setPaymentDate(payment.getPaymentDate());
        paymentDto.setPaymentAmount(payment.getPaymentAmount());
        paymentDto.setPaymentType(payment.getPaymentType() != null ? payment.getPaymentType().getLabel() : null);
        paymentDto.setLabel(payment.getLabel());
        paymentDto.setIsAssociated(
                payment.getIsExternallyAssociated() && paymentHelper.isPaymentAssociatedInOsiris(payment));
        paymentDto.setIsCancelled(payment.getIsCancelled());
        paymentDto.setIsAppoint(payment.getIsAppoint());
        paymentDto.setInvoiceId(payment.getInvoice() != null ? payment.getInvoice().getId() : null);
        paymentDto.setCustomerOrderId(payment.getCustomerOrder() != null ? payment.getCustomerOrder().getId() : null);
        paymentDto.setComment(payment.getComment());

        return paymentDto;
    }

    // Details of the payment
    private PaymentDto mapPaymentDetailsToPaymentDto(PaymentDto paymentDto, Payment payment) {
        return paymentDto;
    }
}
