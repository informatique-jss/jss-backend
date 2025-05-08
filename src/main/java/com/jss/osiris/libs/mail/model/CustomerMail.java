package com.jss.osiris.libs.mail.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Rff;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "idx_customer_mail_send", columnList = "is_sent"),
        @Index(name = "idx_customer_mail_tiers", columnList = "id_tiers"),
        @Index(name = "idx_customer_mail_responsable", columnList = "id_responsable"),
        @Index(name = "idx_customer_mail_quotation", columnList = "id_quotation"),
        @Index(name = "idx_customer_mail_customer_order", columnList = "id_customer_order"),
        @Index(name = "idx_customer_mail_competent_authority", columnList = "id_competent_authority"),
        @Index(name = "idx_customer_mail_confrere", columnList = "id_confrere") })
public class CustomerMail {

    public static String TEMPLATE_WAITING_DEPOSIT = "waiting-deposit";
    public static String TEMPLATE_WAITING_QUOTATION_VALIDATION = "waiting-quotation-validation";
    public static String TEMPLATE_QUOTATION_VALIDATED = "quotation-validated";
    public static String TEMPLATE_SEND_CREDIT_NOTE = "send-credit-note";
    public static String TEMPLATE_CUSTOMER_ORDER_IN_PROGRESS = "customer-order-in-progress";
    public static String TEMPLATE_SEND_ATTACHMENTS = "send-attanchments";
    public static String TEMPLATE_SEND_PUBLICATION_RECEIPT = "send-publication-receipt";
    public static String TEMPLATE_SEND_PUBLICATION_FLAG = "send-publication-flag";
    public static String TEMPLATE_SEND_PROOF_READING = "send-proof-reading";
    public static String TEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE = "send-announcement-confrere";
    public static String TEMPLATE_SEND_ANNOUNCEMENT_ERRATUM_TO_CONFRERE = "send-announcement-erratum-confrere";
    public static String TEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE_REMINDER = "send-announcement-confrere-reminder";
    public static String TEMPLATE_SEND_CONFRERE_PROVIDER_INVOICE_REMINDER = "send-confrere-provider-invoice-reminder";
    public static String TEMPLATE_CUSTOMER_ORDER_FINALIZATION = "customer-order-finalization";
    public static String TEMPLATE_SEND_CUSTOMER_BILAN_PUBLICATION_REMINDER = "send-customer-bilan-publication-reminder";
    public static String TEMPLATE_BILLING_CLOSURE = "billing-closure";
    public static String TEMPLATE_INVOICE_REMINDER = "invoice-reminder";
    public static String TEMPLATE_MISSING_ATTACHMENT = "missing-attachment";
    public static String TEMPLATE_RENEW_PASSWORD = "renew-password"; // TODO : delete
    public static String TEMPLATE_SEND_TOKEN = "send-token";
    public static String TEMPLATE_REQUEST_RIB = "request-rib";
    public static String TEMPLATE_SEND_RFF = "send-rff";
    public static String TEMPLATE_SEND_COMPETENT_AUTHORITY_REMINDER = "send-competent-authority-reminder";
    public static String TEMPLATE_SEND_WEBINAR_SUBSCRIPTION = "send-webinar-subscription";
    public static String TEMPLATE_SEND_DEMO_CONFIRMATION = "send-demo-confirmation";
    public static String TEMPLATE_SEND_DEMO_REQUEST = "send-demo-request";

    @Id
    @SequenceGenerator(name = "customer_mail_sequence", sequenceName = "customer_mail_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_mail_sequence")
    private Integer id;

    private String headerPicture;

    @Column(columnDefinition = "TEXT")
    private String explaination;

    @Column(length = 1000)
    private String cbLink;

    private Boolean sendToMe;

    private Boolean copyToMe;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mail_compute_result")
    MailComputeResult mailComputeResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee_reply_to")
    private Employee replyTo;

    private String replyToMail;
    private String copyToMail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee_send_to_me")
    private Employee sendToMeEmployee;

    @Column(length = 2000)
    private String subject;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer_order")
    @JsonIgnore // For client-side performance purpose
    @JsonIgnoreProperties(value = { "assoAffaireOrders", "tiers", "responsable", "confrere", "invoices",
            "providerInvoices" }, allowSetters = true)
    CustomerOrder customerOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quotation")
    Quotation quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tiers")
    Tiers tiers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable")
    Responsable responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_confrere")
    Confrere confrere;

    @OneToMany(mappedBy = "customerMail")
    @JsonIgnoreProperties(value = { "customerMail" }, allowSetters = true)
    private List<Attachment> attachments;

