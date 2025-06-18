import { Component, Input, OnInit } from '@angular/core';
import { NgbPopover, NgbPopoverModule } from '@ng-bootstrap/ng-bootstrap';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { Post } from '../../model/Post';
import { ReadingFolder } from '../../model/ReadingFolder';
import { Responsable } from '../../model/Responsable';
import { LoginService } from '../../services/login.service';
import { PostService } from '../../services/post.service';
import { ReadingFolderService } from '../../services/reading.folder.service';

@Component({
  selector: 'bookmark',
  templateUrl: './bookmark.component.html',
  styleUrls: ['./bookmark.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, NgbPopover, NgbPopoverModule]
})
export class BookmarkComponent implements OnInit {

  currentUser: Responsable | undefined;
  readingFolders: ReadingFolder[] = [];
  @Input() post: Post | undefined;

  constructor(private postService: PostService,
    private loginService: LoginService,
    private readingFolderService: ReadingFolderService) { }

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(user => {
      if (user)
        this.currentUser = user;
    });
    this.fetchReadingFolders();
  }

  unBookmarkPost(post: Post) {
    this.postService.deleteAssoMailPost(post).subscribe(response => {
      if (response)
        post.isBookmarked = false;
    });
  }

  bookmarkPost(post: Post, readingFolder?: ReadingFolder) {
    this.postService.addAssoMailPost(post, readingFolder).subscribe(response => {
      if (response) {
        post.isBookmarked = true;
      }
    });
  }

  fetchReadingFolders() {
    this.readingFolderService.getReadingFolders().subscribe(response => {
      if (response)
        this.readingFolders.push(...response);
    });
  }

  handleBookmarkPost(post: Post, readingFolder?: ReadingFolder) {
    if (post.isBookmarked)
      this.unBookmarkPost(post);
    else this.bookmarkPost(post, readingFolder);
  }

}
