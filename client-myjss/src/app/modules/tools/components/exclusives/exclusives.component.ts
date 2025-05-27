import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { PlatformService } from '../../../main/services/platform.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { Category } from '../../model/Category';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'exclusives',
  templateUrl: './exclusives.component.html',
  styleUrls: ['./exclusives.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class ExclusivesComponent implements OnInit {

  searchResults: Post[] = [];
  searchText: string = "";
  debounce: any;
  isLoading: boolean = false;
  searchObservableRef: Subscription | undefined;
  categoryExclusive!: Category;

  currentPage: number = 0;
  page: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;

  exclusivePostsForm!: FormGroup;

  @ViewChild('exclusivitySection') exclusivitySection!: ElementRef;

  constructor(private formBuilder: FormBuilder,
    private postService: PostService,
    private appService: AppService,
    private constantService: ConstantService,
    private platformService: PlatformService
  ) { }

  ngOnInit() {
    this.exclusivePostsForm = this.formBuilder.group({});
    this.categoryExclusive = this.constantService.getCategoryExclusivity();
    this.searchPosts(0);
  }

  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.5
        });
      });
  }

  searchForPosts() {
    if (this.searchText && this.searchText.length > 2) {
      clearTimeout(this.debounce);
      this.isLoading = true;
      this.searchResults = [];
      this.debounce = setTimeout(() => {
        this.searchPosts(0);
      }, 500);
    }
  }

  searchPosts(page: number) {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();
    if (this.categoryExclusive)
      this.searchObservableRef = this.postService.searchMyJssPostsByCategory(this.searchText, this.categoryExclusive, page, this.pageSize).subscribe(response => {
        if (response && response.content) {
          this.searchResults = response.content;
          this.currentPage = 0;
          this.totalPages = response.page.totalPages;
        }
        this.isLoading = false;
      });
  }

  clearSearch() {
    this.searchText = '';
    this.searchPosts(0);
  }

  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages) {
      this.page = pageNumber;
      this.searchPosts(pageNumber);
      if (this.exclusivitySection && this.exclusivitySection.nativeElement) {
        this.exclusivitySection.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    }
  }

  get pages(): number[] {
    const pagesToShow = 5;
    let start = Math.max(0, this.page - Math.floor(pagesToShow / 2));
    let end = start + pagesToShow;

    if (end > this.totalPages) {
      end = this.totalPages;
      start = Math.max(0, end - pagesToShow);
    }

    return Array.from({ length: end - start }, (_, i) => start + i);
  }

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "tag/" + tag.slug, undefined);
  }
}
