import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { AttachmentType } from '../../model/AttachmentType';
import { IAttachment } from '../../model/IAttachment';
import { IAttachmentCode } from '../../model/IAttachmentCode';
import { instanceOfIAttachmentCode } from '../../../../libs/TypeHelper';

@Component({
  selector: 'upload-attachement-dialog',
  templateUrl: './upload-attachement-dialog.component.html',
  styleUrls: ['./upload-attachement-dialog.component.css']
})
export class UploadAttachementDialogComponent implements OnInit {

  entity: IAttachment | IAttachmentCode = {} as IAttachment;
  entityType: string = "";
  replaceExistingAttachementType = false;
  forcedAttachmentType: AttachmentType | undefined;
  forcedFileExtension: string | undefined;

  instanceOfIAttachmentCode = instanceOfIAttachmentCode;

  constructor(private uploadTiersAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent>) { }

  ngOnInit() {
  }

  endUpload(last: any) {
    this.uploadTiersAttachementDialogRef.close(last);
  }
}
