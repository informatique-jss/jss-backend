import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../../../environments/environment';
import { getRawTextFromHtml } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { Post } from '../../../tools/model/Post';
import { ReadingFolder } from '../../../tools/model/ReadingFolder';
import { PostService } from '../../../tools/services/post.service';
import { ReadingFolderService } from '../../../tools/services/reading.folder.service';

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
  frontendJssUrl = environment.frontendJssUrl;
  readingFolder: ReadingFolder | undefined;

  getRawTextFromHtml = getRawTextFromHtml;

  constructor(private postService: PostService, private appService: AppService,
    private activeRoute: ActivatedRoute, private readingFolderService: ReadingFolderService
  ) { }

  ngOnInit() {
    if (this.activeRoute.snapshot.params['idReadingFolder']) {
      this.idReadingFolder = this.activeRoute.snapshot.params['idReadingFolder'];
      if (this.idReadingFolder)
        this.readingFolderService.getReadingFolder(this.idReadingFolder).subscribe(response => {
          this.readingFolder = response;
        })
    }
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
}
