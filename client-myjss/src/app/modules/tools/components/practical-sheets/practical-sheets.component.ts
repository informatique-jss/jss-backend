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
  loading: boolean = false;
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
        this.postService.getFirstPostsByMyJssCategory(this.secondSearchText, undefined).subscribe({
          next: response => {
            this.loading = true;
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
          complete: () => this.loading = false
        });
      }
    });
    this.getTopPosts();
    this.getTendencyPosts();
    this.getMostSeenPosts();
  }

  practicalSheetsForm = this.formBuilder.group({});

  searchForPosts() {
    clearTimeout(this.debounce);
    this.loading = true;
    this.searchResults = [];
    this.debounce = setTimeout(() => {
      this.searchPosts();
    }, 500);
  }

  searchPosts() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    if (this.searchText && this.searchText.length > 2 && this.selectedMyJssCategory)
      this.searchObservableRef = this.postService.searchPostsByMyJssCategory(this.searchText, this.selectedMyJssCategory).subscribe(response => {
        if (!this.searchResults)
          this.searchResults = [];
        if (response)
          this.searchResults.push(...response);
      });
    this.loading = false;
  }

  searchForSecondPosts() {
    clearTimeout(this.debounce);
    this.secondSearchResults = [];
    this.debounce = setTimeout(() => {
      this.searchSecondPosts();
    }, 500);
  }

  searchSecondPosts() {
    if (this.secondSearchObservableRef)
      this.secondSearchObservableRef.unsubscribe();

    if (this.secondSearchText && this.secondSearchText.length > 2)
      this.secondSearchObservableRef = this.postService.searchPostsByMyJssCategory(this.secondSearchText, this.secondSelectedMyJssCategory).subscribe(response => {
        if (response)
          for (let post of response) {
            for (let category of post.myJssCategories) {
              if (category.id) {
                if (!this.secondSearchResults[category.id])
                  this.secondSearchResults[category.id] = [];
                this.secondSearchResults[category.id].push(post);
              }
            }
          }
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
      this.postService.getPostsByMyJssCategory(myJssCategory).subscribe(response => {
        if (response && myJssCategory.id) {
          this.postsByMyJssCategory[myJssCategory.id] = response;
          this.myJssCategoriesFullLoaded.push(myJssCategory.id);
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


  geRawText(text: string) {
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
