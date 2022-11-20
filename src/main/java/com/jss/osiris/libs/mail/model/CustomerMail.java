package com.jss.osiris.libs.mail.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

@Entity
public class CustomerMail {
    @Id
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
    private String cbExplanation;

    @Column(length = 1000)
    private String cbLink;

    @Column(length = 2000)
    private String explainationWarning;

    public String getExplainationWarning() {
        return explainationWarning;
    }

    public void setExplainationWarning(String explainationWarning) {
        this.explainationWarning = explainationWarning;
    }

    @OneToMany(mappedBy = "customerMail", cascade = CascadeType.ALL)
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_mail_compute_result")
    MailComputeResult mailComputeResult;

    @ManyToOne
    @JoinColumn(name = "id_employee_reply_to")
    private Employee replyTo;

    @Column(length = 2000)
    private String subject;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @ManyToOne
    @JoinColumn(name = "id_customer_order")
    CustomerOrder customerOrder;

    @ManyToOne
    @JoinColumn(name = "id_invoice")
    Invoice invoice;

    private Boolean sendInvoiceAttachment;

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

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Boolean getSendInvoiceAttachment() {
        return sendInvoiceAttachment;
    }

    public void setSendInvoiceAttachment(Boolean sendInvoiceAttachment) {
        this.sendInvoiceAttachment = sendInvoiceAttachment;
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

}
