package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.repository.CompetentAuthorityRepository;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.DocumentAssocieInfogreffe;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.EvenementInfogreffe;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.FormaliteInfogreffe;
import com.jss.osiris.modules.osiris.quotation.repository.infoGreffe.FormaliteInfogreffeRepository;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.FormaliteService;
import com.jss.osiris.modules.osiris.quotation.service.FormaliteStatusService;

@Service
public class FormaliteInfogreffeServiceImpl implements FormaliteInfogreffeService {

    @Autowired
    InfogreffeDelegateService infogreffeDelegateService;

    @Autowired
    FormaliteInfogreffeRepository formaliteInfogreffeRepository;

    @Autowired
    CompetentAuthorityRepository competentAuthorityRepository;

    @Autowired
    BatchService batchService;

    @Autowired
    AttachmentTypeService attachmentTypeService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    ConstantService constantService;

    @Autowired
    FormaliteService formaliteService;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    @Override
    public FormaliteInfogreffe addOrUpdateFormaliteInfogreffe(FormaliteInfogreffe formaliteInfogreffe) {
        if (formaliteInfogreffe.getEvenements() != null && formaliteInfogreffe.getEvenements().size() > 0)
            for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffe.getEvenements()) {
                evenementInfogreffe.setFormaliteInfogreffe(formaliteInfogreffe);
                if (evenementInfogreffe.getDocumentsAssocies() != null
                        && evenementInfogreffe.getDocumentsAssocies().size() > 0) {
                    for (DocumentAssocieInfogreffe documentAssocieInfogreffe : evenementInfogreffe
                            .getDocumentsAssocies())
                        documentAssocieInfogreffe.setEvenementInfogreffe(evenementInfogreffe);
                }
            }
        return formaliteInfogreffeRepository.save(formaliteInfogreffe);
    }

    @Override
    public FormaliteInfogreffe getFormaliteInfogreffe(Integer formaliteNumero) {
        FormaliteInfogreffe formaliteInfogreffe = formaliteInfogreffeRepository
                .findByIdentifiantFormalite_FormaliteNumero(formaliteNumero);
        return formaliteInfogreffe;
    }

    @Override
    public void refreshAllFormaliteInfogreffe(Boolean isRefreshOnlyToday)
            throws OsirisException {
        List<FormaliteInfogreffe> formalitesInfogreffe = infogreffeDelegateService
                .getAllInfogreffeFormalities();
        if (formalitesInfogreffe != null && formalitesInfogreffe.size() > 0) {
            for (FormaliteInfogreffe formaliteInfogreffe : formalitesInfogreffe) {
                setInfogreffeFormaliteEvenementDate(formaliteInfogreffe);
                setInfogreffeFormaliteEvenementCodeEtat(formaliteInfogreffe);
                if (formaliteInfogreffe.getEntreprise() != null
                        && formaliteInfogreffe.getEntreprise().getSiren() == null)
                    formaliteInfogreffe.setEntreprise(null);

                if (!isRefreshOnlyToday || formaliteInfogreffe.getEvenements() == null
                        || formaliteInfogreffe.getEvenements() != null
                                && formaliteInfogreffe.getEvenements().size() > 0
                                && formaliteInfogreffe.getEvenements().get(0).getCreatedDate()
                                        .isAfter(LocalDateTime.now().withHour(00).withMinute(00).minusSeconds(01))) {
                    // Save only if new one
                    FormaliteInfogreffe currentFormaliteInfogreffe = getFormaliteInfogreffe(
                            formaliteInfogreffe.getIdentifiantFormalite().getFormaliteNumero());

                    if (currentFormaliteInfogreffe == null)
                        addOrUpdateFormaliteInfogreffe(formaliteInfogreffe);
                    batchService.declareNewBatch(Batch.REFRESH_FORMALITE_INFOGREFFE_DETAIL,
                            formaliteInfogreffe.getIdentifiantFormalite().getFormaliteNumero());
                }

            }
        }
    }

    @Override
    public void refreshFormaliteInfogreffeDetail(FormaliteInfogreffe formaliteInfogreffe)
            throws OsirisException {
        FormaliteInfogreffe formaliteInfogreffeDetail = infogreffeDelegateService
                .getInfogreffeFormalite(formaliteInfogreffe);

        // Determine if status changed
        formaliteInfogreffeDetail.setFormalite(formaliteInfogreffe.getFormalite());
        setInfogreffeFormaliteEvenementDate(formaliteInfogreffeDetail);
        setInfogreffeFormaliteEvenementCodeEtat(formaliteInfogreffeDetail);
        EvenementInfogreffe currentStatus = getLastEvenementInfogreffe(formaliteInfogreffe, true);
        EvenementInfogreffe newStatus = getLastEvenementInfogreffe(formaliteInfogreffeDetail, true);
        Boolean hasChangedStatus = currentStatus == null
                || !currentStatus.getCodeEtat().equals(newStatus.getCodeEtat());

        if (formaliteInfogreffe.getEntreprise() != null
                && (formaliteInfogreffe.getEntreprise().getSiren() == null
                        || formaliteInfogreffe.getEntreprise().getSiren().equals("null")))
            formaliteInfogreffe.setEntreprise(null);
        addOrUpdateFormaliteInfogreffe(formaliteInfogreffeDetail);

        formaliteInfogreffe = getFormaliteInfogreffe(
                formaliteInfogreffe.getIdentifiantFormalite().getFormaliteNumero());

        if (hasChangedStatus) {
            refreshWaitedCompetentAuthorithy(formaliteInfogreffe);
            refreshFormaliteStatusFromInfogreffeStatus(formaliteInfogreffe);
        }

        if (formaliteInfogreffe.getFormalite() != null) {
            if (formaliteInfogreffe.getEvenements() != null
                    && formaliteInfogreffe.getEvenements().size() > 0) {
                for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffe.getEvenements()) {
                    if (evenementInfogreffe.getDocumentsAssocies() != null
                            && evenementInfogreffe.getDocumentsAssocies().size() > 0) {
                        for (DocumentAssocieInfogreffe documentAssocieInfogreffe : evenementInfogreffe
                                .getDocumentsAssocies()) {
                            List<Attachment> existingAttachment = attachmentService
                                    .findByDocumentAssocieInfogreffe(documentAssocieInfogreffe);
                            if (existingAttachment == null || existingAttachment.size() == 0) {
                                InputStream documentInfogreffe = infogreffeDelegateService
                                        .getAttachmentFileFromEvenementInfogreffe(documentAssocieInfogreffe);

                                int size = 0;
                                if (documentInfogreffe != null) {
                                    try {
                                        size = documentInfogreffe.available();
                                    } catch (Exception e) {
                                        throw new OsirisException(e, "Impossible to determine size of document");
                                    }
                                    if (size > 1) {
                                        String filename = documentAssocieInfogreffe.getTypeDocument() + "_"
                                                + evenementInfogreffe.getCreatedDate()
                                                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
                                                + ".pdf";
                                        List<Attachment> attachments = attachmentService.addAttachment(
                                                documentInfogreffe,
                                                formaliteInfogreffeDetail.getFormalite().getProvision().get(0).getId(),
                                                null,
                                                Provision.class.getSimpleName(),
                                                getAttachmentTypeDocumentAssocieInfogreffe(documentAssocieInfogreffe),
                                                filename,
                                                false, null,
                                                null, null, null);

                                        for (Attachment attachment : attachments) {
                                            if (attachment.getUploadedFile().getFilename().equals(filename)) {
                                                attachment.setDocumentAssocieInfogreffe(documentAssocieInfogreffe);
                                                attachmentService.addOrUpdateAttachment(attachment);
                                                break;
                                            }
                                        }
                                    }
                                }

                                try {
                                    if (documentInfogreffe != null)
                                        documentInfogreffe.close();
                                } catch (IOException e) {
                                    throw new OsirisException(e,
                                            "Impossible to close input stream for Inforgreffe document associe "
                                                    + documentAssocieInfogreffe.getEvenementInfogreffe()
                                                            .getFormaliteInfogreffe().getId());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void refreshWaitedCompetentAuthorithy(FormaliteInfogreffe formaliteInfogreffe) {
        if (formaliteInfogreffe != null) {
            if (formaliteInfogreffe.getEvenements() != null && formaliteInfogreffe.getEvenements().size() > 0) {
                formaliteInfogreffe.getEvenements().sort(
                        (o1, o2) -> ((LocalDateTime) o1.getCreatedDate())
                                .compareTo((LocalDateTime) (o2.getCreatedDate())));
                if (formaliteInfogreffe.getEvenements().get(0).getCodeEtat()
                        .equals(FormaliteInfogreffe.INFOGREFFE_STATUS_RECEIVED)) {
                    List<CompetentAuthority> competentAuthorities = competentAuthorityRepository
                            .findByInpiReference(formaliteInfogreffe.getGreffeDestinataire().getCodeEDI());

                    if (competentAuthorities != null && competentAuthorities.size() == 1) {
                        formaliteInfogreffe.getFormalite()
                                .setWaitedCompetentAuthority(competentAuthorities.get(0));
                        formaliteService.addOrUpdateFormalite(formaliteInfogreffe.getFormalite());
                    }
                }
            }
        }
    }

    private void refreshFormaliteStatusFromInfogreffeStatus(FormaliteInfogreffe formaliteInfogreffe)
            throws OsirisException {
        if (formaliteInfogreffe.getFormalite() != null && formaliteInfogreffe.getEvenements() != null
                && formaliteInfogreffe.getEvenements().size() > 0) {

            if (formaliteInfogreffe.getFormalite().getFormaliteStatus().getIsCloseState() != null
                    && formaliteInfogreffe.getFormalite().getFormaliteStatus().getIsCloseState())
                return;

            EvenementInfogreffe lastEvent = getLastEvenementInfogreffe(formaliteInfogreffe, true);

            if (lastEvent != null) {
                if (lastEvent.getCodeEtat()
                        .equals(FormaliteInfogreffe.INFOGREFFE_STATUS_REJECT)
                        || lastEvent.getCodeEtat()
                                .equals(FormaliteInfogreffe.INFOGREFFE_STATUS_STRICT_REJECT)) {
                    formaliteInfogreffe.getFormalite().setFormaliteStatus(
                            formaliteStatusService
                                    .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_AUTHORITY_REJECTED));
                    CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                            formaliteInfogreffe.getFormalite()
                                    .getProvision().get(0).getService().getAssoAffaireOrder().getCustomerOrder(),
                            null,
                            "Formalité Infogreffe n°" + formaliteInfogreffe.getReferenceTechnique() + " rejetée par "
                                    + (formaliteInfogreffe.getGreffeDestinataire() != null
                                            ? formaliteInfogreffe.getGreffeDestinataire().getNom()
                                            : ""),
                            false, false);

                    customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                            constantService.getActiveDirectoryGroupFormalites());
                }
                if (lastEvent.getCodeEtat()
                        .equals(FormaliteInfogreffe.INFOGREFFE_STATUS_VALIDATED)) {
                    formaliteInfogreffe.getFormalite().setFormaliteStatus(
                            formaliteStatusService
                                    .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_AUTHORITY_VALIDATED));
                    CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                            formaliteInfogreffe.getFormalite()
                                    .getProvision().get(0).getService().getAssoAffaireOrder().getCustomerOrder(),
                            null,
                            "Formalité Infogreffe n°" + formaliteInfogreffe.getReferenceTechnique() + " validée par "
                                    + (formaliteInfogreffe.getGreffeDestinataire() != null
                                            ? formaliteInfogreffe.getGreffeDestinataire().getNom()
                                            : ""),
                            false, false);

                    customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                            constantService.getActiveDirectoryGroupFormalites());
                }
                formaliteService.addOrUpdateFormalite(formaliteInfogreffe.getFormalite());
            }
        }
    }

    @Override
    public EvenementInfogreffe getLastEvenementInfogreffe(FormaliteInfogreffe formaliteInfogreffe,
            boolean onlyNonNullStatus) {
        if (formaliteInfogreffe.getFormalite() != null && formaliteInfogreffe.getEvenements() != null
                && formaliteInfogreffe.getEvenements().size() > 0) {

            formaliteInfogreffe.getEvenements().sort(
                    (o1, o2) -> ((LocalDateTime) o2.getCreatedDate())
                            .compareTo((LocalDateTime) (o1.getCreatedDate())));

            for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffe.getEvenements())
                if (!onlyNonNullStatus || evenementInfogreffe.getCodeEtat() != null
                        && evenementInfogreffe.getCodeEtat().trim().length() > 0)
                    return evenementInfogreffe;
        }
        return null;
    }

    @Override
    public List<FormaliteInfogreffe> getFormaliteInfogreffeByReference(String reference) {
        return formaliteInfogreffeRepository.findByReference(reference);
    }

    private void setInfogreffeFormaliteEvenementDate(FormaliteInfogreffe formaliteInfogreffe) {
        if (formaliteInfogreffe != null && formaliteInfogreffe.getEvenements() != null
                && formaliteInfogreffe.getEvenements().size() > 0) {
            for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffe.getEvenements()) {
                if (evenementInfogreffe.getDate() != null)
                    evenementInfogreffe.setCreatedDate(evenementInfogreffe.getDate());
            }
        }
    }

    private void setInfogreffeFormaliteEvenementCodeEtat(FormaliteInfogreffe formaliteInfogreffe) {
        if (formaliteInfogreffe != null && formaliteInfogreffe.getEvenements() != null
                && formaliteInfogreffe.getEvenements().size() > 0) {
            for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffe.getEvenements()) {
                if (evenementInfogreffe.getCodeEtat() == null)
                    evenementInfogreffe.setCodeEtat("");
            }
        }
    }

    private AttachmentType getAttachmentTypeDocumentAssocieInfogreffe(
            DocumentAssocieInfogreffe documentAssocieInfogreffe)
            throws OsirisException {
        if (documentAssocieInfogreffe.getTypeDocument()
                .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_FACTURE))
            return constantService.getAttachmentTypeProviderInvoice();
        if (documentAssocieInfogreffe.getTypeDocument()
                .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_KBIS))
            return constantService.getAttachmentTypeKbisUpdated();
        if (documentAssocieInfogreffe.getTypeDocument()
                .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_RBE_COPIE))
            return constantService.getAttachmentTypeRbe();
        if (documentAssocieInfogreffe.getTypeDocument()
                .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_CERT_DEPOT))
            return constantService.getAttachmentTypeDepositReceipt();
        if (documentAssocieInfogreffe.getTypeDocument()
                .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_AUTRE))
            return constantService.getAttachmentTypeAutreInfogreffe();
        if (documentAssocieInfogreffe.getTypeDocument()
                .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_INVALIDATION)
                || documentAssocieInfogreffe.getTypeDocument()
                        .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_PIECE_MANQUANTE)
                || documentAssocieInfogreffe.getTypeDocument()
                        .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_REJET))
            return constantService.getAttachmentTypeRefusInfogreffe();
        return null;
    }
}
