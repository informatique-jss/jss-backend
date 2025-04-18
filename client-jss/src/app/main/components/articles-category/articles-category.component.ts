import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { JssCategoryService } from '../../services/jss.category.service';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'articles-category',
  templateUrl: './articles-category.component.html',
  styleUrls: ['./articles-category.component.css'],
  standalone: false
})
export class ArticlesCategoryComponent implements OnInit {

  page: number = 0;
  posts: Post[] = [];
  categories: JssCategory[] = [];
  currentCategory: JssCategory | null = null;
  slug: string = "";
  displayLoadMoreButton: boolean = true;

  getTimeReading = getTimeReading;

  constructor(
    private postService: PostService,
    private jssCategoryService: JssCategoryService,
    private activatedRoute: ActivatedRoute,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    if (this.slug && this.slug.length > 0)
      this.jssCategoryService.getAvailableJssCategories().subscribe(categories => {
        if (categories && categories.length > 0) {
          this.categories = categories;
          for (let category of this.categories) {
            if (category.slug == this.slug) {
              this.currentCategory = category;
              break;
            }
          }
          if (this.currentCategory)
            this.fetchNextPosts();
        }
      })
  }

  getNextPosts() {
    this.page++;
    this.fetchNextPosts();
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }

  fetchNextPosts() {
    if (this.currentCategory)
      this.postService.getTopPostByJssCategory(this.page, 10, this.currentCategory).subscribe(pagedPosts => {
        if (pagedPosts.content && pagedPosts.content.length > 0) {
          this.posts.push(...pagedPosts.content);
        } else {
          this.displayLoadMoreButton = false;
        }
      });
  }
}
