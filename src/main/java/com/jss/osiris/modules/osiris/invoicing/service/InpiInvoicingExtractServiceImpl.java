package com.jss.osiris.modules.osiris.invoicing.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.InpiInvoicingExtract;
import com.jss.osiris.modules.osiris.invoicing.repository.InpiInvoicingExtractRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;

@Service
public class InpiInvoicingExtractServiceImpl implements InpiInvoicingExtractService {

    @Autowired
    InpiInvoicingExtractRepository inpiInvoicingExtractRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Pattern REMBT_PATTERN = Pattern.compile("Rembt\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern NUMERO_PATTERN = Pattern.compile("N[°º]?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern GENERIC_NUMBER_PATTERN = Pattern.compile("(\\d{6,})");

    private static final Pattern CREDIT_NOTE_PATTERN = Pattern.compile("(approvisionnement|rembt)",
            Pattern.CASE_INSENSITIVE);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> uploadInpiInvoicingExtractFile(InputStream file) throws OsirisException {
        try {
            List<InpiInvoicingExtract> extracts = parseInpiInvoicingExtractFile(file);

            if (extracts == null || extracts.isEmpty()) {
                throw new OsirisClientMessageException("Le fichier INPI est vide ou invalide");
            }

            inpiInvoicingExtractRepository.saveAll(extracts);
            return List.of();

        } catch (DataAccessException dae) {
            // saveAll failure (DB, constraint, etc.)
            throw new OsirisClientMessageException("Erreur lors de l'enregistrement des données INPI");

        } catch (OsirisException oe) {
            throw oe;

        } catch (Exception e) {
            // parsing or unexpected errors
            throw new OsirisException(
                    e,
                    "Error while parsing INPI invoicing extract file");
        }
    }

    public List<InpiInvoicingExtract> parseInpiInvoicingExtractFile(InputStream inputStream) throws OsirisException {

        List<InpiInvoicingExtract> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("Windows-1252")))) {

            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // skip empty lines
                }

                String[] values = line.split(";", -1);
                if (values[1] != null && values[1].toUpperCase().contains("RAC VIREMENT")) {
                    continue; // skipe lines that have RAC VIREMENT
                }
                InpiInvoicingExtract row = new InpiInvoicingExtract();
                row.setInpiOrder(extractInpiOrderNumber(values[1]));
                row.setPreTaxPrice(sum(parseBigDecimal(values[3]), parseBigDecimal(values[4]),
                        parseBigDecimal(values[7]), parseBigDecimal(values[8])));
                row.setVatPrice(sum(parseBigDecimal(values[9]), parseBigDecimal(values[5])));
                row.setLiasseNumber(values[11]);
                row.setCreditNote(isCreditNote(values[1]));
                row.setAccountingDate(parseDate(values[0]));
                row.setApplicationDate(parseDate(values[2]));
                row.setClientReference(values[13]);
                row.setLabel(values[1]);
                row.setDenomination(values[12]);

                results.add(row);

            }

        } catch (IOException e) {
            throw new OsirisException("Parsing INPI invoicing extract failed");
        }

        return results;
    }

    private boolean isCreditNote(String label) {
        return CREDIT_NOTE_PATTERN.matcher(label).find();
    }

    private Integer extractInpiOrderNumber(String label) {
        if (label == null)
            return null;
        Matcher cdeMatcher = REMBT_PATTERN.matcher(label);
        if (cdeMatcher.find()) {
            return parseInteger(cdeMatcher.group(1));
        }

        Matcher numeroMatcher = NUMERO_PATTERN.matcher(label);
        if (numeroMatcher.find()) {
            return parseInteger(numeroMatcher.group(1));
        }

        Matcher fallBackMatcher = GENERIC_NUMBER_PATTERN.matcher(label);
        if (fallBackMatcher.find()) {
            return parseInteger(fallBackMatcher.group(1));
        }
        return null;
    }

    private BigDecimal parseBigDecimal(String val) {

        if (val == null || val.isBlank())
            return BigDecimal.ZERO;

        String cleanedVal = val.trim().replace('\u2212', '-').replace(",", ".").replace('\u00A0', ' ').replace(" ", "")
                .replaceAll("[^0-9eE.\\-]", "");

        if (cleanedVal.isEmpty())
            return BigDecimal.ZERO;

        return new BigDecimal(cleanedVal);

    }

    private BigDecimal sum(BigDecimal... values) {
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                result = result.add(value);
            }
        }
        return result;
    }

    private Integer parseInteger(String val) {
        if (val == null || val.isBlank())
            return null;
        return Integer.parseInt(val.trim());
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value, DATE_FORMAT);
    }

    @Override
    public List<InpiInvoicingExtract> getInpiInvoicingExtractByDateBetween(LocalDate startDate, LocalDate endDate) {
        return inpiInvoicingExtractRepository.findByAccountingDateBetween(startDate, endDate);
    }

}
