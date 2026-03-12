package com.jss.osiris.modules.myjss.wordpress.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.model.NewspaperPage;
import com.jss.osiris.modules.myjss.wordpress.repository.NewspaperPageRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.UploadedFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class NewspaperPageServiceImpl implements NewspaperPageService {

    @Autowired
    NewspaperPageRepository newspaperPageRepository;

    @Autowired
    NewspaperService newspaperService;

    @PersistenceContext
    private EntityManager entityManager;

    public NewspaperPage addOrUpdateNewspaperPage(NewspaperPage newspaperPage) {
        return newspaperPageRepository.save(newspaperPage);
    }

    @Override
    public Page<Newspaper> searchNewspapers(String searchText, String sortBy) {
        Sort sort = Sort.unsorted();
        if ("dateAsc".equalsIgnoreCase(sortBy))
            sort = Sort.by("newspaper.date").ascending();
        else if ("dateDesc".equalsIgnoreCase(sortBy))
            sort = Sort.by("newspaper.date").descending();

        Pageable pageable = PageRequest.of(0, 1000, sort);

        if (searchText != null && searchText.trim().length() > 0)
            return newspaperPageRepository.findByContent(searchText, pageable);

        return newspaperService.getNewspapers(pageable);
    }

    @Transactional
    @Override
    public void fillDbWithNewspaperPages() throws OsirisException {
        List<Newspaper> newspapersToExtract = newspaperService.getNewspapers();
        int i = 0;
        for (Newspaper newspaper : newspapersToExtract) {
            UploadedFile uploadedFile = newspaper.getUploadedFullFile();
            if (uploadedFile != null) {
                // TODO : delete for dev ?
                // --------- For dev : -------------
                // String prefix = "Y:";
                // processPdfAndSavePages(prefix + uploadedFile.getPath().substring(8),
                // newspaper);

                // --------- For production : -------------
                processPdfAndSavePages(uploadedFile.getPath(), newspaper);
            }
            i++;
            System.out.println("---------- Newspaper No. " + i + " with id = " + newspaper.getId()
                    + " has been processed ---------------------");
        }
    }

    @Override
    public List<NewspaperPage> processPdfAndSavePages(String filePath, Newspaper newspaper) throws OsirisException {
        List<NewspaperPage> createdPages = new ArrayList<>();
        PdfReader reader = null;

        try {
            reader = new PdfReader(filePath);
            int numberOfPages = reader.getNumberOfPages();

            for (int i = 1; i <= numberOfPages; i++) {
                String textContent = PdfTextExtractor.getTextFromPage(reader, i, new SimpleTextExtractionStrategy());

                NewspaperPage page = new NewspaperPage();

                // Systematic cleaning: remove NULL characters and trim
                page.setContent(cleanTextContent(textContent));
                page.setPageNumber(i);
                page.setNewspaper(newspaper);

                createdPages.add(page);
                newspaperPageRepository.save(page);

                // Flush in batches for performance
                if (i % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear(); // Releases memory from the persistence context
                }
            }
            return createdPages;

        } catch (IOException e) {
            throw new OsirisException(e, "Error while processing PDF");
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private String cleanTextContent(String content) {
        if (content == null)
            return "";

        // Removes the NULL character (\u0000) that causes PostgreSQL to fail
        return content.replace("\u0000", "")
                .replace("\\u0000", ""); // Additional security for certain parsing libraries
    }
}
