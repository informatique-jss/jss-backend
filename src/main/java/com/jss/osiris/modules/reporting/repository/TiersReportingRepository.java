package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.reporting.model.ITiersReporting;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface TiersReportingRepository extends QueryCacheCrudRepository<Tiers, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select " +
                        " tiers.id as tiersId, " +
                        " respo.id as responsableId, " +
                        " coalesce (tiers.denomination, " +
                        " concat(tiers.firstname, " +
                        " ' ', " +
                        " tiers.lastname)) as tiersLabel, " +
                        " concat(respo.firstname, " +
                        " ' ', " +
                        " respo.lastname) as responsableLabel, " +
                        " case " +
                        " when respo.id is not null then concat(e2.firstname, " +
                        " ' ', " +
                        " e2.lastname) " +
                        " else concat(e1.firstname, " +
                        " ' ', " +
                        " e1.lastname) " +
                        " end as salesEmployeeLabel, " +
                        " tiers.postal_code as tiersPostalCode, " +
                        " tiers.cedex_complement as tiersCedexComplement, " +
                        " city.label as tiersCity, " +
                        " country.label as tiersCountry, " +
                        " tiers.intercommunity_vat as tiersIntercommunityVat, " +
                        " tt1.label as tiersType, " +
                        " tt2.label as responsableType, " +
                        " tiers.is_individual as isTiersIndividual, " +
                        " case " +
                        " when respo.id is not null then count(distinct co2.id) " +
                        " else count(distinct co1.id) " +
                        " end as nbrCustomerOrder, " +
                        " case " +
                        " when respo.id is not null then count(distinct p2.id_announcement) " +
                        " else count(distinct p1.id_announcement) " +
                        " end as nbrAnnouncement, " +
                        " case " +
                        " when respo.id is not null then count(distinct p2.id)-count(distinct p2.id_announcement) " +
                        " else count(distinct p1.id)-count(distinct p1.id_announcement) " +
                        " end as nbrFormalities, " +
                        " case " +
                        " when respo.id is not null then sum(ii2.pre_tax_price) " +
                        " else sum(ii1.pre_tax_price) " +
                        " end as turnoverAmount, " +
                        " case " +
                        " when respo.id is not null then sum(case when bt2.is_debour = true then 0 else ii2.pre_tax_price end) "
                        +
                        " else sum(case when bt1.is_debour = true then 0 else ii1.pre_tax_price end) " +
                        " end as turnoverAmountWithoutDebour, " +
                        " case " +
                        " when docTiersPaper.is_recipient_client = true " +
                        " and docTiersPaper.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docTiersPaper.is_recipient_client = true " +
                        " and docTiersPaper.is_recipient_affaire = false then 'Client' " +
                        " when docTiersPaper.is_recipient_client = false " +
                        " and docTiersPaper.is_recipient_affaire = true then 'Affaire' " +
                        " end as docTiersPaperRecipient, " +
                        " case " +
                        " when docTiersNumeric.is_recipient_client = true " +
                        " and docTiersNumeric.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docTiersNumeric.is_recipient_client = true " +
                        " and docTiersNumeric.is_recipient_affaire = false then 'Client' " +
                        " when docTiersNumeric.is_recipient_client = false " +
                        " and docTiersNumeric.is_recipient_affaire = true then 'Affaire' " +
                        " end as docTiersPaperNumeric, " +
                        " case " +
                        " when docTiersBilling.is_recipient_client = true " +
                        " and docTiersBilling.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docTiersBilling.is_recipient_client = true " +
                        " and docTiersBilling.is_recipient_affaire = false then 'Client' " +
                        " when docTiersBilling.is_recipient_client = false " +
                        " and docTiersBilling.is_recipient_affaire = true then 'Affaire' " +
                        " end as docTiersBilling, " +
                        " bltTiers.label as docTiersBillingLabel, " +
                        " case " +
                        " when docResponsablePaper.is_recipient_client = true " +
                        " and docResponsablePaper.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docResponsablePaper.is_recipient_client = true " +
                        " and docResponsablePaper.is_recipient_affaire = false then 'Client' " +
                        " when docResponsablePaper.is_recipient_client = false " +
                        " and docResponsablePaper.is_recipient_affaire = true then 'Affaire' " +
                        " end as docResponsablePaperRecipient, " +
                        " case " +
                        " when docResponsableNumeric.is_recipient_client = true " +
                        " and docResponsableNumeric.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docResponsableNumeric.is_recipient_client = true " +
                        " and docResponsableNumeric.is_recipient_affaire = false then 'Client' " +
                        " when docResponsableNumeric.is_recipient_client = false " +
                        " and docResponsableNumeric.is_recipient_affaire = true then 'Affaire' " +
                        " end as docResponsablePaperNumeric, " +
                        " case " +
                        " when docResponsableBilling.is_recipient_client = true " +
                        " and docResponsableBilling.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docResponsableBilling.is_recipient_client = true " +
                        " and docResponsableBilling.is_recipient_affaire = false then 'Client' " +
                        " when docResponsableBilling.is_recipient_client = false " +
                        " and docResponsableBilling.is_recipient_affaire = true then 'Affaire' " +
                        " end as docResponsableBilling, " +
                        " bltResponsable.label as docResponsableBillingLabel, " +
                        " pt.label as tiersPaymentType, " +
                        " tiers.payment_iban as tiersPaymentIban, " +
                        " tiers.payment_bic as tiersPaymentBic, " +
                        " tiers.is_provisional_payment_mandatory as tiersIsProvisionnalPaymentMandatory " +
                        " from " +
                        " tiers " +
                        " left join payment_type pt on " +
                        " pt.id = tiers.id_payment_type " +
                        " left join responsable respo on " +
                        " respo.id_tiers = tiers.id " +
                        " left join employee e1 on " +
                        " e1.id = tiers.id_commercial " +
                        " left join employee e2 on " +
                        " e2.id = respo.id_commercial " +
                        " left join city on " +
                        " city.id = tiers.id_city " +
                        " left join country on " +
                        " country.id = tiers.id_country " +
                        " left join tiers_type tt1 on " +
                        " tt1.id = tiers.id_tiers_type " +
                        " left join tiers_type tt2 on " +
                        " tt2.id = respo.id_responsable_type " +
                        " left join customer_order co1 on " +
                        " co1.id_tiers = tiers.id " +
                        " left join customer_order co2 on " +
                        " co2.id_responsable = respo.id " +
                        " left join asso_affaire_order aao1 on " +
                        " aao1.id_customer_order = co1.id " +
                        " left join asso_affaire_order aao2 on " +
                        " aao2.id_customer_order = co2.id " +
                        " left join provision p1 on " +
                        " p1.id_asso_affaire_order = aao1.id " +
                        " left join provision p2 on " +
                        " p2.id_asso_affaire_order = aao2.id " +
                        " left join invoice_item ii1 on " +
                        " ii1.id_provision = p1.id " +
                        " left join invoice_item ii2 on " +
                        " ii2.id_provision = p2.id " +
                        " left join billing_item bi1 on " +
                        " bi1.id = ii1.id_billing_item " +
                        " left join billing_item bi2 on " +
                        " bi2.id = ii2.id_billing_item " +
                        " left join billing_type bt1 on " +
                        " bt1.id = bi1.id_billing_type " +
                        " left join billing_type bt2 on " +
                        " bt2.id = bi2.id_billing_type " +
                        " left join document docTiersPaper on " +
                        " docTiersPaper.id_tiers = tiers.id " +
                        " and docTiersPaper.id_document_type = :documentTypePaperId " +
                        " left join document docTiersNumeric on " +
                        " docTiersNumeric.id_tiers = tiers.id " +
                        " and docTiersNumeric.id_document_type = :documentTypeNumericId " +
                        " left join document docTiersBilling on " +
                        " docTiersBilling.id_tiers = tiers.id " +
                        " and docTiersBilling.id_document_type = :documentTypeBillingId " +
                        " left join billing_label_type bltTiers on " +
                        " bltTiers.id = docTiersBilling.id_billing_label_type " +
                        " left join document docResponsablePaper on " +
                        " docResponsablePaper.id_responsable = respo.id " +
                        " and docResponsablePaper.id_document_type = :documentTypePaperId " +
                        " left join document docResponsableNumeric on " +
                        " docResponsableNumeric.id_responsable = respo.id " +
                        " and docResponsableNumeric.id_document_type = :documentTypeNumericId " +
                        " left join document docResponsableBilling on " +
                        " docResponsableBilling.id_responsable = respo.id " +
                        " and docResponsableBilling.id_document_type = :documentTypeBillingId " +
                        " left join billing_label_type bltResponsable on " +
                        " bltResponsable.id = docResponsableBilling.id_billing_label_type  " +
                        " group by " +
                        " tiers.id , " +
                        " respo.id , " +
                        " concat(e2.firstname, " +
                        " ' ', " +
                        " e2.lastname) , " +
                        " concat(e1.firstname, " +
                        " ' ', " +
                        " e1.lastname) , " +
                        " concat(respo.firstname, " +
                        " ' ', " +
                        " respo.lastname) , " +
                        " coalesce (concat(e2.firstname, " +
                        " ' ', " +
                        " e2.lastname), " +
                        " concat(e1.firstname, " +
                        " ' ', " +
                        " e1.lastname)) , " +
                        " tiers.address , " +
                        " tiers.postal_code, " +
                        " tiers.cedex_complement , " +
                        " city.label , " +
                        " country.label , " +
                        " tiers.intercommunity_vat , " +
                        " tt1.label , " +
                        " tt2.label , " +
                        " tiers.is_individual , " +
                        " bltResponsable.label , " +
                        " bltTiers.label , " +
                        " pt.label , " +
                        " tiers.payment_iban, " +
                        " tiers.payment_bic , " +
                        " tiers.is_provisional_payment_mandatory , " +
                        " case " +
                        " when docTiersPaper.is_recipient_client = true " +
                        " and docTiersPaper.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docTiersPaper.is_recipient_client = true " +
                        " and docTiersPaper.is_recipient_affaire = false then 'Client' " +
                        " when docTiersPaper.is_recipient_client = false " +
                        " and docTiersPaper.is_recipient_affaire = true then 'Affaire' " +
                        " end , " +
                        " case " +
                        " when docTiersNumeric.is_recipient_client = true " +
                        " and docTiersNumeric.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docTiersNumeric.is_recipient_client = true " +
                        " and docTiersNumeric.is_recipient_affaire = false then 'Client' " +
                        " when docTiersNumeric.is_recipient_client = false " +
                        " and docTiersNumeric.is_recipient_affaire = true then 'Affaire' " +
                        " end , " +
                        " case " +
                        " when docTiersBilling.is_recipient_client = true " +
                        " and docTiersBilling.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docTiersBilling.is_recipient_client = true " +
                        " and docTiersBilling.is_recipient_affaire = false then 'Client' " +
                        " when docTiersBilling.is_recipient_client = false " +
                        " and docTiersBilling.is_recipient_affaire = true then 'Affaire' " +
                        " end , " +
                        " case " +
                        " when docResponsablePaper.is_recipient_client = true " +
                        " and docResponsablePaper.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docResponsablePaper.is_recipient_client = true " +
                        " and docResponsablePaper.is_recipient_affaire = false then 'Client' " +
                        " when docResponsablePaper.is_recipient_client = false " +
                        " and docResponsablePaper.is_recipient_affaire = true then 'Affaire' " +
                        " end , " +
                        " case " +
                        " when docResponsableNumeric.is_recipient_client = true " +
                        " and docResponsableNumeric.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docResponsableNumeric.is_recipient_client = true " +
                        " and docResponsableNumeric.is_recipient_affaire = false then 'Client' " +
                        " when docResponsableNumeric.is_recipient_client = false " +
                        " and docResponsableNumeric.is_recipient_affaire = true then 'Affaire' " +
                        " end , " +
                        " case " +
                        " when docResponsableBilling.is_recipient_client = true " +
                        " and docResponsableBilling.is_recipient_affaire = true then 'Client / Affaire' " +
                        " when docResponsableBilling.is_recipient_client = true " +
                        " and docResponsableBilling.is_recipient_affaire = false then 'Client' " +
                        " when docResponsableBilling.is_recipient_client = false " +
                        " and docResponsableBilling.is_recipient_affaire = true then 'Affaire' " +
                        " end " +
                        "")
        List<ITiersReporting> getTiersReporting(
                        @Param("documentTypePaperId") Integer documentTypePaperId,
                        @Param("documentTypeNumericId") Integer documentTypeNumericId,
                        @Param("documentTypeBillingId") Integer documentTypeBillingId);
}