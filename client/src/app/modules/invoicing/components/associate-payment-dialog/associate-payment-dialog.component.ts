import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN, INVOICING_PAYMENT_LIMIT_REFUND_EUROS } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { instanceOfConfrere } from 'src/app/libs/TypeHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { QuotationComponent } from 'src/app/modules/quotation/components/quotation/quotation.component';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { OrderingSearchResult } from 'src/app/modules/quotation/model/OrderingSearchResult';
import { ITiers } from 'src/app/modules/tiers/model/ITiers';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { AppService } from 'src/app/services/app.service';
import { CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT } from '../../../../libs/Constants';
import { CustomerOrderService } from '../../../quotation/services/customer.order.service';
import { Responsable } from '../../../tiers/model/Responsable';
import { AssociationSummaryTable } from '../../model/AssociationSummaryTable';
import { InvoiceSearch } from '../../model/InvoiceSearch';
import { InvoiceSearchResult } from '../../model/InvoiceSearchResult';
import { Payment } from '../../model/Payment';
import { PaymentAssociate } from '../../model/PaymentAssociate';
import { InvoiceService } from '../../services/invoice.service';
import { PaymentService } from '../../services/payment.service';
import { AmountDialogComponent } from '../amount-dialog/amount-dialog.component';
import { getCustomerOrderForIQuotation, getCustomerOrderForInvoice, getCustomerOrderNameForITiers, getRemainingToPay } from '../invoice-tools';

@Component({
  selector: 'associate-payment-dialog',
  templateUrl: './associate-payment-dialog.component.html',
  styleUrls: ['./associate-payment-dialog.component.css']
})
export class AssociatePaymentDialogComponent implements OnInit {

  customerOrder: CustomerOrder | undefined;
  invoice: Invoice | undefined;
  payment: Payment | undefined;
  associations: AssociationSummaryTable[] = [] as Array<AssociationSummaryTable>;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  selectedRefundTiers: Tiers | undefined;
  selectedRefundConfrere: Confrere | undefined;
  selectedRefundAffaire: Affaire | undefined;
  tiersOrder: ITiers | undefined | null;

  refreshTable: Subject<void> = new Subject<void>();

  CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT = CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT;
  CUSTOMER_ORDER_STATUS_OPEN = CUSTOMER_ORDER_STATUS_OPEN;
  INVOICING_PAYMENT_LIMIT_REFUND_EUROS: number = INVOICING_PAYMENT_LIMIT_REFUND_EUROS;

  invoiceStatusSend = this.constantService.getInvoiceStatusSend();
  invoiceStatusCreditNoteReceived = this.constantService.getInvoiceStatusCreditNoteReceived();
  invoiceStatusCreditNoteEmited = this.constantService.getInvoiceStatusCreditNoteEmited();
  invoiceStatusReceived = this.constantService.getInvoiceStatusReceived();

  getAmountRemaining = getRemainingToPay;

  doNotInitializeAsso: boolean = false;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;

  constructor(public dialogRef: MatDialogRef<AssociatePaymentDialogComponent>,
    private appService: AppService,
    private invoiceService: InvoiceService,
    public amountDialog: MatDialog,
    private paymentService: PaymentService,
    private customerOrderService: CustomerOrderService,
    private constantService: ConstantService,
    private formBuilder: FormBuilder,
  ) { }

