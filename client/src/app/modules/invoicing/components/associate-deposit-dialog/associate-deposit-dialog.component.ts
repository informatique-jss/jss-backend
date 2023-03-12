import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BILLED, INVOICING_PAYMENT_LIMIT_REFUND_EUROS } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { QuotationComponent } from 'src/app/modules/quotation/components/quotation/quotation.component';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { ITiers } from 'src/app/modules/tiers/model/ITiers';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { AppService } from 'src/app/services/app.service';
import { CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT } from '../../../../libs/Constants';
import { OrderingSearchResult } from '../../../quotation/model/OrderingSearchResult';
import { CustomerOrderService } from '../../../quotation/services/customer.order.service';
import { AssociationSummaryTable } from '../../model/AssociationSummaryTable';
import { Deposit } from '../../model/Deposit';
import { InvoiceSearchResult } from '../../model/InvoiceSearchResult';
import { PaymentAssociate } from '../../model/PaymentAssociate';
import { DepositService } from '../../services/deposit.service';
import { InvoiceSearchResultService } from '../../services/invoice.search.result.service';
import { InvoiceService } from '../../services/invoice.service';
import { AmountDialogComponent } from '../amount-dialog/amount-dialog.component';
import { getAmountPayed, getCustomerOrderForInvoice, getCustomerOrderForIQuotation, getCustomerOrderNameForITiers, getRemainingToPay } from '../invoice-tools';

@Component({
  selector: 'associate-deposit-dialog',
  templateUrl: './associate-deposit-dialog.component.html',
  styleUrls: ['./associate-deposit-dialog.component.css']
})
export class AssociateDepositDialogComponent implements OnInit, AfterContentChecked {

  customerOrder: CustomerOrder | undefined;
  deposit: Deposit | undefined;
  associationSummaryTable: AssociationSummaryTable[] = [] as Array<AssociationSummaryTable>;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  selectedRefundTiers: any | undefined;

  refreshTable: Subject<void> = new Subject<void>();

  CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT = CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT;
  INVOICING_PAYMENT_LIMIT_REFUND_EUROS: number = INVOICING_PAYMENT_LIMIT_REFUND_EUROS;

  invoiceStatusSend = this.constantService.getInvoiceStatusSend();
  invoiceStatusReceived = this.constantService.getInvoiceStatusReceived();
  paymentWayInbound = this.constantService.getPaymentWayInbound();

  getAmountRemaining = getRemainingToPay;

  constructor(public dialogRef: MatDialogRef<AssociateDepositDialogComponent>,
    private appService: AppService,
    public amountDialog: MatDialog,
    private depositService: DepositService,
    private customerOrderService: CustomerOrderService,
    private invoiceSearchResultService: InvoiceSearchResultService,
    private invoiceService: InvoiceService,
    private changeDetectorRef: ChangeDetectorRef,
    private constantService: ConstantService,
    private formBuilder: FormBuilder,
  ) { }

