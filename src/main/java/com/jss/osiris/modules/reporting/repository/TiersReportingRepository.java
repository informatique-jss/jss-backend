package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.ITiersReporting;

public interface TiersReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        "         select " +
                        " 	coalesce(t.denomination, " +
                        " 	concat(t.firstname, " +
                        " 	' ', " +
                        " 	t.lastname)) tiersLabel, " +
                        " 	tc.label as tiersCategory, " +
                        " 	string_agg(m1.mail, " +
                        " 	',') as tiersMail, " +
                        " 	concat(r.firstname, " +
                        " 	' ', " +
                        " 	r.lastname) as responsableLabel, " +
                        " 	tc2.label as responsableCategory, " +
                        " 	string_agg(m2.mail, " +
                        " 	',') as responsableMail, " +
                        " 	coalesce(concat(e2.firstname, " +
                        " 	' ', " +
                        " 	e2.lastname), " +
                        " 	concat(e1.firstname, " +
                        " 	' ', " +
                        " 	e2.lastname)) as salesEmployeeLabel " +
                        " from " +
                        " 	tiers t " +
                        " left join tiers_category tc on " +
                        " 	tc.id = t.id_tiers_category " +
                        " left join responsable r on " +
                        " 	r.id_tiers = t.id " +
                        " left join tiers_category tc2 on " +
                        " 	tc2.id = r.id_responsable_category " +
                        " left join asso_tiers_mail atm on " +
                        " 	atm.id_tiers = t.id " +
                        " left join mail m1 on " +
                        " 	m1.id = atm.id_mail " +
                        " left join asso_responsable_mail atm2 on " +
                        " 	atm2.id_tiers = r.id " +
                        " left join mail m2 on " +
                        " 	m2.id = atm2.id_mail " +
                        " left join employee e1 on " +
                        " 	e1.id = t.id_commercial " +
                        " left join employee e2 on " +
                        " 	e2.id = r.id_commercial " +
                        " group by " +
                        " 	coalesce(t.denomination, " +
                        " 	concat(t.firstname, " +
                        " 	' ', " +
                        " 	t.lastname)), " +
                        " 	tc.label, " +
                        " 	concat(r.firstname, " +
                        " 	' ', " +
                        " 	r.lastname) , " +
                        " 	tc2.label , " +
                        " 	coalesce(concat(e2.firstname, " +
                        " 	' ', " +
                        " 	e2.lastname), " +
                        " 	concat(e1.firstname, " +
                        " 	' ', " +
                        " 	e2.lastname)) " +
                        "")
        List<ITiersReporting> getTiersReporting();
}