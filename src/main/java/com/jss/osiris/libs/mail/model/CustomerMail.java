package com.jss.osiris.libs.mail.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Rff;
import com.jss.osiris.modules.tiers.model.Tiers;

@Entity
@Table(indexes = {
        @Index(name = "idx_customer_mail_tiers", columnList = "id_tiers"),
        @Index(name = "idx_customer_mail_responsable", columnList = "id_responsable"),
        @Index(name = "idx_customer_mail_quotation", columnList = "id_quotation"),
        @Index(name = "idx_customer_mail_customer_order", columnList = "id_customer_order"),
        @Index(name = "idx_customer_mail_confrere", columnList = "id_confrere") })
public class CustomerMail {
    @Id
    @SequenceGenerator(name = "customer_mail_sequence", sequenceName = "customer_mail_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_mail_sequence")
    private Integer id;

    private String headerPicture;

    @Column(length = 1000)
    private String title;

    @Column(length = 2000)
    private String subtitle;

    @Column(length = 1000)
    private String label;

    @Column(length = 2000)
    private String labelSubtitle;

    @Column(length = 2000)
    private String explaination;

    @Column(columnDefinition = "TEXT")
    private String explainationElements;

    @Column(length = 2000)
    private String explaination2;

    @Column(length = 2000)
    private String explaination3;

    @Column(length = 2000)
    private String paymentExplaination;

    @Column(length = 2000)
    private String paymentExplaination2;

    @Column(length = 2000)
    private String cbExplanation;

    @Column(length = 1000)
    private String cbLink;

    @Column(length = 2000)
    private String quotationValidation;

    @Column(length = 1000)
    private String quotationValidationLink;

    @Column(length = 2000)
    private String paymentExplainationWarning;

    @OneToMany(mappedBy = "customerMail", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "customerMail" }, allowSetters = true)
    private List<VatMail> vatMails;

    private Float preTaxPriceTotal;
    private Float discountTotal;
    private Float preTaxPriceTotalWithDicount;

    @OneToMany(mappedBy = "customerMail", cascade = CascadeType.ALL)
    private List<CustomerMailAssoAffaireOrder> customerMailAssoAffaireOrders;

    private Float priceTotal;

    @Column(length = 2000)
    private String totalSubtitle;

    @Column(length = 1000)
    private String greetings;

    private Boolean sendToMe;

    private Boolean copyToMe;

    @Column(columnDefinition = "TEXT")
    private String customerMailCustomMessage;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mail_compute_result")
    MailComputeResult mailComputeResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee_reply_to")
    private Employee replyTo;

    private String replyToMail;

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

    private Boolean isSent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rff")
    Rff rff;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelSubtitle() {
        return labelSubtitle;
    }

    public void setLabelSubtitle(String labelSubtitle) {
        this.labelSubtitle = labelSubtitle;
    }

    public String getExplaination() {
        return explaination;
    }

    public void setExplaination(String explaination) {
        this.explaination = explaination;
    }

    public String getExplainationElements() {
        return explainationElements;
    }

    public void setExplainationElements(String explainationElements) {
        this.explainationElements = explainationElements;
    }

    public String getExplaination2() {
        return explaination2;
    }

    public void setExplaination2(String explaination2) {
        this.explaination2 = explaination2;
    }

    public Float getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(Float discountTotal) {
        this.discountTotal = discountTotal;
    }

    public Float getPreTaxPriceTotalWithDicount() {
        return preTaxPriceTotalWithDicount;
    }

    public void setPreTaxPriceTotalWithDicount(Float preTaxPriceTotalWithDicount) {
        this.preTaxPriceTotalWithDicount = preTaxPriceTotalWithDicount;
    }

    public List<VatMail> getVatMails() {
        return vatMails;
    }

    public void setVatMails(List<VatMail> vatMails) {
        this.vatMails = vatMails;
    }

    public Float getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(Float priceTotal) {
        this.priceTotal = priceTotal;
    }

    public String getTotalSubtitle() {
        return totalSubtitle;
    }

    public void setTotalSubtitle(String totalSubtitle) {
        this.totalSubtitle = totalSubtitle;
    }

    public String getGreetings() {
        return greetings;
    }

    public void setGreetings(String greetings) {
        this.greetings = greetings;
    }

    public Float getPreTaxPriceTotal() {
        return preTaxPriceTotal;
    }

    public void setPreTaxPriceTotal(Float preTaxPriceTotal) {
        this.preTaxPriceTotal = preTaxPriceTotal;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getSendToMe() {
        return sendToMe;
    }

    public void setSendToMe(Boolean sendToMe) {
        this.sendToMe = sendToMe;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public List<CustomerMailAssoAffaireOrder> getCustomerMailAssoAffaireOrders() {
        return customerMailAssoAffaireOrders;
    }

    public void setCustomerMailAssoAffaireOrders(List<CustomerMailAssoAffaireOrder> customerMailAssoAffaireOrders) {
        this.customerMailAssoAffaireOrders = customerMailAssoAffaireOrders;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public String getCbExplanation() {
        return cbExplanation;
    }

    public void setCbExplanation(String cbExplanation) {
        this.cbExplanation = cbExplanation;
    }

    public String getCbLink() {
        return cbLink;
    }

    public void setCbLink(String cbLink) {
        this.cbLink = cbLink;
    }

    public Employee getSendToMeEmployee() {
        return sendToMeEmployee;
    }

    public void setSendToMeEmployee(Employee sendToMeEmployee) {
        this.sendToMeEmployee = sendToMeEmployee;
    }

    public String getExplaination3() {
        return explaination3;
    }

    public void setExplaination3(String explaination3) {
        this.explaination3 = explaination3;
    }

    public String getReplyToMail() {
        return replyToMail;
    }

    public void setReplyToMail(String replyToMail) {
        this.replyToMail = replyToMail;
    }

    public String getPaymentExplaination() {
        return paymentExplaination;
    }

    public void setPaymentExplaination(String paymentExplaination) {
        this.paymentExplaination = paymentExplaination;
    }

    public String getPaymentExplaination2() {
        return paymentExplaination2;
    }

    public void setPaymentExplaination2(String paymentExplaination2) {
        this.paymentExplaination2 = paymentExplaination2;
    }

    public String getPaymentExplainationWarning() {
        return paymentExplainationWarning;
    }

    public void setPaymentExplainationWarning(String paymentExplainationWarning) {
        this.paymentExplainationWarning = paymentExplainationWarning;
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

    public Tiers getTiers() {
        return tiers;
    }

    public void setTiers(Tiers tiers) {
        this.tiers = tiers;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
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

    public String getCustomerMailCustomMessage() {
        return customerMailCustomMessage;
    }

    public void setCustomerMailCustomMessage(String customerMailCustomMessage) {
        this.customerMailCustomMessage = customerMailCustomMessage;
    }

    public Boolean getCopyToMe() {
        return copyToMe;
    }

    public void setCopyToMe(Boolean copyToMe) {
        this.copyToMe = copyToMe;
    }

    public String getQuotationValidation() {
        return quotationValidation;
    }

    public void setQuotationValidation(String quotationValidation) {
        this.quotationValidation = quotationValidation;
    }

    public String getQuotationValidationLink() {
        return quotationValidationLink;
    }

    public void setQuotationValidationLink(String quotationValidationLink) {
        this.quotationValidationLink = quotationValidationLink;
    }

    public Rff getRff() {
        return rff;
    }

    public void setRff(Rff rff) {
        this.rff = rff;
    }

}
