import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { forkJoin, tap } from 'rxjs';
import { MAX_SIZE_UPLOAD_FILES } from 'src/app/libs/Constants';
import { formatBytes } from 'src/app/libs/FormatHelper';
import { instanceOfIAttachmentCode } from 'src/app/libs/TypeHelper';
import { TypeDocument } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDocument';
import { AppService } from 'src/app/services/app.service';
import { AttachmentType } from '../../model/AttachmentType';
import { IAttachment } from '../../model/IAttachment';
import { IAttachmentCode } from '../../model/IAttachmentCode';
import { AttachmentTypeService } from '../../services/attachment.type.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';

@Component({
  selector: 'multiple-upload',
  templateUrl: './multiple-upload.component.html',
  styleUrls: ['./multiple-upload.component.css']
})
export class MultipleUploadComponent implements OnInit {

  @Input() entity: IAttachment | IAttachmentCode = {} as IAttachment;
  @Input() entityType: string = "";
  @Output() endOfUpload: EventEmitter<any> = new EventEmitter<any>();
  pageSelection: string | null = null;
  toPage: number | null = null;
  files: any[] = [];
  progress: number = 0;
  isSending = false;
  @Input() replaceExistingAttachementType = false;
  @Input() forcedAttachmentType: AttachmentType | undefined;
  @Input() forcedFileExtension: string | undefined;
  @Input() isDirectUpload: boolean = false;

  attachmentTypes: AttachmentType[] = [] as Array<AttachmentType>;

  @Input() attachmentType: AttachmentType | null = null;
  filename: string = "";
  @Input() typeDocument: TypeDocument | null = null;

  instanceOfIAttachmentCode = instanceOfIAttachmentCode;

  constructor(private formBuilder: UntypedFormBuilder,
    protected attachmentTypeService: AttachmentTypeService,
    private appService: AppService,
    private uploadAttachmentService: UploadAttachmentService) { }

  ngOnInit() {
    this.attachmentTypeService.getAttachmentTypes().subscribe(response => {
      this.attachmentTypes = response;
      if (this.forcedAttachmentType && response != null && response.length > 0) {
        response.forEach(attachmentType => {
          if (attachmentType.id == this.forcedAttachmentType!.id) {
            this.attachmentTypes.push(attachmentType);
            this.attachmentType = attachmentType;
          }
        })
      }
    })
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
          this.appService.displaySnackBar("Taille maximale d'import limitée à 10 Mo", true, 15);
        }
        if (this.forcedFileExtension) {
          var extensionRegexp = /(?:\.([^.]+))?$/;
          if (!extensionRegexp.exec(file.name)![1] || extensionRegexp.exec(file.name)![1].toLowerCase() != this.forcedFileExtension.toLowerCase()) {
            this.files = [];
            this.appService.displaySnackBar("Le fichier doit être au format " + this.forcedFileExtension.toUpperCase(), true, 15);
          }
        }
      }
  }

  uploadFiles() {
    if (this.attachmentType != null && this.files != null && this.files.length > 0 && this.entityType != "") {
      // Check if filename exists
      let found = false;
      if (this.entity.attachments != null && this.entity.attachments != undefined && this.entity.attachments.length > 0) {
        this.entity.attachments.forEach(attachement => {
          if (!found && attachement.uploadedFile.filename == this.filename) {
            found = true;
            this.appService.displaySnackBar("Nom de fichier déjà existant", true, 15);
            this.resetForm();
          }
        })
      }

      if (this.attachmentType != null && !found) {
        this.isSending = true;
        let promises = [];
        for (let file of this.files) {
          promises.push(this.uploadAttachmentService.uploadAttachment(file, this.entity, this.entityType, this.attachmentType, this.files.length == 1 ? this.filename : file.name, this.replaceExistingAttachementType, this.pageSelection, this.typeDocument));
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
              if (res instanceof HttpResponse && res.body && res.body.length > last.length)
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
