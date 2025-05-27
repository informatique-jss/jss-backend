import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ASSO_SERVICE_DOCUMENT_ENTITY_TYPE } from '../../../../libs/Constants';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { GenericDatePickerComponent } from '../../../miscellaneous/components/forms/generic-date-picker/generic-datetime-picker.component';
import { SingleUploadComponent } from '../../../miscellaneous/components/forms/single-upload/single-upload.component';
import { AssoServiceDocument } from '../../../my-account/model/AssoServiceDocument';
import { Attachment } from '../../../my-account/model/Attachment';
import { AttachmentService } from '../../../my-account/services/attachment.service';
import { UploadState } from '../../model/UploadState';

@Component({
  selector: 'quotation-file-uploader',
  templateUrl: './quotation-file-uploader.component.html',
  styleUrls: ['./quotation-file-uploader.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericDatePickerComponent, SingleUploadComponent]
})
export class QuotationFileUploaderComponent implements OnInit {

  ASSO_SERVICE_DOCUMENT_ENTITY_TYPE = ASSO_SERVICE_DOCUMENT_ENTITY_TYPE;

  @Input() entity: AssoServiceDocument | undefined;
  @Input() uploadedFiles: Attachment[] = [];
  @Output() isUploadComplete: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() isUploadDeleted: EventEmitter<boolean> = new EventEmitter<boolean>();

  uploadStateMap: Map<number, UploadState> = new Map();
  uploadProgress: number = 0;
  isUploading: boolean = false;
  isError: boolean = false;
  isComplete: boolean = false;
  uploadedKB = 0;
  totalKB = 0;

  constructor(
    private attachmentService: AttachmentService,
  ) { }

  ngOnInit() {
    if (this.entity) {
      let assoServDoc: AssoServiceDocument = this.entity;
      for (let attachment of assoServDoc.attachments) {
        this.uploadStateMap.set(attachment.id, { progress: 100, isComplete: true, isError: false, isUploading: false });
      }
    }
  }

  handleErrorChange(status: boolean) {
    this.isError = status;
  }

  handleUploadedSizeChange(kb: number) {
    this.uploadedKB = kb;
  }

  handleTotalSizeChange(kb: number) {
    this.totalKB = kb;
  }

  deleteFile(attachment: Attachment) {
    this.attachmentService.deleteAttachment(attachment.id).subscribe((res) => {
      if (res) {
        this.isUploadDeleted.emit(true);
        this.isComplete = true;
      } else {
        console.warn("The file was not deleted");
      }
    });
  }

  updateUploadProgress(event: any) {
    const state = this.uploadStateMap.get(event.id);
    if (state) {
      state.progress = event.progress;
      state.isUploading = event.progress < 100;
      state.isComplete = event.progress === 100;
    }
  }

  onUploadComplete(uploadedFileIds: any) {
    for (let fileId of uploadedFileIds) {
      this.uploadStateMap.set(fileId, { progress: 100, isComplete: true, isError: false, isUploading: false });
    }
    this.isUploadComplete.emit(true);
  }

}
