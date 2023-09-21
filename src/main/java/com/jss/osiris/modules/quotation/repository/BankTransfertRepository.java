package com.jss.osiris.modules.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearchResult;
import com.jss.osiris.modules.quotation.model.BankTransfert;

public interface BankTransfertRepository extends QueryCacheCrudRepository<BankTransfert, Integer> {

        @Query(nativeQuery = true, value = " select r.id as id,"
                        + " r.transfert_date_time as transfertDate,"
                        + " r.transfert_amount  as transfertAmount ,"
                        + " r.label as transfertLabel,"
                        + " r.transfert_iban as transfertIban,"
                        + " coalesce(provider.label, confrere.label, competent_authority.label) as invoiceBillingLabel,"
                        + " null as competentAuthorityLabel,"
                        + " (select max(coalesce(a1.denomination,a1.firstname ||' ' || a1.lastname)) from affaire a1 join asso_affaire_order a2 on a1.id = a2.id_affaire where a2.id_customer_order = r.id_customer_order) as affaireLabel ,"
                        + " r.is_already_exported  as isAlreadyExported, "
                        + " r.is_selected_for_export  as isSelectedForExport "
                        + " from bank_transfert r "
                        + " left join invoice  on invoice.id_bank_transfert = r.id "
                        + " left join provider on provider.id = invoice.id_provider "
                        + " left join competent_authority on competent_authority.id = invoice.id_competent_authority "
                        + " left join confrere on confrere.id = invoice.id_confrere "
                        + " where is_cancelled=false and (:isHideExportedRefunds=false OR r.is_already_exported=false) "
                        + " and (:isDisplaySelectedForExportBankTransfert=false OR r.is_selected_for_export=true) "
                        + " and r.transfert_date_time>=:startDate and r.transfert_date_time<=:endDate "
                        + "  and (:minAmount is null or r.transfert_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + "  and (:maxAmount is null or r.transfert_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:label is null or  upper(r.label)  like '%' || upper(CAST(:label as text))  || '%' )")
        List<BankTransfertSearchResult> findTransferts(
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideExportedRefunds") boolean isHideExportedRefunds,
                        @Param("isDisplaySelectedForExportBankTransfert") boolean isDisplaySelectedForExportBankTransfert);
}