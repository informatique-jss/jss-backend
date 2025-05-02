import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { jarallax } from 'jarallax';
import { Subscription } from 'rxjs';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { Category } from '../../model/Category';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'exclusives',
  templateUrl: './exclusives.component.html',
  styleUrls: ['./exclusives.component.css'],
  standalone: false
})
export class ExclusivesComponent implements OnInit {

  exclusivePosts: Post[] = [];
  searchText: string = "";
  searchResults: Post[] = [];
  debounce: any;
  isLoading: boolean = false;
  searchObservableRef: Subscription | undefined;
  categoryExclusive: Category = this.constantService.getCategoryExclusivity();

  currentPage: number = 0;
  pageSize: number = 6;
  totalPages: number = 0;
  totalPagesInit: number = 0;

  constructor(private formBuilder: FormBuilder,
    private postService: PostService,
    private appService: AppService,
    private constantService: ConstantService
  ) { }

  ngOnInit() {
    this.loadPosts(this.currentPage, this.pageSize);
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  exclusivePostsForm = this.formBuilder.group({});

  searchForPosts() {
    if (this.searchText && this.searchText.length > 2) {
      clearTimeout(this.debounce);
      this.isLoading = true;
      this.searchResults = [];
      this.debounce = setTimeout(() => {
        this.searchPosts();
      }, 500);
    }
  }

  searchPosts() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();
    if (this.categoryExclusive)
      this.searchObservableRef = this.postService.searchMyJssPostsByCategory(this.searchText, this.categoryExclusive, 0, this.pageSize).subscribe(response => {
        if (!this.searchResults)
          this.searchResults = [];
        if (response && response.content) {
          this.searchResults = response.content;
          this.totalPages = response.page.totalPages;
          this.currentPage = response.page.pageNumber;
          this.pageSize = response.page.pageSize;
        }
        this.isLoading = false;
      });
  }

  clearSearch() {
    this.searchText = '';
    this.searchResults = [];
    this.totalPages = this.totalPagesInit;
    this.currentPage = 0;
  }

  loadPosts(page: number, size: number) {
    if (this.categoryExclusive)
      this.postService.searchMyJssPostsByCategory(this.searchText, this.categoryExclusive, page, size)
        .subscribe(response => {
          if (response) {
            this.exclusivePosts = response.content;
            this.totalPages = response.page.totalPages;
            this.totalPagesInit = response.page.totalPages;
            this.currentPage = response.page.pageNumber;
            this.pageSize = response.page.pageSize;
          }
        });
  }

  changePageSize(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.pageSize = +value;
    this.loadPosts(0, this.pageSize);
  }

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "tag/" + tag.slug, undefined);
  }
}
