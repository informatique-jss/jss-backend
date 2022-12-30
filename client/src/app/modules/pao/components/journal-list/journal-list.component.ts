import { Component, OnInit } from '@angular/core';
import { formatDateForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from '../../../../services/app.service';
import { UploadAttachmentService } from '../../../miscellaneous/services/upload.attachment.service';
import { Journal } from '../../model/Journal';
import { JournalService } from '../../services/journal.service';

@Component({
  selector: 'journal-list',
  templateUrl: './journal-list.component.html',
  styleUrls: ['./journal-list.component.css']
})
export class JournalListComponent implements OnInit {

  constructor(
    private appService: AppService,
    private journalService: JournalService,
    private uploadAttachmentService: UploadAttachmentService,
  ) { }

  journals: Journal[] = [] as Array<Journal>;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  ngOnInit() {
    this.appService.changeHeaderTitle("Journaux");
    this.journalService.getJournals().subscribe(response => this.journals = response);

    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Numéro de journal" } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Description" } as SortTableColumn);
    this.displayedColumns.push({ id: "journalDate", fieldName: "journalDate", label: "Date de publication", valueFonction: formatDateForSortTable } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "feed", actionName: "Editer le journal", actionLinkFunction: (action: SortTableAction, element: any) => {
        if (element)
          return ['/journal/add', element.id];
        return undefined;
      }, display: true,
    } as SortTableAction);

    this.tableAction.push({
      actionIcon: "visibility", actionName: "Prévisualiser le journal", actionClick: (action: SortTableAction, element: any): void => {
        this.uploadAttachmentService.previewAttachment(element.attachments[0]);
      }, display: true
    } as SortTableAction);
    this.tableAction.push({
      actionIcon: "download", actionName: "Télécharger le journal", actionClick: (action: SortTableAction, element: any): void => {
        this.uploadAttachmentService.downloadAttachment(element.attachments[0]);
      }, display: true
    } as SortTableAction);

  }

  addJournal() {
    this.appService.openRoute(null, "/journal/add", null);
  }

}