  refundForm = this.formBuilder.group({
    refundTiers: ['', Validators.required],
  });

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "payment", fieldName: "payment.id", label: "Paiement" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerOrder.id", label: "Commande" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoice.id", label: "Facture" } as SortTableColumn);
    this.displayedColumns.push({ id: "initialAmount", fieldName: "invoice", label: "Montant TTC", valueFonction: (element: any): string => { return this.getInitialAmount(element) + " €"; } } as SortTableColumn);
    this.displayedColumns.push({ id: "amountPayed", fieldName: "invoice", label: "Montant associé", valueFonction: (element: any): string => { return this.getAmountAssociated(element) + " €"; } } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer l'association", actionClick: (action: SortTableAction, element: any): void => {
        if (element && this.associations)
          for (let asso of this.associations)
            if (asso.customerOrder && element.customerOrder && asso.customerOrder.id == element.customerOrder.id || asso.invoice && element.invoice && asso.invoice.id == element.invoice.id) {
              this.associations.splice(this.associations.indexOf(asso), 1);
              this.refreshSummaryTables();
              return;
            }
      }, display: true,
    } as SortTableAction);

    if (this.payment && this.invoice && !this.doNotInitializeAsso) {
      this.associateInvoice(this.invoice);
    }

    if (this.payment && this.customerOrder && !this.doNotInitializeAsso)
      this.associateOrder(this.customerOrder);

    this.tiersOrder = this.getTiersOrder();

    if (this.payment) {
      if (this.payment.paymentAmount > 0) {
        this.invoiceSearch.minAmount = 0;
      } else {
        this.invoiceSearch.maxAmount = 0;
      }
    }
  }

  onConfirm(): void {
    if (this.payment && this.tiersOrder) {
      let paymentAssociate = {} as PaymentAssociate;

      if (this.selectedRefundTiers == null && this.selectedRefundAffaire == null && this.selectedRefundConfrere == null && this.getBalance() > INVOICING_PAYMENT_LIMIT_REFUND_EUROS) {
        this.appService.displaySnackBar("Veuillez choisir un tiers à rembourser", true, 30);
        return;
      }
      paymentAssociate.affaireRefund = this.selectedRefundAffaire;
      paymentAssociate.tiersRefund = this.selectedRefundTiers;
      paymentAssociate.confrereRefund = this.selectedRefundConfrere;
      paymentAssociate.tiersOrder = this.tiersOrder as Tiers;
      paymentAssociate.payment = this.payment;

      if (this.associations) {
        // sort to put invoice first, they will be associated first in the backend
        this.associations.sort((a: AssociationSummaryTable, b: AssociationSummaryTable) => {
          if (a.invoice && !b.invoice) return -1;
          if (!a.invoice && b.invoice) return 1;
          return a.amountUsed - b.amountUsed;
        })
        for (let asso of this.associations) {
          if (!paymentAssociate.byPassAmount)
            paymentAssociate.byPassAmount = [] as Array<number>;

          if (asso.invoice) {
            if (!paymentAssociate.invoices)
              paymentAssociate.invoices = [] as Array<Invoice>;
            paymentAssociate.invoices.push(asso.invoice);
          }
          if (asso.customerOrder) {
            if (!paymentAssociate.customerOrders)
              paymentAssociate.customerOrders = [] as Array<CustomerOrder>;
            paymentAssociate.customerOrders.push(asso.customerOrder);
          }
          paymentAssociate.byPassAmount.push(this.getAmountAssociated(asso));
        }
        this.paymentService.associatePaymentAndInvoiceAndCustomerOrder(paymentAssociate).subscribe(response => {
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

  associateInvoiceFromSearch(invoiceIn: InvoiceSearchResult) {
    this.invoiceService.getInvoiceById(invoiceIn.invoiceId).subscribe(invoice => this.associateInvoice(invoice));
  }

  associateInvoice(invoice: Invoice) {
    if (this.associations)
      for (let asso of this.associations)
        if (asso.invoice && asso.invoice.id == invoice.id) {
          this.appService.displaySnackBar("Cette facture est déjà associée à ce paiement", true, 15);
          return;
        }

    if (this.invoice && invoice.invoiceStatus && invoice.invoiceStatus.id == this.invoiceStatusCreditNoteEmited.id) {
      this.appService.displaySnackBar("Impossible d'associer un paiement avec une facture au status " + this.invoice.invoiceStatus.label, true, 15);
      return;
    }

    if (this.payment) {
      if (this.payment.paymentAmount > 0) {
        if (invoice.invoiceStatus.id != this.invoiceStatusSend.id && invoice.invoiceStatus.id != this.invoiceStatusCreditNoteReceived.id) {
          this.appService.displaySnackBar("Veuillez choisir une facture au statut " + this.invoiceStatusSend.label, true, 15);
          return;
        }
      } else {
        if (invoice.invoiceStatus.id != this.invoiceStatusReceived.id) {
          this.appService.displaySnackBar("Veuillez choisir une facture au statut " + this.invoiceStatusReceived.label, true, 15);
          return;
        }
        if (this.payment && Math.round(invoice.totalPrice * 100) != Math.abs(Math.round(this.payment.paymentAmount * 100))) {
          this.appService.displaySnackBar("Veuillez choisir une facture avec un total de " + this.payment.paymentAmount + " €", true, 15);
          return;
        }
        if (this.payment && invoice.manualPaymentType.id != this.payment.paymentType.id) {
          this.appService.displaySnackBar("Le type de réglement de la facture et le type de paiement doivent être identiques", true, 15);
          return;
        }
      }
    }

    if (!this.isSameCustomerOrder(getCustomerOrderForInvoice(invoice))) {
      this.appService.displaySnackBar("Veuillez choisir une facture du même donneur d'ordre que les autres éléments associés au paiement", true, 15);
      return;
    }
    let amountDialogRef = this.amountDialog.open(AmountDialogComponent, {
      width: '100%'
    });

    let maxAmount = this.getBalance();
    if (invoice.totalPrice > 0 && getRemainingToPay(invoice) < maxAmount)
      maxAmount = getRemainingToPay(invoice);
    else if (invoice.totalPrice < 0 && -getRemainingToPay(invoice) > maxAmount)
      maxAmount = - getRemainingToPay(invoice);

    amountDialogRef.componentInstance.label = "Indiquer le montant à utiliser (max : " + Math.round(maxAmount) + " €) :";
    amountDialogRef.componentInstance.maxAmount = Math.round(maxAmount * 100) / 100;
    amountDialogRef.afterClosed().subscribe(response => {
      if (response != null) {
        let asso = { payment: this.payment, invoice: invoice } as AssociationSummaryTable;
        asso.amountUsed = parseFloat(response);
        this.associations.push(asso);
        if (!this.tiersOrder || !this.tiersOrder.id)
          this.tiersOrder = this.getTiersOrder();
        this.refreshSummaryTables();
      } else {
        return;
      }
    });
  }

  associateOrderFromSearch(orderIn: OrderingSearchResult) {
    this.customerOrderService.getCustomerOrder(orderIn.customerOrderId).subscribe(order => this.associateOrder(order as CustomerOrder));
  }

  associateOrder(order: CustomerOrder) {
    if (this.associations)
      for (let asso of this.associations)
        if (asso.customerOrder && asso.customerOrder.id == order.id) {
          this.appService.displaySnackBar("Cette commande est déjà associée à ce paiement", true, 15);
          return;
        }

    if (!this.isSameCustomerOrder(getCustomerOrderForIQuotation(order))) {
      this.appService.displaySnackBar("Veuillez choisir une commande du même donneur d'ordre que les autres éléments associés au paiement", true, 15);
      return;
    }

    if (order.customerOrderStatus && order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED) {
      this.appService.displaySnackBar("Il est impossible de choisir une commande abandonnée", true, 15);
      return;
    }

    if (order.customerOrderStatus && order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED) {
      this.appService.displaySnackBar("Il est impossible de choisir une commande facturée", true, 15);
      return;
    }

    let amountDialogRef = this.amountDialog.open(AmountDialogComponent, {
      width: '35%'
    });
    let maxAmount = this.getBalance();
    amountDialogRef.componentInstance.label = "Indiquer le montant à utiliser (max : " + maxAmount + " €) :";
    amountDialogRef.componentInstance.maxAmount = Math.round(maxAmount * 100) / 100;
    amountDialogRef.afterClosed().subscribe(response => {
      if (response != null) {
        let asso = { payment: this.payment, customerOrder: order, } as AssociationSummaryTable;
        asso.amountUsed = parseFloat(response);
        this.associations.push(asso);
        this.refreshSummaryTables();
        if (!this.tiersOrder || !this.tiersOrder.id)
          this.tiersOrder = this.getTiersOrder();
      } else {
        return;
      }
    });
  }

  isSameCustomerOrder(newCustomerOrder: ITiers): boolean {
    let currentCustomerOrder: ITiers | undefined = undefined;

    // If responsable, consider tiers
    if ((newCustomerOrder as Responsable).tiers)
      newCustomerOrder = (newCustomerOrder as Responsable).tiers;

    if (this.associations)
      for (let asso of this.associations) {
        let customerOrder;
        if (asso.invoice)
          customerOrder = getCustomerOrderForInvoice(asso.invoice);
        if (asso.customerOrder)
          customerOrder = getCustomerOrderForIQuotation(asso.customerOrder);
        if (currentCustomerOrder == undefined)
          currentCustomerOrder = customerOrder;

        if ((currentCustomerOrder as Responsable).tiers)
          currentCustomerOrder = (currentCustomerOrder as Responsable).tiers;

        if (currentCustomerOrder != undefined && newCustomerOrder != undefined && currentCustomerOrder?.id != newCustomerOrder.id)
          return false;
      }
    return true;
  }

  getCustomerOrderNameForITiers = getCustomerOrderNameForITiers;

  getTiersOrder(): ITiers | null {
    let customerOrder: ITiers | undefined = undefined;
    if (this.associations && this.associations.length > 0) {
      if (this.associations[0].invoice) {
        customerOrder = getCustomerOrderForInvoice(this.associations[0].invoice);
      } else {
        customerOrder = getCustomerOrderForIQuotation(this.associations[0].customerOrder);
      }
    } else if (this.invoice) {
      customerOrder = getCustomerOrderForInvoice(this.invoice!);
    } else if (this.customerOrder) {
      customerOrder = getCustomerOrderForIQuotation(this.customerOrder);
    } else {
      return null;
    }
    // If responsable, return associate Tiers
    if ((customerOrder as any).tiers)
      customerOrder = ((customerOrder as any).tiers as Tiers);

    return customerOrder;
  }

  getRefundCustomerOrder(): ITiers | null {
    let customerOrder = this.getTiersOrder();

    if (!customerOrder)
      return null;

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
    if (this.associations && this.associations.length > 0) {
      for (let asso of this.associations) {
        if (asso.invoice && asso.invoice.customerOrder) {
          affaires.push(...asso.invoice.customerOrder.assoAffaireOrders.filter(asso => asso.affaire && asso.affaire.paymentIban && asso.affaire.paymentIban != "").map(asso => asso.affaire));
        } else {
          affaires.push(...asso.customerOrder.assoAffaireOrders.filter(asso => asso.affaire && asso.affaire.paymentIban && asso.affaire.paymentIban != "").map(asso => asso.affaire));
        }
      }
    } else if (this.invoice && this.invoice.customerOrder) {
      affaires.push(...this.invoice!.customerOrder.assoAffaireOrders.filter(asso => asso.affaire && asso.affaire.paymentIban && asso.affaire.paymentIban != "").map(asso => asso.affaire));
    }
    return affaires;
  }

  getInitialAmount(element: any): number {
    let total = 0;
    if (element) {
      if (element.invoice)
        return getRemainingToPay(element.invoice);
      if (element.customerOrder)
        total = QuotationComponent.computePriceTotal(element.customerOrder);
    }
    return total;
  }

  getAmountAssociated(element: any): number {
    return element.amountUsed;
  }

  getBalance() {
    let balance = 0;
    if (this.payment) {
      balance = this.payment.paymentAmount;
      for (let asso of this.associations) {
        balance -= this.getAmountAssociated(asso);
      }
    }
    return Math.round(balance * 100) / 100;
  }

  selectRefundTiers() {
    let refundTiers = this.getRefundCustomerOrder();
    if (refundTiers) {
      if (instanceOfConfrere(refundTiers)) {
        this.selectedRefundConfrere = refundTiers;
        this.selectedRefundAffaire = undefined;
        this.selectedRefundTiers = undefined;
      } else {
        this.selectedRefundTiers = refundTiers as Tiers;
        this.selectedRefundAffaire = undefined;
        this.selectedRefundConfrere = undefined;
      }
    }
  }

  selectRefundAffaire(affaire: Affaire) {
    if (affaire) {
      this.selectedRefundAffaire = affaire;
      this.selectedRefundConfrere = undefined;
      this.selectedRefundTiers = undefined;
    }
  }

  cancelRefundChoice() {
    this.selectedRefundAffaire = undefined;
    this.selectedRefundConfrere = undefined;
    this.selectedRefundTiers = undefined;
  }
}
