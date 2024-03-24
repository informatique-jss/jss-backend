import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { AttachmentType } from '../../model/AttachmentType';
import { IAttachment } from '../../model/IAttachment';

@Component({
  selector: 'upload-attachement-dialog',
  templateUrl: './upload-attachement-dialog.component.html',
  styleUrls: ['./upload-attachement-dialog.component.css']
})
export class UploadAttachementDialogComponent implements OnInit {

  entity: IAttachment = {} as IAttachment;
  entityType: string = "";
  replaceExistingAttachementType = false;
  forcedAttachmentType: AttachmentType | undefined;
  forcedFileExtension: string | undefined;

  constructor(private uploadTiersAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent>) { }

  ngOnInit() {
  }

  endUpload(last: any) {
    this.uploadTiersAttachementDialogRef.close(last);
  }
}
