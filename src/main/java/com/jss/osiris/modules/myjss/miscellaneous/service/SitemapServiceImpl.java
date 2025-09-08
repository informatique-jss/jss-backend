package com.jss.osiris.modules.myjss.miscellaneous.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.miscellaneous.model.Sitemap;
import com.jss.osiris.modules.myjss.miscellaneous.model.UrlEntry;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.service.AuthorService;
import com.jss.osiris.modules.myjss.wordpress.service.JssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.MyJssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.myjss.wordpress.service.PublishingDepartmentService;
import com.jss.osiris.modules.myjss.wordpress.service.SerieService;
import com.jss.osiris.modules.myjss.wordpress.service.TagService;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.service.AnnouncementService;

@Service
public class SitemapServiceImpl implements SitemapService {

    @Autowired
    PostService postService;

    @Autowired
    AnnouncementService announcementService;

    @Value("${my.jss.entry.point}")
    private String myJssEntryPoint;

    @Value("${jss.media.entry.point}")
    private String jssMediaEntryPoint;

    @Value("${server.my.jss.entry.point}")
    private String serverMyJssEntryPoint;

    @Value("${server.jss.entry.point}")
    private String serverJssEntryPoint;

    @Value("${upload.file.directory}")
    private String uploadFolder;

    @Autowired
    private JssCategoryService jssCategoryService;

    @Autowired
    MyJssCategoryService myJssCategoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    AuthorService authorService;

    @Autowired
    SerieService serieService;

    @Autowired
    PublishingDepartmentService publishingDepartmentService;

