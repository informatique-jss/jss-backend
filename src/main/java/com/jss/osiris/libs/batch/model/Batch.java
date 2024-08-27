package com.jss.osiris.libs.batch.model;

import java.time.LocalDateTime;

import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.node.model.Node;
import com.jss.osiris.modules.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_batch_settings_id", columnList = "id_batch_settings"),
        @Index(name = "idx_batch_settings_status", columnList = "id_batch_status,id_batch_settings") })
public class Batch implements IId {
    public static String REFRESH_FORMALITE_GUICHET_UNIQUE = "REFRESH_FORMALITE_GUICHET_UNIQUE";
    public static String SIGN_FORMALITE_GUICHET_UNIQUE = "SIGN_FORMALITE_GUICHET_UNIQUE";
    public static String DECLARE_NEW_ACTE_DEPOSIT_ON_GUICHET_UNIQUE = "DECLARE_NEW_ACTE_DEPOSIT_ON_GUICHET_UNIQUE";
    public static String PAY_FORMALITE_GUICHET_UNIQUE = "PAY_FORMALITE_GUICHET_UNIQUE";
    public static String DAILY_ACCOUNT_CLOSING = "DAILY_ACCOUNT_CLOSING";
    public static String ACTIVE_DIRECTORY_USER_UPDATE = "ACTIVE_DIRECTORY_USER_UPDATE";
    public static String PURGE_NOTIFICATION = "PURGE_NOTIFICATION";
    public static String PURGE_LOGS = "PURGE_LOGS";
    public static String PURGE_BATCH = "PURGE_BATCH";
    public static String CHECK_CENTRAL_PAY_PAYMENT_REQUEST = "CHECK_CENTRAL_PAY_PAYMENT_REQUEST";
    public static String SEND_MAIL = "SEND_MAIL";
    public static String SEND_REMINDER_FOR_QUOTATION = "SEND_REMINDER_FOR_QUOTATION";
    public static String SEND_REMINDER_FOR_CUSTOMER_ORDER_DEPOSITS = "SEND_REMINDER_FOR_CUSTOMER_ORDER_DEPOSITS";
    public static String SEND_REMINDER_FOR_INVOICES = "SEND_REMINDER_FOR_INVOICES";
    public static String SEND_REMINDER_TO_CONFRERE_FOR_ANNOUNCEMENTS = "SEND_REMINDER_TO_CONFRERE_FOR_ANNOUNCEMENTS";
    public static String SEND_REMINDER_TO_CONFRERE_FOR_PROVIDER_INVOICE = "SEND_REMINDER_TO_CONFRERE_FOR_PROVIDER_INVOICE";
    public static String SEND_REMINDER_TO_CUSTOMER_FOR_PROOF_READING = "SEND_REMINDER_TO_CUSTOMER_FOR_PROOF_READING";
    public static String SEND_REMINDER_TO_CUSTOMER_FOR_MISSING_ATTACHMENT_QUERIES = "SEND_REMINDER_TO_CUSTOMER_FOR_MISSING_ATTACHMENT_QUERIES";
    public static String SEND_REMINDER_TO_CUSTOMER_FOR_BILAN_PUBLICATION = "SEND_REMINDER_TO_CUSTOMER_FOR_BILAN_PUBLICATION";
    public static String PUBLISH_ANNOUNCEMENT_TO_ACTU_LEGALE = "PUBLISH_ANNOUNCEMENT_TO_ACTU_LEGALE";
    public static String SEND_PUBLICATION_FLAG = "SEND_PUBLICATION_FLAG";
    public static String DO_OCR_ON_INVOICE = "DO_OCR_ON_INVOICE";
    public static String DO_OCR_ON_RECEIPT = "DO_OCR_ON_RECEIPT";
    public static String CLEAN_AUDIT = "CLEAN_AUDIT";
    public static String UPDATE_COMPETENT_AUTHORITY = "UPDATE_COMPETENT_AUTHORITY";
    public static String UPDATE_AFFAIRE_FROM_RNE = "UPDATE_AFFAIRE_FROM_RNE";
    public static String AUTOMATCH_PAYMENT = "AUTOMATCH_PAYMENT";
    public static String SEND_BILLING_CLOSURE_RECEIPT = "SEND_BILLING_CLOSURE_RECEIPT";
    public static String REINDEX_DIRECT_DEBIT_BANK_TRANSFERT = "REINDEX_DIRECT_DEBIT_BANK_TRANSFERT";
    public static String REINDEX_INVOICE = "REINDEX_INVOICE";
    public static String REINDEX_PAYMENT = "REINDEX_PAYMENT";
    public static String REINDEX_REFUND = "REINDEX_REFUND";
    public static String REINDEX_AFFAIRE = "REINDEX_AFFAIRE";
    public static String REINDEX_ASSO_AFFAIRE_ORDER = "REINDEX_ASSO_AFFAIRE_ORDER";
    public static String REINDEX_BANK_TRANSFERT = "REINDEX_BANK_TRANSFERT";
    public static String REINDEX_CUSTOMER_ORDER = "REINDEX_CUSTOMER_ORDER";
    public static String REINDEX_QUOTATION = "REINDEX_QUOTATION";
    public static String REINDEX_RESPONSABLE = "REINDEX_RESPONSABLE";
    public static String REINDEX_TIERS = "REINDEX_TIERS";

    @Id
    @SequenceGenerator(name = "batch_sequence", sequenceName = "batch_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batch_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_batch_settings")
    private BatchSettings batchSettings;

    @Column(nullable = false)
    private LocalDateTime createdDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer entityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_batch_status")
    private BatchStatus batchStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_node")
    private Node node;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_osiris_log")
    private OsirisLog osirisLog;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public BatchSettings getBatchSettings() {
        return batchSettings;
    }

    public void setBatchSettings(BatchSettings batchSettings) {
        this.batchSettings = batchSettings;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(BatchStatus batchStatus) {
        this.batchStatus = batchStatus;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public OsirisLog getOsirisLog() {
        return osirisLog;
    }

    public void setOsirisLog(OsirisLog osirisLog) {
        this.osirisLog = osirisLog;
    }

}
