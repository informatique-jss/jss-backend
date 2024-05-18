import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { getTimeReading } from '../../../libs/FormatHelper';
import { MyJssCategory } from '../../model/MyJssCategory';
import { Post } from '../../model/Post';
import { MyJssCategoryService } from '../../services/myjss.category.service';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'articles-category',
  templateUrl: './articles-category.component.html',
  styleUrls: ['./articles-category.component.css']
})
export class ArticlesCategoryComponent implements OnInit {

  page: number = 1;
  posts: Post[] = [];
  categories: MyJssCategory[] = [];
  currentCategory: MyJssCategory | null = null;
  slug: string = "";
  displayLoadMoreButton: boolean = true;

  getTimeReading = getTimeReading;

  constructor(
    private postService: PostService,
    private myJssCategoryService: MyJssCategoryService,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    if (this.slug && this.slug.length > 0)
      this.myJssCategoryService.getAvailableMyJssCategories().subscribe(categories => {
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

  fetchNextPosts() {
    if (this.currentCategory)
      this.postService.getTopPostByMyJssCategory(this.page, this.currentCategory).subscribe(posts => {
        if (posts && posts.length > 0) {
          this.posts.push(...posts);
        } else {
          this.displayLoadMoreButton = false;
        }
      });
  }
}
