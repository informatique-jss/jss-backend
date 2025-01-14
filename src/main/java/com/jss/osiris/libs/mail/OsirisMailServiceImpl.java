package com.jss.osiris.libs.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.ExportOsirisMail;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class OsirisMailServiceImpl implements OsirisMailService {
    @Autowired
    AutoIndexMailOsirisDelegate autoIndexMailOsirisDelegate;

    @Autowired
    IndexEntityRepository indexEntityRepository;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    TiersService tiersService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    ConstantService constantService;

    @Override
    public void getAttachmentFromOsirisMail() throws OsirisException {
        List<ExportOsirisMail> mailExports = autoIndexMailOsirisDelegate.getPdfMailsFromJavaMailImap();
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = null;

        for (ExportOsirisMail currentExportedMail : mailExports) {
            String foundId = null;
            if (currentExportedMail.getSubjectMail() != null)
                matcher = pattern.matcher(currentExportedMail.getSubjectMail());
            if (matcher != null && matcher.find())
                foundId = matcher.group();
            if (foundId != null) {
                List<IndexEntity> entityFound = indexEntityRepository.searchForEntities(foundId, 1);
                if (entityFound != null && !entityFound.isEmpty()) {
                    if (entityFound.get(0).getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                        CustomerOrder customerOrder = customerOrderService
                                .getCustomerOrder(entityFound.get(0).getEntityId());
                        try {
                            attachmentService.addAttachment(new FileInputStream(currentExportedMail.getFileName()),
                                    customerOrder.getId(), null, CustomerOrder.class.getSimpleName(),
                                    constantService.getAttachmentTypeClientCommunication(),
                                    currentExportedMail.getFileName(),
                                    false, null, null, null, null);
                        } catch (FileNotFoundException e) {
                            throw new OsirisException(e, "file of exported mail not found");
                        }
                    }
                    if (entityFound.get(0).getEntityType().equals(Quotation.class.getSimpleName())) {
                        try {
                            Quotation quotation = quotationService.getQuotation(entityFound.get(0).getEntityId());
                            attachmentService.addAttachment(new FileInputStream(currentExportedMail.getFileName()),
                                    quotation.getId(), null, CustomerOrder.class.getSimpleName(),
                                    constantService.getAttachmentTypeClientCommunication(),
                                    currentExportedMail.getFileName(),
                                    false, null, null, null, null);
                        } catch (FileNotFoundException e) {
                            throw new OsirisException(e, "file of exported mail not found");
                        }
                    }
                    if (entityFound.get(0).getEntityType().equals(Tiers.class.getSimpleName())) {
                        try {
                            Tiers tiers = tiersService.getTiers(entityFound.get(0).getEntityId());
                            attachmentService.addAttachment(new FileInputStream(currentExportedMail.getFileName()),
                                    tiers.getId(), null, Tiers.class.getSimpleName(),
                                    constantService.getAttachmentTypeClientCommunication(),
                                    currentExportedMail.getFileName(),
                                    false, null, null, null, null);
                        } catch (FileNotFoundException e) {
                            throw new OsirisException(e, "file of exported mail not found");
                        }
                    }
                    if (entityFound.get(0).getEntityType().equals(Responsable.class.getSimpleName())) {
                        try {
                            Responsable responsable = responsableService
                                    .getResponsable(entityFound.get(0).getEntityId());
                            attachmentService.addAttachment(new FileInputStream(currentExportedMail.getFileName()),
                                    responsable.getId(), null, Responsable.class.getSimpleName(),
                                    constantService.getAttachmentTypeClientCommunication(),
                                    currentExportedMail.getFileName(),
                                    false, null, null, null, null);
                        } catch (FileNotFoundException e) {
                            throw new OsirisException(e, "file of exported mail not found");
                        }
                    }
                    if (entityFound.get(0).getEntityType().equals(Affaire.class.getSimpleName())) {
                        try {
                            Affaire affaire = affaireService.getAffaire(entityFound.get(0).getEntityId());
                            attachmentService.addAttachment(new FileInputStream(currentExportedMail.getFileName()),
                                    affaire.getId(), null, Affaire.class.getSimpleName(),
                                    constantService.getAttachmentTypeClientCommunication(),
                                    currentExportedMail.getFileName(),
                                    false, null, null, null, null);
                        } catch (FileNotFoundException e) {
                            throw new OsirisException(e, "file of exported mail not found");
                        }
                    }
                    if (entityFound.get(0).getEntityType().equals(CompetentAuthority.class.getSimpleName())) {
                        try {
                            CompetentAuthority competentAuthority = competentAuthorityService
                                    .getCompetentAuthority(entityFound.get(0).getEntityId());
                            attachmentService.addAttachment(new FileInputStream(currentExportedMail.getFileName()),
                                    competentAuthority.getId(), null, CompetentAuthority.class.getSimpleName(),
                                    constantService.getAttachmentTypeClientCommunication(),
                                    currentExportedMail.getFileName(),
                                    false, null, null, null, null);
                        } catch (FileNotFoundException e) {
                            throw new OsirisException(e, "file of exported mail not found");
                        }
                    }
                }
            }
        }

    }

}
