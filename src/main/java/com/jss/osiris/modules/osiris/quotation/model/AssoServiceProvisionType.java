package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeJuridique;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_asso_service_provision_type", columnList = "id_service_type") })
public class AssoServiceProvisionType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_service_provision_type_sequence", sequenceName = "asso_service_provision_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_provision_type_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service_type")
	@IndexedField
	private ServiceType serviceType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_provision_type")
	@IndexedField
	private ProvisionType provisionType;

	@Column(columnDefinition = "TEXT")
	private String apeCodes;

	@ManyToMany
	@JoinTable(name = "asso_service_provision_type_forme_juridique", joinColumns = @JoinColumn(name = "id_asso_service_provision_type"), inverseJoinColumns = @JoinColumn(name = "id_forme_juridique"))
	private List<FormeJuridique> formeJuridiques;

	private Integer minEmployee;
	private Integer maxEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_notice_type")
	private NoticeType noticeType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_notice_type_family")
	private NoticeTypeFamily noticeTypeFamily;

	@Column(length = 400)
	private String customerMessageWhenAdded;

	// 1 hard => 3 easy
	private Integer complexity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_announcement_notice_template")
	@JsonIgnoreProperties(value = { "provisionFamilyTypes" }, allowSetters = true)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private AnnouncementNoticeTemplate announcementNoticeTemplate;

	private Boolean isPriority;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 10, scale = 2)
	private BigDecimal defaultDeboursPrice;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 10, scale = 2)
	private BigDecimal defaultDeboursPriceNonTaxable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProvisionType getProvisionType() {
		return provisionType;
	}

	public void setProvisionType(ProvisionType provisionType) {
		this.provisionType = provisionType;
	}

	public List<FormeJuridique> getFormeJuridiques() {
		return formeJuridiques;
	}

	public void setFormeJuridiques(List<FormeJuridique> formeJuridiques) {
		this.formeJuridiques = formeJuridiques;
	}

	public Integer getMinEmployee() {
		return minEmployee;
	}

	public void setMinEmployee(Integer minEmployee) {
		this.minEmployee = minEmployee;
	}

	public Integer getMaxEmployee() {
		return maxEmployee;
	}

	public void setMaxEmployee(Integer maxEmployee) {
		this.maxEmployee = maxEmployee;
	}

	public String getCustomerMessageWhenAdded() {
		return customerMessageWhenAdded;
	}

	public void setCustomerMessageWhenAdded(String customerMessageWhenAdded) {
		this.customerMessageWhenAdded = customerMessageWhenAdded;
	}

	public String getApeCodes() {
		return apeCodes;
	}

	public void setApeCodes(String apeCodes) {
		this.apeCodes = apeCodes;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public NoticeType getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(NoticeType noticeType) {
		this.noticeType = noticeType;
	}

	public NoticeTypeFamily getNoticeTypeFamily() {
		return noticeTypeFamily;
	}

	public void setNoticeTypeFamily(NoticeTypeFamily noticeTypeFamily) {
		this.noticeTypeFamily = noticeTypeFamily;
	}

	public Integer getComplexity() {
		return complexity;
	}

	public void setComplexity(Integer complexity) {
		this.complexity = complexity;
	}

	public AnnouncementNoticeTemplate getAnnouncementNoticeTemplate() {
		return announcementNoticeTemplate;
	}

	public void setAnnouncementNoticeTemplate(AnnouncementNoticeTemplate announcementNoticeTemplate) {
		this.announcementNoticeTemplate = announcementNoticeTemplate;
	}

	public Boolean getIsPriority() {
		return isPriority;
	}

	public void setIsPriority(Boolean isPriority) {
		this.isPriority = isPriority;
	}

	public BigDecimal getDefaultDeboursPrice() {
		return defaultDeboursPrice;
	}

	public void setDefaultDeboursPrice(BigDecimal defaultDeboursPrice) {
		this.defaultDeboursPrice = defaultDeboursPrice;
	}

	public BigDecimal getDefaultDeboursPriceNonTaxable() {
		return defaultDeboursPriceNonTaxable;
	}

	public void setDefaultDeboursPriceNonTaxable(BigDecimal defaultDeboursPriceNonTaxable) {
		this.defaultDeboursPriceNonTaxable = defaultDeboursPriceNonTaxable;
	}

}
