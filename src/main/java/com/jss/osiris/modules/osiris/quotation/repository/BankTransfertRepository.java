package com.jss.osiris.modules.osiris.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.invoicing.model.BankTransfertSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.BankTransfert;

public interface BankTransfertRepository extends QueryCacheCrudRepository<BankTransfert, Integer> {

        @Query(nativeQuery = true, value = " select r.id as id,"
                        + " r.transfert_date_time as transfertDate,"
                        + " r.transfert_amount  as transfertAmount ,"
                        + " r.label as transfertLabel,"
                        + " r.transfert_iban as transfertIban,"
                        + " coalesce(provider.label,  tiers.denomination, concat (tiers.firstname, ' ', tiers.lastname)) as invoiceBillingLabel,"
                        + " (select max(coalesce(a1.denomination,a1.firstname ||' ' || a1.lastname)) from affaire a1 join asso_affaire_order a2 on a1.id = a2.id_affaire where a2.id_customer_order = r.id_customer_order) as affaireLabel ,"
                        + " r.is_already_exported  as isAlreadyExported, "
                        + " r.is_selected_for_export  as isSelectedForExport, "
                        + " r.comment, "
                        + " r.is_matched as isMatched "
                        + " from bank_transfert r "
                        + " left join invoice  on invoice.id_bank_transfert = r.id "
                        + " left join provider on provider.id = invoice.id_provider "
                        + " left join responsable on responsable.id = invoice.id_responsable "
                        + " left join tiers on tiers.id = responsable.id_tiers "
                        + " where is_cancelled=false and (:isHideExportedBankTransfert=false OR r.is_already_exported=false) "
                        + " and (:isDisplaySelectedForExportBankTransfert=false OR r.is_selected_for_export=true) "
                        + " and (:isHideMatchedBankTransfert=false OR r.is_matched=false) "
                        + " and (:idBankTransfert=0 OR r.id=:idBankTransfert) "
                        + " and r.transfert_date_time>=:startDate and r.transfert_date_time<=:endDate "
                        + " and (:minAmount is null or r.transfert_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + " and (:maxAmount is null or r.transfert_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:idProvider=0 or provider.id=:idProvider) "
                        + " and (:label is null or  CAST(r.id as text) = upper(CAST(:label as text)) or  upper(r.label)  like '%' || upper(CAST(:label as text))  || '%' )")
        List<BankTransfertSearchResult> findTransferts(
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideMatchedBankTransfert") boolean isHideMatchedBankTransfert,
                        @Param("isHideExportedBankTransfert") boolean isHideExportedBankTransfert,
                        @Param("isDisplaySelectedForExportBankTransfert") boolean isDisplaySelectedForExportBankTransfert,
                        @Param("idBankTransfert") Integer idBankTransfert, @Param("idProvider") Integer idProvider);
}