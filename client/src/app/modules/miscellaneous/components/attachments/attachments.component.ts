import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { Attachment } from '../../model/Attachment';
import { IAttachment } from '../../model/IAttachment';
import { UploadAttachmentService } from '../../services/upload.attachment.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { UploadAttachementDialogComponent } from '../upload-attachement-dialog/upload-attachement-dialog.component';

@Component({
  selector: 'attachments',
  templateUrl: './attachments.component.html',
  styleUrls: ['./attachments.component.css']
})
export class AttachmentsComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() entity: IAttachment = {} as IAttachment;
  @Input() entityType: string = "";
  @Input() editMode: boolean = false;
  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns: string[] = ['name', 'attachementType', 'createdBy', 'creationDate', 'action'];

  tiersAttachmentDataSource: MatTableDataSource<Attachment> = new MatTableDataSource<Attachment>();

  filterValue: string = "";

  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;

  constructor(
    protected uploadAttachementDialog: MatDialog,
    public confirmationDialog: MatDialog,
    protected uploadAttachmentService: UploadAttachmentService
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.entity != undefined && this.entity.attachments != undefined) {
      this.setDataTable();
    }
  }

  ngOnInit() {
  }

  setDataTable() {
    this.entity.attachments.sort(function (a: Attachment, b: Attachment) {
      return new Date(b.uploadedFile.creationDate).getTime() - new Date(a.uploadedFile.creationDate).getTime();
    });

    this.tiersAttachmentDataSource = new MatTableDataSource(this.entity.attachments.filter(attachment => attachment.isDisabled == false));
    setTimeout(() => {
      this.tiersAttachmentDataSource.sort = this.sort;
      this.tiersAttachmentDataSource.sortingDataAccessor = (item: Attachment, property) => {
        switch (property) {
          case 'name': return item.uploadedFile.filename;
          case 'attachementType': return item.attachmentType.label;
          case 'createdBy': return item.uploadedFile.createdBy;
          case 'creationDate': return new Date(item.uploadedFile.creationDate).getTime() + "";
          default: return item.uploadedFile.filename;
        }
      };

      this.tiersAttachmentDataSource.filterPredicate = (data: any, filter) => {
        const dataStr = JSON.stringify(data).toLowerCase();
        return dataStr.indexOf(filter) != -1;
      }
    });
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.tiersAttachmentDataSource.filter = filterValue;
  }

  uploadFile() {
    this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
    });
    this.uploadAttachementDialogRef.componentInstance.entity = this.entity;
    this.uploadAttachementDialogRef.componentInstance.entityType = this.entityType;
    this.uploadAttachementDialogRef.afterClosed().subscribe(response => {
      if (response && response != null) {
        this.entity.attachments = response;
        this.setDataTable();
      }
    });
  }

  disableFile(attachement: Attachment) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Désactiver le fichier",
        content: "Êtes-vous sûr de vouloir désactiver ce fichier ?",
        closeActionText: "Annuler",
        validationActionText: "Confirmer"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult)
        this.uploadAttachmentService.disableAttachment(attachement).subscribe(response => {
          attachement.isDisabled = true;
          this.setDataTable();
        });
    });
  }

  previewFile(attachment: Attachment) {
    this.uploadAttachmentService.previewAttachment(attachment);
  }

  downloadFile(attachment: Attachment) {
    this.uploadAttachmentService.downloadAttachment(attachment);
  }

}


