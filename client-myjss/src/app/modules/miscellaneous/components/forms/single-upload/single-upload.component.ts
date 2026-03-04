import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { forkJoin, tap } from 'rxjs';
import { MAX_SIZE_UPLOAD_FILES } from '../../../../../libs/Constants';
import { formatBytes } from '../../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { AppService } from '../../../../main/services/app.service';
import { AttachmentType } from '../../../../my-account/model/AttachmentType';
import { IAttachment } from '../../../../my-account/model/IAttachment';
import { TypeDocument } from '../../../../my-account/model/TypeDocument';
import { UploadAttachmentService } from '../../../../my-account/services/upload.attachment.service';

@Component({
  selector: 'single-upload',
  templateUrl: './single-upload.component.html',
  styleUrls: ['./single-upload.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SingleUploadComponent implements OnInit {

  @Input() entity: IAttachment = {} as IAttachment;
  @Input() entityType: string = "";
  @Output() endOfUpload: EventEmitter<any> = new EventEmitter<any>();
  @Output() progressChange = new EventEmitter<any>();
  @Output() isErrorChange = new EventEmitter<boolean>();
  @Input() forcedFileExtension: string = '';
  @Output() fileDropped = new EventEmitter<any>();
  @Input() fullSize = true;

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  MAX_SIZE_UPLOAD_FILES = MAX_SIZE_UPLOAD_FILES;
  MAX_SIZE_UPLOAD_FILES_IN_MO = formatBytes(MAX_SIZE_UPLOAD_FILES, 10);

  files: any[] = [];
  progress: number = 0;
  uploadedSize = 0;
  totalSize = 0;
  isSending = false;
  @Input() isDirectUpload: boolean = false;

  attachmentTypes: AttachmentType[] = [] as Array<AttachmentType>;
  attachmentForm!: FormGroup;

  @Input() attachmentType: AttachmentType | null = null;
  filename: string = "";
  @Input() typeDocument: TypeDocument | null = null;

  formatBytes = formatBytes;

  constructor(private formBuilder: UntypedFormBuilder,
    private appService: AppService,
    private uploadAttachmentService: UploadAttachmentService) { }

  ngOnInit() {
    if (this.forcedFileExtension)
      this.forcedFileExtension = this.forcedFileExtension.toLowerCase();

    this.attachmentForm = this.formBuilder.group({
      attachmentType: ['', Validators.required],
      filename: ['', Validators.required],
    });
  }

  fileBrowseHandler(files: any | null) {
    this.files = files;
    if (this.files.length == 1)
      this.filename = this.files[0].name;
    this.attachmentForm.markAllAsTouched();
    this.checkFiles();
    this.fileDropped.emit(this.files);
    if (this.isDirectUpload)
      this.uploadFiles();
  }

  checkFiles() {
    if (this.files) {
      const filesArray = Array.from(this.files);

      for (let file of filesArray) {
        if (file.size > MAX_SIZE_UPLOAD_FILES) {
          this.files = [];
          this.appService.displayToast("Taille maximale d'import limitée à 10 Mo", true, "Erreur", 15);
          break;
        }

        if (this.forcedFileExtension) {
          var extensionRegexp = /(?:\.([^.]+))?$/;
          if (!extensionRegexp.exec(file.name)![1] || extensionRegexp.exec(file.name)![1].toLowerCase() != this.forcedFileExtension.toLowerCase()) {
            this.files = [];
            this.appService.displayToast("Le fichier doit être au format " + this.forcedFileExtension.toUpperCase(), true, "Erreur", 15);
          }
        }

      }
    }
  }

  uploadFiles() {
    if (this.attachmentType != null && this.files?.length > 0 && this.entityType !== "") {
      let found = false;

      if (this.entity.attachments?.length > 0) {
        this.entity.attachments.forEach(attachement => {
          if (!found && attachement.description === this.filename) {
            found = true;
            this.appService.displayToast("Nom de fichier déjà existant", true, "Erreur", 15);
            this.isErrorChange.emit(true);
            this.resetForm();
          }
        });
      }

      if (!found) {
        this.isSending = true;
        this.progress = 0;
        this.uploadedSize = 0;
        this.totalSize = Array.from(this.files).reduce((sum, f) => sum + f.size, 0);
        this.progressChange.emit({ id: 0, progress: 0 });
        this.isErrorChange.emit(false);

        let uploadRequests = Array.from(this.files).map(file => {
          return this.uploadAttachmentService.uploadAttachment(
            file,
            this.entity,
            this.entityType,
            this.attachmentType!,
            this.files.length === 1 ? this.filename : file.name,
            this.typeDocument
          ).pipe(
            tap(event => {
              if (event.type === HttpEventType.DownloadProgress) {
                this.uploadedSize += event.loaded;
                let percent = Math.round((this.uploadedSize / this.totalSize) * 100);
                this.progressChange.emit({ id: 0, progress: percent });
                this.progress = percent;
              }
            })
          );
        });

        forkJoin(uploadRequests).subscribe({
          next: responses => {
            let last = [];
            for (let res of responses) {
              if (res instanceof HttpResponse && res.body?.length > last.length) {
                last = res.body;
              }
            }
            this.endOfUpload.emit(last);
            this.resetForm();
          },
          error: () => {
            this.isErrorChange.emit(true);
          }
        });
      }
    }
  }

  resetForm() {
    this.isSending = false;
    this.files = [];
    this.progress = 0;
    this.uploadedSize = 0;
    this.totalSize = 0;
    this.progressChange.emit(0);
  }

  onCardClick(): void {
    this.fileInput?.nativeElement.click();
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    const card = event.target as HTMLElement;
    card.classList.add('dragover');
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    const card = event.target as HTMLElement;
    card.classList.remove('dragover');
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    const card = event.target as HTMLElement;
    card.classList.remove('dragover');
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.fileBrowseHandler(files);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const files: File[] = Array.from(input.files);
      this.fileBrowseHandler(files);
    }
  }

}
