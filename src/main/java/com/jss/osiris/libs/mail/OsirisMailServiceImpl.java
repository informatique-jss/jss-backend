package com.jss.osiris.libs.mail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;

@Service
public class OsirisMailServiceImpl implements OsirisMailService {
    @Autowired
    AutoIndexMailOsirisDelegate autoIndexMailOsirisDelegate;

    @Autowired
    IndexEntityRepository indexEntityRepository;

    @Override
    public void getAttachmentFromOsirisMail() throws OsirisException {
        List<Document> mailExports = autoIndexMailOsirisDelegate.getPdfMailsFromJavaMailImap();

    for(Document currentDoc : mailExports){
        currentDoc.open();
       currentDoc.get
        if(currentDoc.getSubject())
    }

       List<IndexEntity> entityFound= indexEntityRepository.searchForEntities(searchQuery, 1);
    }

}