  refundForm = this.formBuilder.group({
    refundTiers: ['', Validators.required],
  });

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "deposit", fieldName: "deposit.id", label: "Acompte" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerOrder.id", label: "Commande" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoice.id", label: "Facture" } as SortTableColumn);
    this.displayedColumns.push({ id: "initialAmount", fieldName: "invoice", label: "Montant TTC", valueFonction: (element: any): string => { return this.getInitialAmount(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "initialPayed", fieldName: "invoice", label: "Montant déjà réglé", valueFonction: (element: any): string => { return this.getInitialPayedAmount(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "amountPayed", fieldName: "invoice", label: "Montant associé", valueFonction: (element: any): string => { return this.getAmountPayed(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "finalPayed", fieldName: "invoice", label: "Montant final réglé", valueFonction: (element: any): string => { return this.getFinalPayed(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "remainingToPay", fieldName: "invoice", label: "Montant restant à payer", valueFonction: (element: any): string => { return this.getRemainingToPay(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "isLettreeValidated", fieldName: "invoice", label: "Facture lettrée", valueFonction: (element: any): any => { if (element.invoice) { return (this.getRemainingToPay(element) <= 0 ? "Oui" : "Non"); } } } as SortTableColumn);
    this.displayedColumns.push({ id: "isDepositValidated", fieldName: "invoice", label: "Acompte validé", valueFonction: (element: any): any => { if (element.customerOrder) { return (this.getRemainingToPay(element) <= 0 && element.customerOrder ? "Oui" : "Non"); } } } as SortTableColumn);

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

    if (this.deposit && this.customerOrder) {
      this.associationSummaryTable.push({
        deposit: this.deposit, customerOrder: this.customerOrder, amountUsed: Math.min(this.getInitialAmount({ customerOrder: this.customerOrder }) - this.getInitialPayedAmount({ customerOrder: this.customerOrder }), this.deposit.depositAmount)
      } as AssociationSummaryTable)
    };

    this.selectedRefundTiers = this.getRefundCustomerOrder();
    this.refundForm.get("refundTiers")!.valueChanges.subscribe(
      (newValue) => {
        this.selectedRefundTiers = newValue as any;
      }
    )
  }

  onConfirm(): void {
    if (this.deposit) {
      let paymentAssociate = {} as PaymentAssociate;

      if (this.selectedRefundTiers == null && this.amountRemaining() > 0 && this.amountRemaining() > INVOICING_PAYMENT_LIMIT_REFUND_EUROS) {
        this.appService.displaySnackBar("Veuillez choisir un tiers à rembourser", true, 30);
        return;
      }
      paymentAssociate.affaireRefund = (this.selectedRefundTiers && this.selectedRefundTiers.rna != undefined) ? this.selectedRefundTiers : null;
      paymentAssociate.tiersRefund = (!this.getRefundCustomerOrder() || (this.getRefundCustomerOrder() as any).label) ? null : this.getRefundCustomerOrder() as Tiers;
      paymentAssociate.confrereRefund = ((!this.getRefundCustomerOrder() || this.getRefundCustomerOrder() as any).label) ? this.getRefundCustomerOrder() as Confrere : null;
      paymentAssociate.deposit = this.deposit;

      if (this.associationSummaryTable) {
        for (let asso of this.associationSummaryTable) {
          if (asso.invoice) {
            if (!paymentAssociate.invoices)
              paymentAssociate.invoices = [] as Array<Invoice>;
            if (!paymentAssociate.byPassAmount)
              paymentAssociate.byPassAmount = [] as Array<number>;
            paymentAssociate.invoices.push(asso.invoice);
            paymentAssociate.byPassAmount.push(asso.amountUsed);
          }
        }
        for (let asso of this.associationSummaryTable) {
          if (asso.customerOrder) {
            if (!paymentAssociate.customerOrders)
              paymentAssociate.customerOrders = [] as Array<CustomerOrder>;
            if (!paymentAssociate.byPassAmount)
              paymentAssociate.byPassAmount = [] as Array<number>;
            paymentAssociate.customerOrders.push(asso.customerOrder);
            paymentAssociate.byPassAmount.push(asso.amountUsed);
          }
        }
        this.depositService.associateDepositAndInvoiceAndCustomerOrder(paymentAssociate).subscribe(response => {
          this.dialogRef.close(true);
        })
      }
    }
  }

  onClose(): void {
    this.dialogRef.close(false);
  }

  refreshSummaryTables() {
    this.refreshTable.next();
  }

  associateInvoice(invoiceIn: InvoiceSearchResult) {
    this.invoiceService.getInvoiceById(invoiceIn.invoiceId).subscribe(invoice => {
      if (this.associationSummaryTable)
        for (let asso of this.associationSummaryTable)
          if (asso.invoice && asso.invoice.id == invoice.id) {
            this.appService.displaySnackBar("Cette facture est déjà associée à cet acompte", true, 15);
            return;
          }
      if (invoice.invoiceStatus.id != this.invoiceStatusSend.id) {
        this.appService.displaySnackBar("Veuillez choisir une facture au statut " + this.invoiceStatusSend.label, true, 15);
        return;
      }

      let customerOrderId: number = 0;
      if (invoice.confrere)
        customerOrderId = invoice.confrere.id
      if (invoice.responsable)
        customerOrderId = invoice.responsable.id
      if (invoice.tiers)
        customerOrderId = invoice.tiers.id

      if (!this.isSameCustomerOrder(customerOrderId)) {
        this.appService.displaySnackBar("Veuillez choisir une facture du même donneur d'ordre que les autres éléments associés à l'acompte", true, 15);
        return;
      }
      let amountDialogRef = this.amountDialog.open(AmountDialogComponent, {
        width: '100%'
      });
      let asso = { deposit: this.deposit, invoice: invoice } as AssociationSummaryTable;
      let maxAmount = Math.round((Math.min(this.getLeftMaxAmountPayed(asso), this.getInitialAmount(asso) - this.getInitialPayedAmount(asso))) * 100) / 100;
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
    })
  }

  associateOrder(orderIn: OrderingSearchResult) {
    this.customerOrderService.getCustomerOrder(orderIn.customerOrderId).subscribe(order => {
      if (this.associationSummaryTable)
        for (let asso of this.associationSummaryTable)
          if (asso.customerOrder && asso.customerOrder.id == order.id) {
            this.appService.displaySnackBar("Cette commande est déjà associée à cet acompte", true, 15);
            return;
          }

      let customerOrderId: number = 0;
      if (order.confrere)
        customerOrderId = order.confrere.id
      if (order.responsable)
        customerOrderId = order.responsable.id
      if (order.tiers)
        customerOrderId = order.tiers.id


      if (!this.isSameCustomerOrder(customerOrderId)) {
        this.appService.displaySnackBar("Veuillez choisir une commande du même donneur d'ordre que les autres éléments associés à l'acompte", true, 15);
        return;
      }

      let orderAs = order as CustomerOrder;
      if (orderAs && orderAs.customerOrderStatus && orderAs.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED) {
        this.appService.displaySnackBar("Il est impossible de choisir une commande abandonnée", true, 15);
        return;
      }

      let amountDialogRef = this.amountDialog.open(AmountDialogComponent, {
        width: '35%'
      });

      let asso = {} as AssociationSummaryTable;
      // If invoice found on CustomerOrder, associate invoice, not customer order
      if (orderAs && orderAs.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED) {
        this.invoiceSearchResultService.getInvoiceForCustomerOrder(orderAs).subscribe(invoices => {
          for (let invoice of invoices)
            if (invoice.invoiceStatus && invoice.invoiceStatusId == this.constantService.getInvoiceStatusSend().id) {
              this.invoiceService.getInvoiceById(invoice.invoiceId).subscribe(invoice => {
                asso = { deposit: this.deposit, invoice: invoice, } as AssociationSummaryTable;
              })
            }
        })
      } else
        asso = { deposit: this.deposit, customerOrder: order, } as AssociationSummaryTable;
      let maxAmount = Math.round((this.amountRemaining()) * 100) / 100;
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
    })
  }

  isSameCustomerOrder(newCustomerOrder: number): boolean {
    let currentCustomerOrder: ITiers | undefined = undefined;
    if (this.associationSummaryTable)
      for (let asso of this.associationSummaryTable) {
        let customerOrder;
        if (asso.invoice)
          customerOrder = getCustomerOrderForInvoice(asso.invoice);
        if (asso.customerOrder)
          customerOrder = getCustomerOrderForIQuotation(asso.customerOrder);
        if (currentCustomerOrder == undefined)
          currentCustomerOrder = customerOrder;
        if (currentCustomerOrder != undefined && newCustomerOrder != undefined && currentCustomerOrder?.id != newCustomerOrder)
          return false;
      }
    return true;
  }

  getCustomerOrderNameForITiers = getCustomerOrderNameForITiers;
  getRefundCustomerOrder(): ITiers | null {
    let customerOrder: ITiers | undefined = undefined;
    if (this.associationSummaryTable && this.associationSummaryTable.length > 0) {
      if (this.associationSummaryTable[0].invoice) {
        customerOrder = getCustomerOrderForInvoice(this.associationSummaryTable[0].invoice);
      } else {
        customerOrder = getCustomerOrderForIQuotation(this.associationSummaryTable[0].customerOrder);
      }
    } else {
      customerOrder = getCustomerOrderForIQuotation(this.customerOrder!);
    }
    // If responsable, return associate Tiers
    if ((customerOrder as any).tiers)
      return (customerOrder as any).tiers;

    let refundDocument = getDocument(this.constantService.getDocumentTypeRefund(), customerOrder);

    // If confrere check regie
    if (refundDocument && refundDocument.regie && refundDocument.regie.iban)
      return customerOrder;

    if (!refundDocument || !refundDocument.refundIBAN)
      return null;
    return customerOrder;
  }

  getAllAffaireRefundable(): Affaire[] {
    let affaires: Affaire[] = [] as Array<Affaire>;
    if (this.associationSummaryTable && this.associationSummaryTable.length > 0) {
      for (let asso of this.associationSummaryTable) {
        if (asso.invoice && asso.invoice.customerOrder) {
          affaires.push(...asso.invoice.customerOrder.assoAffaireOrders.filter(asso => asso.affaire && asso.affaire.paymentIban && asso.affaire.paymentIban != "").map(asso => asso.affaire));
        } else {
          affaires.push(...asso.customerOrder.assoAffaireOrders.filter(asso => asso.affaire && asso.affaire.paymentIban && asso.affaire.paymentIban != "").map(asso => asso.affaire));
        }
      }
    } else if (this.customerOrder) {
      affaires.push(...this.customerOrder.assoAffaireOrders.filter(asso => asso.affaire && asso.affaire.paymentIban && asso.affaire.paymentIban != "").map(asso => asso.affaire));
    }
    return affaires;
  }

  amountToPayCompletely() {
    let amount = 0;
    if (this.customerOrder && this.deposit) {
      let remainingToPay = Math.round((QuotationComponent.computePriceTotal(this.customerOrder) - QuotationComponent.computePayed(this.customerOrder)) * 100) / 100;
      amount = remainingToPay - Math.round(this.deposit.depositAmount * 100) / 100;
    }
    return amount;
  }

  amountRemaining(): number {
    if (this.deposit) {
      let amountRemaining = this.deposit.depositAmount;
      if (this.associationSummaryTable)
        for (let asso of this.associationSummaryTable)
          amountRemaining -= this.getAmountPayed(asso);
      return Math.round(amountRemaining * 100) / 100;
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
    let amount = 0;
    if (element) {
      if (element.invoice)
        amount = getAmountPayed(element.invoice);
      if (element.customerOrder)
        amount = QuotationComponent.computePayed(element.customerOrder);
    }
    // Do not consider current deposit
    if (this.deposit && element.customerOrder && this.customerOrder && element.customerOrder.id == this.customerOrder.id)
      amount -= this.deposit.depositAmount;
    return amount;
  }

  getAmountPayed(element: any): number {
    return element.amountUsed;
  }

  getLeftMaxAmountPayed(element: any): number {
    if (this.deposit) {
      let amountRemaining = this.deposit.depositAmount;
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
    return this.getAmountPayed(element) + this.getInitialPayedAmount(element);
  }

  getRemainingToPay(element: any): number {
    return Math.round(Math.max(0, this.getInitialAmount(element) - this.getFinalPayed(element)) * 100) / 100;
  }
}
