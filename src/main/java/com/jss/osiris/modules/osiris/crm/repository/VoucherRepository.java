package com.jss.osiris.modules.osiris.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Voucher;

public interface VoucherRepository extends QueryCacheCrudRepository<Voucher, Integer> {
    Voucher findByCode(String code);

    @Query("select v from Voucher v where (startDate is null OR startDate <= CURRENT_DATE) AND (endDate is null OR endDate >= CURRENT_DATE) and isCancelled=false")
    List<Voucher> findActiveVouchers();

    List<Voucher> findByCodeContainingIgnoreCaseAndIsCancelled(String code, Boolean isCancelled);
}
