import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { INVOICING_STATUS_SENT, QUOTATION_STATUS_WAITING_DEPOSIT } from 'src/app/libs/Constants';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { QuotationComponent } from 'src/app/modules/quotation/components/quotation/quotation.component';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { AppService } from 'src/app/services/app.service';
import { AssociationSummaryTable } from '../../model/AssociationSummaryTable';
import { Payment } from '../../model/Payment';
import { AmountDialogComponent } from '../amount-dialog/amount-dialog.component';
import { InvoiceListComponent } from '../invoice-list/invoice-list.component';

@Component({
  selector: 'associate-payment-dialog',
  templateUrl: './associate-payment-dialog.component.html',
  styleUrls: ['./associate-payment-dialog.component.css']
})
export class AssociatePaymentDialogComponent implements OnInit {

  invoice: Invoice | undefined;
  payment: Payment | undefined;
  associationSummaryTable: AssociationSummaryTable[] = [] as Array<AssociationSummaryTable>;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  refreshTable: Subject<void> = new Subject<void>();

  QUOTATION_STATUS_WAITING_DEPOSIT = QUOTATION_STATUS_WAITING_DEPOSIT;
  INVOICING_STATUS_SENT = INVOICING_STATUS_SENT;

  getAmountRemaining = InvoiceListComponent.getAmountRemaining;

