import { Directive, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { isInt } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Responsable } from '../../model/Responsable';
import { Tag } from '../../model/Tag';
import { LoginService } from '../../services/login.service';
import { PostService } from '../../services/post.service';

@Directive()
export abstract class GenericHubComponent<T extends { id: number }> implements OnInit {

  debounce: any;
  searchObservableRef: Subscription | undefined;
  @Input() selectedEntityType: T | undefined;
  isDisplayNewPosts: boolean = false;
  linkedTags: Tag[] = [] as Array<Tag>;
  mostSeenPostsByEntityType: Post[] = [] as Array<Post>;
  postsByEntityType: { [key: number]: Array<Post> } = {};

  tagsByEntityType: Tag[] = [] as Array<Tag>;
  pageSize: number = 15;
  page: number = 0;
  totalPage: number = 0;
  searchText: string = "";
  searchResults: Post[] = [] as Array<Post>;
  hubForm!: FormGroup;
  currentUser: Responsable | undefined;

  constructor(protected appService: AppService,
    protected formBuilder: FormBuilder,
    protected activeRoute: ActivatedRoute,
    protected postService: PostService,
    protected loginService: LoginService,
    protected router: Router,
  ) { }

  ngOnInit() {
    if (this.activeRoute.snapshot.firstChild)
      this.initPageNumber(this.activeRoute.snapshot.firstChild!.params['page-number']);
    this.router.events.subscribe(url => {
      if ((url instanceof NavigationStart)) {
        this.initPageNumber(url.url)
      }
    });
    this.refresh();
  }

  initPageNumber(url: string) {
    let urlParam = url.split("/");
    this.page = 0;
    if (isInt(urlParam[urlParam.length - 1]))
      this.page = parseInt(urlParam[urlParam.length - 1]);
    this.refresh();
  }

  refresh() {
    if (this.activeRoute.snapshot.params['isDisplayNews'])
      this.isDisplayNewPosts = this.activeRoute.snapshot.params['isDisplayNews'];
    this.hubForm = this.formBuilder.group({});
    this.fetchPosts();
    this.fetchTags();
    this.fetchMostSeenPosts();
    this.loginService.getCurrentUser().subscribe(user => {
      if (user)
        this.currentUser = user;
    });
  }

  abstract getAllPostByEntityType(selectedEntityType: T, page: number, pageSize: number, searchText: string, isDisplayNewPosts: boolean): Observable<PagedContent<Post>>;
  abstract getAllTagByEntityType(selectedEntityType: T, isDisplayNewPosts: boolean): Observable<Array<Tag>>;
  abstract getMostSeenPostByEntityType(selectedEntityType: T, page: number, pageSize: number): Observable<PagedContent<Post>>

  getRouterUrl() {
    return this.router.url.split('/').slice(0, -1).join('/');
  }

  fetchPosts() {
    if (this.selectedEntityType && this.selectedEntityType.id && (!this.searchText || this.searchText.length > 2))
      this.getAllPostByEntityType(this.selectedEntityType, this.page, this.pageSize, this.searchText, this.isDisplayNewPosts).subscribe(data => {
        if (data && this.selectedEntityType && !this.searchText) {
          this.postsByEntityType[this.selectedEntityType.id] = data.content;
        }
        if (data && this.searchText && this.searchText.length > 2)
          this.searchResults = data.content;
        this.totalPage = data.page.totalPages;
      });
  }

  fetchTags() {
    if (this.selectedEntityType)
      this.getAllTagByEntityType(this.selectedEntityType, this.isDisplayNewPosts).subscribe(data => {
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
    this.page = 0;
    clearTimeout(this.debounce);
    this.searchResults = [];

    this.debounce = setTimeout(() => {
      this.fetchPosts();
      this.fetchTags();
      this.fetchMostSeenPosts();
    }, 500);
  }

  clearSearch() {
    this.searchText = '';
    this.searchResults = [];
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
}
