import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { UploadAttachementDialogComponent } from 'src/app/modules/miscellaneous/components/upload-attachement-dialog/upload-attachement-dialog.component';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { INPI_INVOICING_EXTRACT_ENTITY_TYPE } from 'src/app/routing/search/search.component';

@Component({
  selector: 'gu-matching',
  templateUrl: './gu-matching.component.html',
  styleUrls: ['./gu-matching.component.css'],
})
export class GuMatchingComponent {

  constructor(
    private formBuilder: FormBuilder,
    public confirmationDialog: MatDialog,
    public uploadAttachementDialog: MatDialog,
    private constantService: ConstantService
  ) { }


  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;


  importInpiInvoicingExtractFile() {
    this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
    });
    this.uploadAttachementDialogRef.componentInstance.entity = { id: 1 } as IAttachment;
    this.uploadAttachementDialogRef.componentInstance.entityType = INPI_INVOICING_EXTRACT_ENTITY_TYPE.entityType;
    this.uploadAttachementDialogRef.componentInstance.forcedAttachmentType = this.constantService.getAttachmentTypeBillingClosure();
    this.uploadAttachementDialogRef.afterClosed().subscribe(response => { });

  }
}
