package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.PhoneTeams;
import org.springframework.data.repository.query.Param;

public interface PhoneRepository extends CrudRepository<Phone, Integer> {

    List<Phone> findByPhoneNumberContainingIgnoreCase(String phone);

    @Query(nativeQuery = true, value = "SELECT DISTINCT " +
            "CASE " +
            "WHEN t.id IS NOT NULL THEN 'Tiers' " +
            "WHEN r.id IS NOT NULL THEN 'Responsable' " +
            "WHEN c.id IS NOT NULL THEN 'Confrere' " +
            "WHEN p.id IS NOT NULL THEN 'Provider' " +
            "END as ENTITY_TYPE, COALESCE(t.id, r.id, c.id, p.id) as ENTITY_ID " +
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
    List<PhoneTeams> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}