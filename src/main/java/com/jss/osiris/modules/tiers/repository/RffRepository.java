package com.jss.osiris.modules.tiers.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.tiers.model.IRffCompute;
import com.jss.osiris.modules.tiers.model.Rff;

public interface RffRepository extends QueryCacheCrudRepository<Rff, Integer> {

        @Query(nativeQuery = true, value = "" +
                        "     with date_range_quarter as ( " +
                        "         select " +
                        "             generate_series( " +
                        "             '2023-01-01'\\:\\:date, " +
                        "             '2083-12-31'\\:\\:date, " +
                        "             interval '3 months'  " +
                        "           )\\:\\:date as date " +
                        "         ), " +
                        "         quarter_dates as ( " +
                        "         select " +
                        "             date_trunc('quarter', " +
                        "             date)\\:\\:date as start_date, " +
                        "             (lead( date_trunc('quarter', " +
                        "             date)\\:\\:date) over (partition by 1)) -1 as end_date " +
                        "         from " +
                        "             date_range_quarter ), " +
                        "           date_range_month as ( " +
                        "         select " +
                        "             generate_series( " +
                        "             '2023-01-01'\\:\\:date, " +
                        "             '2083-12-31'\\:\\:date, " +
                        "             interval '1 month'     " +
                        "           )\\:\\:date as date " +
                        "         ), " +
                        "         month_dates as ( " +
                        "         select " +
                        "             date_trunc('month', " +
                        "             date)\\:\\:date as start_date, " +
                        "             (lead( date_trunc('month', " +
                        "             date)\\:\\:date) over (partition by 1)) -1 as end_date " +
                        "         from " +
                        "             date_range_month ), " +
                        "           date_range_year as ( " +
                        "         select " +
                        "             generate_series( " +
                        "             '2023-01-01'\\:\\:date, " +
                        "             '2083-12-31'\\:\\:date, " +
                        "             interval '1 year'     " +
                        "           )\\:\\:date as date " +
                        "         ), " +
                        "         year_dates as ( " +
                        "         select " +
                        "             date_trunc('year', " +
                        "             date)\\:\\:date as start_date, " +
                        "             (lead( date_trunc('year', " +
                        "             date)\\:\\:date) over (partition by 1)) -1 as end_date " +
                        "         from " +
                        "             date_range_year ), " +
                        "         lettering_date_time as ( " +
                        "         select " +
                        "             id_invoice, " +
                        "             max(lettering_date_time) as lettering_date_time " +
                        "         from " +
                        "             accounting_record ar " +
                        "         where " +
                        "             id_invoice is not null " +
                        "             and lettering_number is not null " +
                        "         group by " +
                        "             id_invoice), " +
                        "         ii as ( " +
                        "         select " +
                        "             i.id_tiers, " +
                        "             i.id_responsable, " +
                        "             i.id_confrere , " +
                        "             bt_al.id as id_al, " +
                        "             bt_for.id as id_for, " +
                        "             coalesce (ii.pre_tax_price , " +
                        "             0) - coalesce(ii.discount_amount, " +
                        "             0) as pre_tax_price, " +
                        "             lettering_date_time " +
                        "         from " +
                        "             invoice_item ii " +
                        "         join billing_item bi on " +
                        "             bi.id = ii.id_billing_item " +
                        "         join invoice i on " +
                        "             i.id = ii.id_invoice " +
                        "         left join lettering_date_time ldt on " +
                        "             ldt.id_invoice = i.id " +
                        "         left join billing_type bt_al on " +
                        "             bt_al.id = bi.id_billing_type " +
                        "             and bt_al.is_used_for_insertion_rff " +
                        "         left join billing_type bt_for on " +
                        "             bt_for.id = bi.id_billing_type " +
                        "             and bt_for.is_used_for_formalite_rff " +
                        "         where " +
                        "             i.id_invoice_status = 84 " +
                        "             and coalesce (bt_al.id, " +
                        "             bt_for.id) is not null " +
                        "                 and ldt.lettering_date_time- interval '270 days' <= i.created_date  " +
                        "                 )    " +
                        "         select " +
                        "             r.id as tiersId, " +
                        "             null as responsableId,  " +
                        "             sum(case when ii.id_al is not null and r.rff_insertion_rate is not null and r.rff_insertion_rate>0 then r.rff_insertion_rate else 0 end / 100 * ii.pre_tax_price) as rffAl , "
                        +
                        "             sum(case when ii.id_for is not null and r.rff_formalite_rate is not null and r.rff_formalite_rate>0 then r.rff_formalite_rate else 0 end / 100 * ii.pre_tax_price) as rffFor, "
                        +
                        "              coalesce (yd.start_date , " +
                        "             md.start_date, " +
                        "             qd.start_date) as startDate, " +
                        "               coalesce ( yd.end_date, " +
                        "             md.end_date, " +
                        "             qd.end_date) as endDate, rff.id as rffId " +
                        "         from " +
                        "             tiers r " +
                        "         left join responsable r2 on " +
                        "             r.id = r2.id_tiers " +
                        "         left join ii on " +
                        "             ii.id_responsable = r2.id " +
                        "             or ii.id_tiers = r.id " +
                        "         left join year_dates yd on " +
                        "             (r.id_rff_frequency = :rffFrequencyAnnual " +
                        "                 or r.id_rff_frequency is null) " +
                        "             and ii.lettering_date_time between yd.start_date and yd.end_date " +
                        "         left join month_dates md on " +
                        "             r.id_rff_frequency = :rffFrequencyMonthly " +
                        "             and ii.lettering_date_time between md.start_date and md.end_date " +
                        "         left join quarter_dates qd on " +
                        "             r.id_rff_frequency = :rffFrequencyQuarterly " +
                        "             and ii.lettering_date_time between qd.start_date and qd.end_date " +
                        "               left join rff on rff.id_tiers = r.id and rff.id_responsable is null and rff.start_date = coalesce (yd.start_date ,md.start_date,qd.start_date) and rff.end_date = coalesce ( yd.end_date,md.end_date,qd.end_date) "
                        +
                        "         where " +
                        "             coalesce (r.rff_formalite_rate, " +
                        "             r.rff_insertion_rate ) is not null " +
                        "            and coalesce (r.rff_formalite_rate, " +
                        "             r.rff_insertion_rate ) >0 " +
                        "             and coalesce (yd.start_date, " +
                        "             yd.end_date, " +
                        "             md.start_date, " +
                        "             md.end_date, " +
                        "             qd.start_date, " +
                        "             qd.end_date) is not null and   coalesce (yd.start_date ,md.start_date, qd.start_date) >= :startDate and  coalesce (yd.end_date ,md.end_date, qd.end_date) <= :endDate "
                        +
                        "             and r.id not in ( " +
                        "             select " +
                        "                 id_tiers " +
                        "             from " +
                        "                 responsable r3 " +
                        "             where  " +
                        "                 coalesce (r3.rff_formalite_rate, " +
                        "                 r3.rff_insertion_rate ) is not null " +
                        "               and  coalesce (r3.rff_formalite_rate, " +
                        "                 r3.rff_insertion_rate )  >0 ) " +
                        "                   and  ( :idTiers =0 or r.id = :idTiers) " +
                        "                   and  ( :idResponsable =0 or r2.id = :idResponsable) " +
                        "                   and  ( :idSalesEmployee =0 or r.id_commercial = :idSalesEmployee  or r2.id_commercial = :idSalesEmployee) "
                        +
                        "         group by " +
                        "             r.id , rff.id, " +
                        "             coalesce (yd.start_date , " +
                        "             md.start_date, " +
                        "             qd.start_date) , " +
                        "               coalesce ( yd.end_date, " +
                        "             md.end_date, " +
                        "             qd.end_date) " +
                        "         union all  " +
                        "         select " +
                        "             r.id as tiersId, " +
                        "             r2.id as responsableId,  " +
                        "             sum(case when ii.id_al is not null and r.rff_insertion_rate is not null and r.rff_insertion_rate>0 then r.rff_insertion_rate else 0 end / 100 * ii.pre_tax_price) as rffAl , "
                        +
                        "             sum(case when ii.id_for is not null and r.rff_formalite_rate is not null and r.rff_formalite_rate>0 then r.rff_formalite_rate else 0 end / 100 * ii.pre_tax_price) as rffFor, "
                        +
                        "              yd.start_date as startDate, " +
                        "             yd.end_date as endDate, rff.id as rffId " +
                        "         from " +
                        "             tiers r " +
                        "         join responsable r2 on " +
                        "             r.id = r2.id_tiers " +
                        "         left join ii on " +
                        "             ii.id_responsable = r2.id " +
                        "         join year_dates yd on " +
                        "             (r.id_rff_frequency = :rffFrequencyAnnual " +
                        "                 or r.id_rff_frequency is null) " +
                        "             and ii.lettering_date_time between yd.start_date and yd.end_date " +
                        "           left join rff on rff.id_tiers = r.id and rff.id_responsable =r2.id and rff.start_date =\t yd.start_date and rff.end_date =  yd.end_date"
                        +
                        "         where " +
                        "             coalesce (r2.rff_formalite_rate, " +
                        "             r2.rff_insertion_rate ) is not null " +
                        "             and coalesce (r2.rff_formalite_rate, " +
                        "             r2.rff_insertion_rate ) >0 " +
                        "             and yd.start_date is not null and   yd.start_date >= :startDate and yd.end_date <= :endDate "
                        +
                        "                   and  ( :idTiers =0 or r.id = :idTiers) " +
                        "                   and  ( :idResponsable =0 or r2.id = :idResponsable) " +
                        "                   and  ( :idSalesEmployee =0 or r.id_commercial = :idSalesEmployee  or r2.id_commercial = :idSalesEmployee) "
                        +
                        "         group by " +
                        "             r2.id,  rff.id, " +
                        "             r.id, " +
                        "             yd.start_date, " +
                        "             yd.end_date   " +
                        "")
        public List<IRffCompute> getRffComputes(@Param("rffFrequencyAnnual") Integer rffFrequencyAnnual,
                        @Param("rffFrequencyQuarterly") Integer rffFrequencyQuarterly,
                        @Param("rffFrequencyMonthly") Integer rffFrequencyMonthly, @Param("idTiers") Integer idTiers,
                        @Param("idResponsable") Integer idResponsable,
                        @Param("idSalesEmployee") Integer idSalesEmployee,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}