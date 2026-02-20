package com.jss.osiris.modules.osiris.accounting.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.model.PaySlipLineType;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.BulletinLigne;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.NibelisEmployee;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.PaySlip;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.Salarie;
import com.jss.osiris.modules.osiris.accounting.repository.NibelisEmployeeRepository;
import com.jss.osiris.modules.osiris.accounting.repository.PaySlipRepository;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;

@Service
public class NibelisServiceImpl implements NibelisService {

    @Autowired
    private PaySlipRepository paySlipRepository;

    @Autowired
    private NibelisEmployeeRepository employeeRepository;

    @Autowired
    private NibelisDelegateService nibelisDelegateService;

    @Autowired
    PaySlipLineTypeService paySlipLineTypeService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    ConstantService constantService;

    public List<PaySlip> getAllPaySlips(LocalDate period) throws OsirisException {
        List<PaySlip> paySlips = new ArrayList<PaySlip>();
        List<Salarie> salaries = nibelisDelegateService.getSalaries();
        if (salaries != null) {
            for (Salarie salarie : salaries) {
                List<BulletinLigne> bulletinLines = nibelisDelegateService.getBulletin(salarie.getId_nibelis(), period);
                NibelisEmployee employee = saveEmployee(salarie);
                paySlips.addAll(savePaySlips(bulletinLines, employee, period));
            }
        }
        return paySlips;
    }

    private List<PaySlip> savePaySlips(List<BulletinLigne> bulletinLines, NibelisEmployee salarie, LocalDate period) {
        List<PaySlip> existingPayslips = paySlipRepository.findByEmployeeAndMonth(salarie,
                period.getYear() + "-" + period.getMonthValue());
        if (existingPayslips != null)
            for (PaySlip paySlip : existingPayslips)
                paySlipRepository.delete(paySlip);

        List<PaySlip> paySlips = new ArrayList<PaySlip>();

        for (BulletinLigne bulletinLine : bulletinLines) {
            PaySlip paySlip = mapToPaySlip(bulletinLine, salarie, period);
            paySlips.add(paySlipRepository.save(paySlip));
        }
        return paySlips;
    }

    private PaySlip mapToPaySlip(BulletinLigne bulletinLine, NibelisEmployee salarie, LocalDate period) {
        PaySlip paySlip = new PaySlip();
        paySlip.setEmployee(salarie);
        paySlip.setMonth(period.getYear() + "-" + period.getMonthValue());
        paySlip.setLineOrder(bulletinLine.getOrdre());
        paySlip.setSectionId(bulletinLine.getId_rubrique());
        paySlip.setSectionCode(bulletinLine.getCode_rubrique());
        paySlip.setSectionType(bulletinLine.getType_rubrique());
        paySlip.setSectionTypeLabel(bulletinLine.getType_rubrique_libelle());
        paySlip.setLabel(bulletinLine.getLibelle());
        paySlip.setNumber(bulletinLine.getNombre());
        paySlip.setBase(bulletinLine.getBase());
        paySlip.setEmployeeRate(bulletinLine.getTaux_salariale());
        paySlip.setEmployeeAmount(bulletinLine.getMontant_salariale());
        paySlip.setEmployerRate(bulletinLine.getTaux_patronale());
        paySlip.setEmployerAmount(bulletinLine.getMontant_patronale());
        paySlip.setEmployerCoefficient(bulletinLine.getCoefficient_patronale());
        return paySlip;
    }

    private NibelisEmployee saveEmployee(Salarie salarie) {
        NibelisEmployee employee = mapToEmployee(salarie);
        return employeeRepository.save(employee);
    }

    private NibelisEmployee mapToEmployee(Salarie salarie) {
        NibelisEmployee employee = new NibelisEmployee();
        employee.setCompanyId(salarie.getId_societe().intValue());
        employee.setEstablishmentId(salarie.getId_etablissement().intValue());
        employee.setNibelisId(salarie.getId_nibelis().intValue());
        employee.setEmployeeId(salarie.getMatricule());
        employee.setName(salarie.getNom());
        employee.setHireDate(
                salarie.getDate_embauche() != null && !salarie.getDate_embauche().equals("")
                        ? LocalDate.parse(salarie.getDate_embauche())
                        : null);
        employee.setSeniorityDate(
                salarie.getDate_anciennete() != null && !salarie.getDate_anciennete().equals("")
                        ? LocalDate.parse(salarie.getDate_anciennete())
                        : null);
        employee.setDepartureDate(salarie.getDate_depart() != null && !salarie.getDate_depart().equals("")
                ? LocalDate.parse(salarie.getDate_depart())
                : null);
        employee.setContractStartDate(
                salarie.getDate_debut_contrat() != null && !salarie.getDate_debut_contrat().equals("")
                        ? LocalDate.parse(salarie.getDate_debut_contrat())
                        : null);
        employee.setContractEndDate(
                salarie.getDate_fin_contrat() != null && !salarie.getDate_fin_contrat().equals("")
                        ? LocalDate.parse(salarie.getDate_fin_contrat())
                        : null);
        employee.setContractNumber(salarie.getNumero_contrat());
        employee.setPresentPeriod(salarie.getPresent_periode());
        employee.setBulletinPeriod(salarie.getBulletin_periode());
        employee.setMultiSheet(salarie.getMulti_fiche());
        employee.setLineOrder(salarie.getOrdr());
        return employee;
    }

