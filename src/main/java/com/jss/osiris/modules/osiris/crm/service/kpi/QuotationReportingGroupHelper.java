package com.jss.osiris.modules.osiris.crm.service.kpi;

import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

@org.springframework.stereotype.Service
public class QuotationReportingGroupHelper {
	public ReportingGroup getQuotationType(IQuotation quotation) {
		ReportingGroup currentReportingGroup = ReportingGroup.OTHER;
		if (quotation != null && quotation.getAssoAffaireOrders() != null) {
			for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
				if (asso.getServices() != null) {
					for (Service service : asso.getServices()) {
						if (service.getServiceTypes() != null) {
							for (ServiceType serviceType : service.getServiceTypes()) {
								if (serviceType.getReportingGroup() != null) {
									for (ReportingGroup reportingGroup : ReportingGroup.values()) {
										if (reportingGroup.getLabel().equals(serviceType.getReportingGroup())) {
											if (reportingGroup == ReportingGroup.FORMALITIES) {
												return ReportingGroup.FORMALITIES;
											} else if (reportingGroup == ReportingGroup.ANNOUNCEMENTS) {
												if (currentReportingGroup != ReportingGroup.FORMALITIES)
													currentReportingGroup = ReportingGroup.ANNOUNCEMENTS;
											} else if (reportingGroup == ReportingGroup.DEPOSIT) {
												if (currentReportingGroup != ReportingGroup.FORMALITIES
														&& currentReportingGroup != ReportingGroup.ANNOUNCEMENTS)
													currentReportingGroup = ReportingGroup.DEPOSIT;
											} else {
												if (currentReportingGroup != ReportingGroup.FORMALITIES
														&& currentReportingGroup != ReportingGroup.ANNOUNCEMENTS
														&& currentReportingGroup != ReportingGroup.DEPOSIT)
													currentReportingGroup = reportingGroup;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return currentReportingGroup;
	}
}