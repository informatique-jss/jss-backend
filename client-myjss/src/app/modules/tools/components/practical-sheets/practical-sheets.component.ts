import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
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
})

export class PracticalSheetsComponent implements OnInit {

  debounce: any;
  postsInitDropdown: Post[] | undefined;
  searchResults: Post[] | undefined;
  displayLoadMoreButton: boolean = true;
  searchText: string = "";
  searchObservableRef: Subscription | undefined;
  secondSearchObservableRef: Subscription | undefined;
  searchInProgress: boolean = false;

  secondSearchText: string = "";
  secondSelectedMyJssCategory = {} as MyJssCategory;

  practicalSheetsForm = this.formBuilder.group({});
  selectedValue: MyJssCategory = {} as MyJssCategory;
  myJssCategories: MyJssCategory[] = [] as Array<MyJssCategory>;
  selectedMyJssCategory: MyJssCategory = {} as MyJssCategory;

  fetchedCategories: { [key: number]: boolean } = {};
  expandedCardIndex: number | null = null;
  postsByMyJssCategory: { [key: number]: Array<Post> } = {};

  constructor(
    private formBuilder: FormBuilder,
    private postService: PostService,
    private appService: AppService,
    private myJssCategoryService: MyJssCategoryService
  ) { }
  ngOnInit() {
    this.myJssCategoryService.getMyJssCategories().subscribe(response => {
      if (response) {
        this.myJssCategories = response;
        this.postService.getFirstPostsByMyJssCategory(this.secondSearchText, this.secondSelectedMyJssCategory).subscribe(response => {
          if (response) {
            this.fillPostsByCategory(response);
            console.log(this.postsByMyJssCategory);
          }
        });
        this.fillFetchedCategories();
      }
    });
  }

  fillPostsByCategory(postsInit: Post[]) {
    postsInit.forEach(post => {
      post.myJssCategories.forEach(category => {
        if (!this.postsByMyJssCategory[category.id])
          this.postsByMyJssCategory[category.id] = [];
        this.postsByMyJssCategory[category.id].push(post);
      });
    });
  }

  fillFetchedCategories() {
    this.myJssCategories.forEach(category => {
      this.fetchedCategories[category.id] = false;
    });
    console.log(this.fetchedCategories);
  }

  selectMyJssCategory(myJssCategory: MyJssCategory, inputKey: string) {
    if (inputKey === 'input1') {
      this.selectedMyJssCategory = myJssCategory;
      this.searchForPosts();
    } else if (inputKey === 'input2') {
      this.secondSelectedMyJssCategory = myJssCategory;
      this.filterForPosts();
    }
  }

  searchForPosts() {
    this.searchInProgress = true;
    clearTimeout(this.debounce);
    this.searchResults = [];
    this.debounce = setTimeout(() => {
      this.searchPosts();
    }, 500);
  }

  searchPosts() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    this.searchInProgress = true;
    if (this.searchText && this.searchText.length > 2 || this.selectedMyJssCategory.id)
      this.searchObservableRef = this.postService.searchPostsByMyJssCategory(this.searchText, this.selectedMyJssCategory).subscribe(response => {
        if (!this.searchResults)
          this.searchResults = [];
        if (response)
          this.searchResults.push(...response);
        this.searchInProgress = false;

        if (!response || response.length == 0)
          this.displayLoadMoreButton = false;
      })
  }
  clearSearch() {
    this.searchText = '';
    this.searchForPosts();
  }
  clearFilter() {
    this.secondSearchText = '';
    this.filterForPosts();
  }

  filterForPosts() {
    this.searchInProgress = true;
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.filterPosts();
    }, 500);
  }

  filterPosts() {
    if ((this.secondSearchText && this.secondSearchText.length > 2) || this.secondSelectedMyJssCategory.id) {
      this.secondSearchObservableRef = this.postService.getFirstPostsByMyJssCategory(this.secondSearchText, this.secondSelectedMyJssCategory).subscribe(response => {
        if (response) {
          this.postsInitDropdown = response;
        }
      });

      if (this.expandedCardIndex != null) {
        this.postService.getPostsByMyJssCategory(this.secondSearchText, this.myJssCategories[this.expandedCardIndex]).subscribe(response => {
          if (response && this.expandedCardIndex != null)
            this.postsByMyJssCategory[this.expandedCardIndex] = response;
        });
      }

    }
    else if (this.secondSearchText = "") {
      this.secondSearchObservableRef = this.postService.getFirstPostsByMyJssCategory(this.secondSearchText, this.secondSelectedMyJssCategory).subscribe(response => {
        if (response) {
          this.postsInitDropdown = response;
        }
      });
      if (this.expandedCardIndex != null) {
        this.postService.getPostsByMyJssCategory(this.secondSearchText, this.myJssCategories[this.expandedCardIndex]).subscribe(response => {
          if (response && this.expandedCardIndex != null)
            this.postsByMyJssCategory[this.expandedCardIndex] = response;
        });
      }
    }
  }

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }

  isExpanded(index: number): boolean {
    return this.expandedCardIndex === index;
  }

  toggleCard(myJssCategory: MyJssCategory): void {
    if (this.expandedCardIndex === myJssCategory.id) {
      this.expandedCardIndex = null;
    } else {
      this.expandedCardIndex = myJssCategory.id;
    }

    if (this.expandedCardIndex !== null && this.postsByMyJssCategory[myJssCategory.id] && !this.fetchedCategories[myJssCategory.id]) {
      this.postService.getPostsByMyJssCategory(this.secondSearchText, myJssCategory).subscribe(response => {
        if (response) {
          this.postsByMyJssCategory[myJssCategory.id] = response;
          this.fetchedCategories[myJssCategory.id] = true;
        }
      });
    }
  }

  truncateChar(text: string, charlimit: number): string {
    if (!text || text.length <= charlimit) {
      return text;
    }

    let without_html = text.replace(/<(?:.|\n)*?>/gm, '');
    let shortened = without_html.substring(0, charlimit) + "...";
    return shortened;
  }

}
