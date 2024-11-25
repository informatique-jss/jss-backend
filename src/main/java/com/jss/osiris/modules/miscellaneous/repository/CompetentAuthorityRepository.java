package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.miscellaneous.model.ICompetentAuthorityMailReminder;

import jakarta.persistence.QueryHint;

public interface CompetentAuthorityRepository extends QueryCacheCrudRepository<CompetentAuthority, Integer> {

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CompetentAuthority> findByLabelContainingIgnoreCase(String label);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CompetentAuthority> findByCities_Id(Integer cityId);

        Optional<CompetentAuthority> findByApiId(String apiId);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CompetentAuthority> findByCompetentAuthorityType_Id(Integer competentAuthorityTypeId);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })

        CompetentAuthority findByIntercommunityVat(String intercommunityVat);

        CompetentAuthority findByAzureCustomReference(String azureCustomReference);

        List<CompetentAuthority> findByInpiReference(String inpiReference);

        List<CompetentAuthority> findByCities_IdAndCompetentAuthorityType(Integer cityId,
                        CompetentAuthorityType competentAuthorityType);

        @Query(nativeQuery = true, value = "" +
                        " select p.id_employee as employeeId,ca.id as competentAuthorityId, p.id as provisionId,STRING_AGG(  distinct cast(coalesce( acamgm.id_mail,acam.id_mail) as text),',' ) as mailId,max(coalesce(a1.datetime, a2.datetime)) as statusDate "
                        +
                        " from provision p " +
                        " join service s on s.id = p.id_service join asso_affaire_order aao on aao.id = s.id_asso_affaire_order join customer_order co on co.id = aao.id_customer_order "
                        +
                        " join service_type st on st.id  = s.id_service_type  " +
                        " join service_family sf on sf.id = st.id_service_family  " +
                        " left join formalite f on f.id = p.id_formalite  " +
                        " left join simple_provision sp  on sp.id = p.id_simple_provision " +
                        " left join audit a1 on a1.entity  = 'Formalite' and a1.entity_id  = f.id and a1.field_name  = 'formaliteStatus' and a1.new_value =:formaliteWaitingAcStatusCode "
                        +
                        " left join audit a2 on a2.entity  = 'SimpleProvision' and a2.entity_id  = sp.id and a2.field_name  = 'simpleProvisionStatus' and a2.new_value =:simpleProvisionWaitingAcStatusCode "
                        +
                        " join competent_authority ca on ca.id = coalesce(f.id_waited_competent_authority, sp.id_waited_competent_authority)  "
                        +
                        " join competent_authority_type cat on cat.id = ca.id_competent_authority_type  " +
                        " left join asso_competent_authority_mail acam on acam.id_competent_authority  = ca.id " +
                        " left join asso_mail_competent_authority_service_family_group acamg on acamg.id_competent_authority  = ca.id and sf.id_service_family_group  = acamg.id_service_family_group "
                        +
                        " left join asso_mail_competent_authority_service_family_group_mail acamgm on acamg.id = acamgm.id_asso_mail_competent_authority_service_family_group "
                        +
                        " where coalesce (f.id_formalite_status, sp.id_simple_provision_status) in (:simpleProvisionWaitingAcStatusId,:formaliteWaitingAcStatusId)  and coalesce( acamgm.id_mail,acam.id_mail) is not null "
                        +
                        " and coalesce(f.id_waited_competent_authority, sp.id_waited_competent_authority) is not null "
                        +
                        " and cat.is_to_reminder = true " +
                        " and coalesce(ca.is_not_to_reminder, false) = false  and co.id_customer_order_status <>:idCustomerOrderStatusAbandonned "
                        +
                        " and (p.id_employee % 5 +1) = extract(DOW from now()) " +
                        " group by p.id_employee  ,ca.id  , p.id   " +
                        " having max(coalesce(a1.datetime, a2.datetime))< now() -INTERVAL '10 day' " +
                        " order by p.id_employee, ca.id, STRING_AGG(  distinct cast(coalesce( acamgm.id_mail,acam.id_mail) as text),',' )             "
                        +
                        "")
        List<ICompetentAuthorityMailReminder> findCompetentAuthoritiesMailToSend(
                        @Param("simpleProvisionWaitingAcStatusCode") String simpleProvisionWaitingAcStatusCode,
                        @Param("formaliteWaitingAcStatusCode") String formaliteWaitingAcStatusCode,
                        @Param("simpleProvisionWaitingAcStatusId") Integer simpleProvisionWaitingAcStatusId,
                        @Param("formaliteWaitingAcStatusId") Integer formaliteWaitingAcStatusId,
                        @Param("idCustomerOrderStatusAbandonned") Integer idCustomerOrderStatusAbandonned);

}