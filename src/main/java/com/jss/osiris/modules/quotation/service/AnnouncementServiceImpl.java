package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.ActuLegaleAnnouncement;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.AnnouncementSearch;
import com.jss.osiris.modules.quotation.model.AnnouncementSearchResult;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.repository.AnnouncementRepository;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    ActuLegaleDelegate actuLegaleDelegate;

    @Autowired
    AnnouncementStatusService announcementStatusService;

    @Autowired
    AffaireService affaireService;

    @Override
    public List<Announcement> getAnnouncements() {
        return IterableUtils.toList(announcementRepository.findAll());
    }

    @Override
    public Announcement getAnnouncement(Integer id) {
        Optional<Announcement> announcement = announcementRepository.findById(id);
        if (announcement.isPresent())
            return announcement.get();
        return null;
    }

    @Override
    public Announcement addOrUpdateAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Override
    public List<Announcement> getAnnouncementWaitingForPublicationProof() throws OsirisException {
        return announcementRepository.findAnnouncementWaitingForPublicationProof(constantService.getConfrereJssPaper());
    }

    @Override
    public void generatePublicationProof(Announcement announcement) throws OsirisException {
        Attachment currentAttachment = null;

        if (announcement != null) {
            if (announcement.getAttachments() != null && announcement.getAttachments().size() > 0)
                for (Attachment attachment : announcement.getAttachments())
                    if (attachment.getAttachmentType().getId()
                            .equals(constantService.getAttachmentTypePublicationProof().getId())) {
                        currentAttachment = attachment;
                        break;
                    }
            if (currentAttachment == null && announcement.getJournal() != null
                    && announcement.getJournalPages() != null) {
                // if journal filled => generate
                try {
                    String[] pages = announcement.getJournalPages().split(",");
                    ArrayList<Integer> pagesNumber = new ArrayList<Integer>();
                    pagesNumber.add(1);
                    for (String page : pages)
                        pagesNumber.add(Integer.parseInt(page));

                    File destFile = File.createTempFile("proof_target", "pdf");
                    Document targetPdf = new Document();
                    PdfCopy copy = new PdfCopy(targetPdf, new FileOutputStream(destFile));
                    targetPdf.open();
                    PdfReader ReadInputPDF;
                    ReadInputPDF = new PdfReader(
                            announcement.getJournal().getAttachments().get(0).getUploadedFile().getPath());

                    copy.addDocument(ReadInputPDF, pagesNumber);
                    copy.freeReader(ReadInputPDF);
                    targetPdf.close();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
                    attachmentService.addAttachment(new FileInputStream(destFile), announcement.getId(),
                            Announcement.class.getSimpleName(),
                            constantService.getAttachmentTypePublicationProof(),
                            "Publication_proof_" + formatter.format(LocalDateTime.now()) + ".pdf",
                            false, "Justificatif de parution n°" + announcement.getId());
                    destFile.delete();
                } catch (IOException i) {
                    throw new OsirisException(i,
                            "Impossible to read files for publication proof generation for announcement n°"
                                    + announcement.getId());
                } catch (DocumentException e) {
                    throw new OsirisException(e,
                            "Impossible to generate publication proof for announcement n°" + announcement.getId());
                }
            }
        }
    }

    @Override
    public List<AnnouncementSearchResult> searchAnnouncements(AnnouncementSearch announcementSearch) {
        if (announcementSearch.getAffaireName() == null)
            announcementSearch.setAffaireName("");
        else
            announcementSearch.setAffaireName(announcementSearch.getAffaireName().trim());

        if (announcementSearch.getIsStricNameSearch() == null)
            announcementSearch.setIsStricNameSearch(false);

        Integer departementId = 0;
        if (announcementSearch.getDepartment() != null)
            departementId = announcementSearch.getDepartment().getId();

        Integer noticeTypeId = 0;
        if (announcementSearch.getNoticeType() != null)
            noticeTypeId = announcementSearch.getNoticeType().getId();

        if (announcementSearch.getStartDate() == null)
            announcementSearch.setStartDate(LocalDate.now().minusYears(100));

        if (announcementSearch.getEndDate() == null)
            announcementSearch.setEndDate(LocalDate.now().plusYears(100));

        return announcementRepository.searchAnnouncements(announcementSearch.getAffaireName(),
                announcementSearch.getIsStricNameSearch(), departementId,
                announcementSearch.getStartDate(),
                announcementSearch.getEndDate(), noticeTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishAnnouncementsToActuLegale() throws OsirisException {
        List<Announcement> announcements = announcementRepository.getAnnouncementByStatusAndPublicationDateMin(
                announcementStatusService.getAnnouncementStatusByCode(AnnouncementStatus.ANNOUNCEMENT_DONE),
                LocalDate.now().minusWeeks(0));

        if (announcements != null && announcements.size() > 0)
            for (Announcement announcement : announcements) {
                Integer affaire = announcementRepository.getAffaireForAnnouncement(announcement.getId());
                if (affaire == null)
                    throw new OsirisException(null,
                            "Impossible to find affaire for announcement n°" + announcement.getId());

                ActuLegaleAnnouncement actuLegaleAnnouncement = actuLegaleDelegate.publishAnnouncement(announcement,
                        affaireService.getAffaire(affaire));

                announcement.setActuLegaleId(actuLegaleAnnouncement.getId());
                addOrUpdateAnnouncement(announcement);
                if (actuLegaleAnnouncement == null || actuLegaleAnnouncement.getId() == null)
                    throw new OsirisException(null, "Impossible to publish announcement n°" + announcement.getId());
            }
    }
}