    @Column(name = "is_sent")
    private Boolean isSent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rff")
    private Rff rff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_missing_attachment_query")
    private MissingAttachmentQuery missingAttachmentQuery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_provision")
    private Provision provision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competent_authority")
    private CompetentAuthority competentAuthority;

    private String mailTemplate;

    private LocalDateTime toSendAfter;

    private Boolean isCancelled;

    private Boolean isLastReminder;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeaderPicture() {
        return headerPicture;
    }

    public void setHeaderPicture(String headerPicture) {
        this.headerPicture = headerPicture;
    }

    public static String getTEMPLATE_WAITING_DEPOSIT() {
        return TEMPLATE_WAITING_DEPOSIT;
    }

    public static void setTEMPLATE_WAITING_DEPOSIT(String tEMPLATE_WAITING_DEPOSIT) {
        TEMPLATE_WAITING_DEPOSIT = tEMPLATE_WAITING_DEPOSIT;
    }

    public static String getTEMPLATE_WAITING_QUOTATION_VALIDATION() {
        return TEMPLATE_WAITING_QUOTATION_VALIDATION;
    }

    public static void setTEMPLATE_WAITING_QUOTATION_VALIDATION(String tEMPLATE_WAITING_QUOTATION_VALIDATION) {
        TEMPLATE_WAITING_QUOTATION_VALIDATION = tEMPLATE_WAITING_QUOTATION_VALIDATION;
    }

    public static String getTEMPLATE_QUOTATION_VALIDATED() {
        return TEMPLATE_QUOTATION_VALIDATED;
    }

    public static void setTEMPLATE_QUOTATION_VALIDATED(String tEMPLATE_QUOTATION_VALIDATED) {
        TEMPLATE_QUOTATION_VALIDATED = tEMPLATE_QUOTATION_VALIDATED;
    }

    public static String getTEMPLATE_SEND_CREDIT_NOTE() {
        return TEMPLATE_SEND_CREDIT_NOTE;
    }

    public static void setTEMPLATE_SEND_CREDIT_NOTE(String tEMPLATE_SEND_CREDIT_NOTE) {
        TEMPLATE_SEND_CREDIT_NOTE = tEMPLATE_SEND_CREDIT_NOTE;
    }

    public static String getTEMPLATE_CUSTOMER_ORDER_IN_PROGRESS() {
        return TEMPLATE_CUSTOMER_ORDER_IN_PROGRESS;
    }

    public static void setTEMPLATE_CUSTOMER_ORDER_IN_PROGRESS(String tEMPLATE_CUSTOMER_ORDER_IN_PROGRESS) {
        TEMPLATE_CUSTOMER_ORDER_IN_PROGRESS = tEMPLATE_CUSTOMER_ORDER_IN_PROGRESS;
    }

    public static String getTEMPLATE_SEND_ATTACHMENTS() {
        return TEMPLATE_SEND_ATTACHMENTS;
    }

    public static void setTEMPLATE_SEND_ATTACHMENTS(String tEMPLATE_SEND_ATTACHMENTS) {
        TEMPLATE_SEND_ATTACHMENTS = tEMPLATE_SEND_ATTACHMENTS;
    }

    public static String getTEMPLATE_SEND_PUBLICATION_RECEIPT() {
        return TEMPLATE_SEND_PUBLICATION_RECEIPT;
    }

    public static void setTEMPLATE_SEND_PUBLICATION_RECEIPT(String tEMPLATE_SEND_PUBLICATION_RECEIPT) {
        TEMPLATE_SEND_PUBLICATION_RECEIPT = tEMPLATE_SEND_PUBLICATION_RECEIPT;
    }

    public static String getTEMPLATE_SEND_PUBLICATION_FLAG() {
        return TEMPLATE_SEND_PUBLICATION_FLAG;
    }

    public static void setTEMPLATE_SEND_PUBLICATION_FLAG(String tEMPLATE_SEND_PUBLICATION_FLAG) {
        TEMPLATE_SEND_PUBLICATION_FLAG = tEMPLATE_SEND_PUBLICATION_FLAG;
    }

    public static String getTEMPLATE_SEND_PROOF_READING() {
        return TEMPLATE_SEND_PROOF_READING;
    }

    public static void setTEMPLATE_SEND_PROOF_READING(String tEMPLATE_SEND_PROOF_READING) {
        TEMPLATE_SEND_PROOF_READING = tEMPLATE_SEND_PROOF_READING;
    }

    public static String getTEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE() {
        return TEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE;
    }

    public static void setTEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE(String tEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE) {
        TEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE = tEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE;
    }

    public static String getTEMPLATE_SEND_ANNOUNCEMENT_ERRATUM_TO_CONFRERE() {
        return TEMPLATE_SEND_ANNOUNCEMENT_ERRATUM_TO_CONFRERE;
    }