    @Override
    @Transactional(dontRollbackOn = Exception.class)
    public List<AccountingRecord> generateAccountingRecordForPayslip(LocalDate period, boolean persist)
            throws OsirisException {
        List<PaySlip> payslips = getAllPaySlips(period);

        Map<Integer, BigDecimal> balanceMap = new HashMap<>();
        boolean isError = false;

        if (payslips != null) {
            for (PaySlip payslip : payslips) {
                PaySlipLineType type = paySlipLineTypeService.getPaySlipLineTypeByLabel(payslip.getLabel());

                if (type == null) {
                    isError = true;
                    PaySlipLineType lineType = new PaySlipLineType();
                    lineType.setLabel(payslip.getLabel());
                    lineType.setCode(lineType.getLabel().replaceAll(" ", "_").toUpperCase());
                    paySlipLineTypeService.addOrUpdatePaySlipLineType(lineType);
                    continue;
                }

                if (Boolean.TRUE.equals(type.getIsNotToUse())) {
                    continue;
                }

                if (type.getAccountingAccountDebit() != null) {
                    BigDecimal debitAmount = BigDecimal.ZERO;
                    if (Boolean.TRUE.equals(type.getIsUseEmployerPart()) && payslip.getEmployerAmount() != null) {
                        debitAmount = new BigDecimal(payslip.getEmployerAmount().toString()).abs();
                    } else if (payslip.getEmployeeAmount() != null) {
                        debitAmount = new BigDecimal(payslip.getEmployeeAmount().toString()).abs();
                    }

                    if (debitAmount.compareTo(BigDecimal.ZERO) != 0) {
                        balanceMap.merge(type.getAccountingAccountDebit().getId(), debitAmount, BigDecimal::add);
                    }
                }

                if (type.getAccountingAccountCredit() != null) {
                    BigDecimal creditAmount = BigDecimal.ZERO;
                    String accNumber = type.getAccountingAccountCredit().getPrincipalAccountingAccount().getCode();

                    if (accNumber != null && accNumber.startsWith("43")) {
                        BigDecimal partPatronale = payslip.getEmployerAmount() != null
                                ? new BigDecimal(payslip.getEmployerAmount().toString()).abs()
                                : BigDecimal.ZERO;
                        BigDecimal partSalariale = payslip.getEmployeeAmount() != null
                                ? new BigDecimal(payslip.getEmployeeAmount().toString()).abs()
                                : BigDecimal.ZERO;
                        creditAmount = partPatronale.add(partSalariale);
                    } else {
                        if (Boolean.TRUE.equals(type.getIsUseEmployerPart()) && payslip.getEmployerAmount() != null) {
                            creditAmount = new BigDecimal(payslip.getEmployerAmount().toString()).abs();
                        } else if (payslip.getEmployeeAmount() != null) {
                            creditAmount = new BigDecimal(payslip.getEmployeeAmount().toString()).abs();
                        }
                    }

                    if (creditAmount.compareTo(BigDecimal.ZERO) != 0) {
                        balanceMap.merge(type.getAccountingAccountCredit().getId(), creditAmount.negate(),
                                BigDecimal::add);
                    }
                }
            }
        }

        if (isError) {
            throw new OsirisException("Error: Missing mapping for some payslip lines.");
        }

        AccountingJournal miscellaneousJournal = constantService.getAccountingJournalMiscellaneousOperations();
        List<AccountingRecord> records = new ArrayList<>();

        for (Map.Entry<Integer, BigDecimal> entry : balanceMap.entrySet()) {
            BigDecimal balance = entry.getValue();

            if (balance.compareTo(BigDecimal.ZERO) == 0)
                continue;

            AccountingRecord record = new AccountingRecord();
            record.setOperationDateTime(period.atTime(12, 0));
            record.setAccountingAccount(accountingAccountService.getAccountingAccount(entry.getKey()));
            record.setAccountingJournal(miscellaneousJournal);
            record.setLabel("Paie " + period.getMonthValue() + "-" + period.getYear());

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                record.setDebitAmount(balance);
                record.setCreditAmount(BigDecimal.ZERO);
            } else {
                record.setDebitAmount(BigDecimal.ZERO);
                record.setCreditAmount(balance.abs());
            }
            records.add(record);
        }
        return records;
    }
}