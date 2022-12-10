import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MAX_SIZE_UPLOAD_FILES } from 'src/app/libs/Constants';
import { AppService } from 'src/app/services/app.service';
import { AttachmentType } from '../../model/AttachmentType';
import { IAttachment } from '../../model/IAttachment';
import { AttachmentTypeService } from '../../services/attachment.type.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';

@Component({
  selector: 'upload-attachement-dialog',
  templateUrl: './upload-attachement-dialog.component.html',
  styleUrls: ['./upload-attachement-dialog.component.css']
})
export class UploadAttachementDialogComponent implements OnInit {

  entity: IAttachment = {} as IAttachment;
  entityType: string = "";
  file: any = null;
  progress: number = 0;
  isSending = false;
  replaceExistingAttachementType = false;
  forcedAttachmentType: AttachmentType | undefined;

  attachmentTypes: AttachmentType[] = [] as Array<AttachmentType>;

  attachmentType: AttachmentType | null = null;
  filename: string = "";

  constructor(private uploadTiersAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent>,
    private formBuilder: UntypedFormBuilder,
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

  onFileDropped($event: any) {
    this.file = $event;
    this.filename = this.file.name;
    this.attachmentForm.markAllAsTouched();
    this.checkFile();
  }

  fileBrowseHandler(files: any) {
    this.file = files.files[0];
    this.filename = this.file.name;
    this.attachmentForm.markAllAsTouched();
    this.checkFile();
  }

  deleteFile() {
    this.file = null;
    this.attachmentType = null;
    this.attachmentForm.markAllAsTouched();
  }

  checkFile() {
    if (this.file != null && this.file.size > MAX_SIZE_UPLOAD_FILES) {
      this.deleteFile();
      this.appService.displaySnackBar("Taille maximale d'import limitée à 5 Mo", true, 15);
    }
  }

  uploadFile() {
    if (this.attachmentType != null && this.filename != null && this.filename != "" && this.file != null && this.entityType != "") {
      // Check if filename exists
      let found = false;
      if (this.entity.attachments != null && this.entity.attachments != undefined && this.entity.attachments.length > 0) {
        this.entity.attachments.forEach(attachement => {
          if (!found && attachement.uploadedFile.filename == this.filename) {
            found = true;
            this.appService.displaySnackBar("Nom de fichier déjà existant", true, 15);
          }
        })
      }

      if (this.attachmentType != null && !found) {
        this.isSending = true;
        this.uploadAttachmentService.uploadAttachment(this.file, this.entity, this.entityType, this.attachmentType, this.filename, this.replaceExistingAttachementType).subscribe(event => {
          if (event.type === HttpEventType.UploadProgress && event.total != undefined) {
            this.progress = Math.round(100 * event.loaded / event.total);
          } else if (event instanceof HttpResponse) {
            this.uploadTiersAttachementDialogRef.close(event.body);
            this.isSending = false;
          }
        },
          err => {
            this.appService.displaySnackBar("Erreur lors de l'envoi du fichier", true, 15);
            this.uploadTiersAttachementDialogRef.close(null);
            this.isSending = false;
          });
      }
    }
  }

  formatBytes(bytes: number, decimals: number) {
    if (bytes === 0) {
      return '0 Bytes';
    }
    const k = 1024;
    const dm = decimals <= 0 ? 0 : decimals || 2;
    const sizes = ['Octets', 'Ko', 'Mo', 'Go', 'To', 'Po', 'Eo', 'Zo', 'Yo'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
  }
}
