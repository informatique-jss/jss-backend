package com.jss.osiris.modules.osiris.accounting.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;

@Service
public class ReconciliationServiceImpl {

    @Autowired
    PaymentService paymentService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    ConstantService constantService;

    private LocalDate currentClosedAccountingDate = null;

    @Transactional
    public void validateAllPayments() throws OsirisException {

        currentClosedAccountingDate = constantService.getDateAccountingClosureForAccountant();

        Integer lastProcessedId = 6320758;
        int pageSize = 1000;

        while (lastProcessedId < 8369620) {
            Integer endId = lastProcessedId + pageSize;
            List<Payment> payments = paymentService.getPaymentsForReconciliation(lastProcessedId, endId);
            lastProcessedId = lastProcessedId + pageSize;

            for (Payment payment : payments) {
                System.out.println(payments.indexOf(payment) + "/" + payments.size() + " / " + payment.getId());

                if (payment.getBankId() != null && payment.getBankId().startsWith("H")
                        && payment.getOriginPayment() == null) {
                    if (validateGrappeGlobale(payment))
                        validateGrappe(payment);
                } else if (payment.getBankId() == null && payment.getOriginPayment() == null
                        && payment.getPaymentType().getId().equals(114735)
                        && payment.getCheckNumber() != null) {
                    validateGrappe(payment);
                } else if (payment.getBankId() == null && payment.getOriginPayment() == null
                        && payment.getAccountingRecords() != null && payment.getPaymentType().getId().equals(112720)
                        && payment.getBankTransfert() != null) {
                    validateGrappe(payment);
                } else if (payment.getBankId() == null && payment.getOriginPayment() == null
                        && payment.getAccountingRecords() != null && payment.getPaymentType().getId().equals(72561)
                        && payment.getDirectDebitTransfert() != null) {
                    validateGrappe(payment);
                } else if (isCbRefund(payment)) {
                    validateGrappe(payment);
                } else if (isCaisseRefund(payment)) {
                    validateGrappe(payment);
                } else if (payment.getAccountingRecords() != null && hasAccountingRecordInBank(payment)) {
                    for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
                        accountingRecord.setEquilibrated(null);
                        accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, true);
                    }
                }
            }
        }
    }

    private boolean isFromANAndTop(Payment payment) {
        if (!hasAccountingRecordInBank(payment))
            return false;

        return payment.getPaymentDate() != null &&
                payment.getAccountingRecords().get(0).getOperationDateTime() != null
                && payment.getPaymentDate().getYear() != payment.getAccountingRecords().get(0).getOperationDateTime()
                        .getYear()
                && payment.getPaymentDate().getYear() < currentClosedAccountingDate.getYear()
                && (payment.getOriginPayment() == null || !hasAccountingRecordInBank(payment.getOriginPayment()));
    }

    private boolean isFromAn(Payment payment) {
        if (!hasAccountingRecordInBank(payment))
            return false;

        if (payment.getAccountingRecords() != null)
            for (AccountingRecord record : payment.getAccountingRecords())
                if (record.getIsANouveau())
                    return true;

        return false;
    }

    private boolean hasAccountingRecordInBank(Payment payment) {
        if (payment.getAccountingRecords() != null) {
            for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
                if (accountingRecord.getAccountingAccount().getId().equals(115311))
                    return true;
            }
        }
        return false;
    }

    private boolean hasAccountingRecordInBankCdn(Payment payment) {
        if (payment.getAccountingRecords() != null) {
            for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
                if (accountingRecord.getAccountingAccount().getId().equals(128557))
                    return true;
            }
        }
        return false;
    }

    private boolean isCbRefund(Payment payment) {
        return payment.getOriginPayment() != null && payment.getOriginPayment().getPaymentType().getId().equals(72562);
    }

    private boolean isCaisseRefund(Payment payment) {
        return payment.getOriginPayment() != null && payment.getOriginPayment().getPaymentType().getId().equals(114757);
    }

    private boolean isCaisse(Payment payment) {
        return payment.getOriginPayment() == null && payment.getPaymentType().getId().equals(114757);
    }

    private boolean isAs400Refund(Payment payment) {
        if (payment.getAccountingRecords() == null || payment.getAccountingRecords().size() == 0)
            return false;

        return hasAccountingRecordInBankCdn(payment);
    }

    private void validateGrappe(Payment payment) throws OsirisException {
        validateNode(payment);
    }

    private List<Payment> getAllPaymentGrappe(Payment payment, List<Payment> payments) {
        payments.add(payment);
        if (payment.getChildrenPayments() != null) {
            for (Payment child : payment.getChildrenPayments()) {
                getAllPaymentGrappe(child, payments);
            }
        }
        return payments;
    }

    private boolean validateGrappeGlobale(Payment root) throws OsirisException {
        List<Payment> allNodes = new ArrayList<Payment>();
        allNodes = getAllPaymentGrappe(root, allNodes);

        BigDecimal totalBank = BigDecimal.ZERO;
        if (allNodes != null)
            for (Payment node : allNodes)
                if (node.getBankId() != null && node.getBankId().startsWith("H"))
                    if (node.getPaymentDate().getYear() >= currentClosedAccountingDate.getYear())
                        totalBank = totalBank.add(node.getPaymentAmount());

        BigDecimal totalAccounting = BigDecimal.ZERO;
        if (allNodes != null)
            for (Payment node : allNodes)
                if (node.getAccountingRecords() != null)
                    for (AccountingRecord record : node.getAccountingRecords())
                        if (record.getAccountingAccount().getId().equals(115311)) {
                            if (record.getDebitAmount() != null)
                                totalAccounting = totalAccounting.add(record.getDebitAmount());
                            if (record.getCreditAmount() != null)
                                totalAccounting = totalAccounting.subtract(record.getCreditAmount());
                        }

        // 4. La comparaison magique
        if (totalBank.compareTo(totalAccounting) != 0) {
            for (Payment node : allNodes) {
                if (node.getAccountingRecords() != null && hasAccountingRecordInBank(node)) {
                    String nodeState = "ko";
                    for (AccountingRecord accountingRecord : node.getAccountingRecords()) {
                        if (!nodeState.equals(accountingRecord.getEquilibrated())) {
                            accountingRecord.setEquilibrated(nodeState);
                            if (accountingRecord.getAccountingAccount().getId().equals(115311)) {
                                accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, true);
                            }
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    private BigDecimal getAccountingBalanceForPayment(Payment payment) {
        BigDecimal balance = BigDecimal.ZERO;

        if (payment.getAccountingRecords() == null)
            return balance;

        if (payment != null && payment.getAccountingRecords() != null) {
            for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
                if (!Boolean.TRUE.equals(accountingRecord.getIsANouveau()))
                    if (accountingRecord.getCreditAmount() != null)
                        balance = balance.add(accountingRecord.getCreditAmount());
                    else
                        balance = balance.subtract(accountingRecord.getDebitAmount());
            }
        }
        return balance.setScale(2, RoundingMode.HALF_EVEN);
    }

    public boolean isAccountingConsistentWithAmount(Payment payment) {
        BigDecimal balance = BigDecimal.ZERO;
        if (payment != null && payment.getAccountingRecords() != null) {
            for (AccountingRecord accountingRecord : payment.getAccountingRecords()) {
                if (accountingRecord.getAccountingAccount().getId().equals(115311))
                    if (accountingRecord.getCreditAmount() != null)
                        balance = balance.subtract(accountingRecord.getCreditAmount());
                    else
                        balance = balance.add(accountingRecord.getDebitAmount());
            }
        }

        BigDecimal amountToCompare = BigDecimal.ZERO;
        if (payment.getAccountingRecords().size() == 1
                && Boolean.TRUE.equals(payment.getAccountingRecords().get(0).getIsANouveau())) {
            amountToCompare = BigDecimal.ZERO;
        } else if (payment.getRefund() == null && isFromANAndTop(payment)) {
            amountToCompare = payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN).negate();
        } else if (Boolean.TRUE.equals(payment.getIsCancelled()) && isFromANAndTop(payment)) {
            amountToCompare = payment.getPaymentAmount().negate();
        } else if (Boolean.TRUE.equals(payment.getIsCancelled())) {
            amountToCompare = BigDecimal.ZERO;
            // Refund a payment, from bank to bank, so 0 € diff
        } else if (payment.getRefund() != null && Boolean.TRUE.equals(payment.getRefund().getIsAlreadyExported())) {
            if (payment.getOriginPayment() != null && isCbRefund(payment.getOriginPayment()))
                amountToCompare = payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN);
            else if (payment.getOriginPayment() != null && isCaisseRefund(payment.getOriginPayment()))
                amountToCompare = payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN);
            else if (payment.getOriginPayment() != null && isAs400Refund(payment.getOriginPayment()))
                amountToCompare = payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN);
            else if (isFromANAndTop(payment))
                amountToCompare = payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN).negate();
            else if (isFromAn(payment))
                amountToCompare = payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN);
            else
                amountToCompare = BigDecimal.ZERO;
        } else if (payment.getRefund() != null && !Boolean.TRUE.equals(payment.getRefund().getIsAlreadyExported())) {
            amountToCompare = payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN).negate();
        } else
            amountToCompare = payment.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN);
        return balance.setScale(2, RoundingMode.HALF_EVEN).compareTo(amountToCompare) == 0;
    }

    private void validateNode(Payment node) throws OsirisException {
        boolean isNodeOk = true;

        if (getAccountingBalanceForPayment(node).compareTo(BigDecimal.ZERO) != 0) {
            isNodeOk = false;
        }

        if (!isAccountingConsistentWithAmount(node)) {
            isNodeOk = false;
        }

        BigDecimal sumChildren = BigDecimal.ZERO;
        if (node.getChildrenPayments() != null && node.getChildrenPayments().size() > 0) {
            for (Payment p : node.getChildrenPayments()) {
                boolean done = false;

                // If current payment is not refund, sub payment is refund, total amount
                // decrease
                if (node.getRefund() == null && p.getRefund() != null) {
                    sumChildren = sumChildren.add(p.getPaymentAmount().negate());
                    done = true;
                }

                // If current payment has refund attached but is from bank, sub payment is a
                // payment refund, so same rule than previous
                if (node.getBankId() != null && node.getRefund() != null
                        && node.getPaymentAmount().compareTo(BigDecimal.ZERO) > 0
                        && p.getRefund() != null
                        && node.getRefund().getId().equals(p.getRefund().getId())) {
                    sumChildren = sumChildren.add(p.getPaymentAmount().negate());
                    done = true;
                }

                // If current payment and sub payment have different refunds attached, same rule
                // than previous
                if (node.getBankId() == null && node.getRefund() != null && p.getRefund() != null
                        && !node.getRefund().getId().equals(p.getRefund().getId())) {
                    sumChildren = sumChildren.add(p.getPaymentAmount().negate());
                    done = true;
                }

                if (!done)
                    sumChildren = sumChildren.add(p.getPaymentAmount());

            }

            if (node.getPaymentAmount().setScale(2, RoundingMode.HALF_EVEN)
                    .compareTo(sumChildren.setScale(2, RoundingMode.HALF_EVEN)) != 0) {
                isNodeOk = false;
            }
        }

        if (node.getAccountingRecords() != null && hasAccountingRecordInBank(node)) {
            String nodeState = isNodeOk ? "ok" : "ko";
            for (AccountingRecord accountingRecord : node.getAccountingRecords()) {
                if (!nodeState.equals(accountingRecord.getEquilibrated())) {
                    accountingRecord.setEquilibrated(nodeState);
                    if (accountingRecord.getAccountingAccount().getId().equals(115311)) {
                        accountingRecordService.addOrUpdateAccountingRecord(accountingRecord, true);
                        if (!isNodeOk) {
                            System.out.println("KO sur : " + node.getId());
                            System.out.println("next");
                        }
                    }
                }
            }
        }

        for (Payment child : node.getChildrenPayments()) {
            if (!child.getId().equals(node.getId()))
                validateNode(child);
        }
    }
}
