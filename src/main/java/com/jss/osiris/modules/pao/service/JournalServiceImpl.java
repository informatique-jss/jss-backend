package com.jss.osiris.modules.pao.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.pao.model.Journal;
import com.jss.osiris.modules.pao.repository.JournalRepository;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.service.AnnouncementService;

@Service
public class JournalServiceImpl implements JournalService {

    @Autowired
    JournalRepository journalRepository;

    @Autowired
    AnnouncementService announcementService;

    @Override
    @Cacheable(value = "journalList", key = "#root.methodName")
    public List<Journal> getJournals() {
        return IterableUtils.toList(journalRepository.findAll());
    }

    @Override
    @Cacheable(value = "journal", key = "#id")
    public Journal getJournal(Integer id) {
        Optional<Journal> journal = journalRepository.findById(id);
        if (journal.isPresent())
            return journal.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "journalList", allEntries = true),
            @CacheEvict(value = "journal", key = "#journal.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public Journal addOrUpdateJournal(Journal journal) throws OsirisException {
        journalRepository.save(journal);
        findAnnouncementInJournal(journal);
        return journal;
    }

    private void findAnnouncementInJournal(Journal journal) throws OsirisException {

        List<Announcement> announcements = announcementService.getAnnouncementWaitingForPublicationProof();

        if (announcements != null && announcements.size() > 0 && journal.getAttachments() != null) {
            // parse journal
            HashMap<Integer, String> pageContent = new HashMap<Integer, String>();
            PdfReader pdfReader;
            try {
                pdfReader = new PdfReader(journal.getAttachments().get(0).getUploadedFile().getPath());

                int pages = pdfReader.getNumberOfPages();
                for (int i = 1; i < pages; i++) {
                    pageContent.put(i, PdfTextExtractor.getTextFromPage(pdfReader, i));
                }
                pdfReader.close();
            } catch (IOException e) {
                throw new OsirisException(e, "Can't open file for journal n°" + journal.getId());
            }

            // find in journal
            for (Announcement announcement : announcements) {
                ArrayList<String> journalPages = new ArrayList<String>();
                for (Integer i : pageContent.keySet()) {
                    if (pageContent.get(i).contains(announcement.getId() + ""))
                        journalPages.add(i + "");
                }
                if (journalPages.size() > 0) {
                    // fill announcement with information
                    announcement.setJournalPages(String.join(",", journalPages));
                    announcement.setJournal(journal);
                    announcementService.addOrUpdateAnnouncement(announcement);

                    // call receipt generation
                    announcementService.generatePublicationProof(announcement);
                }
            }

        }
    }
}
