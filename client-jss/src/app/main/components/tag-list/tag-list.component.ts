import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { MyJssCategory } from '../../model/MyJssCategory';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';

@Component({
  selector: 'app-tag-list',
  templateUrl: './tag-list.component.html',
  styleUrls: ['./tag-list.component.css']
})
export class TagListComponent implements OnInit {

  page: number = 0;
  posts: Post[] = [];
  currentTag: Tag | null = null;
  slug: string = "";
  displayLoadMoreButton: boolean = true;

  getTimeReading = getTimeReading;

  constructor(
    private postService: PostService,
    private tagService: TagService,
    private activatedRoute: ActivatedRoute,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    if (this.slug && this.slug.length > 0)
      this.tagService.getTagBySlug(this.slug).subscribe(tag => {
        this.currentTag = tag;
        this.fetchNextPosts();
      })
  }

  getNextPosts() {
    this.page++;
    this.fetchNextPosts();
  }

  openCategoryPosts(category: MyJssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }

  fetchNextPosts() {
    if (this.currentTag)
      this.postService.getTopPostByTag(this.page, this.currentTag).subscribe(posts => {
        if (posts && posts.length > 0) {
          this.posts.push(...posts);
        } else {
          this.displayLoadMoreButton = false;
        }
      });
  }
}
