import { Component, OnInit } from '@angular/core';
import { PdfToolsService } from '../../miscellaneous/services/pdfTools.service';
import { AttachmentType } from '../../miscellaneous/model/AttachmentType';
import { IAttachment } from '../../miscellaneous/model/IAttachment';
import { AppService } from 'src/app/services/app.service';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { AttachmentTypeService } from '../../miscellaneous/services/attachment.type.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
export const MAX_SIZE_UPLOAD_FILES = 5242880;

@Component({
  selector: 'pdf-tools',
  templateUrl: './pdf-tools.component.html',
  styleUrls: ['./pdf-tools.component.css']
})
export class PdfToolsComponent{

  entity: IAttachment = {} as IAttachment;
  entityType: string = "";
  file: any = null;
  progress: number = 0;
  isSending = false;
  replaceExistingAttachementType = false;
  forcedAttachmentType: AttachmentType | undefined;
  forcedFileExtension: string | undefined;
  attachmentTypes: AttachmentType[] = [] as Array<AttachmentType>;
  attachmentType: AttachmentType | null = null;
  filename: string = "";

  constructor(
    private formBuilder: UntypedFormBuilder,
    protected attachmentTypeService: AttachmentTypeService,
    private appService: AppService,
    private pdfToolsService: PdfToolsService,
    ) { }

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
    console.log(this.file);
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
    if (this.forcedFileExtension) {
      var extensionRegexp = /(?:\.([^.]+))?$/;
      if (!extensionRegexp.exec(this.file.name)![1] || extensionRegexp.exec(this.file.name)![1].toLowerCase() != this.forcedFileExtension.toLowerCase()) {
        this.deleteFile();
        this.appService.displaySnackBar("Le fichier doit être au format " + this.forcedFileExtension.toUpperCase(), true, 15);
      }
    }
  }

  uploadFile() {
    if (this.filename != null && this.filename != "" && this.file != null) {
      // Check if filename exists
      let found = false;

      if (!found) {
        this.isSending = true;
        this.pdfToolsService.uploadAttachment(this.file,this.filename).subscribe(event => {
          if (event.type === HttpEventType.UploadProgress && event.total != undefined) {
            this.progress = Math.round(100 * event.loaded / event.total);
          } else if (event instanceof HttpResponse) {
            this.isSending = false;
          }
        },
          err => {
            this.appService.displaySnackBar("Erreur lors de l'envoi du fichier", true, 15);
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
