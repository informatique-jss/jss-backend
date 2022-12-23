package com.jss.osiris.modules.quotation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementSearchResult;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.Confrere;

public interface AnnouncementRepository extends CrudRepository<Announcement, Integer> {

        @Query("select a from Announcement a where confrere=:journalJssPaper and journal is null")
        List<Announcement> findAnnouncementWaitingForPublicationProof(
                        @Param("journalJssPaper") Confrere journalJssPaper);

        @Query(nativeQuery = true, value = "" +
                        " select a.id, coalesce(affaire.denomination, affaire.firstname || ' ' || affaire.lastname) as affaireName, "
                        +
                        " d.code as department, " +
                        " a.publication_date as publicationDate, " +
                        " STRING_AGG( DISTINCT notice_type.label,', ' order by notice_type.label)  as noticeTypeLabels, "
                        +
                        " a.notice " +
                        " from announcement a " +
                        " join provision p on p.id_announcement = a.id " +
                        " join asso_affaire_order asso_affaire on asso_affaire.id = p.id_asso_affaire_order " +
                        " join affaire on affaire.id = asso_affaire.id_affaire " +
                        " join department d on d.id = a.id_department " +
                        " left join asso_announcement_notice_type asso on a.id = asso.id_announcement " +
                        " left join  notice_type   on notice_type.id = asso.id_notice_type " +
                        " where ((:isStricNameSearch = true and (:affaireName='' or upper(coalesce(affaire.denomination, affaire.firstname || ' ' || affaire.lastname))=upper(:affaireName ) ) )"
                        +
                        " or (:isStricNameSearch = false and (:affaireName='' or upper(coalesce(affaire.denomination, affaire.firstname || ' ' || affaire.lastname))  like '%' || upper(:affaireName)  || '%'  ))) "
                        +
                        " and (   a.publication_date <=:endDate ) " +
                        " and (  a.publication_date >=:startDate ) " +
                        "  and ( :departmentId = 0 or a.id_department = :departmentId) " +
                        "  and ( :noticeTypeId = 0 or asso.id_notice_type = :noticeTypeId) " +
                        "  group by a.id, coalesce(affaire.denomination, affaire.firstname || ' ' || affaire.lastname),d.code,a.publication_date,a.notice ")
        List<AnnouncementSearchResult> searchAnnouncements(@Param("affaireName") String affaireName,
                        @Param("isStricNameSearch") Boolean isStricNameSearch,
                        @Param("departmentId") Integer departmentId,
                        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                        @Param("noticeTypeId") Integer noticeTypeId);

        @Query(nativeQuery = true, value = " " +
                        " select affaire.id " +
                        " from announcement a join provision p on p.id_announcement = a.id " +
                        " join asso_affaire_order asso on asso.id = p.id_asso_affaire_order " +
                        " join affaire on affaire.id = asso.id_affaire  " +
                        " where a.id = :announcementId " +
                        "")
        Integer getAffaireForAnnouncement(@Param("announcementId") Integer announcementId);

        @Query("select a from Announcement a where a.announcementStatus=:announcementStatus and publicationDate is not null and actuLegaleId is null and publicationDate<=:publicationDate and confrere = :confrere")
        List<Announcement> getAnnouncementByStatusAndPublicationDateMin(
                        @Param("announcementStatus") AnnouncementStatus announcementStatus,
                        @Param("publicationDate") LocalDate publicationDate, @Param("confrere") Confrere confrere);
}
