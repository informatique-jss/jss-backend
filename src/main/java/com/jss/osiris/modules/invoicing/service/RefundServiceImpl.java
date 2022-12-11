package com.jss.osiris.modules.invoicing.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.model.RefundSearch;
import com.jss.osiris.modules.invoicing.model.RefundSearchResult;
import com.jss.osiris.modules.invoicing.repository.RefundRepository;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    RefundRepository refundRepository;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    ConstantService constantService;

    @Autowired
    DocumentService documentService;

    @Autowired
    IndexEntityService indexEntityService;

    @Override
    public List<Refund> getRefunds() {
        return IterableUtils.toList(refundRepository.findAll());
    }

    @Override
    public Refund getRefund(Integer id) {
        Optional<Refund> refund = refundRepository.findById(id);
        if (refund.isPresent())
            return refund.get();
        return null;
    }

    @Override
    public Refund addOrUpdateRefund(
            Refund refund) {
        refundRepository.save(refund);
        indexEntityService.indexEntity(refund, refund.getId());
        return refund;
    }

    @Override
    public void reindexRefunds() {
        List<Refund> refunds = getRefunds();
        if (refunds != null)
            for (Refund refund : refunds)
                indexEntityService.indexEntity(refund, refund.getId());
    }

    @Override
    public List<RefundSearchResult> searchRefunds(RefundSearch refundSearch) {
        return refundRepository.findRefunds(
                refundSearch.getStartDate(),
                refundSearch.getEndDate(), refundSearch.getMinAmount(), refundSearch.getMaxAmount(),
                refundSearch.getLabel(), refundSearch.isHideExportedRefunds(), refundSearch.isHideMatchedRefunds());
    }

    @Override
    public void generateRefund(ITiers tiersRefund, Affaire affaireRefund, Payment payment, Deposit deposit,
            Float amount)
            throws OsirisException, OsirisClientMessageException {
        Refund refund = new Refund();
        if (tiersRefund instanceof Confrere)
            refund.setConfrere((Confrere) tiersRefund);
        if (tiersRefund instanceof Tiers)
            refund.setTiers((Tiers) tiersRefund);
        if (affaireRefund != null) {
            refund.setAffaire(affaireRefund);
            refund.setRefundIBAN(affaireRefund.getPaymentIban());
            refund.setRefundType(constantService.getRefundTypeVirement());
        } else {
            Document refundDocument = documentService.getRefundDocument(tiersRefund.getDocuments());
            if (refundDocument != null) {
                refund.setRefundType(refundDocument.getRefundType());
                refund.setRefundIBAN(refundDocument.getRefundIBAN());
            }
        }

        if (refund.getRefundIBAN() == null || refund.getRefundType() == null)
            throw new OsirisClientMessageException(
                    "IBAN non trouvé pour effectuer le remboursement. Merci de renseigner le tiers ou l'affaire. L'opération est annulée.");

        if (payment != null) {
            refund.setLabel("Remboursement du paiement n°" + payment.getId());
            refund.setPayment(payment);
        } else if (deposit != null) {
            refund.setLabel("Remboursement de l'acompte n°" + deposit
                    .getId());
            refund.setDeposit(deposit);
        }
        refund.setRefundAmount(amount);
        refund.setIsMatched(false);
        refund.setIsAlreadyExported(false);
        refund.setRefundDateTime(LocalDateTime.now());
        this.addOrUpdateRefund(refund);
        // TODO : fait au moment de la génération du remboursement ou au moment de sa
        // constatation sur le compte ?
        accountingRecordService.generateAccountingRecordsForRefund(refund);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File getRefundExport(RefundSearch refundSearch) throws OsirisException {

        List<RefundSearchResult> refunds = searchRefunds(refundSearch);

        if (refunds != null && refunds.size() > 0) {
            File csvOutputFile;
            try {
                csvOutputFile = File.createTempFile("refundExport", "csv");
            } catch (IOException e1) {
                throw new OsirisException("Impossible to creat temp file for refund export");
            }

            List<String[]> dataLines = new ArrayList<>();

            for (RefundSearchResult refund : refunds)
                dataLines.add(new String[] { refund.getId() + "", refund.getRefundLabel(), refund.getRefundIban(),
                        refund.getRefundAmount() + "" });

            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                dataLines.stream()
                        .map(this::convertToCSV)
                        .forEach(pw::println);
            } catch (FileNotFoundException e) {
                throw new OsirisException("Impossible to read file " + csvOutputFile.getAbsolutePath());
            }

            for (RefundSearchResult refund : refunds) {
                Refund fetchedRefund = getRefund(refund.getId());
                fetchedRefund.setIsAlreadyExported(true);
                addOrUpdateRefund(fetchedRefund);
            }
            return csvOutputFile;
        }
        return null;
    }

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(";"));
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null)
            return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(";") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