    public static void setTEMPLATE_SEND_ANNOUNCEMENT_ERRATUM_TO_CONFRERE(
            String tEMPLATE_SEND_ANNOUNCEMENT_ERRATUM_TO_CONFRERE) {
        TEMPLATE_SEND_ANNOUNCEMENT_ERRATUM_TO_CONFRERE = tEMPLATE_SEND_ANNOUNCEMENT_ERRATUM_TO_CONFRERE;
    }

    public static String getTEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE_REMINDER() {
        return TEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE_REMINDER;
    }

    public static void setTEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE_REMINDER(
            String tEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE_REMINDER) {
        TEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE_REMINDER = tEMPLATE_SEND_ANNOUNCEMENT_TO_CONFRERE_REMINDER;
    }

    public static String getTEMPLATE_SEND_CONFRERE_PROVIDER_INVOICE_REMINDER() {
        return TEMPLATE_SEND_CONFRERE_PROVIDER_INVOICE_REMINDER;
    }

    public static void setTEMPLATE_SEND_CONFRERE_PROVIDER_INVOICE_REMINDER(
            String tEMPLATE_SEND_CONFRERE_PROVIDER_INVOICE_REMINDER) {
        TEMPLATE_SEND_CONFRERE_PROVIDER_INVOICE_REMINDER = tEMPLATE_SEND_CONFRERE_PROVIDER_INVOICE_REMINDER;
    }

    public static String getTEMPLATE_CUSTOMER_ORDER_FINALIZATION() {
        return TEMPLATE_CUSTOMER_ORDER_FINALIZATION;
    }

    public static void setTEMPLATE_CUSTOMER_ORDER_FINALIZATION(String tEMPLATE_CUSTOMER_ORDER_FINALIZATION) {
        TEMPLATE_CUSTOMER_ORDER_FINALIZATION = tEMPLATE_CUSTOMER_ORDER_FINALIZATION;
    }

    public static String getTEMPLATE_BILLING_CLOSURE() {
        return TEMPLATE_BILLING_CLOSURE;
    }

    public static void setTEMPLATE_BILLING_CLOSURE(String tEMPLATE_BILLING_CLOSURE) {
        TEMPLATE_BILLING_CLOSURE = tEMPLATE_BILLING_CLOSURE;
    }

    public static String getTEMPLATE_INVOICE_REMINDER() {
        return TEMPLATE_INVOICE_REMINDER;
    }

    public static void setTEMPLATE_INVOICE_REMINDER(String tEMPLATE_INVOICE_REMINDER) {
        TEMPLATE_INVOICE_REMINDER = tEMPLATE_INVOICE_REMINDER;
    }

    public static String getTEMPLATE_MISSING_ATTACHMENT() {
        return TEMPLATE_MISSING_ATTACHMENT;
    }

    public static void setTEMPLATE_MISSING_ATTACHMENT(String tEMPLATE_MISSING_ATTACHMENT) {
        TEMPLATE_MISSING_ATTACHMENT = tEMPLATE_MISSING_ATTACHMENT;
    }

    public static String getTEMPLATE_RENEW_PASSWORD() {
        return TEMPLATE_RENEW_PASSWORD;
    }

    public static void setTEMPLATE_RENEW_PASSWORD(String tEMPLATE_RENEW_PASSWORD) {
        TEMPLATE_RENEW_PASSWORD = tEMPLATE_RENEW_PASSWORD;
    }

    public static String getTEMPLATE_REQUEST_RIB() {
        return TEMPLATE_REQUEST_RIB;
    }

    public static void setTEMPLATE_REQUEST_RIB(String tEMPLATE_REQUEST_RIB) {
        TEMPLATE_REQUEST_RIB = tEMPLATE_REQUEST_RIB;
    }

    public static String getTEMPLATE_SEND_RFF() {
        return TEMPLATE_SEND_RFF;
    }

    public static void setTEMPLATE_SEND_RFF(String tEMPLATE_SEND_RFF) {
        TEMPLATE_SEND_RFF = tEMPLATE_SEND_RFF;
    }

    public String getExplaination() {
        return explaination;
    }

    public void setExplaination(String explaination) {
        this.explaination = explaination;
    }

    public String getCbLink() {
        return cbLink;
    }

    public void setCbLink(String cbLink) {
        this.cbLink = cbLink;
    }

    public Boolean getSendToMe() {
        return sendToMe;
    }

    public void setSendToMe(Boolean sendToMe) {
        this.sendToMe = sendToMe;
    }

    public Boolean getCopyToMe() {
        return copyToMe;
    }

