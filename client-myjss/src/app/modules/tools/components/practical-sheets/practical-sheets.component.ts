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

  searchObservableRef: Subscription | undefined;
  searchText: string = "";
  selectedMyJssCategory: MyJssCategory = {} as MyJssCategory;
  searchResults: Post[] = [];

  secondSearchObservableRef: Subscription | undefined;
  secondSearchText: string = "";
  secondSelectedMyJssCategory = {} as MyJssCategory;
  secondSearchResults: { [key: number]: Array<Post> } = {};

  myJssCategories: MyJssCategory[] = [];
  myJssCategoriesFullLoaded: number[] = [];

  expandedCardIndex: number = -1;
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
        // Add "Toutes les catégories"
        this.myJssCategories.push({ id: null, categoryOrder: -1, name: 'Toutes les categories', slug: "all-categories" });
        this.myJssCategories = response;
        this.postService.getFirstPostsByMyJssCategory(this.secondSearchText, undefined).subscribe(response => {
          if (response) {
            for (let post of response) {
              for (let category of post.myJssCategories) {
                if (category.id) {
                  if (!this.postsByMyJssCategory[category.id])
                    this.postsByMyJssCategory[category.id] = [];
                  this.postsByMyJssCategory[category.id].push(post);
                }
              }
            }
          }
        });
      }
    });
  }

  practicalSheetsForm = this.formBuilder.group({});

  selectMyJssCategory(myJssCategory: MyJssCategory) {
    this.selectedMyJssCategory = myJssCategory;
    this.searchForPosts();
  }

  selectSecondMyJssCategory(myJssCategory: MyJssCategory) {
    this.secondSelectedMyJssCategory = myJssCategory;
    this.searchForSecondPosts();
  }

  searchForPosts() {
    clearTimeout(this.debounce);
    this.searchResults = [];
    this.debounce = setTimeout(() => {
      this.searchPosts();
    }, 500);
  }

  searchForSecondPosts() {
    clearTimeout(this.debounce);
    this.secondSearchResults = [];
    this.debounce = setTimeout(() => {
      this.searchSecondPosts();
    }, 500);
  }

  searchPosts() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    if (this.searchText && this.searchText.length > 2)
      this.searchObservableRef = this.postService.searchPostsByMyJssCategory(this.searchText, this.selectedMyJssCategory).subscribe(response => {
        if (!this.searchResults)
          this.searchResults = [];
        if (response)
          this.searchResults.push(...response);
      })
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
    const searchTerm = this.searchText.toLowerCase();
    const index = text.toLowerCase().indexOf(searchTerm);

    if (index === -1) return text;

    const beforeMatch = text.substring(0, index);
    const match = text.substring(index, index + searchTerm.length);
    const afterMatch = text.substring(index + searchTerm.length);

    return `${beforeMatch}<span style="color: #FFFF00">${match}</span>${afterMatch}`;
  }
}
