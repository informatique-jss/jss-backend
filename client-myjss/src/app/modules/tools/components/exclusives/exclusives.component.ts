import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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

  allPosts: Post[] = [];
  paginatedSearchResults: Post[] = [];
  searchResults: Post[] = [];
  searchText: string = "";
  debounce: any;
  isLoading: boolean = false;
  searchObservableRef: Subscription | undefined;
  categoryExclusive: Category = this.constantService.getCategoryExclusivity();

  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;

  @ViewChild('exclusivitySection') exclusivitySection!: ElementRef;

  constructor(private formBuilder: FormBuilder,
    private postService: PostService,
    private appService: AppService,
    private constantService: ConstantService
  ) { }

  ngOnInit() {
    this.searchPosts();
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
      this.searchObservableRef = this.postService.searchMyJssPostsByCategory(this.searchText, this.categoryExclusive, 0, 100).subscribe(response => {
        if (response && response.content) {
          this.searchResults = response.content;
          this.currentPage = 0;
          this.totalPages = Math.ceil(this.searchResults.length / this.pageSize);
          this.updatePaginatedPosts();
        }
        this.isLoading = false;
      });
  }

  clearSearch() {
    this.searchText = '';
    this.currentPage = 0;
    this.searchPosts();
    this.updatePaginatedPosts();
  }

  updatePaginatedPosts() {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;

    this.paginatedSearchResults = this.searchResults.slice(start, end);
    this.totalPages = Math.ceil(this.searchResults.length / this.pageSize);
  }

  loadPosts(page: number) {
    this.currentPage = page;
    this.updatePaginatedPosts();
    if (this.exclusivitySection && this.exclusivitySection.nativeElement) {
      this.exclusivitySection.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "tag/" + tag.slug, undefined);
  }
}
