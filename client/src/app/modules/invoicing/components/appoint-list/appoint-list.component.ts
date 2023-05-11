import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Appoint } from '../../model/Appoint';
import { AppointService } from '../../services/appoint.service';

@Component({
  selector: 'appoint-list',
  templateUrl: './appoint-list.component.html',
  styleUrls: ['./appoint-list.component.css']
})
export class AppointListComponent implements OnInit {

  appoints: Appoint[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  searchLabel: string = "";

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private appointService: AppointService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° de l'appoint" } as SortTableColumn);
    this.displayedColumns.push({ id: "appointDate", fieldName: "appointDate", label: "Date d'émission", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn);
    this.displayedColumns.push({ id: "appointAmount", fieldName: "appointAmount", label: "Montant de l'appoint", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "originPayment", fieldName: "originPayment.id", label: "Paiement d'origine" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoice.id", label: "Facture d'origine", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture" } as SortTableColumn);
    this.displayedColumns.push({ id: "refund", fieldName: "refund", label: "N° de remboursement", valueFonction: (element: any) => { return (element.refunds && element.refunds.length > 0) ? element.refunds[0].id : "" } } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "account_balance_wallet", actionName: "Rembourser l'appoint", actionClick: (action: SortTableAction, element: any) => {
        if (element && (!element.refunds || !element.refunds.length) && element.appointAmount > 0) {
          this.appointService.refundAppoint(element as Appoint).subscribe(response => this.searchAppoints());
        }
        return undefined;
      }, display: true,
    } as SortTableAction);
  }

  appointForm = this.formBuilder.group({
  });

  getActionLink(action: SortTableColumn, element: any) {
    if (element && action.id == "invoice" && element.invoice)
      return ['/invoicing/view', element.invoice.id];
    return undefined;
  }

  searchAppoints() {
    if (this.appointForm.valid) {
      this.appointService.getAppoints(this.searchLabel).subscribe(response => {
        this.appoints = response.filter(appoint => appoint.appointAmount > 0);
      })
    }
  }

  refundAppoint(appoint: Appoint) {
  }
}
