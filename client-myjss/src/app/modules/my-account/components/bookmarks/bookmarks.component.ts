import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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
  idReadingFolder: number | undefined;

  constructor(private postService: PostService, private appService: AppService,
    private activeRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    if (this.activeRoute.snapshot.params['idReadingFolder'])
      this.idReadingFolder = this.activeRoute.snapshot.params['idReadingFolder'];
    this.fetchBookmarkPostsByReadingFolder();
  }

  fetchBookmarkPostsByReadingFolder() {
    if (this.idReadingFolder)
      this.postService.getBookmarkPostsByMailAndReadingFolders(this.idReadingFolder, 0, 15).subscribe(data => {
        if (data)
          this.bookmarkPosts = data.content;
      });
  }

  unBookmarkPost(post: Post) {
    this.postService.deleteBookmarkPost(post).subscribe(response => {
      if (response)
        post.isBookmarked = false;
    });
  }

  bookmarkPost(post: Post) {
    if (this.idReadingFolder)
      this.postService.addBookmarkPost(post, this.idReadingFolder).subscribe(response => {
        if (response)
          post.isBookmarked = true;
      });
  }

  openPost(post: Post, event: any) {
    this.appService.openJssRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openJssRoute(event, "post/author/" + author.slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openJssRoute(event, "post/tag/" + tag.slug, undefined);
  }

}
