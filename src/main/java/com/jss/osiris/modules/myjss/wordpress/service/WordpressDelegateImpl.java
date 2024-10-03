package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.Media;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Page;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

@Service
public class WordpressDelegateImpl implements WordpressDelegate {
    @Value("${wordpress.entry.point}")
    private String wordpressEntryPoint;

    private String departmentRequestUrl = "/departement";
    private String myJssCategoryRequestUrl = "/myjss_category";
    private String categoryRequestUrl = "/categories";
    private String tagRequestUrl = "/tags";
    private String postRequestUrl = "/posts";
    private String mediaRequestUrl = "/media";
    private String usersRequestUrl = "/users";
    private String pageRequestUrl = "/pages";

    @Override
    public List<PublishingDepartment> getAvailableDepartments() {
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

    @Override
    public List<MyJssCategory> getAvailableMyJssCategories() {
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

    @Override
    public List<Category> getAvailableCategories() {
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

    @Override
    public Media getMedia(Integer id) {
        ResponseEntity<Media> response = new RestTemplate()
                .getForEntity(wordpressEntryPoint + mediaRequestUrl + "/" + id
                        + "?_fields=id,author,date,media_details,media_type,alt_text", Media.class);

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    @Override
    public Author getAuthor(Integer id) {
        ResponseEntity<Author> response = new RestTemplate()
                .getForEntity(wordpressEntryPoint + usersRequestUrl + "/" + id, Author.class);

        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    @Override
    @SuppressWarnings({ "null" })
    public List<Page> getAllPages() {
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

    @Override
    @SuppressWarnings({ "null" })
    public List<Tag> getAvailableTags() {
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

    @Override
    @SuppressWarnings({ "null" })
    public List<Post> getPosts(int page, Integer myJssCategoryId, Integer categoryId) {
        ResponseEntity<List<Post>> response = null;
        String params = "?_fields=id,acf,author,myjss_category,title,excerpt,date,modified,departement,featured_media,slug,sticky,tags,content&per_page=10&page="
                + page + "&orderby=date";
        if (myJssCategoryId != null)
            params += "&myjss_category=" + myJssCategoryId;
        if (categoryId != null)
            params += "&categories=" + categoryId;
        try {
            response = new RestTemplate()
                    .exchange(
                            wordpressEntryPoint + postRequestUrl + params,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<Post>>() {
                            });
        } catch (Exception e) {
            if (e.getMessage().contains("est plus grand que le nombre de pages"))
                return null;
            throw e;
        }

        return response.getBody();
    }
}
