import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { AppRestService } from '../../services/appRest.service';
import { Author } from '../model/Author';
import { Category } from '../model/Category';
import { JssCategory } from '../model/JssCategory';
import { Media } from '../model/Media';
import { Post } from '../model/Post';
import { PublishingDepartment } from '../model/PublishingDepartment';
import { Tag } from '../model/Tag';


@Injectable({
  providedIn: 'root'
})
export class PostService extends AppRestService<Post> {

  private readonly defaultImage = '/assets/images/blog-img.jpg';
  private readonly authorImage = '/assets/images/author-img.jpg';


  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getTopPost(page: number, pageSize: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", pageSize), "posts/jss/top");
  }

  getPostsTendency() {
    return this.getList(new HttpParams(), "posts/jss/tendency");
  }

  getPostBySlug(slug: string) {
    return this.get(new HttpParams().set("slug", slug), "posts/slug");
  }

  getPostSerieBySlug(slug: string) {
    return this.getList(new HttpParams().set("slug", slug), "posts/serie/slug");
  }

  completeMediaInPosts(posts: Post[]) {
    posts.forEach(post => {
      if (!post.media) {
        post.media = {} as Media;
        post.media.id = 0;
        post.media.date = new Date();
        post.media.media_type = 'image';
        post.media.alt_text = 'Logo JSS';
        post.media.file = 'JSS-logo.png';
        post.media.urlFull = 'assets/images/logo.png';
        post.media.urlLarge = 'assets/images/logo.png';
        post.media.urlMedium = 'assets/images/logo.png';
        post.media.urlMediumLarge = 'assets/images/logo.png';
        post.media.urlThumbnail = 'assets/images/logo.png';
        post.media.length = 0;
      }
    });
  }

  getTopPostByJssCategory(page: number, jssCategory: JssCategory) {
    return this.getList(new HttpParams().set("page", page).set("categoryId", jssCategory.id), "posts/top/jss-category");
  }

  getTopPostByTag(page: number, tag: Tag) {
    return this.getList(new HttpParams().set("page", page).set("tagId", tag.id), "posts/top/tag");
  }

