package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.PhoneSearch;

public interface PhoneRepository extends CrudRepository<Phone, Integer> {

    @Query(nativeQuery = true, value = "SELECT DISTINCT " +
            "CASE " +
            "WHEN t.id IS NOT NULL THEN 'Tiers' " +
            "WHEN r.id IS NOT NULL THEN 'Responsable' " +
            "WHEN c.id IS NOT NULL THEN 'Confrere' " +
            "WHEN p.id IS NOT NULL THEN 'Provider' " +
            "END as entityType, " +
            "CASE " +
            "WHEN t.id IS NOT NULL THEN coalesce(t.denomination, t.firstname||' ' || t.lastname) " +
            "WHEN r.id IS NOT NULL THEN r.firstname||' ' || r.lastname " +
            "WHEN c.id IS NOT NULL THEN c.label " +
            "WHEN p.id IS NOT NULL THEN  p.label " +
            "END as entityLabel, " +
            " COALESCE(t.id, r.id, c.id, p.id) as entityId " +
            "FROM phone " +
            "LEFT OUTER JOIN asso_tiers_phone atp ON phone.id = atp.id_phone " +
            "LEFT OUTER JOIN tiers t ON atp.id_tiers = t.id " +
            "LEFT OUTER JOIN asso_responsable_phone arp ON phone.id = arp.id_phone " +
            "LEFT OUTER JOIN responsable r ON arp.id_tiers = r.id " +
            "LEFT OUTER JOIN asso_provider_phone app ON phone.id = app.id_phone " +
            "LEFT OUTER JOIN provider p ON app.id_provider = p.id " +
            "LEFT OUTER JOIN asso_confrere_phone acp ON phone.id = acp.id_phone " +
            "LEFT OUTER JOIN confrere c ON acp.id_confrere = c.id " +
            "WHERE phone.phone_number = :phoneNumber")
    List<PhoneSearch> findByPhoneNumberForFront(@Param("phoneNumber") String phoneNumber);
    // search a phone by tiers, responsable, confrere or provider

    List<Phone> findByPhoneNumber(String phoneNumber);
}