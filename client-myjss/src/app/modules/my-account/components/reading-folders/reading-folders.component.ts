import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../../../environments/environment';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GenericInputComponent } from "../../../miscellaneous/components/forms/generic-input/generic-input.component";
import { Post } from '../../../tools/model/Post';
import { ReadingFolder } from '../../../tools/model/ReadingFolder';
import { PostService } from '../../../tools/services/post.service';
import { ReadingFolderService } from '../../../tools/services/reading.folder.service';

@Component({
  selector: 'reading-folders',
  templateUrl: './reading-folders.component.html',
  styleUrls: ['./reading-folders.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class ReadingFoldersComponent implements OnInit {

  readingFolders: ReadingFolder[] = [];
  newReadingFolder: ReadingFolder = {} as ReadingFolder;

  folderToDelete: ReadingFolder | undefined;
  readingFolderForm!: FormGroup;

  purchasedPosts: Post[] = [];

  frontendJssUrl = environment.frontendJssUrl;

  constructor(private readingFolderService: ReadingFolderService,
    private appService: AppService,
    private formBuilder: FormBuilder,
    private modalService: NgbModal,
    private postService: PostService) { }

  ngOnInit() {
    this.readingFolderForm = this.formBuilder.group({});
    this.fetchReadingFolders();
    this.fetchPurchasedPosts();
  }

  fetchPurchasedPosts() {
    this.postService.getPurchasedPosts().subscribe(res => {
      this.purchasedPosts = res;
    });
  }

  fetchReadingFolders() {
    this.readingFolderService.getReadingFolders().subscribe(response => {
      if (response) {
        this.readingFolders = response;
        for (let readingFolder of this.readingFolders)
          this.postService.getBookmarkPostsByMailAndReadingFolders(readingFolder.id, 0, 1).subscribe(response => {
            if (response)
              readingFolder.posts = response.content;
          });
      }
    });
  }

  openDeletionModal(content: any, folder: ReadingFolder) {
    this.folderToDelete = folder;
    this.modalService.open(content, { centered: true, size: 'md' });
  }

  openCreationModal(content: any) {
    this.modalService.open(content, { centered: true, size: 'md' });
  }

  createReadingFolder() {
    if (this.newReadingFolder && this.newReadingFolder.label.length > 0) {
      this.readingFolderService.createReadingFolder(this.newReadingFolder).subscribe(response => {
        if (response)
          this.readingFolders.push(response);
        this.newReadingFolder = {} as ReadingFolder;
      });
    }
  }

  deleteReadingFolder(readingFolder: ReadingFolder) {
    this.readingFolderService.deleteReadingFolder(readingFolder).subscribe(response => {
      if (response)
        this.readingFolders.splice(this.readingFolders.indexOf(readingFolder), 1);
    });
  }
}