    public void setCopyToMe(Boolean copyToMe) {
        this.copyToMe = copyToMe;
    }

    public MailComputeResult getMailComputeResult() {
        return mailComputeResult;
    }

    public void setMailComputeResult(MailComputeResult mailComputeResult) {
        this.mailComputeResult = mailComputeResult;
    }

    public Employee getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Employee replyTo) {
        this.replyTo = replyTo;
    }

    public String getReplyToMail() {
        return replyToMail;
    }

    public void setReplyToMail(String replyToMail) {
        this.replyToMail = replyToMail;
    }

    public Employee getSendToMeEmployee() {
        return sendToMeEmployee;
    }

    public void setSendToMeEmployee(Employee sendToMeEmployee) {
        this.sendToMeEmployee = sendToMeEmployee;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }

    public Tiers getTiers() {
        return tiers;
    }

    public void setTiers(Tiers tiers) {
        this.tiers = tiers;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }

    public Confrere getConfrere() {
        return confrere;
    }

    public void setConfrere(Confrere confrere) {
        this.confrere = confrere;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }

    public Rff getRff() {
        return rff;
    }

    public void setRff(Rff rff) {
        this.rff = rff;
    }

    public MissingAttachmentQuery getMissingAttachmentQuery() {
        return missingAttachmentQuery;
    }

    public void setMissingAttachmentQuery(MissingAttachmentQuery missingAttachmentQuery) {
        this.missingAttachmentQuery = missingAttachmentQuery;
    }

    public Provision getProvision() {
        return provision;
    }

    public void setProvision(Provision provision) {
        this.provision = provision;
    }

    public String getMailTemplate() {
        return mailTemplate;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    public LocalDateTime getToSendAfter() {
        return toSendAfter;
    }

    public void setToSendAfter(LocalDateTime toSendAfter) {
        this.toSendAfter = toSendAfter;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Boolean getIsLastReminder() {
        return isLastReminder;
    }

    public void setIsLastReminder(Boolean isLastReminder) {
        this.isLastReminder = isLastReminder;
    }

    public static String getTEMPLATE_SEND_CUSTOMER_BILAN_PUBLICATION_REMINDER() {
        return TEMPLATE_SEND_CUSTOMER_BILAN_PUBLICATION_REMINDER;
    }

    public static void setTEMPLATE_SEND_CUSTOMER_BILAN_PUBLICATION_REMINDER(
            String tEMPLATE_SEND_CUSTOMER_BILAN_PUBLICATION_REMINDER) {
        TEMPLATE_SEND_CUSTOMER_BILAN_PUBLICATION_REMINDER = tEMPLATE_SEND_CUSTOMER_BILAN_PUBLICATION_REMINDER;
    }

    public CompetentAuthority getCompetentAuthority() {
        return competentAuthority;
    }

    public void setCompetentAuthority(CompetentAuthority competentAuthority) {
        this.competentAuthority = competentAuthority;
    }

    public static String getTEMPLATE_SEND_COMPETENT_AUTHORITY_REMINDER() {
        return TEMPLATE_SEND_COMPETENT_AUTHORITY_REMINDER;
    }

    public static void setTEMPLATE_SEND_COMPETENT_AUTHORITY_REMINDER(
            String tEMPLATE_SEND_COMPETENT_AUTHORITY_REMINDER) {
        TEMPLATE_SEND_COMPETENT_AUTHORITY_REMINDER = tEMPLATE_SEND_COMPETENT_AUTHORITY_REMINDER;
    }

    public static String getTEMPLATE_SEND_WEBINAR_SUBSCRIPTION() {
        return TEMPLATE_SEND_WEBINAR_SUBSCRIPTION;
    }

    public static void setTEMPLATE_SEND_WEBINAR_SUBSCRIPTION(
            String tEMPLATE_SEND_WEBINAR_SUBSCRIPTION) {
        TEMPLATE_SEND_WEBINAR_SUBSCRIPTION = tEMPLATE_SEND_WEBINAR_SUBSCRIPTION;
    }

    public static void setTEMPLATE_SEND_DEMO_CONFIRMATION(
            String tEMPLATE_SEND_DEMO_CONFIRMATION) {
        TEMPLATE_SEND_DEMO_CONFIRMATION = tEMPLATE_SEND_DEMO_CONFIRMATION;
    }

    public static String getTEMPLATE_SEND_DEMO_CONFIRMATION() {
        return TEMPLATE_SEND_DEMO_CONFIRMATION;
    }

    public String getCopyToMail() {
        return copyToMail;
    }

    public void setCopyToMail(String copyToMail) {
        this.copyToMail = copyToMail;
    }

}
