import { Component, OnInit } from '@angular/core';
import { PdfToolsService } from '../../miscellaneous/services/pdfTools.service';

@Component({
  selector: 'pdf-tools',
  templateUrl: './pdf-tools.component.html',
  styleUrls: ['./pdf-tools.component.css']
})
export class PdfToolsComponent implements OnInit {
  selectedFile: File | undefined;

  constructor(
    private pdfToolsService: PdfToolsService,
  ) { }

  ngOnInit(): void {}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  compressPdf() {
    if (!this.selectedFile) {
      console.log("no file found");
      return;
    }
    const formData = new FormData();
    formData.append('file', this.selectedFile);
    const pdfFormData = {pdf: formData};

    this.pdfToolsService.sendPdf(pdfFormData).subscribe((response: any) => {
      console.log('test');
    }, (error: any) => {
      console.log(error);
    });
  }
  /**
   * uploadFile() {
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
   *
   */
}
