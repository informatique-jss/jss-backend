package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

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

    @Query("SELECT ca FROM CompetentAuthority ca JOIN ca.competentAuthorityType cat WHERE ca.city.id = :cityId AND cat.code = :authorityTypeCode")
    CompetentAuthority findByCityIdAndByAuthorityType(@Param("cityId") Integer cityId,
            @Param("authorityTypeCode") String authorityTypeCode);

}