package com.jss.osiris.modules.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.BankTransfertSearchResult;
import com.jss.osiris.modules.quotation.model.BankTransfert;

public interface BankTransfertRepository extends CrudRepository<BankTransfert, Integer> {

        @Query(nativeQuery = true, value = " select r.id as id,"
                        + " r.transfert_date_time as transfertDate,"
                        + " r.transfert_amount  as transfertAmount ,"
                        + " r.label as transfertLabel,"
                        + " r.transfert_iban as transfertIban,"
                        + " coalesce(provider.label, confrere.label, competent_authority.label) as invoiceBillingLabel,"
                        + " ca.label as competentAuthorityLabel,"
                        + " r.is_already_exported  as isAlreadyExported "
                        + " from bank_transfert r "
                        + " left join invoice  on invoice.id_bank_transfert = r.id "
                        + " left join debour  on debour.id_bank_transfert = r.id "
                        + " left join competent_authority ca on ca.id = debour.id_competent_authority "
                        + " left join provider on provider.id = invoice.id_provider "
                        + " left join competent_authority on competent_authority.id = invoice.id_competent_authority "
                        + " left join confrere on confrere.id = invoice.id_confrere "
                        + " where (:isHideExportedRefunds=false OR r.is_already_exported=false) "
                        + " and r.transfert_date_time>=:startDate and r.transfert_date_time<=:endDate "
                        + "  and (:minAmount is null or r.transfert_amount>=CAST(CAST(:minAmount as text) as real) ) "
                        + "  and (:maxAmount is null or r.transfert_amount<=CAST(CAST(:maxAmount as text) as real) )"
                        + " and (:label is null or  upper(r.label)  like '%' || upper(CAST(:label as text))  || '%' )")
        List<BankTransfertSearchResult> findTransferts(
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("label") String label,
                        @Param("isHideExportedRefunds") boolean isHideExportedRefunds);
}