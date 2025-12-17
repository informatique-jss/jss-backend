package com.jss.osiris.libs.mail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.model.IndexationMail;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

@Service
public class IndexationMailServiceImpl implements IndexationMailService {

    @Autowired
    SearchService searchService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    ConstantService constantService;

    @Override
    public boolean attachIndexationMailToEntity(IndexationMail currentExportedMail) throws OsirisException {
        Pattern p = Pattern.compile("\\d+");
        // match any number id with order or quotation etc and get corresponding entity
        // to add attachment from email
        Matcher m = p.matcher(currentExportedMail.getSubject());
        while (m.find()) {
            try {
                indexMailOnEntity(Integer.parseInt(m.group()), CustomerOrder.class.getSimpleName(),
                        currentExportedMail);
                indexMailOnEntity(Integer.parseInt(m.group()), Quotation.class.getSimpleName(), currentExportedMail);
                indexMailOnEntity(Integer.parseInt(m.group()), Affaire.class.getSimpleName(), currentExportedMail);
                indexMailOnEntity(Integer.parseInt(m.group()), Responsable.class.getSimpleName(), currentExportedMail);
                indexMailOnEntity(Integer.parseInt(m.group()), Tiers.class.getSimpleName(), currentExportedMail);
                indexMailOnEntity(Integer.parseInt(m.group()), CompetentAuthority.class.getSimpleName(),
                        currentExportedMail);
            } catch (NumberFormatException e) {
            }
        }
        return true;
    }

    private void indexMailOnEntity(Integer idToFind, String entityType, IndexationMail currentExportedMail)
            throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException, OsirisException {
        List<IndexEntity> entitiesFound = searchService.searchForEntities(idToFind + "", entityType, true);
        if (entitiesFound != null && entitiesFound.size() == 1) {
            attachmentService.addAttachment(currentExportedMail.getMailText(), entitiesFound.get(0).getEntityId(), null,
                    entityType,
                    constantService.getAttachmentTypeClientCommunication(),
                    ("Mail client - " + currentExportedMail.getSubject().replace(":", " ")
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmm")) + ".html"),
                    false, null, null, null, null);
        }
    }

}
