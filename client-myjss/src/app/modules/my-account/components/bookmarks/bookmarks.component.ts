import { Component, Input, OnInit } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { Author } from '../../../tools/model/Author';
import { Post } from '../../../tools/model/Post';
import { Tag } from '../../../tools/model/Tag';
import { PostService } from '../../../tools/services/post.service';

@Component({
  selector: 'app-bookmarks',
  templateUrl: './bookmarks.component.html',
  styleUrls: ['./bookmarks.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, NgbTooltipModule]

})
export class BookmarksComponent implements OnInit {

  @Input() mail: string | undefined;
  @Input() validationToken: string | null = null;
  bookmarkPosts: Post[] = [] as Array<Post>;

  constructor(private postService: PostService, private appService: AppService) { }

  ngOnInit() {
    this.fetchBookmarkPosts();
  }

  fetchBookmarkPosts() {
    this.postService.getBookmarkPostsByMail(0, 15).subscribe(data => {
      if (data)
        this.bookmarkPosts = data.content;

    });
  }

  unBookmarkPost(post: Post) {
    this.postService.deleteAssoMailPost(post).subscribe(response => {
      if (response)
        post.isBookmarked = false;
    });
  }

  bookmarkPost(post: Post) {
    this.postService.addAssoMailPost(post).subscribe(response => {
      if (response)
        post.isBookmarked = true;
    });
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "post/author/" + author.slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "post/tag/" + tag.slug, undefined);
  }
}
