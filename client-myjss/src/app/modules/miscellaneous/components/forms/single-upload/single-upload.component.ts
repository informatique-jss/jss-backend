import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { forkJoin, tap } from 'rxjs';
import { AppService } from '../../../../../libs/app.service';
import { MAX_SIZE_UPLOAD_FILES } from '../../../../../libs/Constants';
import { formatBytes } from '../../../../../libs/FormatHelper';
import { AttachmentType } from '../../../../my-account/model/AttachmentType';
import { IAttachment } from '../../../../my-account/model/IAttachment';
import { TypeDocument } from '../../../../my-account/model/TypeDocument';
import { UploadAttachmentService } from '../../../../my-account/services/upload.attachment.service';

@Component({
  selector: 'single-upload',
  templateUrl: './single-upload.component.html',
  styleUrls: ['./single-upload.component.css']
})
export class SingleUploadComponent implements OnInit {

  @Input() entity: IAttachment = {} as IAttachment;
  @Input() entityType: string = "";
  @Output() endOfUpload: EventEmitter<any> = new EventEmitter<any>();
  files: any[] = [];
  progress: number = 0;
  isSending = false;
  @Input() isDirectUpload: boolean = false;

  attachmentTypes: AttachmentType[] = [] as Array<AttachmentType>;

  @Input() attachmentType: AttachmentType | null = null;
  filename: string = "";
  @Input() typeDocument: TypeDocument | null = null;

  constructor(private formBuilder: UntypedFormBuilder,
    private appService: AppService,
    private uploadAttachmentService: UploadAttachmentService) { }

  ngOnInit() {
  }

  attachmentForm = this.formBuilder.group({
    attachmentType: ['', Validators.required],
    filename: ['', Validators.required],
  });

  fileBrowseHandler(files: any) {
    this.files = files.files;
    if (this.files.length == 1)
      this.filename = this.files[0].name;
    this.attachmentForm.markAllAsTouched();
    this.checkFiles();
    if (this.isDirectUpload)
      this.uploadFiles();
  }

  checkFiles() {
    if (this.files)
      for (let file of this.files) {
        if (file.size > MAX_SIZE_UPLOAD_FILES) {
          this.files = [];
          this.appService.displayToast("Taille maximale d'import limitée à 10 Mo", true, "Erreur", 15);
        }
      }
  }

  uploadFiles() {
    if (this.attachmentType != null && this.files != null && this.files.length > 0 && this.entityType != "") {
      // Check if filename exists
      let found = false;
      if (this.entity.attachments != null && this.entity.attachments != undefined && this.entity.attachments.length > 0) {
        this.entity.attachments.forEach(attachement => {
          if (!found && attachement.description == this.filename) {
            found = true;
            this.appService.displayToast("Nom de fichier déjà existant", true, "Erreur", 15);
            this.resetForm();
          }
        })
      }

      if (this.attachmentType != null && !found) {
        this.isSending = true;
        let promises = [];
        for (let file of this.files) {
          promises.push(this.uploadAttachmentService.uploadAttachment(file, this.entity, this.entityType, this.attachmentType, this.files.length == 1 ? this.filename : file.name, this.typeDocument));
        }

        forkJoin(
          promises.map(req => {
            return req.pipe(
              tap(e => {
                if (e.type === HttpEventType.UploadProgress) {
                  if (e.loaded == e.total)
                    this.progress++;
                }
              })
            );
          })
        ).subscribe(response => {
          let last = [];
          if (response) {
            for (let res of response)
              if (res instanceof HttpResponse && res.body.length > last.length)
                last = res.body;
          }
          this.endOfUpload.next(last);
          this.resetForm();
        })
      }
    }
  }

  resetForm() {
    this.isSending = false;
    this.files = [];
    this.progress = 0;
  }

  formatBytes = formatBytes;
}
