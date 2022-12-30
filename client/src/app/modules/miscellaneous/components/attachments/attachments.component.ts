import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { Attachment } from '../../model/Attachment';
import { AttachmentType } from '../../model/AttachmentType';
import { IAttachment } from '../../model/IAttachment';
import { SortTableAction } from '../../model/SortTableAction';
import { SortTableColumn } from '../../model/SortTableColumn';
import { ConstantService } from '../../services/constant.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { UploadAttachementDialogComponent } from '../upload-attachement-dialog/upload-attachement-dialog.component';

@Component({
  selector: 'attachments',
  templateUrl: './attachments.component.html',
  styleUrls: ['./attachments.component.css']
})
export class AttachmentsComponent implements OnInit {


  @Input() entity: IAttachment = {} as IAttachment;
  @Input() entityType: string = "";
  @Input() editMode: boolean = false;

  displayedColumns: SortTableColumn[] = [];
  tableActions: SortTableAction[] = [] as Array<SortTableAction>;
  searchText: string | undefined;

  filterValue: string = "";

  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;

  attachmentTypesToHide: AttachmentType[] = [this.constantService.getAttachmentTypeAutomaticMail()];

  filteredAttachments: Attachment[] = [];

  constructor(
    protected uploadAttachementDialog: MatDialog,
    public confirmationDialog: MatDialog,
    protected uploadAttachmentService: UploadAttachmentService,
    private constantService: ConstantService,
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.entity != undefined && this.entity.attachments != undefined) {
      this.setDataTable();
    }
  }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "name", fieldName: "uploadedFile.filename", label: "Nom" } as SortTableColumn);
    this.displayedColumns.push({ id: "attachementType", fieldName: "attachmentType.label", label: "Type de document" } as SortTableColumn);
    this.displayedColumns.push({ id: "createdBy", fieldName: "uploadedFile.createdBy", label: "Ajouté par" } as SortTableColumn);
    this.displayedColumns.push({ id: "creationDate", fieldName: "uploadedFile.creationDate", label: "Ajouté le", valueFonction: formatDateTimeForSortTable } as SortTableColumn);

    this.tableActions.push({
      actionIcon: "visibility", actionName: "Prévisualiser le fichier", actionClick: (action: SortTableAction, element: any): void => {
        this.uploadAttachmentService.previewAttachment(element);
      }, display: true
    } as SortTableAction);
    this.tableActions.push({
      actionIcon: "download", actionName: "Télécharger le fichier", actionClick: (action: SortTableAction, element: any): void => {
        this.uploadAttachmentService.downloadAttachment(element);
      }, display: true
    } as SortTableAction);
    this.tableActions.push({
      actionIcon: "block", actionName: "Désactiver le fichier", actionClick: (action: SortTableAction, element: any): void => {
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
            this.uploadAttachmentService.disableAttachment(element).subscribe(response => {
              element.isDisabled = true;
              this.setDataTable();
            });
        });
      }, display: true
    } as SortTableAction);
  }
  formatDateTimeForSortTable = formatDateTimeForSortTable;

  setDataTable() {
    this.entity.attachments.sort(function (a: Attachment, b: Attachment) {
      return new Date(b.uploadedFile.creationDate).getTime() - new Date(a.uploadedFile.creationDate).getTime();
    });

    this.filteredAttachments = [];
    if (this.entity && this.entity.attachments)
      for (let attachment of this.entity.attachments)
        if (this.attachmentTypesToHide)
          for (let toHideType of this.attachmentTypesToHide)
            if (toHideType.id != attachment.attachmentType.id)
              this.filteredAttachments.push(attachment);
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
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
}


