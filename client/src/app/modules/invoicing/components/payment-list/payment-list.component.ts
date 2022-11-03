import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
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

  @Output() actionBypass: EventEmitter<Payment> = new EventEmitter<Payment>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;
  paymentWayInbound = this.constantService.getPaymentWayInbound();

  constructor(
    private paymentService: PaymentService,
    private constantService: ConstantService,
    private paymentWayService: PaymentWayService,
    private appService: AppService,
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
    this.displayedColumns.push({ id: "isInternallyAssociated", fieldName: "isInternallyAssociated", label: "Associé dans Osiris", valueFonction: (element: any) => { return (element.invoice || element.customerOrder) ? "Oui" : "Non" } } as SortTableColumn);
    this.displayedColumns.push({ id: "isExternallyAssociated", fieldName: "isExternallyAssociated", label: "Associé hors Osiris", valueFonction: (element: any) => { return element.isExternallyAssociated ? "Oui" : "Non" } } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoices.id", label: "Facture associée", valueFonction: this.getInvoiceLabel, actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn);

    if (this.overrideIconAction == "") {
      this.tableAction.push({
        actionIcon: "merge_type", actionName: "Indiquer comme associé hors Osiris", actionClick: (action: SortTableAction, element: any) => {
          if ((!element.invoice && !element.customerOrder))
            this.paymentService.setExternallyAssociated(element as Payment).subscribe(response => {
              this.searchPayments();
            });
          else
            this.appService.displaySnackBar("Paiement déjà associé dans Osiris", true, 20);
        }, display: true,
      } as SortTableAction);
      this.tableAction.push({
        actionIcon: "remove_done", actionName: "Indiquer comme non associé hors Osiris", actionClick: (action: SortTableAction, element: any) => {
          if ((!element.invoice && !element.customerOrder && element.isExternallyAssociated))
            this.paymentService.unsetExternallyAssociated(element as Payment).subscribe(response => {
              this.searchPayments();
            });
          else
            this.appService.displaySnackBar("Paiement non indiqué associé hors Osiris", true, 20);
        }, display: true,
      } as SortTableAction);
    } else {
      this.tableAction.push({
        actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (action: SortTableAction, element: any) => {
          this.actionBypass.emit(element);
        }, display: true,
      } as SortTableAction);
    };

    this.paymentSearch.isHideAssociatedPayments = true;
  }

  paymentForm = this.formBuilder.group({
  });

  getActionLink(action: SortTableColumn, element: any) {
    if (element && action.id == "invoice" && element.invoices && element.invoices[0] && element.invoices.length == 1)
      return ['/invoicing', element.invoices[0].id];
    return undefined;
  }

  getInvoiceLabel(element: any) {
    if (element && element.invoice && element.invoice.id)
      return element.invoice.id;
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
