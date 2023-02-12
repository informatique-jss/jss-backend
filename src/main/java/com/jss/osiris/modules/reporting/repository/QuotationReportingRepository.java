package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.IQuotationReporting;

public interface QuotationReportingRepository extends CrudRepository<Quotation, Integer> {

    @Query(nativeQuery = true, value = "" +
            " select  " +
            " affaire.id as affaireId, " +
            " coalesce(affaire.denomination, affaire.firstname || ' '||affaire.lastname) as affaireLabel, " +
            " customer_order.id as customerOrderId, " +
            " provision.id as provisionId, " +
            " provision_t.label as provisionTypeLabel, " +
            " provision_ft.label as provisionFamilyTypeLabel, " +
            " coalesce(confrere.label, respo.firstname || ' '||respo.lastname, coalesce(tiers.denomination,tiers.firstname || ' ' ||tiers.lastname)) as customerOrderLabel, "
            +
            " coalesce(tiers.denomination,tiers.firstname || ' ' ||tiers.lastname) as tiersLabel, " +
            " sum(coalesce(invoice_item.pre_tax_price,0)) as preTaxPrice, " +
            " sum(coalesce(invoice_item.pre_tax_price,0)+coalesce(invoice_item.vat_price,0)-coalesce(invoice_item.discount_amount,0)) as taxedPrice "
            +
            " from asso_affaire_order asso " +
            " join provision on provision.id_asso_affaire_order = asso.id " +
            " join affaire on affaire.id = asso.id_affaire " +
            " join customer_order customer_order on customer_order.id = asso.id_customer_order " +
            " left join responsable respo on respo.id = customer_order.id_responsable " +
            " left join tiers on tiers.id = customer_order.id_tiers or tiers.id = respo.id_tiers " +
            " left join confrere on confrere.id = customer_order.id_confrere " +
            " left join provision_type provision_t on provision_t.id = provision.id_provision_type " +
            " left join provision_family_type provision_ft on provision_ft.id = provision.id_provision_family_type " +
            " left join invoice_item on invoice_item.id_provision = provision.id " +
            " group by " +
            " affaire.id , " +
            " coalesce(affaire.denomination, affaire.firstname || ' '||affaire.lastname) , " +
            " customer_order.id  , " +
            " provision.id  , " +
            " provision_t.label  , " +
            " provision_ft.label , " +
            " coalesce(confrere.label, respo.firstname || ' '||respo.lastname, coalesce(tiers.denomination,tiers.firstname || ' ' ||tiers.lastname))  , "
            +
            " coalesce(tiers.denomination,tiers.firstname || ' ' ||tiers.lastname)   " +
            "")
    List<IQuotationReporting> getQuotationReporting();
}