  getIleDeFranceTopPost(page: number, size: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size), "posts/top/department/all");
  }

  getTopPostByDepartment(page: number, size: number, department: PublishingDepartment) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size).set("departmentId", department.id), "posts/top/department");
  }

  getTopPostByAuthor(page: number, author: Author) {
    return this.getList(new HttpParams().set("page", page).set("authorId", author.id), "posts/top/author");
  }

  getTopPostInterview(page: number) {
    return this.getList(new HttpParams().set("page", page), "posts/top/interview");
  }

  getTopPostPodcast(page: number) {
    return this.getList(new HttpParams().set("page", page), "posts/top/podcast");
  }

  getNextArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/next");
  }

  getPreviousArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/previous");
  }



  private createMockAuthor(id: number, name: string): Author {
    return {
      id,
      name,
      slug: name.toLowerCase().replace(/ /g, '-'),
      description: `Description de ${name}`,
      avatar_url_size_24: this.authorImage,
      avatar_url_size_48: this.authorImage,
      avatar_url_size_96: this.authorImage,
    };
  }

  private createMockMedia(id: number, alt: string, author?: Author): Media {
    return {
      id,
      fullAuthor: author,
      date: new Date(),
      media_type: 'image',
      alt_text: alt,
      file: 'blog-img.jpg',
      urlFull: this.defaultImage,
      urlLarge: this.defaultImage,
      urlMedium: this.defaultImage,
      urlMediumLarge: this.defaultImage,
      urlThumbnail: this.defaultImage,
      length: 0
    };
  }

  private createMockCategory(id: number, name: string): Category {
    return {
      id,
      name,
      slug: name.toLowerCase().replace(/ /g, '-'),
      count: 0
    };
  }

  private createMockJssCategory(id: number, name: string, color: string): JssCategory {
    return {
      id,
      name,
      slug: name.toLowerCase().replace(/ /g, '-'),
      color,
      picture: this.createMockMedia(id + 100, `${name} logo`),
      count: 0,
      categoryOrder: id
    };
  }

  private createMockDepartment(id: number, name: string, code: string): PublishingDepartment {
    return {
      id,
      name,
      code
    };
  }

  private createMockTag(id: number, name: string): Tag {
    return {
      id,
      name,
      slug: name.toLowerCase().replace(/ /g, '-'),
      count: 0
    };
  }

  getPosts(): Observable<Post[]> {
    const author = this.createMockAuthor(1, 'Jean Dupont');

    const posts: Post[] = [
      {
        id: 1,
        titleText: 'Comment booster votre productivité avec Angular',
        excerptText: 'Découvrez nos astuces pour travailler plus efficacement avec Angular 19.',
        contentText: 'Contenu complet de l’article...',
        date: new Date('2025-04-10'),
        modified: new Date('2025-04-12'),
        slug: 'booster-productivite-angular',
        podcastUrl: '',
        videoUrl: '',
        sticky: false,
        mediaTimeLength: 180,
        relatedPosts: [],
        fullAuthor: author,
        jssCategories: [this.createMockJssCategory(1, 'Angular', '#dd0031')],
        postCategories: [this.createMockCategory(1, 'Développement')],
        departments: [this.createMockDepartment(1, 'Équipe éditoriale', 'EDIT')],
        postTags: [this.createMockTag(1, 'angular'), this.createMockTag(2, 'performance')],
        media: this.createMockMedia(1, 'Image d’article', author),
        isPremium: false
      },
      {
        id: 2,
        titleText: 'Comprendre RxJS avec des exemples simples',
        excerptText: 'Apprenez à utiliser les observables et les opérateurs comme un pro.',
        contentText: 'Contenu complet de l’article...',
        date: new Date('2025-04-08'),
        modified: new Date('2025-04-10'),
        slug: 'comprendre-rxjs-exemples',
        podcastUrl: '',
        videoUrl: '',
        sticky: false,
        mediaTimeLength: 220,
        relatedPosts: [],
        fullAuthor: author,
        jssCategories: [this.createMockJssCategory(2, 'RxJS', '#b7178c')],
        postCategories: [this.createMockCategory(2, 'Programmation')],
        departments: [this.createMockDepartment(2, 'Département tech', 'TECH')],
        postTags: [this.createMockTag(3, 'rxjs')],
        media: this.createMockMedia(2, 'RxJS cover image', author),
        isPremium: true
      },
      {
        id: 3,
        titleText: 'State management dans Angular : NgRx ou Signal ?',
        excerptText: 'Un comparatif complet entre les deux approches.',
        contentText: 'Contenu complet de l’article...',
        date: new Date('2025-03-30'),
        modified: new Date('2025-04-02'),
        slug: 'state-management-angular',
        podcastUrl: '',
        videoUrl: '',
        sticky: true,
        mediaTimeLength: 310,
        relatedPosts: [],
        fullAuthor: author,
        jssCategories: [this.createMockJssCategory(3, 'State', '#00796b')],
        postCategories: [this.createMockCategory(3, 'Architecture')],
        departments: [this.createMockDepartment(3, 'Pôle Angular', 'ANG')],
        postTags: [this.createMockTag(4, 'state'), this.createMockTag(5, 'ngrx')],
        media: this.createMockMedia(3, 'State management image', author),
        isPremium: false
      },
      {
        id: 4,
        titleText: 'Créer un design system scalable en Angular',
        excerptText: 'Les bonnes pratiques pour créer des composants réutilisables.',
        contentText: 'Contenu complet de l’article...',
        date: new Date('2025-03-25'),
        modified: new Date('2025-03-27'),
        slug: 'design-system-angular',
        podcastUrl: '',
        videoUrl: '',
        sticky: false,
        mediaTimeLength: 195,
        relatedPosts: [],
        fullAuthor: author,
        jssCategories: [this.createMockJssCategory(4, 'Design System', '#3f51b5')],
        postCategories: [this.createMockCategory(4, 'Design')],
        departments: [this.createMockDepartment(4, 'UX/UI', 'UX')],
        postTags: [this.createMockTag(6, 'design-system')],
        media: this.createMockMedia(4, 'Design System article image', author),
        isPremium: true
      }
    ];

    return of(posts);
  }


}
