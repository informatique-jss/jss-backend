import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { AppService } from '../../../../libs/app.service';
import { MyJssCategory } from '../../model/MyJssCategory';
import { Post } from '../../model/Post';
import { MyJssCategoryService } from '../../services/myjss.category.service';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'practical-sheets',
  templateUrl: './practical-sheets.component.html',
  styleUrls: ['./practical-sheets.component.css'],
  standalone: false
})

export class PracticalSheetsComponent implements OnInit {

  debounce: any;
  isLoading: boolean = false;
  isLoading2: boolean = false;
  searchObservableRef: Subscription | undefined;
  searchText: string = "";
  selectedMyJssCategory: MyJssCategory | undefined;
  searchResults: Post[] = [];

  secondSearchObservableRef: Subscription | undefined;
  secondSearchText: string = "";
  secondSelectedMyJssCategory: MyJssCategory = {} as MyJssCategory;
  secondSearchResults: { [key: number]: Array<Post> } = {};

  myJssCategories: MyJssCategory[] = [];
  allMyJssCategories: MyJssCategory = { id: -1, name: 'Toutes les categories', slug: "all-categories", categoryOrder: 1 };
  myJssCategoriesFullLoaded: number[] = [];

  expandedCardIndex: number = -1;
  postsByMyJssCategory: { [key: number]: Array<Post> } = {};

  topPosts: Post[] = [];
  tendencyPosts: Post[] = [];
  mostSeenPosts: Post[] = [];
  page: number = 0;

  @ViewChild('searchInput') searchInput: ElementRef | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private postService: PostService,
    private appService: AppService,
    private myJssCategoryService: MyJssCategoryService,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit() {
    this.myJssCategories.push(this.allMyJssCategories);
    this.myJssCategoryService.getMyJssCategories().subscribe(response => {
      if (response) {
        this.myJssCategories.push(...response);
        this.selectedMyJssCategory = this.myJssCategories[0];
        this.secondSelectedMyJssCategory = this.myJssCategories[0];
        this.postService.getFirstPostsByMyJssCategory(this.secondSearchText, undefined).subscribe(response => {
          for (let post of response) {
            for (let category of post.myJssCategories) {
              if (category.id) {
                if (!this.postsByMyJssCategory[category.id])
                  this.postsByMyJssCategory[category.id] = [];
                this.postsByMyJssCategory[category.id].push(post);
              }
            }
          }
        },
        );
      }
    });

    let slug = this.activatedRoute.snapshot.params['slug'];
    if (slug)
      this.openTagSearch(slug, null);

    this.getTopPosts(undefined);
    this.getTendencyPosts();
    this.getMostSeenPosts();
  }

  practicalSheetsForm = this.formBuilder.group({});

  @ViewChild('searchResultsSection') searchResultsSection: ElementRef | undefined;
  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
    if (this.searchResultsSection && !this.searchResultsSection.nativeElement.contains(event.target)) {
      this.searchResults = [];
    }
  }

  searchForPosts() {
    if (this.searchText && this.searchText.length > 2 && this.selectedMyJssCategory) {
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

    this.searchObservableRef = this.postService.searchPostsByMyJssCategory(this.searchText, this.selectedMyJssCategory!, 0, 100).subscribe(response => {
      if (!this.searchResults)
        this.searchResults = [];
      if (response && response.content)
        this.searchResults.push(...response.content);

      this.isLoading = false;
    });
  }

  searchForSecondPosts() {
    if (this.secondSearchText && this.secondSearchText.length > 2) {
      clearTimeout(this.debounce);
      this.isLoading2 = true;
      this.secondSearchResults = [];
      this.debounce = setTimeout(() => {
        this.searchSecondPosts();
      }, 500);
    }
  }

  searchSecondPosts() {
    if (this.secondSearchObservableRef)
      this.secondSearchObservableRef.unsubscribe();

    this.secondSearchObservableRef = this.postService.searchPostsByMyJssCategory(this.secondSearchText, this.secondSelectedMyJssCategory, 0, 100000).subscribe(response => {
      if (response && response.content) {
        for (let post of response.content) {
          for (let category of post.myJssCategories) {
            if (category.id) {
              if (!this.secondSearchResults[category.id])
                this.secondSearchResults[category.id] = [];
              this.secondSearchResults[category.id].push(post);
            }
          }
        }
      }
      this.isLoading2 = false;
    })
  }

  clearSecondSearch() {
    this.secondSearchText = '';
    this.secondSearchResults = [];
  }

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }

  isExpanded(index: number): boolean {
    return this.expandedCardIndex === index;
  }

  toggleCard(myJssCategory: MyJssCategory): void {
    if (this.expandedCardIndex === myJssCategory.id)
      this.expandedCardIndex = -1;
    else if (myJssCategory.id)
      this.expandedCardIndex = myJssCategory.id;

    if (this.expandedCardIndex >= 0 && myJssCategory.id && this.myJssCategoriesFullLoaded.indexOf(myJssCategory.id) < 0) {
      this.postService.getPostsByMyJssCategory(myJssCategory, 0, 100000).subscribe(response => {
        if (response && myJssCategory.id && response.content) {
          this.postsByMyJssCategory[myJssCategory.id] = response.content;
          this.myJssCategoriesFullLoaded.push(myJssCategory.id);
        }
      });
    }
  }

  openTagSearch(tagSlug: string, event: any) {
    if (tagSlug && this.searchInput) {
      this.searchInput.nativeElement.scrollIntoView({ behavior: 'smooth', block: "center" })
      this.searchText = tagSlug;
    }
  }

  /**************** posts carousel ***********************/
  getTopPosts(category: MyJssCategory | undefined, doNotOverriteSelectedCategory: boolean = false) {
    if (category) {
      this.selectedMyJssCategory = category;
      this.postService.getTopPostByMyJssCategory(this.page, this.selectedMyJssCategory).subscribe(response => {
        if (response && response.content.length > 0) {
          this.topPosts = response.content;
        } else {
          this.getTopPosts(undefined, true);
        }
      });
    } else {
      if (!doNotOverriteSelectedCategory)
        this.selectedMyJssCategory = this.allMyJssCategories;
      this.postService.getTopPosts(this.page).subscribe(response => {
        if (response) {
          this.topPosts = response;
        }
      });
    }
  }

  getTendencyPosts() {
    this.postService.getTendencyPosts().subscribe(response => {
      if (response && response.length > 0) {
        this.tendencyPosts.push(...response);
      }
    });
  }
  getMostSeenPosts() {
    this.postService.getMostSeenPosts().subscribe(response => {
      if (response && response.length > 0) {
        this.mostSeenPosts.push(...response);
      }
    });
  }
}
