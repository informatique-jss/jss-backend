package com.jss.osiris.modules.osiris.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.GuMatchingResultDto;
import com.jss.osiris.modules.osiris.invoicing.model.InpiInvoicingExtract;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.MatchingStatusEnum;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.Cart;

@Service
public class GuMatchingServiceImpl implements GuMatchingService {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    InpiInvoicingExtractService inpiInvoicingExtractService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Autowired
    ConstantService constantService;

    private static final BigDecimal TOLERANCE = new BigDecimal("0.02");

    @Override
    public List<GuMatchingResultDto> getInpiMatchingResult(LocalDateTime startDate,
            LocalDateTime endDate) throws OsirisException {
        List<GuMatchingResultDto> guMatchingResult = new ArrayList<>();

        List<InpiInvoicingExtract> inpiExtract = inpiInvoicingExtractService
                .getInpiInvoicingExtractByDateBetween(startDate.toLocalDate(), endDate.toLocalDate());

        List<Invoice> invoices = invoiceService.getInvoicesByProviderAndInvoiceStatusInAndCreatedDateBetween(
                constantService.getCompetentAuthorityInpi().getProvider(),
                Arrays.asList(constantService.getInvoiceStatusReceived(), constantService.getInvoiceStatusPayed()),
                startDate,
                endDate);

        Map<String, Invoice> osirisInvoicesByInpiOrder = new HashMap<String, Invoice>();
        // Prepare map of osiris invoices by inpi order
        for (Invoice invoice : invoices) {
            Cart cart = invoice.getCart();
            if (cart == null)
                continue;

            Long inpiOrder = cart.getMipOrderNum();
            boolean isCredit = Boolean.TRUE.equals(invoice.getIsCreditNote());

            if (inpiOrder != null) {
                String key = buildKey(inpiOrder, isCredit);
                osirisInvoicesByInpiOrder.put(key, invoice);
            }
        }

        // start matching process
        Set<Invoice> matchedInvoices = new HashSet<>();
        for (InpiInvoicingExtract inpiExtr : inpiExtract) {
            Long order = inpiExtr.getInpiOrder() != null
                    ? inpiExtr.getInpiOrder().longValue()
                    : null;

            boolean isCredit = Boolean.TRUE.equals(inpiExtr.isCreditNote());
            Invoice invoice = null;

            if (order != null) {
                String key = buildKey(order, isCredit);
                invoice = osirisInvoicesByInpiOrder.get(key);
            }

            if (invoice == null) {
                guMatchingResult
                        .add(buildGuMatchingResultDto(inpiExtr, null, MatchingStatusEnum.MISSING_IN_OSIRIS.label));
                continue;
            }
            matchedInvoices.add(invoice);

            BigDecimal inpiAmount = inpiExtr.getPreTaxPrice()
                    .add(inpiExtr.getVatPrice());

            BigDecimal osirisAmount = invoiceHelper.getPriceTotal(invoice);

            if (inpiAmount.compareTo(osirisAmount) != 0) {
                // if (isAmountMismatch(inpiAmount, osirisAmount)) {
                guMatchingResult
                        .add(buildGuMatchingResultDto(inpiExtr, invoice, MatchingStatusEnum.AMOUNT_MISMATCH.label));
            }
        }
        // missing in inpi extract
        for (Invoice invoice : osirisInvoicesByInpiOrder.values()) {

            if (!matchedInvoices.contains(invoice)) {
                guMatchingResult
                        .add(buildGuMatchingResultDto(null, invoice, MatchingStatusEnum.MISSING_IN_INPI_EXTRACT.label));
            }
        }

        return guMatchingResult;
    }

    private String buildKey(Long inpiOrder, boolean isCredit) {
        if (inpiOrder == null)
            return null;
        return inpiOrder + "_" + (Boolean.TRUE.equals(isCredit) ? "C" : "D");
    }

    private GuMatchingResultDto buildGuMatchingResultDto(
            InpiInvoicingExtract inpi,
            Invoice invoice,
            String matchingStatus) {
        GuMatchingResultDto dto = new GuMatchingResultDto();

        if (inpi != null) {
            dto.setInpiAmount(inpi.getPreTaxPrice().add(inpi.getVatPrice()));
            dto.setLiasseNumber(inpi.getLiasseNumber());
            dto.setDate(inpi.getAccountingDate());
        }

        if (invoice != null) {
            dto.setInvoiceId(invoice.getId());
            dto.setOsirisAmount(invoiceHelper.getPriceTotal(invoice));
            dto.setDate(invoice.getCreatedDate().toLocalDate());
            dto.setCustomerOrderId(invoice.getCustomerOrder() != null ? invoice.getCustomerOrder().getId() : null);
            if (inpi == null) {
                dto.setLiasseNumber(invoice.getCart().getFormaliteGuichetUnique() != null
                        ? invoice.getCart().getFormaliteGuichetUnique().getLiasseNumber()
                        : "");
            }
        }
        dto.setMatchingStatus(matchingStatus);

        return dto;
    }

    private boolean isAmountMismatch(BigDecimal inpiAmount, BigDecimal osirisAmount) {

        BigDecimal inpiNorm = normalize(inpiAmount);
        BigDecimal osirisNorm = normalize(osirisAmount);

        if (inpiNorm == null || osirisNorm == null)
            return true;

        BigDecimal diff = inpiNorm.subtract(osirisNorm).abs();

        return diff.compareTo(TOLERANCE) > 0;
    }

    private BigDecimal normalize(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

}