    private static final int MAX_URLS_PER_FILE = 45000;
    private static final String BASENAME = "sitemap";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateSitemaps() throws OsirisException {

        File folder = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files)
                file.delete();
        }

        generateSitemapsForMyJssArticles();
        generateSitemapsForOtherMyJssUrl();
        generateSitemapsForJssMediaArticles();
        generateSitemapsForJssMediaAnnouncements();
        generateSitemapsForOtherJssMediaUrl();
        generateSummarySitemaps();
    }

    private void generateSitemapsForJssMediaAnnouncements() throws OsirisException {
        List<Announcement> announcements = announcementService.getAllAnnouncementsForWebsite();

        int total = announcements.size();
        int fileCount = (int) Math.ceil((double) total / MAX_URLS_PER_FILE);
        XmlMapper xmlMapper = new XmlMapper();

        for (int i = 0; i < fileCount; i++) {
            int fromIndex = i * MAX_URLS_PER_FILE;
            int toIndex = Math.min(fromIndex + MAX_URLS_PER_FILE, total);
            List<UrlEntry> entries = new ArrayList<UrlEntry>();

            for (int j = fromIndex; j < toIndex; j++) {
                Announcement announcement = announcements.get(j);
                entries.add(new UrlEntry(jssMediaEntryPoint + "/announcement/" + announcement.getId(),
                        announcement.getPublicationDate().toString(), "yearly", "0.0"));
            }

            Sitemap sitemap = new Sitemap(entries);

            File file;
            if (fileCount == 1) {
                file = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder,
                        BASENAME + "-announcement-media" + ".xml");
            } else {
                file = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder,
                        BASENAME + "-announcement-media" + "-" + (i + 1) + ".xml");
            }

            try {
                xmlMapper.writeValue(file, sitemap);
            } catch (StreamWriteException e) {
                throw new OsirisException(e, "Error when writing sitemap for announcements");
            } catch (DatabindException e) {
                throw new OsirisException(e, "Error when writing sitemap for announcements");
            } catch (IOException e) {
                throw new OsirisException(e, "Error when writing sitemap for announcements");
            }
        }
    }

    private void generateSitemapsForJssMediaArticles() throws OsirisException {
        List<Post> posts = postService.getAllPostsForJssMedia();
        generateSitemapsForPosts(posts, jssMediaEntryPoint, "-articles-media");

    }

    private void generateSitemapsForMyJssArticles() throws OsirisException {
        List<Post> posts = postService.getAllPostsForJssMedia();
        generateSitemapsForPosts(posts, myJssEntryPoint, "-articles-myjss");
    }

    private void generateSitemapsForPosts(List<Post> posts, String siteEntryPoint, String sitemapSuffix)
            throws OsirisException {
        int total = posts.size();
        int fileCount = (int) Math.ceil((double) total / MAX_URLS_PER_FILE);
        XmlMapper xmlMapper = new XmlMapper();

        for (int i = 0; i < fileCount; i++) {
            int fromIndex = i * MAX_URLS_PER_FILE;
            int toIndex = Math.min(fromIndex + MAX_URLS_PER_FILE, total);
            List<UrlEntry> entries = new ArrayList<UrlEntry>();

            for (int j = fromIndex; j < toIndex; j++) {
                Post post = posts.get(j);
                entries.add(new UrlEntry(siteEntryPoint + "/post/" + post.getSlug(), post.getModified().toString(),
                        post.getDate().toLocalDate().isBefore(LocalDate.now().minusWeeks(1)) ? "monthly" : "daily",
                        post.getDate().toLocalDate().isBefore(LocalDate.now().minusWeeks(1)) ? "0.0" : "0.5"));
            }

            Sitemap sitemap = new Sitemap(entries);

            File file;
            if (fileCount == 1) {
                file = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder,
                        BASENAME + sitemapSuffix + ".xml");
            } else {
                file = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder,
                        BASENAME + sitemapSuffix + "-" + (i + 1) + ".xml");
            }

            try {
                xmlMapper.writeValue(file, sitemap);
            } catch (StreamWriteException e) {
                throw new OsirisException(e, "Error when writing sitemap for announcements");
            } catch (DatabindException e) {
                throw new OsirisException(e, "Error when writing sitemap for announcements");
            } catch (IOException e) {
                throw new OsirisException(e, "Error when writing sitemap for announcements");
            }
        }
    }

    private void generateSitemapsForOtherJssMediaUrl() throws OsirisException {
        List<UrlEntry> entries = new ArrayList<UrlEntry>();
        XmlMapper xmlMapper = new XmlMapper();

        List<JssCategory> categories = jssCategoryService.getAvailableJssCategories();
        for (JssCategory category : categories) {
            entries.add(new UrlEntry(jssMediaEntryPoint + "/post/category/" + category.getSlug()));

            List<Tag> tags = tagService.getAllTagsByJssCategory(category);
            if (tags != null)
                for (Tag tag : tags)
                    entries.add(new UrlEntry(jssMediaEntryPoint + "/post/tag/" + tag.getSlug()));
        }

        List<Author> authors = authorService.getAllAuthors();
        for (Author author : authors)
            entries.add(new UrlEntry(jssMediaEntryPoint + "/post/author/" + author.getSlug()));

        List<Serie> series = serieService.getAvailableSeries();
        if (series != null)
            for (Serie serie : series)
                entries.add(new UrlEntry(jssMediaEntryPoint + "/post/serie/" + serie.getSlug()));

        List<PublishingDepartment> departments = publishingDepartmentService.getAvailableDepartments();
        for (PublishingDepartment department : departments)
            entries.add(new UrlEntry(jssMediaEntryPoint + "/post/department/" + department.getCode()));

        entries.add(new UrlEntry(jssMediaEntryPoint + "/home"));
        entries.add(new UrlEntry(jssMediaEntryPoint + "/announcement/search"));
        entries.add(new UrlEntry(jssMediaEntryPoint + "/podcasts"));
        entries.add(new UrlEntry(jssMediaEntryPoint + "/subscription"));
        entries.add(new UrlEntry(jssMediaEntryPoint + "/kiosk"));
        entries.add(new UrlEntry(jssMediaEntryPoint + "/contribute"));

        Sitemap sitemap = new Sitemap(entries);

        File file = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder,
                BASENAME + "-other-media" + ".xml");
        try {
            xmlMapper.writeValue(file, sitemap);
        } catch (StreamWriteException e) {
            throw new OsirisException(e, "Error when writing sitemap for announcements");
        } catch (DatabindException e) {
            throw new OsirisException(e, "Error when writing sitemap for announcements");
        } catch (IOException e) {
            throw new OsirisException(e, "Error when writing sitemap for announcements");
        }
    }

    private void generateSitemapsForOtherMyJssUrl() throws OsirisException {

        List<UrlEntry> entries = new ArrayList<UrlEntry>();
        XmlMapper xmlMapper = new XmlMapper();

        entries.add(new UrlEntry(myJssEntryPoint + "/compagny/about-us"));
        entries.add(new UrlEntry(myJssEntryPoint + "/compagny/our-story"));
        entries.add(new UrlEntry(myJssEntryPoint + "/compagny/our-team"));
        entries.add(new UrlEntry(myJssEntryPoint + "/compagny/join-us"));
        entries.add(new UrlEntry(myJssEntryPoint + "/home"));
        entries.add(new UrlEntry(myJssEntryPoint + "/demo"));
        entries.add(new UrlEntry(myJssEntryPoint + "/prices"));
        entries.add(new UrlEntry(myJssEntryPoint + "/contact"));
        entries.add(new UrlEntry(myJssEntryPoint + "/newsletter"));
        entries.add(new UrlEntry(myJssEntryPoint + "/privacy-policy"));
        entries.add(new UrlEntry(myJssEntryPoint + "/disclaimer"));
        entries.add(new UrlEntry(myJssEntryPoint + "/terms"));
        entries.add(new UrlEntry(myJssEntryPoint + "/404"));
        entries.add(new UrlEntry(myJssEntryPoint + "/profile/login"));
        entries.add(new UrlEntry(myJssEntryPoint + "/services/announcement"));
        entries.add(new UrlEntry(myJssEntryPoint + "/services/formality"));
        entries.add(new UrlEntry(myJssEntryPoint + "/services/apostille"));
        entries.add(new UrlEntry(myJssEntryPoint + "/services/domiciliation"));
        entries.add(new UrlEntry(myJssEntryPoint + "/services/document"));
        entries.add(new UrlEntry(myJssEntryPoint + "/quotation/identification"));
        entries.add(new UrlEntry(myJssEntryPoint + "/tools/practical-sheets"));
        entries.add(new UrlEntry(myJssEntryPoint + "/tools/mandatory-documents"));
        entries.add(new UrlEntry(myJssEntryPoint + "/tools/exclusives"));
        entries.add(new UrlEntry(myJssEntryPoint + "/tools/webinars"));

        Sitemap sitemap = new Sitemap(entries);

        File file = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder,
                BASENAME + "-other-myjss" + ".xml");
        try {
            xmlMapper.writeValue(file, sitemap);
        } catch (StreamWriteException e) {
            throw new OsirisException(e, "Error when writing sitemap for announcements");
        } catch (DatabindException e) {
            throw new OsirisException(e, "Error when writing sitemap for announcements");
        } catch (IOException e) {
            throw new OsirisException(e, "Error when writing sitemap for announcements");
        }
    }

    private void generateSummarySitemaps() throws OsirisException {
        File folder = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException(
                    "Folder not found: " + uploadFolder.trim() + File.separator + Sitemap.siteMapFolder);
        }

        // JSS Media
        List<UrlEntry> entries = Arrays.stream(folder.listFiles())
                .filter(file -> file.isFile() && file.getName().startsWith("sitemap")
                        && file.getName().endsWith(".xml") && file.getName().contains("-media"))
                .map(file -> {
                    String loc = serverJssEntryPoint + "/" + file.getName();
                    return new UrlEntry(loc);
                })
                .collect(Collectors.toList());

        Sitemap index = new Sitemap(entries);

        XmlMapper xmlMapper = new XmlMapper();
        File outFile = new File(folder, "sitemap-index-media.xml");
        try {
            xmlMapper.writeValue(outFile, index);
        } catch (StreamWriteException e) {
            throw new OsirisException(e, "Error when writing index sitemap for announcements");
        } catch (DatabindException e) {
            throw new OsirisException(e, "Error when writing index sitemap for announcements");
        } catch (IOException e) {
            throw new OsirisException(e, "Error when writing index sitemap for announcements");
        }

        // MyJSS
        entries = Arrays.stream(folder.listFiles())
                .filter(file -> file.isFile() && file.getName().startsWith("sitemap")
                        && file.getName().endsWith(".xml") && file.getName().contains("-myjss"))
                .map(file -> {
                    String loc = serverMyJssEntryPoint + "/" + file.getName();
                    return new UrlEntry(loc);
                })
                .collect(Collectors.toList());

        index = new Sitemap(entries);

        File outFile2 = new File(folder, "sitemap-index-myjss.xml");
        try {
            xmlMapper.writeValue(outFile2, index);
        } catch (StreamWriteException e) {
            throw new OsirisException(e, "Error when writing index sitemap for announcements");
        } catch (DatabindException e) {
            throw new OsirisException(e, "Error when writing index sitemap for announcements");
        } catch (IOException e) {
            throw new OsirisException(e, "Error when writing index sitemap for announcements");
        }
    }
}
