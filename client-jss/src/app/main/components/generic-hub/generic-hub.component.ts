import { Directive, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';

@Directive()
export abstract class GenericHubComponent<T extends { id: number }> implements OnInit {

  debounce: any;
  searchObservableRef: Subscription | undefined;
  @Input() selectedEntityType: T | undefined;
  linkedTags: Tag[] = [] as Array<Tag>;
  mostSeenPostsByEntityType: Post[] = [] as Array<Post>;
  postsByEntityType: { [key: number]: Array<Post> } = {};
  postsByEntityTypeFullLoaded: number[] = [];

  tagsByEntityType: Tag[] = [] as Array<Tag>;
  pageSize: number = 15;
  page: number = 0;
  totalPage: number = 0;
  searchText: string = "";
  searchResults: Post[] = [] as Array<Post>;

  constructor(protected appService: AppService, protected formBuilder: FormBuilder) { }

  ngOnInit() {
    this.fetchPosts(0);
    this.fetchTags();
    this.fetchMostSeenPosts();
  }

  hubForm = this.formBuilder.group({});

  abstract getAllPostByEntityType(selectedEntityType: T, page: number, pageSize: number, searchText: string): Observable<PagedContent<Post>>;
  abstract getAllTagByEntityType(selectedEntityType: T): Observable<Array<Tag>>;
  abstract getMostSeenPostByEntityType(selectedEntityType: T, page: number, pageSize: number): Observable<PagedContent<Post>>

  fetchPosts(page: number) {
    if (this.selectedEntityType && this.selectedEntityType.id && (this.postsByEntityTypeFullLoaded.indexOf(this.selectedEntityType.id) < 0 || (this.searchText && this.searchText.length > 2)))
      this.getAllPostByEntityType(this.selectedEntityType, page, this.pageSize, this.searchText).subscribe(data => {
        if (data && this.selectedEntityType && !this.searchText) {
          this.postsByEntityType[this.selectedEntityType.id] = data.content;
          this.postsByEntityTypeFullLoaded.push(this.selectedEntityType.id);
        }
        if (data && this.searchText && this.searchText.length > 2)
          this.searchResults = data.content;
        this.totalPage = data.page.totalPages;
      });
  }

  fetchTags() {
    if (this.selectedEntityType)
      this.getAllTagByEntityType(this.selectedEntityType).subscribe(data => {
        if (data)
          this.tagsByEntityType = data;
      });
  }

  fetchMostSeenPosts() {
    if (this.selectedEntityType)
      this.getMostSeenPostByEntityType(this.selectedEntityType, this.page, this.pageSize).subscribe(data => {
        if (data)
          this.mostSeenPostsByEntityType = data.content;
      });
  }

  searchForPosts() {
    if (this.searchText && this.searchText.length > 2) {
      clearTimeout(this.debounce);
      this.searchResults = [];

      this.debounce = setTimeout(() => {
        this.fetchPosts(0);
      }, 500);
    }
  }

  clearSearch() {
    this.searchText = '';
    this.searchResults = [];
  }

  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPage) {
      this.page = pageNumber;
      this.fetchPosts(pageNumber);
    }
  }

  get pages(): number[] {
    const pagesToShow = 5;
    let start = Math.max(0, this.page - Math.floor(pagesToShow / 2));
    let end = start + pagesToShow;

    if (end > this.totalPage) {
      end = this.totalPage;
      start = Math.max(0, end - pagesToShow);
    }

    return Array.from({ length: end - start }, (_, i) => start + i);
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "post/category/" + category.slug, undefined);
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "post/author/" + author.slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "post/tag/" + tag.slug, undefined);
  }
}
