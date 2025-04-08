import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import Fuse from 'fuse.js';
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
  selectTest: string = "";
  searchResults: Post[] = [];

  secondSearchObservableRef: Subscription | undefined;
  secondSearchText: string = "";
  secondSelectedMyJssCategory: MyJssCategory = {} as MyJssCategory;
  secondSearchResults: { [key: number]: Array<Post> } = {};

  myJssCategories: MyJssCategory[] = [];
  myJssCategoriesFullLoaded: number[] = [];

  expandedCardIndex: number = -1;
  postsByMyJssCategory: { [key: number]: Array<Post> } = {};

  topPosts: Post[] = [];
  tendencyPosts: Post[] = [];
  mostSeenPosts: Post[] = [];
  page: number = 0;

  constructor(
    private formBuilder: FormBuilder,
    private postService: PostService,
    private appService: AppService,
    private myJssCategoryService: MyJssCategoryService
  ) { }

  ngOnInit() {
    this.myJssCategories.push({ id: -1, name: 'Toutes les categories', slug: "all-categories", categoryOrder: 1 });
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
    this.getTopPosts();
    this.getTendencyPosts();
    this.getMostSeenPosts();
  }

  practicalSheetsForm = this.formBuilder.group({});

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
      if (response)
        this.searchResults.push(...response);

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
      if (response) {
        for (let post of response) {
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

  clearSearch() {
    this.searchText = '';
    this.searchResults = [];
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
        if (response && myJssCategory.id) {
          this.postsByMyJssCategory[myJssCategory.id] = response;
          this.myJssCategoriesFullLoaded.push(myJssCategory.id);
        }
      });
    }
  }

  highlightText(text: string): string {
    if (!this.searchText) return text;
    const words = text.replace(/[^\w\s]/g, '').split(' ');
    const fuseResult = new Fuse(words, {
      includeScore: true,
      threshold: 0.35,
    });

    const result = fuseResult.search(this.searchText);
    if (result.length === 0) return text;

    let highlightedText = text;
    if (result.length > 0) {
      result.forEach((item: { item: any; }) => {
        const wordToHighlight = item.item;
        const regex = new RegExp(`\\b${wordToHighlight}\\b`, 'g');
        highlightedText = highlightedText.replace(regex, `<mark style="background-color: yellow;">${wordToHighlight}</mark>`);
      });
    }
    return highlightedText;
  }


  getRawText(text: string) {
    return text.replace(/<[^>]+>/g, '');
  }

  /**************** posts carousel ***********************/
  getTopPosts() {
    this.postService.getTopPosts(this.page).subscribe(response => {
      if (response && response.length > 0) {
        this.topPosts.push(...response);
      }
    });
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
