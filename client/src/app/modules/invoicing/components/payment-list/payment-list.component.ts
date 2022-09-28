import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { PAYEMENT_WAY_INBOUND_CODE } from 'src/app/libs/Constants';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { Payment } from '../../model/Payment';
import { PaymentSearch } from '../../model/PaymentSearch';
import { PaymentService } from '../../services/payment.service';
import { PaymentWayService } from '../../services/payment.way.service';

@Component({
  selector: 'payment-list',
  templateUrl: './payment-list.component.html',
  styleUrls: ['./payment-list.component.css']
})
export class PaymentListComponent implements OnInit {
  paymentSearch: PaymentSearch = {} as PaymentSearch;
  payments: Payment[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  PAYEMENT_WAY_INBOUND_CODE = PAYEMENT_WAY_INBOUND_CODE;

  constructor(
    private appService: AppService,
    private paymentService: PaymentService,
    private paymentWayService: PaymentWayService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.putDefaultPeriod();
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° du paiement" } as SortTableColumn);
    this.displayedColumns.push({ id: "paymentWay", fieldName: "paymentWay.label", label: "Sens" } as SortTableColumn);
    this.displayedColumns.push({ id: "payemntDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "payemntAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoices.id", label: "Facture(s) associée(s)", valueFonction: this.getInvoiceLabel, actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "settings", actionName: "Voir le détail du paiement / associer", actionClick: (action: SortTableAction, element: any): void => {

      }, display: true,
    } as SortTableAction);
  }

  paymentForm = this.formBuilder.group({
  });

  getActionLink(action: SortTableColumn, element: any) {
    if (element && action.id == "invoice" && element.invoices && element.invoices[0] && element.invoices.length == 1)
      return ['/invoicing', element.invoices[0].id];
    return undefined;
  }

  getInvoiceLabel(element: any) {
    if (element && element.invoices)
      return element.invoices.map((e: { id: any; }) => e.id).join(", ");
    return "";
  }

  putDefaultPeriod() {
    if (!this.paymentSearch.startDate && !this.paymentSearch.endDate) {
      this.paymentSearch.startDate = new Date();
      this.paymentSearch.endDate = new Date();
      this.paymentSearch.startDate.setDate(this.paymentSearch.endDate.getDate() - 30);
    }
  }

  searchPayments() {
    if (this.paymentForm.valid && this.paymentSearch.startDate && this.paymentSearch.endDate) {
      this.paymentSearch.startDate = new Date(toIsoString(this.paymentSearch.startDate));
      this.paymentSearch.endDate = new Date(toIsoString(this.paymentSearch.endDate));
      this.paymentService.getPayments(this.paymentSearch).subscribe(response => {
        this.payments = response;
      })
    }
  }

  // TODO : à retirer avant la MEP !!
  currentPayment = {} as Payment;
  paymentForm2 = this.formBuilder.group({
  });
  addPayment() {
    this.paymentWayService.getPaymentWays().subscribe(response => {
      for (let paymentWay of response) {
        if (paymentWay.code == "INBOUND")
          this.currentPayment.paymentWay = paymentWay;
      }
      this.paymentService.addOrUpdatePayment(this.currentPayment).subscribe(response => {
        this.searchPayments();
        this.currentPayment = {} as Payment;
      })
    })

  }
}
