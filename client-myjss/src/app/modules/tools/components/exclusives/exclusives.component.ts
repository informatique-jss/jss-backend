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
  categoryExclusive: Category | undefined;

  constructor(private formBuilder: FormBuilder,
    private postService: PostService,
    private appService: AppService,
    private constantService: ConstantService
  ) { }

  ngOnInit() {
    this.categoryExclusive = this.constantService.getCategoryExclusivity();
    console.log(this.categoryExclusive);
    this.postService.searchMyJssPostsByCategory(this.searchText, this.categoryExclusive, 0, 100).subscribe(response => {
      if (response)
        this.exclusivePosts = response.content;
    });
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
        if (!this.searchResults)
          this.searchResults = [];
        if (response && response.content)
          this.searchResults = response.content;

        this.isLoading = false;
      });
  }

  clearSearch() {
    this.searchText = '';
    this.searchResults = [];
  }

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "tag/" + tag.slug, undefined);
  }
}
