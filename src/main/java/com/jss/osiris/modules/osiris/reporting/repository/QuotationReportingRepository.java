package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.reporting.model.IQuotationReporting;

public interface QuotationReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select    " +
                        " affaire.id as affaireId,   " +
                        " affaire.siren as affaireSiren,   " +
                        " affaire.siret as affaireSiret,   " +
                        " coalesce(affaire.denomination, affaire.firstname || ' '||affaire.lastname) as affaireLabel,  "
                        +
                        " customer_order.id as quotationId,   " +
                        " provision.id as provisionId,   " +
                        " provision_t.label as provisionTypeLabel,   " +
                        " ca.label as waitedCompetentAuthorityLabel,   " +
                        " customer_order_status.label as quotationStatusLabel,   " +
                        " provision_ft.label as provisionFamilyTypeLabel,   " +
                        " coalesce(initcap(to_char(a.publication_date,'MM - tmmonth')),'N/A') as publicationDateMonth,   "
                        +
                        " coalesce(initcap(to_char(a.publication_date,'tmw')),'N/A') as publicationDateWeek,   " +
                        "   coalesce(initcap(to_char(customer_order.created_date,'MM - tmmonth')),'N/A') as quotationCreatedDateMonth,  "
                        +
                        " coalesce(initcap(to_char(customer_order.created_date,'YYYY-MM-DD')),'N/A') as quotationCreatedDateDay,  "
                        +
                        " coalesce(confrere.label, respo.firstname || ' '||respo.lastname, coalesce(tiers.denomination,tiers.firstname || ' ' ||tiers.lastname)) as customerOrderLabel,  "
                        +
                        " coalesce(tiers.denomination,tiers.firstname || ' ' ||tiers.lastname) as tiersLabel,   " +
                        " coalesce(ast.label, sps.label, ds.label,   fs.label) as provisionStatus,   " +
                        " confrere_a.label as confrereAnnouncementLabel,   " +
                        " ntf.label as noticeTypeFamilyLabel,   " +
                        "  STRING_AGG(DISTINCT nt.label ,', '  ) as noticeTypeLabel,   " +
                        " origin.label as customerOrderOriginLabel," +
                        " e1.firstname || ' ' || e1.lastname as provisionAssignedToLabel,   " +
                        " e2.firstname || ' ' || e2.lastname as salesEmployeeLabel,   " +
                        " sum(coalesce(case when billing_type.is_fee=false and billing_type.is_debour = false then invoice_item.pre_tax_price end,0)) as preTaxPriceWithoutDebour, "
                        +
                        "  quotation_creator.firstname || ' ' || quotation_creator.lastname as quotationCreator,    "
                        +
                        " max((substring(invoice_item.label,'(\\d)(?=\\s*caract)'))) as characterNumber " +
                        " from asso_affaire_order asso   " +
                        " join service on service.id_asso_affaire_order = asso.id   " +
                        " join provision on provision.id_service = service.id   " +
                        " left join employee e1 on e1.id = provision.id_employee   " +
                        " join affaire on affaire.id = asso.id_affaire   " +
                        " join quotation customer_order on customer_order.id = asso.id_quotation   " +
                        " join customer_order_origin origin on origin.id = customer_order.id_customer_order_origin" +
                        " join quotation_status customer_order_status on customer_order_status.id = customer_order.id_quotation_status  "
                        +
                        " left join responsable respo on respo.id = customer_order.id_responsable   " +
                        " left join tiers on tiers.id = customer_order.id_tiers or tiers.id = respo.id_tiers   " +
                        " left join employee e2 on e2.id = tiers.id_commercial   " +
                        " left join confrere on confrere.id = customer_order.id_confrere   " +
                        " left join provision_type provision_t on provision_t.id = provision.id_provision_type   " +
                        " left join provision_family_type provision_ft on provision_ft.id = provision.id_provision_family_type  "
                        +
                        " left join invoice_item on invoice_item.id_provision = provision.id   " +
                        " left join billing_item on billing_item.id = invoice_item.id_billing_item   " +
                        " left join billing_type on billing_item.id_billing_type = billing_type.id   " +
                        " left join announcement a on a.id = provision.id_announcement    " +
                        " left join confrere confrere_a on confrere_a.id = a.id_confrere    " +
                        " left join notice_type_family ntf on ntf.id = a.id_notice_type_family    " +
                        " left join asso_announcement_notice_type nta on nta.id_announcement = a.id    " +
                        " left join notice_type nt on nt.id = nta.id_notice_type    " +
                        " left join announcement_status ast on ast.id = a.id_announcement_status   " +
                        " left join simple_provision sp on sp.id = provision.id_simple_provision   " +
                        " left join simple_provision_status sps on sps.id = sp.id_simple_provision_status   " +
                        " left join competent_authority ca on ca.id = sp.id_waited_competent_authority   " +
                        " left join domiciliation d on d.id = provision.id_domiciliation   " +
                        " left join domiciliation_status ds on ds.id = d.id_domicilisation_status    " +
                        " left join formalite f on f.id = provision.id_formalite   " +
                        " left join formalite_status fs on fs.id = f.id_formalite_status  " +
                        " left join audit audit_quotation on audit_quotation.entity_id = customer_order.id and audit_quotation.field_name = 'id' and entity = 'Quotation' "
                        +
                        " left join employee quotation_creator on quotation_creator.username = audit_quotation.username "
                        +
                        "  where customer_order.created_date BETWEEN  date_trunc('year', now()) AND CURRENT_TIMESTAMP   "
                        +
                        "    group by   " +
                        " affaire.id , origin.label,  " +
                        " affaire.siren ,   " +
                        " affaire.siret ,   " +
                        " ca.label ,   " +
                        " customer_order.created_date ,   " +
                        " a.publication_date ,   " +
                        " customer_order_status.label ,   " +
                        " e1.firstname, e1.lastname ,   " +
                        " e2.firstname, e2.lastname ,   " +
                        " coalesce(ast.label, sps.label, ds.label,   fs.label)  , quotation_creator.firstname,quotation_creator.lastname,  "
                        +
                        " coalesce(affaire.denomination, affaire.firstname || ' '||affaire.lastname) ,   " +
                        " customer_order.id  ,   " +
                        " provision.id  ,   " +
                        " confrere_a.label ,   " +
                        " ntf.label ,   " +
                        "  provision_t.label  ,   " +
                        " provision_ft.label   ,     " +
                        " coalesce(confrere.label, respo.firstname || ' '||respo.lastname, coalesce(tiers.denomination,tiers.firstname || ' ' ||tiers.lastname))  ,  "
                        +
                        " coalesce(tiers.denomination,tiers.firstname || ' ' ||tiers.lastname)     " +
                        "")
        List<IQuotationReporting> getQuotationReporting();
}