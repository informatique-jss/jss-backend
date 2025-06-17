import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GenericInputComponent } from "../../../miscellaneous/components/forms/generic-input/generic-input.component";
import { Media } from '../../../tools/model/Media';
import { ReadingFolder } from '../../../tools/model/ReadingFolder';
import { ReadingFolderService } from '../../../tools/services/reading.folder.service';

@Component({
  selector: 'reading-folders',
  templateUrl: './reading-folders.component.html',
  styleUrls: ['./reading-folders.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent]
})
export class ReadingFoldersComponent implements OnInit {

  allBookmarksFolder: ReadingFolder = { id: -1, label: 'Tous les articles', media: {} as Media };
  readingFolders: ReadingFolder[] = [];
  newReadingFolder: ReadingFolder = {} as ReadingFolder;

  folderToDelete: ReadingFolder | undefined;
  readingFolderForm!: FormGroup;

  @ViewChild('creationModal') creationModal!: TemplateRef<any>;
  @ViewChild('deletionModal') deletionModal!: TemplateRef<any>;

  constructor(private readingFolderService: ReadingFolderService,
    private appService: AppService,
    private formBuilder: FormBuilder,
    private modalService: NgbModal,) { }

  ngOnInit() {
    this.readingFolderForm = this.formBuilder.group({});
    this.fetchReadingFolders();
  }

  fetchReadingFolders() {
    this.readingFolderService.getReadingFolders().subscribe(response => {
      this.readingFolders.push(this.allBookmarksFolder);
      if (response)
        this.readingFolders.push(...response);
      if (this.readingFolders.length > 1)
        for (let folder of this.readingFolders.slice(1))
          this.readingFolderService.getFirstPostImageForReadingFolder(folder).subscribe(readingFolder => {
            if (readingFolder)
              folder = readingFolder;
          });
    });
  }

  openDeletionModal(folder: ReadingFolder) {
    this.folderToDelete = folder;
    this.modalService.open(this.deletionModal);
  }

  openCreationModal() {
    this.modalService.open(this.creationModal);
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

  openReadingFolder(event: any, readingFolder: ReadingFolder) {
    this.appService.openRoute(event, "account/reading-folders/" + readingFolder.id, undefined);
  }

}
