package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.quotation.model.Service;

public interface MissingAttachmentQueryRepository extends QueryCacheCrudRepository<MissingAttachmentQuery, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select " +
                        " 	* " +
                        " from " +
                        " 	missing_attachment_query " +
                        " where " +
                        " 	id in ( " +
                        " 	select " +
                        " 		id " +
                        " 	from " +
                        " 		( " +
                        " 		select " +
                        " 			ma.id, " +
                        " 			sum(1) over(partition by s.id " +
                        " 		order by " +
                        " 			ma.created_date_time desc) as n " +
                        " 		from " +
                        " 			missing_attachment_query ma " +
                        " 		join service s on " +
                        " 			s.id = ma.id_service join asso_affaire_order aao on aao.id = s.id_asso_affaire_order join customer_order co on co.id = aao.id_customer_order "
                        +
                        " 		join provision p on " +
                        " 			p.id_service = s.id " +
                        " 		left join simple_provision sp on " +
                        " 			sp.id = p.id_simple_provision " +
                        " 		left join formalite f on " +
                        " 			f.id = p.id_formalite " +
                        " 		where " +
                        " 			(sp.id_simple_provision_status = :simpleProvisionStatusWaitingAttachmentId " +
                        " 				or f.id_formalite_status = :formaliteStatusWaitingAttachmentId ) " +
                        " 			and ma.third_customer_reminder_date_time is null and co.id_customer_order_status <> :customerOrderStatusIdAbandonned "
                        +
                        " 				) t " +
                        " 	where " +
                        " 		t.n = 1) ")
        List<MissingAttachmentQuery> getMissingAttachmentQueriesForCustomerReminder(
                        @Param("simpleProvisionStatusWaitingAttachmentId") Integer simpleProvisionStatusWaitingAttachmentId,
                        @Param("formaliteStatusWaitingAttachmentId") Integer formaliteStatusWaitingAttachmentId,
                        @Param("customerOrderStatusIdAbandonned") Integer customerOrderStatusIdAbandonned);

        List<MissingAttachmentQuery> findByService(Service service);
}