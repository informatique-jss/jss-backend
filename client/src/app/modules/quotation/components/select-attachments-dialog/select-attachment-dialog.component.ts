import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatLegacyDialogRef as MatDialogRef } from '@angular/material/legacy-dialog';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { Attachment } from '../../../miscellaneous/model/Attachment';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { AttachmentMailRequest } from '../../model/AttachmentMailRequest';
import { CustomerOrder } from '../../model/CustomerOrder';
import { Provision } from '../../model/Provision';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'app-select-attachment-type-dialog',
  templateUrl: './select-attachment-dialog.component.html',
  styleUrls: ['./select-attachment-dialog.component.css']
})
export class SelectAttachmentsDialogComponent implements OnInit {

  customerOrder: CustomerOrder | undefined;
  assoAffaireOrder: AssoAffaireOrder | undefined;
  provision: Provision | undefined;
  selectedAttachments: Attachment[] = [];
  displayedColumns: SortTableColumn[] = [];

  attachmentMailRequest: AttachmentMailRequest = {} as AttachmentMailRequest;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "name", fieldName: "uploadedFile.filename", label: "Nom" } as SortTableColumn);
    this.displayedColumns.push({ id: "attachementType", fieldName: "attachmentType.label", label: "Type de document" } as SortTableColumn);
    this.displayedColumns.push({ id: "createdBy", fieldName: "uploadedFile.createdBy", label: "Ajouté par" } as SortTableColumn);
    this.displayedColumns.push({ id: "creationDate", fieldName: "uploadedFile.creationDate", label: "Ajouté le", valueFonction: formatDateTimeForSortTable } as SortTableColumn);

  }

  ngOnChanges(changes: SimpleChanges) {
  }

  attachmentsForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.attachmentsForm.valid;
  }

  selectAttachment(attachment: Attachment) {
    if (this.selectedAttachments)
      for (let att of this.selectedAttachments)
        if (att.id == attachment.id)
          return;
    this.selectedAttachments.push(attachment);
  }

  deleteAttachment(index: number) {
    this.selectedAttachments.splice(index, 1);
  }

  getAttachments() {
    if (this.provision) {
      return this.provision.attachments
    }
    return [];
  }

  generateMail() {
    if (this.customerOrder && this.assoAffaireOrder && this.provision) {
      this.attachmentMailRequest.customerOrder = this.customerOrder;
      this.attachmentMailRequest.assoAffaireOrder = this.assoAffaireOrder;
      this.attachmentMailRequest.attachements = this.selectedAttachments;
      this.dialogRef.close(this.attachmentMailRequest);
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
