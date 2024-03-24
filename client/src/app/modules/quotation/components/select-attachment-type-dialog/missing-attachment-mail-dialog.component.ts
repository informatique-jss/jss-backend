import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { AssoServiceDocument } from '../../model/AssoServiceDocument';
import { MissingAttachmentQuery } from '../../model/MissingAttachmentQuery';
import { Service } from '../../model/Service';
import { MissingAttachmentQueryService } from '../../services/missing-attachment-query.service';
import { ServiceService } from '../../services/service.service';
import { SelectAttachmentsDialogComponent } from '../select-attachments-dialog/select-attachment-dialog.component';

@Component({
  selector: 'app-select-attachment-type-dialog',
  templateUrl: './missing-attachment-mail-dialog.component.html',
  styleUrls: ['./missing-attachment-mail-dialog.component.css']
})
export class MissingAttachmentMailDialogComponent implements OnInit {

  service: Service | undefined;
  displayedColumns: SortTableColumn<AssoServiceDocument>[] = [];
  selectedAssoServiceDocument: AssoServiceDocument[] = [];
  tableValues: AssoServiceDocument[] = [];
  missingAttachmentQuery: MissingAttachmentQuery = {} as MissingAttachmentQuery;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<SelectAttachmentsDialogComponent>,
    private missingAttachmentQueryService: MissingAttachmentQueryService,
    private serviceService: ServiceService,
  ) { }

  refreshTable: Subject<void> = new Subject<void>();

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "typeDocument", fieldName: "typeDocument.label", label: "Type de document" } as SortTableColumn<AssoServiceDocument>);
    this.displayedColumns.push({ id: "typeAttachment", fieldName: "typeDocument.attachmentType.label", label: "Type de PJ" } as SortTableColumn<AssoServiceDocument>);
    this.displayedColumns.push({ id: "isMandatory", fieldName: "isMandatory", label: "Obligataoire ?", valueFonction: (element: AssoServiceDocument, column: SortTableColumn<AssoServiceDocument>) => { return element.isMandatory ? "Oui" : "Non" } } as SortTableColumn<AssoServiceDocument>);
    this.displayedColumns.push({ id: "existingFiles", fieldName: "existingFiles", label: "Fichiers", valueFonction: (element: AssoServiceDocument, column: SortTableColumn<AssoServiceDocument>) => { return element.attachments ? element.attachments.map(attachment => attachment.uploadedFile.filename).join(" / ") : "" } } as SortTableColumn<AssoServiceDocument>);
    this.setTableValues();
  }
  ngOnChanges(changes: SimpleChanges) {
    if (changes.service)
      this.setTableValues();
  }

  setTableValues() {
    if (this.service && this.selectedAssoServiceDocument.length == 0 && this.service.missingAttachmentQueries && this.service.missingAttachmentQueries.length > 0) {
      this.service.missingAttachmentQueries.sort((a: MissingAttachmentQuery, b: MissingAttachmentQuery) => {
        return new Date(b.createdDateTime!).getTime() - new Date(a.createdDateTime!).getTime();
      })
      this.missingAttachmentQuery = this.service.missingAttachmentQueries[0];
      this.missingAttachmentQuery.id = undefined;
      this.selectedAssoServiceDocument = this.missingAttachmentQuery.assoServiceDocument;
    }

    this.tableValues = this.getAssoServiceDocument();
    this.refreshTable.next();
  }

  attachmentTypeForm = this.formBuilder.group({
  });

  selectAssoServiceDocument(assoServiceDocument: AssoServiceDocument) {
    if (this.selectedAssoServiceDocument)
      for (let att of this.selectedAssoServiceDocument)
        if (att.id == assoServiceDocument.id)
          return;
    this.selectedAssoServiceDocument.push(assoServiceDocument);
    this.setTableValues();
  }

  deleteAssoServiceDocument(index: number) {
    this.selectedAssoServiceDocument.splice(index, 1);
    this.setTableValues();
  }

  getAssoServiceDocument() {
    if (this.service) {
      let assos = [];
      if (!this.selectedAssoServiceDocument)
        return this.service.assoServiceDocuments;
      else if (this.service.assoServiceDocuments) {
        for (let serviceAsso of this.service.assoServiceDocuments) {
          let found = false;
          for (let selectedAsso of this.selectedAssoServiceDocument) {
            if (selectedAsso.typeDocument.code == serviceAsso.typeDocument.code) {
              found = true;
              break;
            }
          }
          if (!found)
            assos.push(serviceAsso);
        }
        return assos;
      }
    }
    return [];
  }

  getFormStatus(): boolean {
    return this.attachmentTypeForm.valid;
  }

  generateMail() {
    if (this.attachmentTypeForm.valid && this.selectedAssoServiceDocument.length > 0) {
      this.missingAttachmentQuery.assoServiceDocument = this.selectedAssoServiceDocument;
      this.missingAttachmentQueryService.generateMissingAttachmentMail(this.missingAttachmentQuery).subscribe(response => this.closeDialog());
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
