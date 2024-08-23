package com.jss.osiris.modules.quotation.service.infoGreffe;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.infoGreffe.DocumentAssocieInfogreffe;
import com.jss.osiris.modules.quotation.model.infoGreffe.EvenementInfogreffe;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;
import com.jss.osiris.modules.quotation.repository.infoGreffe.FormaliteInfogreffeRepository;

@Service
public class FormaliteInfogreffeServiceImpl implements FormaliteInfogreffeService {

    @Autowired
    InfogreffeDelegateService infogreffeDelegateService;

    @Autowired
    FormaliteInfogreffeRepository formaliteInfogreffeRepository;

    @Autowired
    BatchService batchService;

    @Autowired
    AttachmentTypeService attachmentTypeService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    ConstantService constantService;

    @Override
    public FormaliteInfogreffe addOrUpdFormaliteInfogreffe(FormaliteInfogreffe formaliteInfogreffe) {
        if (formaliteInfogreffe.getEvenements() != null && formaliteInfogreffe.getEvenements().size() > 0)
            for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffe.getEvenements())
                evenementInfogreffe.setFormaliteInfogreffe(formaliteInfogreffe);

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
                if (formaliteInfogreffe.getEntreprise() != null
                        && formaliteInfogreffe.getEntreprise().getSiren() == null)
                    formaliteInfogreffe.setEntreprise(null);

                addOrUpdFormaliteInfogreffe(formaliteInfogreffe);
                // test boolean +injection batch
                if (!isRefreshOnlyToday || formaliteInfogreffe.getEvenements() == null
                        || formaliteInfogreffe.getEvenements() != null
                                && formaliteInfogreffe.getEvenements().size() > 0
                                && formaliteInfogreffe.getEvenements().get(0).getCreatedDate()
                                        .isAfter(LocalDateTime.now().withHour(00).withMinute(00).minusSeconds(01)))
                    batchService.declareNewBatch(Batch.REFRESH_FORMALITE_INFOGREFFE_DETAIL,
                            formaliteInfogreffe.getIdentifiantFormalite().getFormaliteNumero());
            }
        }
    }

    @Override
    public void refreshFormaliteInfogreffeDetail(FormaliteInfogreffe formaliteInfogreffe)
            throws OsirisException {
        FormaliteInfogreffe formaliteInfogreffeDetail = infogreffeDelegateService
                .getInfogreffeFormalite(formaliteInfogreffe);
        setInfogreffeFormaliteEvenementDate(formaliteInfogreffeDetail);

        if (formaliteInfogreffe.getEntreprise() != null
                && formaliteInfogreffe.getEntreprise().getSiren() == null)
            formaliteInfogreffe.setEntreprise(null);
        formaliteInfogreffeDetail.setFormalite(formaliteInfogreffe.getFormalite());
        addOrUpdFormaliteInfogreffe(formaliteInfogreffeDetail);

        if (formaliteInfogreffe.getFormalite() != null) {
            if (formaliteInfogreffeDetail.getEvenements() != null
                    && formaliteInfogreffeDetail.getEvenements().size() > 0) {
                for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffeDetail.getEvenements()) {
                    if (evenementInfogreffe.getDocumentsAssocies() != null
                            && evenementInfogreffe.getDocumentsAssocies().size() > 0) {
                        for (DocumentAssocieInfogreffe documentAssocieInfogreffe : evenementInfogreffe
                                .getDocumentsAssocies()) {
                            InputStream documentInfogreffe = infogreffeDelegateService
                                    .getAttachmentFileFromEvenementInfogreffe(documentAssocieInfogreffe);
                            attachmentService.addAttachment(documentInfogreffe,
                                    formaliteInfogreffeDetail.getFormalite().getProvision().get(0).getId(), null,
                                    Provision.class.getSimpleName(),
                                    getAttachmentTypeDocumentAssocieInfogreffe(documentAssocieInfogreffe),
                                    documentAssocieInfogreffe.getTypeDocument() + "_"
                                            + evenementInfogreffe.getCreatedDate() + ".pdf",
                                    false, null,
                                    null, null, null);
                        }
                    }
                }
            }
        }
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

    private AttachmentType getAttachmentTypeDocumentAssocieInfogreffe(
            DocumentAssocieInfogreffe documentAssocieInfogreffe)
            throws OsirisException {
        if (documentAssocieInfogreffe.getTypeDocument()
                .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_FACTURE))
            return constantService.getAttachmentTypeProviderInvoice();
        if (documentAssocieInfogreffe.getTypeDocument()
                .equals(DocumentAssocieInfogreffe.INFOGREFFE_ATTACHMENT_KBIS))
            return constantService.getAttachmentTypeKbis();
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
