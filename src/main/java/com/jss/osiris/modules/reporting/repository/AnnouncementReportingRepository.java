package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.IAnnouncementReporting;

public interface AnnouncementReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        "         select " +
                        " 	to_char(date_trunc('year', " +
                        " 	a.publication_date), " +
                        " 	'YYYY') as announcementCreatedDateYear, " +
                        " 	to_char(date_trunc('month', " +
                        " 	a.publication_date), " +
                        " 	'YYYY-MM') as announcementCreatedDateMonth, " +
                        " 	to_char(date_trunc('week', " +
                        " 	a.publication_date), " +
                        " 	'YYYY-MM - tmw') as announcementCreatedDateWeek, " +
                        " 	to_char(date_trunc('day', " +
                        " 	a.publication_date), " +
                        " 	'YYYY-MM-DD') as announcementCreatedDateDay, " +
                        " 	as2.label as announcementStatus, " +
                        " 	c.label as confrereAnnouncementLabel, " +
                        " 	d.code as announcementDepartment, " +
                        " 	sum(coalesce(a.character_number,0)) as characterNumber, " +
                        " 	sum(pre_tax_price)  as preTaxPrice, " +
                        " 	(select STRING_AGG(DISTINCT nt.label ,', '  ) from    asso_announcement_notice_type nta   left join notice_type nt on nt.id = nta.id_notice_type  where nta.id_announcement = a.id )  as noticeTypeLabel, "
                        +
                        " 	ntf.label as noticeTypeFamilyLabel, "
                        +
                        " 	jt.label as journalTypeLabel " +
                        " from " +
                        " 	announcement a " +
                        " join announcement_status as2 on " +
                        " 	as2.id = a.id_announcement_status " +
                        " join provision p on " +
                        " 	p.id_announcement = a.id " +
                        " join asso_affaire_order aao on " +
                        " 	aao.id = p.id_asso_affaire_order " +
                        " join customer_order co on " +
                        " 	co.id = aao.id_customer_order " +
                        " join confrere c on " +
                        " 	c.id = a.id_confrere " +
                        " join department d on " +
                        " 	d.id = a.id_department  " +
                        " left join invoice_item ii on ii.id_provision = p.id and p.id_provision_type  in (select id from provision_type pt where label like 'Annonce%')  "
                        +
                        " left join journal_type jt on jt.id = c.id_journal_type left join notice_type_family ntf on ntf.id = a.id_notice_type_family  "
                        +
                        " where " +
                        " 	co.id_customer_order_status not in :customerOrderStatusIdExcluded " +
                        " group by " +
                        " 	to_char(date_trunc('year', " +
                        " 	a.publication_date), " +
                        " 	'YYYY'), " +
                        " 	to_char(date_trunc('month', " +
                        " 	a.publication_date), " +
                        " 	'YYYY-MM') , " +
                        " 	to_char(date_trunc('week', " +
                        " 	a.publication_date), " +
                        " 	'YYYY-MM - tmw'), " +
                        " 	to_char(date_trunc('day', " +
                        " 	a.publication_date), " +
                        " 	'YYYY-MM-DD'), " +
                        " 	as2.label, ntf.label, " +
                        " 	c.label ,d.code , jt.label ,a.character_number, a.id " +
                        "")
        List<IAnnouncementReporting> getAnnouncementReporting(
                        @Param("customerOrderStatusIdExcluded") List<Integer> customerOrderStatusIdExcluded);
}