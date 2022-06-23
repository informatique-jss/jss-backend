import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Attachment } from '../../model/Attachment';
import { AttachmentType } from '../../model/AttachmentType';
import { IAttachment } from '../../model/IAttachment';
import { AttachmentTypeService } from '../../services/attachment.type.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';
import { UploadAttachementDialogComponent } from '../upload-attachement-dialog/upload-attachement-dialog.component';

@Component({
  selector: 'single-attachment',
  templateUrl: './single-attachment.component.html',
  styleUrls: ['./single-attachment.component.css']
})
export class SingleAttachmentComponent implements OnInit {

  @Input() entity: IAttachment = {} as IAttachment;
  @Input() entityType: string = "";
  @Input() editMode: boolean = false;
  @Input() attachmentTypeCodeToDisplay: string = "";
  /**
   * Fired when an upload is realized.
   * Give the current attachements of the entity in parameter
   */
  @Output() onUploadedFile: EventEmitter<Attachment[]> = new EventEmitter();

  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;

  currentAttachment: Attachment | null = null;

  attachmentType: AttachmentType = {} as AttachmentType;

  constructor(protected uploadAttachementDialog: MatDialog,
    protected attachmentTypeService: AttachmentTypeService,
    protected uploadAttachmentService: UploadAttachmentService) { }

  ngOnInit() {
    this.attachmentTypeService.getAttachmentTypes().subscribe(response => {
      if (this.attachmentTypeCodeToDisplay != "") {
        if (response != null && response.length > 0) {
          response.forEach(attachmentType => {
            if (attachmentType.code == this.attachmentTypeCodeToDisplay) {
              this.attachmentType = attachmentType;
            }
          })
        }
      }
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.entity != undefined && this.entity.attachments != undefined) {
      this.setCurrentAttachment();
    }
  }

  setCurrentAttachment() {
    if (this.attachmentTypeCodeToDisplay != "" && this.entity.attachments != null && this.entity.attachments != undefined && this.entity.attachments.length > 0) {
      this.entity.attachments.forEach(attachment => {
        if (attachment.attachmentType.code == this.attachmentTypeCodeToDisplay)
          this.currentAttachment = attachment;
      })
    }
  }

  uploadFile() {
    this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
    });
    this.uploadAttachementDialogRef.componentInstance.entity = this.entity;
    this.uploadAttachementDialogRef.componentInstance.entityType = this.entityType;
    this.uploadAttachementDialogRef.componentInstance.forcedAttachmentTypeCode = this.attachmentTypeCodeToDisplay;
    this.uploadAttachementDialogRef.componentInstance.replaceExistingAttachementType = true;
    this.uploadAttachementDialogRef.afterClosed().subscribe(response => {
      if (response && response != null) {
        this.entity.attachments = response;
        this.setCurrentAttachment();
        this.onUploadedFile.emit(response);
      }
    });
  }

  previewFile(tiersAttachment: Attachment) {
    this.uploadAttachmentService.previewAttachment(tiersAttachment);
  }

  downloadFile(tiersAttachment: Attachment) {
    this.uploadAttachmentService.downloadAttachment(tiersAttachment);
  }

}
