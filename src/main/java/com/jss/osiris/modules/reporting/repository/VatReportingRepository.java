package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.IVatReporting;

public interface VatReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select to_char(i.created_date,'MM YYYY') as month, " +
                        " v.label as vatLabel, " +
                        " v.code as vatCode, " +
                        " sum(ii.pre_tax_price) as turnoverAmount,  " +
                        " sum(ii.vat_price) as vatAmount " +
                        " from invoice i  " +
                        " join invoice_item ii on ii.id_invoice = i.id " +
                        " join vat v on v.id = ii.id_vat " +
                        " group by to_char(i.created_date,'MM YYYY'), v.label,v.code " +
                        "")
        List<IVatReporting> getVatReporting();
}