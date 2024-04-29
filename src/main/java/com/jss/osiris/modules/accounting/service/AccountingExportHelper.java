package com.jss.osiris.modules.accounting.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.model.AccountingBalance;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewItem;
import com.jss.osiris.modules.accounting.model.AccountingBalanceViewTitle;
import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.accounting.model.AccountingRecordSearchResult;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;

@Service
public class AccountingExportHelper {

        @Autowired
        AccountingRecordService accountingRecordService;

        @Autowired
        ConstantService constantService;

        @Value("${jss.siret}")
        private String siretJss;

        public File getGrandLivre(List<AccountingRecordSearchResult> accountingRecords, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException {
                List<String> accountingAccounts = getAccountingAccountInRecord(accountingRecords);

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
                byte[] rgbB;
                try {
                        rgbB = Hex.decodeHex(rgbS);
                } catch (DecoderException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close workbook");
                        }
                        throw new OsirisException(e, "Unable to decode color " + rgbS);
                }
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

                // Date cells
                XSSFCellStyle styleDate = wb.createCellStyle();
                styleDate.setBorderBottom(BorderStyle.THIN);
                styleDate.setBorderTop(BorderStyle.THIN);
                styleDate.setBorderRight(BorderStyle.THIN);
                styleDate.setBorderLeft(BorderStyle.THIN);
                CreationHelper createHelper = wb.getCreationHelper();
                styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

                for (String accountingAccount : accountingAccounts) {
                        XSSFSheet currentSheet = wb.createSheet(accountingAccount);

                        // Title
                        int currentLine = 0;

                        XSSFRow currentRow = currentSheet.createRow(currentLine++);
                        XSSFCell currentCell = currentRow.createCell(0);
                        currentCell.setCellValue(
                                        "SPPS - " + siretJss + " - " + "Compte : "
                                                        + getAccountingAccountLabel(accountingRecords,
                                                                        accountingAccount)
                                                        + " " + getAccountingAccountNumber(accountingRecords,
                                                                        accountingAccount)
                                                        + " - "
                                                        + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                        + " - "
                                                        + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                        CellRangeAddress region = new CellRangeAddress(0, 1, 0, 9);
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
                        currentCell.setCellValue("N° d'opération");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("N° d'écriture");
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
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Libellé");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Débit");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Crédit");
                        currentCell.setCellStyle(headerCellStyle);

                        // Each record
                        List<AccountingRecordSearchResult> accountingRecordsForAccount = getAccountingRecordForAccountingAccount(
                                        accountingAccount, accountingRecords);

                        Float debit = 0f;
                        Float credit = 0f;
                        if (accountingRecordsForAccount != null) {
                                for (AccountingRecordSearchResult accountingRecord : accountingRecordsForAccount) {
                                        currentRow = currentSheet.createRow(currentLine++);
                                        currentColumn = 0;
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (accountingRecord.getOperationDateTime() != null)
                                                currentCell.setCellValue(
                                                                accountingRecord.getOperationDateTime().toLocalDate());
                                        currentCell.setCellStyle(styleDate);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        currentCell.setCellValue(accountingRecord.getAccountingJournalCode());
                                        currentCell.setCellStyle(recordCellStyle);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (accountingRecord.getOperationId() != null)
                                                currentCell
                                                                .setCellValue(accountingRecord.getOperationId());
                                        currentCell.setCellStyle(recordCellStyle);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (accountingRecord.getId() != null)
                                                currentCell
                                                                .setCellValue(accountingRecord.getId());
                                        currentCell.setCellStyle(recordCellStyle);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (accountingRecord.getContrePasseOperationId() != null)
                                                currentCell.setCellValue(
                                                                accountingRecord.getContrePasseOperationId());
                                        currentCell.setCellStyle(recordCellStyle);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        currentCell.setCellValue(
                                                        accountingRecord.getInvoiceId() != null
                                                                        ? accountingRecord.getInvoiceId() + ""
                                                                        : accountingRecord
                                                                                        .getManualAccountingDocumentNumber());
                                        currentCell.setCellStyle(recordCellStyle);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (accountingRecord.getLetteringNumber() != null) {
                                                currentCell.setCellValue(accountingRecord.getLetteringNumber());
                                        }
                                        currentCell.setCellStyle(recordCellStyle);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        currentCell.setCellValue(accountingRecord.getLabel());
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
                        currentColumn = 7;
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
                        currentColumn = 7;
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Solde");
                        currentCell.setCellStyle(recordCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        if ((credit - debit) < 0)
                                currentCell.setCellValue(Math.abs(credit - debit));
                        else
                                currentCell.setCellValue("");
                        currentCell.setCellStyle(styleCurrency);
                        currentCell = currentRow.createCell(currentColumn++);
                        if ((credit - debit) > 0)
                                currentCell.setCellValue(Math.abs(credit - debit));
                        else
                                currentCell.setCellValue("");
                        currentCell.setCellStyle(styleCurrency);

                        // autosize
                        for (int i = 0; i < 11; i++)
                                currentSheet.autoSizeColumn(i, true);
                }
                File file;
                FileOutputStream outputStream;
                try {
                        file = File.createTempFile("grand-livre", "xlsx");
                        outputStream = new FileOutputStream(file);
                } catch (IOException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close excel file");
                        }
                        throw new OsirisException(e, "Unable to create temp file");
                }

                try {
                        wb.write(outputStream);
                        wb.close();
                        outputStream.close();
                } catch (IOException e) {
                        throw new OsirisException(e, "Unable to save excel file");
                }

                return file;
        }

        public File getBalance(List<AccountingBalance> balanceRecords, boolean isGenerale, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException {

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
                byte[] rgbB;
                try {
                        rgbB = Hex.decodeHex(rgbS);
                } catch (DecoderException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close workbook");
                        }
                        throw new OsirisException(e, "Unable to decode color " + rgbS);
                }
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

                // Date cells
                XSSFCellStyle styleDate = wb.createCellStyle();
                styleDate.setBorderBottom(BorderStyle.THIN);
                styleDate.setBorderTop(BorderStyle.THIN);
                styleDate.setBorderRight(BorderStyle.THIN);
                styleDate.setBorderLeft(BorderStyle.THIN);
                CreationHelper createHelper = wb.getCreationHelper();
                styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

                XSSFSheet currentSheet = wb.createSheet("Balance" + (isGenerale ? " générale" : ""));

                // Title
                int currentLine = 0;

                XSSFRow currentRow = currentSheet.createRow(currentLine++);
                XSSFCell currentCell = currentRow.createCell(0);
                currentCell.setCellValue("Balance" + (isGenerale ? " générale" : ""));

                currentCell.setCellValue(
                                "SPPS - " + siretJss + " - "
                                                + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                + " - "
                                                + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                CellRangeAddress region = new CellRangeAddress(0, 1, 0, isGenerale ? 5 : 11);
                cleanBeforeMergeOnValidCells(currentSheet, region, titleCellStyle);
                currentSheet.addMergedRegion(region);
                currentLine++;

                // Header
                currentRow = currentSheet.createRow(currentLine++);
                int currentColumn = 0;

                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("N° de compte");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Libellé du compte");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Débit");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Crédit");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Solde débit");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Solde crédit");
                currentCell.setCellStyle(headerCellStyle);
                if (!isGenerale) {
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Créances à échoir -30j");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Créances à échoir -60j");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Créances à échoir +60j");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Créances échues -30j");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Créances échues -60j");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Créances échues +60j");
                        currentCell.setCellStyle(headerCellStyle);
                }

                Float debit = 0f;
                Float credit = 0f;
                if (balanceRecords != null) {
                        for (AccountingBalance balanceRecord : balanceRecords) {
                                currentRow = currentSheet.createRow(currentLine++);
                                currentColumn = 0;
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(
                                                balanceRecord.getPrincipalAccountingAccountCode()
                                                                + balanceRecord.getAccountingAccountSubNumber());
                                currentCell.setCellStyle(styleDate);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(
                                                isGenerale ? balanceRecord.getPrincipalAccountingAccountLabel()
                                                                : balanceRecord.getAccountingAccountLabel());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (balanceRecord.getDebitAmount() != null) {
                                        currentCell.setCellValue(balanceRecord.getDebitAmount());
                                        debit += balanceRecord.getDebitAmount();
                                }
                                currentCell.setCellStyle(styleCurrency);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (balanceRecord.getCreditAmount() != null) {
                                        credit += balanceRecord.getCreditAmount();
                                        currentCell.setCellValue(balanceRecord.getCreditAmount());
                                }
                                currentCell.setCellStyle(styleCurrency);

                                Float debitAmount = balanceRecord.getDebitAmount() != null
                                                ? balanceRecord.getDebitAmount()
                                                : 0f;
                                Float creditAmount = balanceRecord.getCreditAmount() != null
                                                ? balanceRecord.getCreditAmount()
                                                : 0f;
                                currentCell = currentRow.createCell(currentColumn++);
                                if (debitAmount > creditAmount) {
                                        currentCell.setCellValue(debitAmount - creditAmount);
                                }
                                currentCell.setCellStyle(styleCurrency);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (debitAmount <= creditAmount) {
                                        currentCell.setCellValue(creditAmount - debitAmount);
                                }
                                currentCell.setCellStyle(styleCurrency);

                                if (!isGenerale) {
                                        currentCell.setCellStyle(styleCurrency);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (balanceRecord.getEchoir30() != null)
                                                currentCell
                                                                .setCellValue(balanceRecord.getEchoir30());
                                        currentCell.setCellStyle(styleCurrency);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (balanceRecord.getEchoir60() != null)
                                                currentCell
                                                                .setCellValue(balanceRecord.getEchoir60());
                                        currentCell.setCellStyle(styleCurrency);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (balanceRecord.getEchoir90() != null)
                                                currentCell
                                                                .setCellValue(balanceRecord.getEchoir90());
                                        currentCell.setCellStyle(styleCurrency);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (balanceRecord.getEchu30() != null)
                                                currentCell
                                                                .setCellValue(balanceRecord.getEchu30());
                                        currentCell.setCellStyle(styleCurrency);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (balanceRecord.getEchu60() != null)
                                                currentCell
                                                                .setCellValue(balanceRecord.getEchu60());
                                        currentCell.setCellStyle(styleCurrency);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        if (balanceRecord.getEchu90() != null)
                                                currentCell
                                                                .setCellValue(balanceRecord.getEchu90());
                                        currentCell.setCellStyle(styleCurrency);
                                }
                        }
                }

                // Accumulation
                currentRow = currentSheet.createRow(currentLine++);
                currentColumn = 1;
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
                currentColumn = 1;
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Solde");
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                if ((credit - debit) < 0)
                        currentCell.setCellValue(Math.abs(credit - debit));
                else
                        currentCell.setCellValue("");
                currentCell.setCellStyle(styleCurrency);
                currentCell = currentRow.createCell(currentColumn++);
                if ((credit - debit) > 0)
                        currentCell.setCellValue(Math.abs(credit - debit));
                else
                        currentCell.setCellValue("");
                currentCell.setCellStyle(styleCurrency);

                // autosize
                for (int i = 0; i < 12; i++)
                        currentSheet.autoSizeColumn(i, true);

                File file;
                FileOutputStream outputStream;
                try {
                        file = File.createTempFile("balance", "xlsx");
                        outputStream = new FileOutputStream(file);
                } catch (IOException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close excel file");
                        }
                        throw new OsirisException(e, "Unable to create temp file");
                }

                try {
                        wb.write(outputStream);
                        wb.close();
                        outputStream.close();
                } catch (IOException e) {
                        throw new OsirisException(e, "Unable to save excel file");
                }

                return file;
        }

        public File getJournal(List<AccountingRecordSearchResult> accountingRecords,
                        AccountingJournal accountingJournal, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException {
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
                byte[] rgbB;
                try {
                        rgbB = Hex.decodeHex(rgbS);
                } catch (DecoderException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close workbook");
                        }
                        throw new OsirisException(e, "Unable to decode color " + rgbS);
                }
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

                // Date cells
                XSSFCellStyle styleDate = wb.createCellStyle();
                styleDate.setBorderBottom(BorderStyle.THIN);
                styleDate.setBorderTop(BorderStyle.THIN);
                styleDate.setBorderRight(BorderStyle.THIN);
                styleDate.setBorderLeft(BorderStyle.THIN);
                CreationHelper createHelper = wb.getCreationHelper();
                styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

                XSSFSheet currentSheet = wb.createSheet("Journal " + accountingJournal.getLabel());

                // Title
                int currentLine = 0;

                XSSFRow currentRow = currentSheet.createRow(currentLine++);
                XSSFCell currentCell = currentRow.createCell(0);
                currentCell.setCellValue("SPPS - " + siretJss + " - Compte : "
                                + accountingJournal.getLabel());

                CellRangeAddress region = new CellRangeAddress(0, 1, 0, 9);
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
                currentCell.setCellValue("N° d'opération");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("N° d'écriture");
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
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Compte");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Sous-compte");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Libellé");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Débit");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Crédit");
                currentCell.setCellStyle(headerCellStyle);

                Float debit = 0f;
                Float credit = 0f;
                if (accountingRecords != null) {
                        for (AccountingRecordSearchResult accountingRecord : accountingRecords) {
                                currentRow = currentSheet.createRow(currentLine++);
                                currentColumn = 0;
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getOperationDateTime() != null)
                                        currentCell.setCellValue(
                                                        accountingRecord.getOperationDateTime().toLocalDate());
                                currentCell.setCellStyle(styleDate);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(accountingRecord.getAccountingJournalCode());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getOperationId() != null)
                                        currentCell
                                                        .setCellValue(accountingRecord.getOperationId());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getId() != null)
                                        currentCell
                                                        .setCellValue(accountingRecord.getId());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getContrePasseOperationId() != null)
                                        currentCell.setCellValue(accountingRecord.getContrePasseOperationId());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(
                                                accountingRecord.getInvoiceId() != null
                                                                ? accountingRecord.getInvoiceId() + ""
                                                                : accountingRecord.getManualAccountingDocumentNumber());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue((accountingRecord.getLetteringNumber() != null
                                                ? accountingRecord.getLetteringNumber() + ""
                                                : ""));
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(accountingRecord.getPrincipalAccountingAccountCode());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(accountingRecord.getAccountingAccountSubNumber() + "");
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(accountingRecord.getLabel());
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
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("");
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
                currentCell.setCellValue("Solde");
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("");
                currentCell.setCellStyle(styleCurrency);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("");
                currentCell.setCellStyle(styleCurrency);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue(credit - debit);
                currentCell.setCellStyle(styleCurrency);

                // autosize
                for (int i = 0; i < 11; i++)
                        currentSheet.autoSizeColumn(i, true);

                File file;
                FileOutputStream outputStream;
                try {
                        file = File.createTempFile("journal", "xlsx");
                        outputStream = new FileOutputStream(file);
                } catch (IOException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close excel file");
                        }
                        throw new OsirisException(e, "Unable to create temp file");
                }

                try {
                        wb.write(outputStream);
                        wb.close();
                        outputStream.close();
                } catch (IOException e) {
                        throw new OsirisException(e, "Unable to save excel file");
                }

                return file;
        }

        public File getAccountingAccount(List<AccountingRecordSearchResult> accountingRecords,
                        AccountingAccount accountingAccount, LocalDateTime startDate,
                        LocalDateTime endDate) throws OsirisException {
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
                byte[] rgbB;
                try {
                        rgbB = Hex.decodeHex(rgbS);
                } catch (DecoderException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close workbook");
                        }
                        throw new OsirisException(e, "Unable to decode color " + rgbS);
                }
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

                // Date cells
                XSSFCellStyle styleDate = wb.createCellStyle();
                styleDate.setBorderBottom(BorderStyle.THIN);
                styleDate.setBorderTop(BorderStyle.THIN);
                styleDate.setBorderRight(BorderStyle.THIN);
                styleDate.setBorderLeft(BorderStyle.THIN);
                CreationHelper createHelper = wb.getCreationHelper();
                styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

                XSSFSheet currentSheet = wb.createSheet(
                                "Compte " + accountingAccount.getPrincipalAccountingAccount().getCode()
                                                + accountingAccount.getAccountingAccountSubNumber());

                // Title
                int currentLine = 0;

                XSSFRow currentRow = currentSheet.createRow(currentLine++);
                XSSFCell currentCell = currentRow.createCell(0);
                currentCell.setCellValue(
                                "SPPS - " + siretJss + " - " + "Compte : " + accountingAccount.getLabel()
                                                + accountingAccount.getPrincipalAccountingAccount().getCode()
                                                + " " + StringUtils.leftPad(
                                                                accountingAccount.getAccountingAccountSubNumber() + "",
                                                                5, "0")
                                                + " - "
                                                + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                + " - "
                                                + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                CellRangeAddress region = new CellRangeAddress(0, 1, 0, 9);
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
                currentCell.setCellValue("N° d'opération");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("N° d'écriture");
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
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Libellé");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Débit");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Crédit");
                currentCell.setCellStyle(headerCellStyle);

                Float debit = 0f;
                Float credit = 0f;
                if (accountingRecords != null) {
                        for (AccountingRecordSearchResult accountingRecord : accountingRecords) {
                                currentRow = currentSheet.createRow(currentLine++);
                                currentColumn = 0;
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getOperationDateTime() != null)
                                        currentCell.setCellValue(
                                                        accountingRecord.getOperationDateTime().toLocalDate());
                                currentCell.setCellStyle(styleDate);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(accountingRecord.getAccountingJournalCode());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getOperationId() != null)
                                        currentCell
                                                        .setCellValue(accountingRecord.getOperationId());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getId() != null)
                                        currentCell
                                                        .setCellValue(accountingRecord.getId());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getContrePasseOperationId() != null)
                                        currentCell.setCellValue(accountingRecord.getContrePasseOperationId());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(
                                                accountingRecord.getInvoiceId() != null
                                                                ? accountingRecord.getInvoiceId() + ""
                                                                : accountingRecord.getManualAccountingDocumentNumber());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getLetteringNumber() != null) {
                                        currentCell.setCellValue(accountingRecord.getLetteringNumber());
                                }
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(accountingRecord.getLabel());
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
                currentColumn = 7;
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
                currentColumn = 7;
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Solde");
                currentCell.setCellStyle(recordCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                if ((credit - debit) < 0)
                        currentCell.setCellValue(Math.abs(credit - debit));
                else
                        currentCell.setCellValue("");
                currentCell.setCellStyle(styleCurrency);
                currentCell = currentRow.createCell(currentColumn++);
                if ((credit - debit) > 0)
                        currentCell.setCellValue(Math.abs(credit - debit));
                else
                        currentCell.setCellValue("");
                currentCell.setCellStyle(styleCurrency);

                // autosize
                for (int i = 0; i < 11; i++)
                        currentSheet.autoSizeColumn(i, true);

                File file;
                FileOutputStream outputStream;
                try {
                        file = File.createTempFile("journal", "xlsx");
                        outputStream = new FileOutputStream(file);
                } catch (IOException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close excel file");
                        }
                        throw new OsirisException(e, "Unable to create temp file");
                }

                try {
                        wb.write(outputStream);
                        wb.close();
                        outputStream.close();
                } catch (IOException e) {
                        throw new OsirisException(e, "Unable to save excel file");
                }

                return file;
        }

        public File getProfitAndLost(List<AccountingBalanceViewTitle> profitAndLostTitles) throws OsirisException {

                XSSFWorkbook wb = new XSSFWorkbook();

                File file;
                FileOutputStream outputStream;
                try {
                        file = File.createTempFile("journal", "xlsx");
                        outputStream = new FileOutputStream(file);
                        generateSheetForBilanAndProfitAndLost(wb, "Compte de résultats", profitAndLostTitles);
                } catch (IOException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close excel file");
                        }
                        throw new OsirisException(e, "Unable to create temp file");
                }

                try {
                        wb.write(outputStream);
                        wb.close();
                        outputStream.close();
                } catch (IOException e) {
                        throw new OsirisException(e, "Unable to save excel file");
                }

                return file;
        }

        public File getBilan(List<AccountingBalanceViewTitle> actifBilanTitles,
                        List<AccountingBalanceViewTitle> passifBilanTitles) throws OsirisException {

                XSSFWorkbook wb = new XSSFWorkbook();

                File file;
                FileOutputStream outputStream;
                try {
                        file = File.createTempFile("journal", "xlsx");
                        outputStream = new FileOutputStream(file);
                        generateSheetForBilanAndProfitAndLost(wb, "Actif", actifBilanTitles);
                        generateSheetForBilanAndProfitAndLost(wb, "Passif", passifBilanTitles);
                } catch (IOException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close excel file");
                        }
                        throw new OsirisException(e, "Unable to create temp file");
                }

                try {
                        wb.write(outputStream);
                        wb.close();
                        outputStream.close();
                } catch (IOException e) {
                        throw new OsirisException(e, "Unable to save excel file");
                }

                return file;
        }

        private XSSFSheet generateSheetForBilanAndProfitAndLost(XSSFWorkbook wb, String sheetName,
                        List<AccountingBalanceViewTitle> titles) throws OsirisException {
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

                // Title
                XSSFCellStyle subTitleCellStyle = wb.createCellStyle();
                subTitleCellStyle.setAlignment(HorizontalAlignment.CENTER);
                subTitleCellStyle.setBorderBottom(BorderStyle.THIN);
                subTitleCellStyle.setBorderTop(BorderStyle.THIN);
                subTitleCellStyle.setBorderRight(BorderStyle.THIN);
                subTitleCellStyle.setBorderLeft(BorderStyle.THIN);
                String rgbSSub = "FFFF99";
                byte[] rgbBSub;
                try {
                        rgbBSub = Hex.decodeHex(rgbSSub);
                } catch (DecoderException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close workbook");
                        }
                        throw new OsirisException(e, "Unable to decode color " + rgbSSub);
                }
                XSSFColor colorSub = new XSSFColor(rgbBSub, null);
                subTitleCellStyle.setFillForegroundColor(colorSub);
                subTitleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Header
                XSSFCellStyle headerCellStyle = wb.createCellStyle();
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderRight(BorderStyle.THIN);
                headerCellStyle.setBorderLeft(BorderStyle.THIN);
                String rgbS = "FFFF99";
                byte[] rgbB;
                try {
                        rgbB = Hex.decodeHex(rgbS);
                } catch (DecoderException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close workbook");
                        }
                        throw new OsirisException(e, "Unable to decode color " + rgbS);
                }
                XSSFColor colorHeader = new XSSFColor(rgbB, null);
                headerCellStyle.setFillForegroundColor(colorHeader);
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

                // Debit / credit header cells
                XSSFCellStyle styleCurrencyHeader = wb.createCellStyle();
                styleCurrencyHeader.setBorderBottom(BorderStyle.THIN);
                styleCurrencyHeader.setBorderTop(BorderStyle.THIN);
                styleCurrencyHeader.setBorderRight(BorderStyle.THIN);
                styleCurrencyHeader.setBorderLeft(BorderStyle.THIN);
                styleCurrencyHeader.setDataFormat((short) 8);
                headerCellStyle.setFillForegroundColor(colorHeader);
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                XSSFSheet currentSheet = wb.createSheet(sheetName);

                // Title
                int currentLine = 0;

                XSSFRow currentRow = currentSheet.createRow(currentLine++);
                XSSFCell currentCell = currentRow.createCell(0);
                currentCell.setCellValue(sheetName);

                CellRangeAddress region = new CellRangeAddress(0, 1, 0, 2);
                cleanBeforeMergeOnValidCells(currentSheet, region, titleCellStyle);
                currentSheet.addMergedRegion(region);
                currentLine++;
                currentLine++;

                List<AccountingBalanceViewTitle> totals = null;

                for (AccountingBalanceViewTitle table : titles) {

                        // Save for bottom table generation
                        if (table.getTotals() != null && table.getTotals().size() > 0)
                                totals = table.getTotals();

                        // Table header
                        currentRow = currentSheet.createRow(currentLine++);
                        currentCell = currentRow.createCell(0);
                        currentCell.setCellValue(table.getLabel());

                        region = new CellRangeAddress(currentLine - 1, currentLine - 1, 0, 2);
                        cleanBeforeMergeOnValidCells(currentSheet, region, subTitleCellStyle);
                        currentSheet.addMergedRegion(region);
                        currentLine++;

                        // Values header
                        currentRow = currentSheet.createRow(currentLine++);
                        int currentColumn = 0;

                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Solde N");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Solde N-1");
                        currentCell.setCellStyle(headerCellStyle);

                        // Values
                        for (AccountingBalanceViewItem item : table.getItems()) {
                                currentRow = currentSheet.createRow(currentLine++);
                                currentColumn = 0;
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(item.getLabel());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(item.getSoldeN());
                                currentCell.setCellStyle(styleCurrency);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(item.getSoldeN1());
                                currentCell.setCellStyle(styleCurrency);
                        }

                        // Subtitles
                        for (AccountingBalanceViewTitle subtitle : table.getSubTitles()) {
                                if (subtitle.getItems() != null) {
                                        for (int i = 0; i < subtitle.getItems().size(); i++) {
                                                AccountingBalanceViewItem item = subtitle.getItems().get(i);
                                                Float soldeN = 0f;
                                                Float soldeN1 = 0f;
                                                if (i == 0 && subtitle.getLabel() != null
                                                                && !subtitle.getLabel().equals("")) {
                                                        currentRow = currentSheet.createRow(currentLine++);
                                                        currentCell = currentRow.createCell(0);
                                                        currentCell.setCellValue(subtitle.getLabel());

                                                        region = new CellRangeAddress(currentLine - 1, currentLine - 1,
                                                                        0, 2);
                                                        cleanBeforeMergeOnValidCells(currentSheet, region,
                                                                        subTitleCellStyle);
                                                        currentSheet.addMergedRegion(region);
                                                }

                                                currentRow = currentSheet.createRow(currentLine++);
                                                currentColumn = 0;
                                                currentCell = currentRow.createCell(currentColumn++);
                                                currentCell.setCellValue(item.getLabel());
                                                currentCell.setCellStyle(recordCellStyle);
                                                currentCell = currentRow.createCell(currentColumn++);
                                                currentCell.setCellValue(item.getSoldeN());
                                                currentCell.setCellStyle(styleCurrency);
                                                currentCell = currentRow.createCell(currentColumn++);
                                                currentCell.setCellValue(item.getSoldeN1());
                                                currentCell.setCellStyle(styleCurrency);

                                                soldeN += item.getSoldeN();
                                                soldeN1 += item.getSoldeN1();

                                                if (i == subtitle.getItems().size() - 1 && subtitle.getLabel() != null
                                                                && !subtitle.getLabel().equals("")) {
                                                        currentRow = currentSheet.createRow(currentLine++);
                                                        currentColumn = 0;
                                                        currentCell = currentRow.createCell(currentColumn++);
                                                        currentCell.setCellValue("Sous-total - " + subtitle.getLabel());
                                                        currentCell.setCellStyle(headerCellStyle);
                                                        currentCell = currentRow.createCell(currentColumn++);
                                                        currentCell.setCellValue(soldeN);
                                                        currentCell.setCellStyle(styleCurrencyHeader);
                                                        currentCell = currentRow.createCell(currentColumn++);
                                                        currentCell.setCellValue(soldeN1);
                                                        currentCell.setCellStyle(styleCurrencyHeader);
                                                }
                                        }
                                } else {
                                        currentRow = currentSheet.createRow(currentLine++);
                                        currentColumn = 0;
                                        currentCell = currentRow.createCell(currentColumn++);
                                        currentCell.setCellValue(subtitle.getLabel());
                                        currentCell.setCellStyle(headerCellStyle);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        currentCell.setCellValue(subtitle.getSoldeN());
                                        currentCell.setCellStyle(styleCurrencyHeader);
                                        currentCell = currentRow.createCell(currentColumn++);
                                        currentCell.setCellValue(subtitle.getSoldeN1());
                                        currentCell.setCellStyle(styleCurrencyHeader);
                                }
                        }
                        currentLine++;
                        currentLine++;
                }

                // Totaux
                if (totals != null) {
                        // Table header
                        currentRow = currentSheet.createRow(currentLine++);
                        currentCell = currentRow.createCell(0);
                        currentCell.setCellValue("Totaux");

                        region = new CellRangeAddress(currentLine - 1, currentLine - 1, 0, 2);
                        cleanBeforeMergeOnValidCells(currentSheet, region, subTitleCellStyle);
                        currentSheet.addMergedRegion(region);
                        currentLine++;

                        // Values header
                        currentRow = currentSheet.createRow(currentLine++);
                        int currentColumn = 0;

                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Solde N");
                        currentCell.setCellStyle(headerCellStyle);
                        currentCell = currentRow.createCell(currentColumn++);
                        currentCell.setCellValue("Solde N-1");
                        currentCell.setCellStyle(headerCellStyle);

                        // Values
                        for (AccountingBalanceViewTitle total : totals) {
                                currentRow = currentSheet.createRow(currentLine++);
                                currentColumn = 0;
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(total.getLabel());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(total.getSoldeN());
                                currentCell.setCellStyle(styleCurrency);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(total.getSoldeN1());
                                currentCell.setCellStyle(styleCurrency);
                        }
                }

                // autosize
                for (int i = 0; i < 11; i++)
                        currentSheet.autoSizeColumn(i, true);

                return currentSheet;
        }

        private List<String> getAccountingAccountInRecord(
                        List<AccountingRecordSearchResult> accountingRecords) {
                ArrayList<String> accountingAccounts = new ArrayList<String>();
                if (accountingRecords != null)
                        for (AccountingRecordSearchResult accountingRecord : accountingRecords)
                                if (!accountingAccounts
                                                .contains(accountingRecord.getPrincipalAccountingAccountCode() +
                                                                accountingRecord.getAccountingAccountSubNumber()))
                                        accountingAccounts.add(accountingRecord.getPrincipalAccountingAccountCode()
                                                        + accountingRecord.getAccountingAccountSubNumber());
                return accountingAccounts;
        }

        private String getAccountingAccountLabel(
                        List<AccountingRecordSearchResult> accountingRecords, String accountingAccount) {
                if (accountingRecords != null)
                        for (AccountingRecordSearchResult accountingRecord : accountingRecords)
                                if ((accountingRecord.getPrincipalAccountingAccountCode()
                                                + accountingRecord.getAccountingAccountSubNumber())
                                                .equals(accountingAccount))
                                        return accountingRecord.getAccountingAccountLabel();
                return "";
        }

        private String getAccountingAccountNumber(List<AccountingRecordSearchResult> accountingRecords,
                        String accountingAccount) {
                if (accountingRecords != null)
                        for (AccountingRecordSearchResult accountingRecord : accountingRecords)
                                if ((accountingRecord.getPrincipalAccountingAccountCode()
                                                + accountingRecord.getAccountingAccountSubNumber())
                                                .equals(accountingAccount))
                                        return accountingRecord.getPrincipalAccountingAccountCode() + StringUtils
                                                        .leftPad(accountingRecord.getAccountingAccountSubNumber(), 5,
                                                                        '0');
                return "";
        }

        private List<AccountingRecordSearchResult> getAccountingRecordForAccountingAccount(
                        String accountingAccountNumber,
                        List<AccountingRecordSearchResult> accountingRecordsIn) {
                ArrayList<AccountingRecordSearchResult> accountingRecords = new ArrayList<AccountingRecordSearchResult>();
                if (accountingAccountNumber != null && accountingRecordsIn != null)
                        for (AccountingRecordSearchResult accountingRecord : accountingRecordsIn)
                                if (accountingAccountNumber
                                                .equals(accountingRecord.getPrincipalAccountingAccountCode() +
                                                                accountingRecord.getAccountingAccountSubNumber()))
                                        accountingRecords.add(accountingRecord);
                return accountingRecords;
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

        public File generateBillingClosure(List<AccountingRecordSearchResult> accountingRecords, String tiersLabel,
                        boolean showAffaireDetails) throws OsirisException {

                if (showAffaireDetails)
                        accountingRecords.sort(new Comparator<AccountingRecordSearchResult>() {
                                @Override
                                public int compare(AccountingRecordSearchResult o1, AccountingRecordSearchResult o2) {
                                        return o1.getAffaireLabel().compareTo(o2.getAffaireLabel());
                                }
                        });

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
                byte[] rgbB;
                try {
                        rgbB = Hex.decodeHex(rgbS);
                } catch (DecoderException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close workbook");
                        }
                        throw new OsirisException(e, "Unable to decode color " + rgbS);
                }
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

                // Date cells
                XSSFCellStyle styleDate = wb.createCellStyle();
                styleDate.setBorderBottom(BorderStyle.THIN);
                styleDate.setBorderTop(BorderStyle.THIN);
                styleDate.setBorderRight(BorderStyle.THIN);
                styleDate.setBorderLeft(BorderStyle.THIN);
                CreationHelper createHelper = wb.getCreationHelper();
                styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

                XSSFSheet currentSheet = wb.createSheet(tiersLabel);

                // Title
                int currentLine = 0;

                XSSFRow currentRow = currentSheet.createRow(currentLine++);
                XSSFCell currentCell = currentRow.createCell(0);
                currentCell.setCellValue(tiersLabel + " - relevé du "
                                + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                CellRangeAddress region = new CellRangeAddress(0, 1, 0, 5);
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
                currentCell.setCellValue("Facture");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Libellé");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Responsable");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Débit");
                currentCell.setCellStyle(headerCellStyle);
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Crédit");
                currentCell.setCellStyle(headerCellStyle);

                Float debit = 0f;
                Float credit = 0f;
                if (accountingRecords != null) {
                        String currentAffaire = "";
                        for (AccountingRecordSearchResult accountingRecord : accountingRecords) {

                                // By pass counter part and appoint
                                AccountingRecord completeAccountingRecord = accountingRecordService
                                                .getAccountingRecord(accountingRecord.getRecordId());
                                if (completeAccountingRecord.getIsCounterPart()
                                                || completeAccountingRecord.getContrePasse() != null)
                                        continue;

                                if (completeAccountingRecord.getAccountingAccount().getId()
                                                .equals(constantService.getAccountingAccountProfit().getId())
                                                || completeAccountingRecord.getAccountingAccount().getId()
                                                                .equals(constantService.getAccountingAccountLost()
                                                                                .getId()))
                                        continue;

                                if (showAffaireDetails
                                                && !currentAffaire.equals(accountingRecord.getAffaireLabel())) {
                                        currentAffaire = accountingRecord.getAffaireLabel();
                                        currentRow = currentSheet.createRow(currentLine++);
                                        currentRow = currentSheet.createRow(currentLine++);
                                        currentColumn = 0;
                                        currentCell = currentRow.createCell(currentColumn++);
                                        currentCell.setCellValue(currentAffaire);
                                        currentCell.setCellStyle(recordCellStyle);
                                }
                                currentRow = currentSheet.createRow(currentLine++);
                                currentColumn = 0;
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getOperationDateTime() != null)
                                        currentCell.setCellValue(
                                                        accountingRecord.getOperationDateTime().toLocalDate());
                                currentCell.setCellStyle(styleDate);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getInvoiceId() != null)
                                        currentCell.setCellValue(accountingRecord.getInvoiceId());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                currentCell.setCellValue(accountingRecord.getLabel() + " - "
                                                + accountingRecord.getAffaireLabel());
                                currentCell.setCellStyle(recordCellStyle);
                                currentCell = currentRow.createCell(currentColumn++);
                                if (accountingRecord.getResponsable() != null)
                                        currentCell
                                                        .setCellValue(accountingRecord.getResponsable());
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
                currentColumn = 3;
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
                currentColumn = 3;
                currentCell = currentRow.createCell(currentColumn++);
                currentCell.setCellValue("Solde");
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

                File file;
                FileOutputStream outputStream;
                try {
                        file = File.createTempFile(
                                        "Relevé de compte - " + createHelper.createDataFormat().getFormat("dd-mm-yyyy"),
                                        "xlsx");
                        outputStream = new FileOutputStream(file);
                } catch (IOException e) {
                        try {
                                wb.close();
                        } catch (IOException e2) {
                                throw new OsirisException(e, "Unable to close excel file");
                        }
                        throw new OsirisException(e, "Unable to create temp file");
                }

                try {
                        wb.write(outputStream);
                        wb.close();
                        outputStream.close();
                } catch (IOException e) {
                        throw new OsirisException(e, "Unable to save excel file");
                }

                return file;
        }
}
