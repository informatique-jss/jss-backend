package com.jss.osiris.modules.osiris.quotation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface AffaireRepository extends QueryCacheCrudRepository<Affaire, Integer> {

        List<Affaire> findBySiret(String siret);

        List<Affaire> findBySiren(String siret);

        @Query(value = "select a from Affaire a where postalCode = :postalCode and isIndividual = true and trim(upper(firstname))=upper(trim(:firstname)) and trim(upper(lastname))=trim(upper(trim(:lastname))) ")
        List<Affaire> findByPostalCodeAndName(@Param("postalCode") String postalCode,
                        @Param("firstname") String firstname, @Param("lastname") String lastname);

        @Query(value = "select a from Affaire a where postalCode = :postalCode and isIndividual = false and trim(upper(denomination))=upper(trim(:denomination))  ")
        List<Affaire> findByPostalCodeAndDenomination(@Param("postalCode") String postalCode,
                        @Param("denomination") String denomination);

        @Query(value = "select a from Affaire a where  isIndividual = false and trim(upper(firstname))=upper(trim(:firstname)) and trim(upper(lastname))=upper(trim(:lastname))  ")
        Affaire findByFirstnameAndLastname(@Param("lastname") String lastname,
                        @Param("firstname") String firstname);

        Affaire findByRna(String rna);

        @Query(value = "select a from Affaire a where exists (select 1 from AssoAffaireOrder aao join aao.customerOrder c where aao.affaire = a  and c.responsable in :responsables) and ( lower(a.denomination) like lower(concat('%', :searchText,'%')) or lower(concat(a.firstname, ' ', a.lastname)) like lower(concat('%', :searchText,'%')) or a.siret like concat(:searchText,'%') or a.id=:idAffaire  ) ")
        List<Affaire> getAffairesForResponsables(Pageable pageableRequest,
                        @Param("responsables") List<Responsable> responsables, @Param("searchText") String searchText,
                        @Param("idAffaire") Integer idAffaire);

        List<Affaire> findAllBySiret(String siret);

        @Query(value = "select a from Affaire a where siret is null and coalesce(isIndividual , false) = false and coalesce(isToNotUpdate , false) = false and (lastRneCheckDate is null or lastRneCheckDate<:lastRneCheckDate) ")
        List<Affaire> getNextAffaireToUpdate(LocalDate lastRneCheckDate);

        @Query(value = "select a from Affaire a where siret is not null and (lastRneCheckDate is null or lastRneCheckDate<:lastRneCheckDate) and  coalesce(isToNotUpdate , false) = false order by coalesce(a.lastRneCheckDate, :defaultPastDate) asc ")
        List<Affaire> getNextAffaireToUpdateForRne(LocalDate lastRneCheckDate, LocalDate defaultPastDate);

        @Query(value = "select a from Affaire a where (siret is null or city is null) and coalesce(isIndividual , false) = false and coalesce(isToNotUpdate , false) = false and  coalesce(isToNotUpdate , false) = false  ")
        List<Affaire> getAffairesForCorrection();

}