import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { CustomerOrder } from '../../model/CustomerOrder';
import { MissingAttachmentQuery } from '../../model/MissingAttachmentQuery';
import { MissingAttachmentQueryService } from '../../services/missing-attachment-query.service';
import { ServiceService } from '../../services/service.service';
import { MissingAttachmentMailDialogComponent } from '../select-attachment-type-dialog/missing-attachment-mail-dialog.component';

@Component({
  selector: 'missing-attachment-queries',
  templateUrl: './missing-attachment-queries.component.html',
  styleUrls: ['./missing-attachment-queries.component.css']
})
export class MissingAttachmentQueriesComponent implements OnInit {

  @Input() customerOrder: CustomerOrder | undefined;
  queries: MissingAttachmentQuery[][] = [];

  constructor(
    private formBuilder: FormBuilder,
    private serviceService: ServiceService,
    private missingAttachmentQueryService: MissingAttachmentQueryService,
    private appService: AppService,
    private constantService: ConstantService,
    public missingAttachmentMailDialog: MatDialog,
  ) { }

  getServiceLabel(service: Service) {
    return this.serviceService.getServiceLabel(service, false, this.constantService.getServiceTypeOther());
  }
  missingAttachmentForm = this.formBuilder.group({});

  displayedColumns: SortTableColumn<MissingAttachmentQuery>[] = [];
  tableActions: SortTableAction<MissingAttachmentQuery>[] = [] as Array<SortTableAction<MissingAttachmentQuery>>;

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "createdDateTime", fieldName: "createdDateTime", label: "Créée le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<MissingAttachmentQuery>);
    this.displayedColumns.push({ id: "firstCustomerReminderDateTime", fieldName: "firstCustomerReminderDateTime", label: "Première relance le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<MissingAttachmentQuery>);
    this.displayedColumns.push({ id: "secondCustomerReminderDateTime", fieldName: "secondCustomerReminderDateTime", label: "Seconde relance le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<MissingAttachmentQuery>);
    this.displayedColumns.push({ id: "thirdCustomerReminderDateTime", fieldName: "thirdCustomerReminderDateTime", label: "Troisième relance le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<MissingAttachmentQuery>);


    this.tableActions.push({
      actionIcon: "fast_forward", actionName: "Envoyer la relance immédiatement", actionClick: (column: SortTableAction<MissingAttachmentQuery>, element: MissingAttachmentQuery, event: any): void => {
        if (element && element.id && (element.thirdCustomerReminderDateTime == null || element.thirdCustomerReminderDateTime == undefined)) {
          this.missingAttachmentQueryService.sendMissingAttachmentQueryImmediatly(element).subscribe(response => {
            this.appService.displaySnackBar("La relance sera envoyée dans quelques secondes", false, 10);
          })
        }
      }, display: true
    } as SortTableAction<MissingAttachmentQuery>);
    this.tableActions.push({
      actionIcon: "visibility", actionName: "Voir la demande", actionClick: (column: SortTableAction<MissingAttachmentQuery>, element: MissingAttachmentQuery, event: any): void => {
        if (element && element.id) {
          const dialogRef = this.missingAttachmentMailDialog.open(MissingAttachmentMailDialogComponent, {
            width: "80%",
            height: "90%",
          });

          dialogRef.componentInstance.editMode = false;
          dialogRef.componentInstance.missingAttachmentQuery = element;
        }
      }, display: true
    } as SortTableAction<MissingAttachmentQuery>);

    this.getValues();

  }

  getValues() {
    if (this.customerOrder && this.customerOrder.assoAffaireOrders)
      for (let asso of this.customerOrder.assoAffaireOrders)
        if (asso.services)
          for (let service of asso.services)
            if (service) {
              this.missingAttachmentQueryService.getMissingAttachmentQueriesForService(service.id).subscribe(response => {
                if (response) {
                  this.queries[service.id] = response;
                  this.queries[service.id].sort(function (a: MissingAttachmentQuery, b: MissingAttachmentQuery) {
                    return new Date(b.createdDateTime!).getTime() - new Date(a.createdDateTime!).getTime();
                  });
                }
              })
            }
  }

}