  constructor(public dialogRef: MatDialogRef<AssociatePaymentDialogComponent>,
    private appService: AppService,
    public amountDialog: MatDialog
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "payment", fieldName: "payment.id", label: "Paiement" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerOrder.id", label: "Commande" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoice.id", label: "Facture" } as SortTableColumn);
    this.displayedColumns.push({ id: "initialAmount", fieldName: "invoice", label: "Montant TTC", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string => { return this.getInitialAmount(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "initialPayed", fieldName: "invoice", label: "Montant déjà réglé", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string => { return this.getInitialPayedAmount(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "amountPayed", fieldName: "invoice", label: "Montant associé", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string => { return this.getAmountPayed(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "finalPayed", fieldName: "invoice", label: "Montant final réglé", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string => { return this.getFinalPayed(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "remainingToPay", fieldName: "invoice", label: "Montant restant à payer", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string => { return this.getRemainingToPay(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "isLettreeValidated", fieldName: "invoice", label: "Facture lettrée", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): any => { if (element.invoice) { return (this.getRemainingToPay(element) <= 0 ? "Oui" : "Non"); } } } as SortTableColumn);
    this.displayedColumns.push({ id: "isDepositValidated", fieldName: "invoice", label: "Acompte validé", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): any => { if (element.customerOrder) { return (this.getRemainingToPay(element) <= 0 && element.customerOrder ? "Oui" : "Non"); } } } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer l'association", actionClick: (action: SortTableAction, element: any): void => {
        if (element && this.associationSummaryTable)
          for (let asso of this.associationSummaryTable)
            if (asso.customerOrder && element.customerOrder && asso.customerOrder.id == element.customerOrder.id || asso.invoice && element.invoice && asso.invoice.id == element.invoice.id) {
              this.associationSummaryTable.splice(this.associationSummaryTable.indexOf(asso), 1);
              this.refreshSummaryTables();
              return;
            }
      }, display: true,
    } as SortTableAction);

    if (this.payment && this.invoice)
      this.associationSummaryTable.push({
        payment: this.payment, invoice: this.invoice, amountUsed: Math.min(this.invoice.totalPrice, this.payment.paymentAmount)
      } as AssociationSummaryTable);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onClose(): void {
    this.dialogRef.close(false);
  }

  refreshSummaryTables() {
    this.refreshTable.next();
  }

  associateInvoice(invoice: Invoice) {
    if (this.associationSummaryTable)
      for (let asso of this.associationSummaryTable)
        if (asso.invoice && asso.invoice.id == invoice.id) {
          this.appService.displaySnackBar("Cette facture est déjà associée à ce paiement", true, 15);
          return;
        }
    if (invoice.invoiceStatus.code != INVOICING_STATUS_SENT) {
      this.appService.displaySnackBar("Veuillez choisir une facture au statut Envoyé", true, 15);
      return;
    }
    let amountDialogRef = this.amountDialog.open(AmountDialogComponent, {
      width: '100%'
    });
    let asso = { payment: this.payment, invoice: invoice } as AssociationSummaryTable;
    let maxAmount = Math.min(this.getLeftMaxAmountPayed(asso), this.getInitialAmount(asso) - this.getInitialPayedAmount(asso));
    amountDialogRef.componentInstance.maxAmount = maxAmount;
    amountDialogRef.afterClosed().subscribe(response => {
      if (response != null) {
        asso.amountUsed = Math.min(parseFloat(response), maxAmount);
        this.associationSummaryTable.push(asso);
        this.refreshSummaryTables();
      } else {
        return;
      }
    });
  }

  associateOrder(order: CustomerOrder) {
    if (this.associationSummaryTable)
      for (let asso of this.associationSummaryTable)
        if (asso.customerOrder && asso.customerOrder.id == order.id) {
          this.appService.displaySnackBar("Cette commande est déjà associée à ce paiement", true, 15);
          return;
        }
    if (order.quotationStatus.code != QUOTATION_STATUS_WAITING_DEPOSIT) {
      this.appService.displaySnackBar("Veuillez choisir une commande au statut En attente d'accompte", true, 15);
      return;
    }
    let amountDialogRef = this.amountDialog.open(AmountDialogComponent, {
      width: '35%'
    });
    let asso = { payment: this.payment, customerOrder: order, } as AssociationSummaryTable;
    let maxAmount = Math.min(this.getLeftMaxAmountPayed(asso), this.getInitialAmount(asso) - this.getInitialPayedAmount(asso));
    amountDialogRef.componentInstance.maxAmount = maxAmount;
    amountDialogRef.afterClosed().subscribe(response => {
      if (response != null) {
        asso.amountUsed = Math.min(parseFloat(response), maxAmount);
        this.associationSummaryTable.push(asso);
        this.refreshSummaryTables();
      } else {
        return;
      }
    });
  }


  associateCustomerOrder(customerOrder: CustomerOrder) {
    this.associationSummaryTable.push({ payment: this.payment, customerOrder: customerOrder } as AssociationSummaryTable);
  }

  amountToPayCompletely() {
    let amount = 0;
    if (this.invoice && this.payment) {
      amount = Math.round(this.getAmountRemaining(this.invoice) * 100) / 100 - Math.round(this.payment.paymentAmount * 100) / 100;
    }
    console.log(amount);
    return amount;
  }

  amountRemaining(): number {
    if (this.payment) {
      let amountRemaining = this.payment.paymentAmount;
      if (this.associationSummaryTable)
        for (let asso of this.associationSummaryTable)
          amountRemaining -= this.getAmountPayed(asso);
      return amountRemaining;
    }
    return 0;
  }

  getInitialAmount(element: any): number {
    if (element) {
      if (element.invoice)
        return element.invoice.totalPrice;
      if (element.customerOrder)
        return QuotationComponent.computePriceTotal(element.customerOrder);
    }
    return 0;
  }

  getInitialPayedAmount(element: any): number {
    if (element) {
      if (element.invoice)
        return InvoiceListComponent.getAmountPayed(element.invoice);
      if (element.customerOrder)
        return QuotationComponent.computePayed(element.customerOrder);
    }
    return 0;
  }

  getAmountPayed(element: any): number {
    return element.amountUsed;
  }

  getLeftMaxAmountPayed(element: any): number {
    if (this.payment) {
      let amountRemaining = this.payment.paymentAmount;
      if (element && this.associationSummaryTable)
        for (let asso of this.associationSummaryTable)
          if (asso.invoice && (!element.invoice || element.invoice.id != asso.invoice.id) || asso.customerOrder && (!element.customerOrder || asso.customerOrder.id != element.customerOrder.id)) {
            amountRemaining -= asso.amountUsed;
          } else {
            return Math.min(amountRemaining, this.getInitialAmount(element) - this.getInitialPayedAmount(element))
          }
      return amountRemaining;
    }
    return 0;
  }

  getFinalPayed(element: any): number {
    return this.getInitialPayedAmount(element) + this.getAmountPayed(element);
  }

  getRemainingToPay(element: any): number {
    return Math.round(Math.max(0, this.getInitialAmount(element) - this.getFinalPayed(element)) * 100) / 100;
  }
}
