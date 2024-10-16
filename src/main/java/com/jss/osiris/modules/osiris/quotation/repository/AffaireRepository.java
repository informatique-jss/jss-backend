package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface AffaireRepository extends QueryCacheCrudRepository<Affaire, Integer> {

        Affaire findBySiret(String siret);

        List<Affaire> findBySiren(String siret);

        @Query(value = "select a from Affaire a where postalCode = :postalCode and isIndividual = true and trim(upper(firstname))=upper(trim(:firstname)) and trim(upper(lastname))=trim(upper(trim(:lastname))) ")
        List<Affaire> findByPostalCodeAndName(@Param("postalCode") String postalCode,
                        @Param("firstname") String firstname, @Param("lastname") String lastname);

        @Query(value = "select a from Affaire a where postalCode = :postalCode and isIndividual = false and trim(upper(denomination))=upper(trim(:denomination))  ")
        List<Affaire> findByPostalCodeAndDenomination(@Param("postalCode") String postalCode,
                        @Param("denomination") String denomination);

        Affaire findByRna(String rna);

        @Query(nativeQuery = true, value = "select * from affaire a  where siren is not null or siret is not null order by coalesce(last_rne_update,'1950-01-01') limit 5000")
        List<Affaire> getAffairesForUpdate();

        @Query(value = "select a from Affaire a where exists (select 1 from AssoAffaireOrder aao join aao.customerOrder c where aao.affaire = a  and c.responsable in :responsables) and ( lower(a.denomination) like lower(concat('%', :searchText,'%')) or lower(concat(a.firstname, ' ', a.lastname)) like lower(concat('%', :searchText,'%')) or a.siret like concat(:searchText,'%') ) ")
        List<Affaire> getAffairesForResponsables(Pageable pageableRequest,
                        @Param("responsables") List<Responsable> responsables, @Param("searchText") String searchText);
}