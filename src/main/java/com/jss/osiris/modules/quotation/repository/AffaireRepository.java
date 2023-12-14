package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.Affaire;

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
}