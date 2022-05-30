import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MAX_SIZE_UPLOAD_FILES } from 'src/app/libs/Constants';
import { AttachmentType } from '../../model/AttachmentType';
import { ITiers } from '../../model/ITiers';
import { Tiers } from '../../model/Tiers';
import { AttachmentTypeService } from '../../services/attachment.type.service';
import { UploadTiersAttachmentService } from '../../services/upload.tiers.attachment.service';

@Component({
  selector: 'upload-tiers-attachement-dialog',
  templateUrl: './upload-tiers-attachement-dialog.component.html',
  styleUrls: ['./upload-tiers-attachement-dialog.component.css']
})
export class UploadTiersAttachementDialogComponent implements OnInit {

  tiers: ITiers = {} as ITiers;
  file: any = null;
  progress: number = 0;
  isSending = false;

  attachmentTypes: AttachmentType[] = [] as Array<AttachmentType>;

  attachmentType: AttachmentType | null = null;
  filename: string = "";

  constructor(private uploadTiersAttachementDialogRef: MatDialogRef<UploadTiersAttachementDialogComponent>,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    protected attachmentTypeService: AttachmentTypeService,
    private uploadTiersAttachmentService: UploadTiersAttachmentService) { }

  ngOnInit() {
    this.attachmentTypeService.getAttachmentTypes().subscribe(response => {
      this.attachmentTypes = response;
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
      let sb = this.snackBar.open("Taille maximale d'import limitée à 5 Mo", 'Fermer', {
        duration: 60 * 1000
      });
      sb.onAction().subscribe(() => {
        sb.dismiss();
      });
    }

  }

  uploadFile() {
    if (this.attachmentType != null && this.filename != null && this.filename != "" && this.file != null) {
      // Check if filename exists
      this.uploadTiersAttachmentService.findAttachmentWithSameFilename(this.tiers, this.filename).subscribe(response => {
        if (response != null && response != undefined && response.length > 0) {
          let sb = this.snackBar.open("Nom de fichier déjà existant", 'Fermer', {
            duration: 60 * 1000
          });
          sb.onAction().subscribe(() => {
            sb.dismiss();
          });
        } else if (this.attachmentType != null) {
          this.isSending = true;
          this.uploadTiersAttachmentService.uploadTiersAttachment(this.file, this.tiers, this.attachmentType, this.filename).subscribe(event => {
            if (event.type === HttpEventType.UploadProgress && event.total != undefined) {
              this.progress = Math.round(100 * event.loaded / event.total);
            } else if (event instanceof HttpResponse) {
              this.uploadTiersAttachementDialogRef.close(event.body);
              this.isSending = false;
            }
          },
            err => {
              let sb = this.snackBar.open("Erreur lors de l'envoi du fichier", 'Fermer', {
                duration: 60 * 1000
              });
              sb.onAction().subscribe(() => {
                sb.dismiss();
              });
              this.uploadTiersAttachementDialogRef.close(null);
              this.isSending = false;
            });
        }
      })
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
