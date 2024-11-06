package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.Media;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Page;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

import jakarta.transaction.Transactional;

@Service
public class WordpressDelegateImpl implements WordpressDelegate {
    @Value("${wordpress.entry.point}")
    private String wordpressEntryPoint;

    private String departmentRequestUrl = "/departement";
    private String myJssCategoryRequestUrl = "/myjss_category";
    private String categoryRequestUrl = "/categories";
    private String serieRequestUrl = "/serie";
    private String tagRequestUrl = "/tags";
    private String postRequestUrl = "/posts";
    private String mediaRequestUrl = "/media";
    private String usersRequestUrl = "/users";
    private String pageRequestUrl = "/pages";

    @Autowired
    PublishingDepartmentService publishingDepartmentService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    MyJssCategoryService myJssCategoryService;

    @Autowired
    PostService postService;

    @Autowired
    PageService pageService;

    @Autowired
    TagService tagService;

    @Autowired
    AuthorService authorService;

    @Autowired
    MediaService mediaService;

    @Autowired
    SerieService serieService;

    private List<PublishingDepartment> getAvailableDepartments() {
        ResponseEntity<List<PublishingDepartment>> response = new RestTemplate().exchange(
                wordpressEntryPoint + departmentRequestUrl + "?_fields=id,name,code,acf&per_page=100", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PublishingDepartment>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    private List<MyJssCategory> getAvailableMyJssCategories() {
        ResponseEntity<List<MyJssCategory>> response = new RestTemplate().exchange(
                wordpressEntryPoint + myJssCategoryRequestUrl + "?_fields=id,name,slug,acf,count&per_page=100",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MyJssCategory>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    private List<Category> getAvailableCategories() {
        ResponseEntity<List<Category>> response = new RestTemplate().exchange(
                wordpressEntryPoint + categoryRequestUrl + "?_fields=id,name,slug,count&per_page=100",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Category>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    private List<Serie> getAvailableSeries() {
        ResponseEntity<List<Serie>> response = new RestTemplate().exchange(
                wordpressEntryPoint + serieRequestUrl + "?per_page=100",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Serie>>() {
                });

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    @SuppressWarnings({ "null" })
    private List<Media> getAllMedia() {
        List<Media> pages = new ArrayList<Media>();

        ResponseEntity<List<Media>> response = getMediaPaginated(1);
        pages.addAll(response.getBody());

        int totalPage = Integer.parseInt(response.getHeaders().get("X-Wp-Totalpages").get(0) + "");
        if (totalPage > 1) {
            int page = 1;
            while (page < totalPage) {
                page++;
                response = getMediaPaginated(page);
                pages.addAll(response.getBody());
            }
        }

        return pages;
    }

    private ResponseEntity<List<Media>> getMediaPaginated(int page) {
        ResponseEntity<List<Media>> response = new RestTemplate()
                .exchange(wordpressEntryPoint + mediaRequestUrl + "?per_page=100&page=" + page
                        + "&_fields=id,author,date,media_details,media_type,alt_text,source_url",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Media>>() {
                        });

        return response;
    }

    @SuppressWarnings({ "null" })
    private List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<Author>();

        ResponseEntity<List<Author>> response = getAuthorPaginated(1);
        authors.addAll(response.getBody());

        int totalPage = Integer.parseInt(response.getHeaders().get("X-Wp-Totalpages").get(0) + "");
        if (totalPage > 1) {
            int page = 1;
            while (page < totalPage) {
                page++;
                response = getAuthorPaginated(page);
                authors.addAll(response.getBody());
            }
        }

        return authors;
    }

    private ResponseEntity<List<Author>> getAuthorPaginated(int page) {
        ResponseEntity<List<Author>> response = new RestTemplate()
                .exchange(wordpressEntryPoint + usersRequestUrl + "?per_page=100",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Author>>() {
                        });

        return response;
    }

    @SuppressWarnings({ "null" })
    private List<Page> getAllPages() {
        List<Page> pages = new ArrayList<Page>();

        ResponseEntity<List<Page>> response = getPagePaginated(1);
        pages.addAll(response.getBody());

        int totalPage = Integer.parseInt(response.getHeaders().get("X-Wp-Totalpages").get(0) + "");
        if (totalPage > 1) {
            int page = 1;
            while (page < totalPage) {
                page++;
                response = getPagePaginated(page);
                pages.addAll(response.getBody());
            }
        }

        return pages;
    }

    private ResponseEntity<List<Page>> getPagePaginated(int page) {
        ResponseEntity<List<Page>> response = new RestTemplate()
                .exchange(wordpressEntryPoint + pageRequestUrl + "?per_page=100&status=publish&page=" + page
                        + "&orderby=menu_order&order=asc&_fields=id,acf,author,date,modified,menu_order,parent,title,featured_media,slug",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Page>>() {
                        });

        return response;
    }

    @SuppressWarnings({ "null" })
    private List<Tag> getAvailableTags() {
        List<Tag> pages = new ArrayList<Tag>();

        ResponseEntity<List<Tag>> response = getTagsPaginated(1);
        pages.addAll(response.getBody());

        int totalPage = Integer.parseInt(response.getHeaders().get("X-Wp-Totalpages").get(0) + "");
        if (totalPage > 1) {
            int page = 1;
            while (page < totalPage) {
                page++;
                response = getTagsPaginated(page);
                pages.addAll(response.getBody());
            }
        }

        return pages;
    }

    private ResponseEntity<List<Tag>> getTagsPaginated(int page) {
        ResponseEntity<List<Tag>> response = new RestTemplate()
                .exchange(
                        wordpressEntryPoint + tagRequestUrl + "?_fields=id,name,count,slug&per_page=100&page=" + page
                                + "&orderby=id",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Tag>>() {
                        });

        return response;
    }

    @SuppressWarnings({ "null" })
    private List<Post> getAvailablePosts() {
        List<Post> pages = new ArrayList<Post>();

        ResponseEntity<List<Post>> response = getPostsPaginated(1);
        pages.addAll(response.getBody());

        int totalPage = Integer.parseInt(response.getHeaders().get("X-Wp-Totalpages").get(0) + "");
        if (totalPage > 1) {
            int page = 1;
            while (page < totalPage) {
                page++;
                response = getPostsPaginated(page);
                pages.addAll(response.getBody());
            }
        }

        return pages;
    }

    private ResponseEntity<List<Post>> getPostsPaginated(int page) {
        ResponseEntity<List<Post>> response = new RestTemplate()
                .exchange(
                        wordpressEntryPoint + postRequestUrl
                                + "?_fields=id,acf,author,categories,premium_percentage,myjss_category,title,excerpt,date,modified,serie,departement,featured_media,slug,sticky,tags,content&per_page=10&page="
                                + page,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Post>>() {
                        });

        return response;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void synchroniseWordpress() throws OsirisException {
        List<PublishingDepartment> departments = getAvailableDepartments();
        if (departments != null)
            for (PublishingDepartment department : departments)
                publishingDepartmentService.addOrUpdatePublishingDepartmentFromWordpress(department);

        List<Category> categories = getAvailableCategories();
        if (categories != null)
            for (Category category : categories)
                categoryService.addOrUpdateCategory(category);

        List<MyJssCategory> myJssCategories = getAvailableMyJssCategories();
        if (myJssCategories != null)
            for (MyJssCategory myJssCategory : myJssCategories)
                myJssCategoryService.addOrUpdateMyJssCategory(myJssCategory);

        List<Serie> series = getAvailableSeries();
        if (series != null)
            for (Serie serie : series)
                serieService.addOrUpdateSerie(serie);

        List<Tag> tags = getAvailableTags();
        if (tags != null)
            for (Tag tag : tags)
                tagService.addOrUpdateTagFromWordpress(tag);

        List<Author> authors = getAllAuthors();
        if (authors != null)
            for (Author author : authors)
                authorService.addOrUpdateAuthor(author);

        List<Media> medias = getAllMedia();
        if (medias != null)
            for (Media media : medias)
                mediaService.addOrUpdateMediaFromWordpress(media);

        List<Page> pages = getAllPages();
        if (pages != null)
            for (Page page : pages)
                pageService.addOrUpdatePageFromWordpress(page);

        List<Post> posts = getAvailablePosts();
        List<Integer> postFetchedId = new ArrayList<Integer>();
        if (posts != null)
            for (Post post : posts) {
                postService.addOrUpdatePostFromWordpress(post);
                postFetchedId.add(post.getId());
            }

        List<Post> postToCancel = postService.getPostExcludedId(postFetchedId);
        if (postToCancel != null)
            for (Post post : postToCancel)
                postService.cancelPost(post);
    }
}
