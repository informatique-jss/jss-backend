package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearch;
import com.jss.osiris.modules.accounting.repository.AccountingRecordRepository;
import com.jss.osiris.modules.miscellaneous.service.VatService;
import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.quotation.model.InvoiceItem;
import com.jss.osiris.modules.quotation.service.InvoiceItemService;
import com.jss.osiris.modules.quotation.service.InvoiceService;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class AccountingRecordServiceImpl implements AccountingRecordService {

    @Autowired
    AccountingRecordRepository accountingRecordRepository;

    @Autowired
    InvoiceItemService invoiceItemService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    VatService vatService;

    @Autowired
    AccountingJournalService accountingJournalService;

    @Override
    @Cacheable(value = "accountingRecordList", key = "#root.methodName")
    public List<AccountingRecord> getAccountingRecords() {
        return IterableUtils.toList(accountingRecordRepository.findAll());
    }

    @Override
    @Cacheable(value = "accountingRecord", key = "#id")
    public AccountingRecord getAccountingRecord(Integer id) {
        Optional<AccountingRecord> accountingRecord = accountingRecordRepository.findById(id);
        if (!accountingRecord.isEmpty())
            return accountingRecord.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "accountingRecordList", allEntries = true),
            @CacheEvict(value = "accountingRecord", key = "#accountingRecord.id")
    })
    public AccountingRecord addOrUpdateAccountingRecord(
            AccountingRecord accountingRecord) {
        return accountingRecordRepository.save(accountingRecord);
    }

    public boolean writeAccountingRecordForInvoiceIssuance(Invoice invoice) {

        return false;
    }

    public void generateAccountingRecordsForSaleOnInvoiceGeneration(Invoice invoice) throws Exception {
        AccountingJournal salesJournal = accountingJournalService.getSalesAccountingJournal();

        if (invoice == null)
            throw new Exception("No invoice provided");

        if (invoice.getCustomerOrder() == null)
            throw new Exception("No customer order in invoice " + invoice.getId());

        ITiers customerOrder = invoiceService.getCustomerOrder(invoice);

        if (customerOrder.getAccountingAccountCustomer() == null)
            throw new Exception("No customer accounting account in ITiers " + customerOrder.getId());

        // One write on customer account for all invoice
        generateNewAccountingRecord(LocalDateTime.now(), null,
                "Commande n°" + invoice.getCustomerOrder().getId(), null,
                invoiceService.getPriceTotal(invoice),
                customerOrder.getAccountingAccountCustomer(), null, invoice, salesJournal);

        // For each invoice item, one write on product and VAT account for each invoice
        // item
        for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
            if (invoiceItem.getBillingItem() == null)
                throw new Exception("No billing item defined in invoice item n°" + invoiceItem.getId());

            if (invoiceItem.getBillingItem().getBillingType() == null)
                throw new Exception(
                        "No billing type defined in billing item n°" + invoiceItem.getBillingItem().getId());

            AccountingAccount producAccountingAccount = accountingAccountService
                    .getProductAccountingAccountFromAccountingAccountList(
                            invoiceItem.getBillingItem().getAccountingAccounts());

            if (producAccountingAccount == null)
                throw new Exception("No product accounting account defined in billing item n°"
                        + invoiceItem.getBillingItem().getId());

            generateNewAccountingRecord(LocalDateTime.now(), null,
                    "Commande n°" + invoice.getCustomerOrder().getId() + " - produit "
                            + invoiceItem.getBillingItem().getBillingType().getLabel(),
                    invoiceItem.getPreTaxPrice() - invoiceItem.getDiscountAmount(), null, producAccountingAccount,
                    invoiceItem, invoice, salesJournal);

            if (invoiceItem.getVat() != null)
                generateNewAccountingRecord(LocalDateTime.now(), null,
                        "Commande n°" + invoice.getCustomerOrder().getId() + " - TVA pour le produit "
                                + invoiceItem.getBillingItem().getBillingType().getLabel(),
                        invoiceItem.getVatPrice(), null, invoiceItem.getVat().getAccountingAccount(),
                        invoiceItem, invoice, salesJournal);
        }
    }

    private AccountingRecord generateNewAccountingRecord(LocalDateTime operationDatetime,
            String manualAccountingDocumentNumber,
            String label, Float creditAmount, Float debitAmount,
            AccountingAccount accountingAccount, InvoiceItem invoiceItem, Invoice invoice, AccountingJournal journal) {
        AccountingRecord accountingRecord = new AccountingRecord();
        accountingRecord.setOperationDateTime(operationDatetime);
        accountingRecord.setManualAccountingDocumentNumber(manualAccountingDocumentNumber);
        accountingRecord.setLabel(label);
        accountingRecord.setCreditAmount(creditAmount);
        accountingRecord.setDebitAmount(debitAmount);
        accountingRecord.setAccountingAccount(accountingAccount);
        accountingRecord.setIsTemporary(true);
        accountingRecord.setInvoiceItem(invoiceItem);
        accountingRecord.setInvoice(invoice);
        accountingRecord.setAccountingJournal(journal);
        accountingRecordRepository.save(accountingRecord);
        return accountingRecord;
    }

    @Override
    @Transactional
    public void dailyAccountClosing() {
        List<AccountingJournal> journals = accountingJournalService.getAccountingJournals();

        if (journals != null && journals.size() > 0) {
            for (AccountingJournal accountingJournal : journals) {
                List<AccountingRecord> accountingRecords = accountingRecordRepository
                        .findByAccountingJournalAndIsTemporary(accountingJournal, true);

                if (accountingRecords != null && accountingRecords.size() > 0) {
                    Integer maxIdOperation = accountingRecordRepository
                            .findMaxIdOperationForAccontingJournalAndMinOperationDateTime(
                                    accountingJournal,
                                    LocalDateTime.now().with(ChronoField.DAY_OF_YEAR, 1)
                                            .with(ChronoField.HOUR_OF_DAY, 0)
                                            .with(ChronoField.MINUTE_OF_DAY, 0).with(ChronoField.SECOND_OF_DAY, 0));

                    if (maxIdOperation == null)
                        maxIdOperation = 0;

                    for (AccountingRecord accountingRecord : accountingRecords) {
                        accountingRecord.setAccountingDateTime(LocalDateTime.now());
                        accountingRecord.setOperationId(maxIdOperation);
                        accountingRecordRepository.save(accountingRecord);
                        accountingRecord.setIsTemporary(false);
                        maxIdOperation++;
                    }
                }
            }
        }
    }

    @Override
    public List<AccountingRecord> searchAccountingRecords(AccountingRecordSearch accountingRecordSearch) {
        return accountingRecordRepository.searchAccountingRecords(accountingRecordSearch.getAccountingClass(),
                accountingRecordSearch.getAccountingAccount(), accountingRecordSearch.getAccountingJournal(),
                accountingRecordSearch.getStartDate(), accountingRecordSearch.getEndDate());
    }

    @Override
    public File getGrandLivre(AccountingAccountClass accountingAccountClass, LocalDateTime startDate,
            LocalDateTime endDate) throws Exception {
        List<AccountingRecord> accountingRecords = accountingRecordRepository
                .searchAccountingRecords(accountingAccountClass, null, null, startDate, endDate);

        List<AccountingAccount> accountingAccounts = getAccountingAccountInRecord(accountingRecords);

        XSSFWorkbook wb = new XSSFWorkbook();

        // Define style
        // Title
        XSSFCellStyle titleCellStyle = wb.createCellStyle();
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont titleFont = wb.createFont();
        titleFont.setBold(true);
        XSSFColor titleColor = new XSSFColor();
        titleColor.setARGBHex("0000FF");
        titleFont.setColor(titleColor);
        titleFont.setFontHeight(14);
        titleCellStyle.setFont(titleFont);
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderTop(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);

        // Header
        XSSFCellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        String rgbS = "FFFF99";
        byte[] rgbB = Hex.decodeHex(rgbS);
        XSSFColor color = new XSSFColor(rgbB, null);
        headerCellStyle.setFillForegroundColor(color);
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Record line
        XSSFCellStyle recordCellStyle = wb.createCellStyle();
        recordCellStyle.setBorderBottom(BorderStyle.THIN);
        recordCellStyle.setBorderTop(BorderStyle.THIN);
        recordCellStyle.setBorderRight(BorderStyle.THIN);
        recordCellStyle.setBorderLeft(BorderStyle.THIN);

        // Debit / credit cells
        XSSFCellStyle styleCurrency = wb.createCellStyle();
        styleCurrency.setBorderBottom(BorderStyle.THIN);
        styleCurrency.setBorderTop(BorderStyle.THIN);
        styleCurrency.setBorderRight(BorderStyle.THIN);
        styleCurrency.setBorderLeft(BorderStyle.THIN);
        styleCurrency.setDataFormat((short) 8);

        for (AccountingAccount accountingAccount : accountingAccounts) {
            XSSFSheet currentSheet = wb.createSheet("C_" + accountingAccount.getAccountingAccountNumber()
                    + accountingAccount.getAccountingAccountSubNumber());

            // Title
            int currentLine = 0;

            XSSFRow currentRow = currentSheet.createRow(currentLine++);
            XSSFCell currentCell = currentRow.createCell(0);
            currentCell.setCellValue("Compte : " + accountingAccount.getLabel());

            CellRangeAddress region = new CellRangeAddress(0, 1, 0, 10);
            cleanBeforeMergeOnValidCells(currentSheet, region, titleCellStyle);
            currentSheet.addMergedRegion(region);
            currentLine++;

            // Header
            currentRow = currentSheet.createRow(currentLine++);
            int currentColumn = 0;

            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Date");
            currentCell.setCellStyle(headerCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Jrn");
            currentCell.setCellStyle(headerCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Pièce");
            currentCell.setCellStyle(headerCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("ContreP");
            currentCell.setCellStyle(headerCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Ref");
            currentCell.setCellStyle(headerCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Lettrage");
            currentCell.setCellStyle(headerCellStyle);
            region = new CellRangeAddress(2, 2, 5, 6);
            cleanBeforeMergeOnValidCells(currentSheet, region, headerCellStyle);
            currentSheet.addMergedRegion(region);
            currentColumn++;
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Libellé");
            currentCell.setCellStyle(headerCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Affaires");
            currentCell.setCellStyle(headerCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Débit");
            currentCell.setCellStyle(headerCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Crédit");
            currentCell.setCellStyle(headerCellStyle);

            // Each record
            List<AccountingRecord> accountingRecordsForAccount = getAccountingRecordForAccountingAccount(
                    accountingAccount, accountingRecords);

            Float debit = 0f;
            Float credit = 0f;
            if (accountingRecordsForAccount != null) {
                for (AccountingRecord accountingRecord : accountingRecordsForAccount) {
                    currentRow = currentSheet.createRow(currentLine++);
                    currentColumn = 0;
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell.setCellValue("Date : laquelle ?");
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell.setCellValue(accountingRecord.getAccountingJournal().getLabel());
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell
                            .setCellValue(
                                    accountingRecord.getInvoice() != null ? accountingRecord.getInvoice().getId() + ""
                                            : accountingRecord.getManualAccountingDocumentNumber());
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell.setCellValue("ContreP : ?");
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell.setCellValue("Ref : ?");
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell.setCellValue("Lettrage : ?");
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell.setCellValue("");
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell.setCellValue(accountingRecord.getLabel());
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    currentCell.setCellValue("Affaires : ?");
                    currentCell.setCellStyle(recordCellStyle);
                    currentCell = currentRow.createCell(currentColumn++);
                    if (accountingRecord.getDebitAmount() != null) {
                        currentCell.setCellValue(accountingRecord.getDebitAmount());
                        debit += accountingRecord.getDebitAmount();
                    }
                    currentCell.setCellStyle(styleCurrency);
                    currentCell = currentRow.createCell(currentColumn++);
                    if (accountingRecord.getCreditAmount() != null) {
                        credit += accountingRecord.getCreditAmount();
                        currentCell.setCellValue(accountingRecord.getCreditAmount());
                    }
                    currentCell.setCellStyle(styleCurrency);
                }
            }

            // Accumulation
            currentRow = currentSheet.createRow(currentLine++);
            currentColumn = 8;
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Total");
            currentCell.setCellStyle(recordCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue(debit);
            currentCell.setCellStyle(styleCurrency);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue(credit);
            currentCell.setCellStyle(styleCurrency);

            // Balance
            currentRow = currentSheet.createRow(currentLine++);
            currentColumn = 8;
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("Total");
            currentCell.setCellStyle(recordCellStyle);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue("");
            currentCell.setCellStyle(styleCurrency);
            currentCell = currentRow.createCell(currentColumn++);
            currentCell.setCellValue(credit - debit);
            currentCell.setCellStyle(styleCurrency);

            // autosize
            for (int i = 0; i < 11; i++)
                currentSheet.autoSizeColumn(i, true);
        }
        File file = File.createTempFile("grand-livre", "xlsx");
        FileOutputStream outputStream = new FileOutputStream(file);
        wb.write(outputStream);
        wb.close();
        outputStream.close();
        return file;
    }

    private List<AccountingAccount> getAccountingAccountInRecord(List<AccountingRecord> accountingRecords) {
        ArrayList<AccountingAccount> accountingAccounts = new ArrayList<AccountingAccount>();
        if (accountingRecords != null)
            for (AccountingRecord accountingRecord : accountingRecords)
                if (!accountingAccounts.contains(accountingRecord.getAccountingAccount()))
                    accountingAccounts.add(accountingRecord.getAccountingAccount());
        return accountingAccounts;
    }

    private List<AccountingRecord> getAccountingRecordForAccountingAccount(AccountingAccount accountingAccount,
            List<AccountingRecord> accountingRecordsIn) {
        ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
        if (accountingAccount != null && accountingRecordsIn != null)
            for (AccountingRecord accountingRecord : accountingRecordsIn)
                if (accountingAccount.getId().equals(accountingRecord.getAccountingAccount().getId()))
                    accountingRecords.add(accountingRecord);
        accountingRecords.sort(new Comparator<AccountingRecord>() {
            @Override
            public int compare(AccountingRecord o1, AccountingRecord o2) {
                return sortRecords(o1, o2);
            }
        });
        return accountingRecords;
    }

    private int sortRecords(AccountingRecord a, AccountingRecord b) {
        if (a != null && b == null)
            return 1;
        if (a == null && b != null)
            return -1;
        if (a == null && b == null)
            return 0;
        // First, by operation id
        if (a.getOperationId() != null && b.getOperationId() != null) {
            return (a.getOperationId() > b.getOperationId()) ? 1 : -1;
        } else {
            // Next by operation date
            if (a.getOperationDateTime() != null && b.getOperationDateTime() != null) {
                return (a.getOperationDateTime().isAfter(b.getOperationDateTime())) ? 1 : -1;
            } else {
                return (a.getId() > b.getId()) ? 1 : -1;
            }
        }
    }

    private void cleanBeforeMergeOnValidCells(XSSFSheet sheet, CellRangeAddress region, XSSFCellStyle cellStyle) {
        for (int rowNum = region.getFirstRow(); rowNum <= region.getLastRow(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            for (int colNum = region.getFirstColumn(); colNum <= region.getLastColumn(); colNum++) {
                XSSFCell currentCell = row.getCell(colNum);
                if (currentCell == null) {
                    currentCell = row.createCell(colNum);
                }
                currentCell.setCellStyle(cellStyle);
            }
        }
    }
}
