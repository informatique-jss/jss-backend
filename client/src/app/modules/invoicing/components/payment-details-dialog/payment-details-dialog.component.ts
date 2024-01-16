import { FlatTreeControl } from '@angular/cdk/tree';
import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { AccountingRecordSearch } from 'src/app/modules/accounting/model/AccountingRecordSearch';
import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { Provider } from 'src/app/modules/miscellaneous/model/Provider';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { BankTransfert } from 'src/app/modules/quotation/model/BankTransfert';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { BankTransfertService } from 'src/app/modules/quotation/services/bank.transfert.service';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { Payment } from '../../model/Payment';
import { PaymentTreeNode } from '../../model/PaymentTreeNode';
import { Refund } from '../../model/Refund';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'payment-details-dialog',
  templateUrl: './payment-details-dialog.component.html',
  styleUrls: ['./payment-details-dialog.component.css']
})
export class PaymentDetailsDialogComponent implements OnInit, AfterContentChecked {

  constructor(
    public dialogRef: MatDialogRef<PaymentDetailsDialogComponent>,
    private paymentService: PaymentService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private bankTransfertService: BankTransfertService,
    private constantService: ConstantService
  ) { }

  @ViewChild('tree') tree: any;
  @Input() payment: Payment | undefined;
  selectedPayment: Payment | undefined;
  paymentForm = this.formBuilder.group({});

  paymentTypeCheck = this.constantService.getPaymentTypeCheques();

  currentCustomerOrder: CustomerOrder | undefined;
  currentInvoice: Invoice | undefined;
  currentRefund: Refund | undefined;
  currentBankTransfert: BankTransfert | undefined;
  currentProvision: Provision | undefined;

  currentTiers: Tiers | undefined;
  currentResponsable: Responsable | undefined;
  currentConfrere: Confrere | undefined;
  currentAffaire: Affaire | undefined;
  currentCompetentAuthority: CompetentAuthority | undefined;
  currentProvider: Provider | undefined;

  searchAccountingRecord: AccountingRecordSearch | undefined;


  ngOnInit() {
    this.fetchPaymentDetails();
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (!this.selectedPayment)
      this.fetchPaymentDetails();
  }

  onConfirm(): void {
    this.dialogRef.close(null);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }

  fetchPaymentDetails() {
    if (this.payment) {
      this.paymentService.getPaymentById(this.payment.id).subscribe(payment => {
        let originPayment = this.getTopPayment(payment);

        if (this.selectedPayment) {
          this.payment = payment;
          this.selectNode({ name: this.getNodeName(this.selectedPayment) } as PaymentTreeNode);
          this.setTreeData();
        }

        if (originPayment.id == payment.id) {
          this.payment = payment;
          this.selectNode({ name: this.getNodeName(payment) } as PaymentTreeNode);
          this.setTreeData();
        }
        else {
          this.paymentService.getPaymentById(originPayment.id).subscribe(lastPayment => {
            this.payment = lastPayment;
            this.selectNode({ name: this.getNodeName(payment) } as PaymentTreeNode);
            this.setTreeData();
          })
        }
      });
    }
  }

  setSelectedPayment(payment: Payment) {
    this.selectedPayment = payment;
    this.currentCustomerOrder = undefined;
    this.currentInvoice = undefined;
    this.currentRefund = undefined;
    this.currentBankTransfert = undefined;
    this.currentProvision = undefined;

    this.currentTiers = undefined;
    this.currentResponsable = undefined;
    this.currentConfrere = undefined;
    this.currentAffaire = undefined;
    this.currentCompetentAuthority = undefined;
    this.currentProvider = undefined;

    this.searchAccountingRecord = undefined;

    setTimeout(() => {
      if (this.selectedPayment) {
        if (this.selectedPayment.customerOrder) {
          this.currentCustomerOrder = this.selectedPayment.customerOrder;
          if (this.selectedPayment.customerOrder.tiers)
            this.currentTiers = this.selectedPayment.customerOrder.tiers;
          if (this.selectedPayment.customerOrder.responsable)
            this.currentResponsable = this.selectedPayment.customerOrder.responsable;
          if (this.selectedPayment.customerOrder.confrere)
            this.currentConfrere = this.selectedPayment.customerOrder.confrere;
          if (this.selectedPayment.customerOrder.assoAffaireOrders && this.selectedPayment.customerOrder.assoAffaireOrders.length == 1)
            this.currentAffaire = this.selectedPayment.customerOrder.assoAffaireOrders[0].affaire;
        }

        if (this.selectedPayment.invoice) {
          this.currentInvoice = this.selectedPayment.invoice;
          if (this.selectedPayment.invoice.customerOrder)
            this.currentCustomerOrder = this.selectedPayment.invoice.customerOrder;
          if (this.selectedPayment.invoice.tiers)
            this.currentTiers = this.selectedPayment.invoice.tiers;
          if (this.selectedPayment.invoice.responsable)
            this.currentResponsable = this.selectedPayment.invoice.responsable;
          if (this.selectedPayment.invoice.confrere)
            this.currentConfrere = this.selectedPayment.invoice.confrere;
          if (this.selectedPayment.invoice.provider)
            this.currentProvider = this.selectedPayment.invoice.provider;
          if (this.selectedPayment.invoice.competentAuthority)
            this.currentCompetentAuthority = this.selectedPayment.invoice.competentAuthority;
          if (this.selectedPayment.invoice.customerOrder && this.selectedPayment.invoice.customerOrder.assoAffaireOrders && this.selectedPayment.invoice.customerOrder.assoAffaireOrders.length == 1)
            this.currentAffaire = this.selectedPayment.invoice.customerOrder.assoAffaireOrders[0].affaire;
        }
        if (this.selectedPayment.refund) {
          this.currentRefund = this.selectedPayment.refund;
          if (this.selectedPayment.refund.invoice)
            this.currentInvoice = this.selectedPayment.refund.invoice;
          if (this.selectedPayment.refund.customerOrder)
            this.currentCustomerOrder = this.selectedPayment.refund.customerOrder;
          if (this.selectedPayment.refund.affaire)
            this.currentAffaire = this.selectedPayment.refund.affaire;
          if (this.selectedPayment.refund.tiers)
            this.currentTiers = this.selectedPayment.refund.tiers;
          if (this.selectedPayment.refund.confrere)
            this.currentConfrere = this.selectedPayment.refund.confrere;
        }
        if (this.selectedPayment.bankTransfert) {
          this.currentBankTransfert = this.selectedPayment.bankTransfert;
          if (this.selectedPayment.bankTransfert.invoices)
            this.currentInvoice = this.selectedPayment.bankTransfert.invoices[0];
          if (this.selectedPayment.bankTransfert.customerOrder)
            this.currentCustomerOrder = this.selectedPayment.bankTransfert.customerOrder;

          if (this.selectedPayment.bankTransfert.customerOrder.tiers)
            this.currentTiers = this.selectedPayment.invoice.tiers;
          if (this.selectedPayment.bankTransfert.customerOrder.responsable)
            this.currentResponsable = this.selectedPayment.bankTransfert.customerOrder.responsable;
          if (this.selectedPayment.bankTransfert.customerOrder.confrere)
            this.currentConfrere = this.selectedPayment.bankTransfert.customerOrder.confrere;
          if (this.selectedPayment.bankTransfert.customerOrder.assoAffaireOrders && this.selectedPayment.bankTransfert.customerOrder.assoAffaireOrders.length == 1)
            this.currentAffaire = this.selectedPayment.bankTransfert.customerOrder.assoAffaireOrders[0].affaire;

          if (this.selectedPayment.bankTransfert.invoices[0]) {
            if (this.selectedPayment.bankTransfert.invoices[0].tiers)
              this.currentTiers = this.selectedPayment.bankTransfert.invoices[0].tiers;
            if (this.selectedPayment.bankTransfert.invoices[0].responsable)
              this.currentResponsable = this.selectedPayment.bankTransfert.invoices[0].responsable;
            if (this.selectedPayment.bankTransfert.invoices[0].confrere)
              this.currentConfrere = this.selectedPayment.bankTransfert.invoices[0].confrere;
            if (this.selectedPayment.bankTransfert.invoices[0].provider)
              this.currentProvider = this.selectedPayment.bankTransfert.invoices[0].provider;
            if (this.selectedPayment.bankTransfert.invoices[0].competentAuthority)
              this.currentCompetentAuthority = this.selectedPayment.bankTransfert.invoices[0].competentAuthority;
          }
        }

        if (this.selectedPayment.provision) {
          this.currentProvision = this.selectedPayment.provision;
          if (this.selectedPayment.provision.assoAffaireOrder)
            this.currentCustomerOrder = this.selectedPayment.provision.assoAffaireOrder.customerOrder;

          this.currentAffaire = this.selectedPayment.provision.assoAffaireOrder.affaire;

          if (this.selectedPayment.provision.assoAffaireOrder.customerOrder.tiers)
            this.currentTiers = this.selectedPayment.provision.assoAffaireOrder.customerOrder.tiers;
          if (this.selectedPayment.provision.assoAffaireOrder.customerOrder.responsable)
            this.currentResponsable = this.selectedPayment.provision.assoAffaireOrder.customerOrder.responsable;
          if (this.selectedPayment.provision.assoAffaireOrder.customerOrder.confrere)
            this.currentConfrere = this.selectedPayment.provision.assoAffaireOrder.customerOrder.confrere;
        }

        this.searchAccountingRecord = {} as AccountingRecordSearch;
        this.searchAccountingRecord.idPayment = this.selectedPayment.id;
        if (this.currentBankTransfert)
          this.searchAccountingRecord.idBankTransfert = this.currentBankTransfert.id;
        if (this.currentCustomerOrder)
          this.searchAccountingRecord.idCustomerOrder = this.currentCustomerOrder.id;
        if (this.currentInvoice)
          this.searchAccountingRecord.idInvoice = this.currentInvoice.id;
        if (this.currentRefund)
          this.searchAccountingRecord.idRefund = this.currentRefund.id;
      }
    }, 0);
  }

  toggleBankTransfertExport(selected: any) {
    if (this.currentBankTransfert) {
      if (selected)
        this.bankTransfertService.selectBankTransfertForExport(this.currentBankTransfert).subscribe(response => this.fetchPaymentDetails());
      else
        this.bankTransfertService.unselectBankTransfertForExport(this.currentBankTransfert).subscribe(response => this.fetchPaymentDetails());
    }
  }


  /** Tree management */

  selectNode(node: PaymentTreeNode) {
    if (this.payment)
      this.selectPayment(node, this.payment);
  }

  selectPayment(node: PaymentTreeNode, payment: Payment) {
    if (payment)
      if (node.name.indexOf(this.getNodeName(payment)) >= 0
        || this.getNodeName(payment).indexOf(node.name) >= 0) {
        this.setSelectedPayment(payment);
        return;
      }
    for (let childPayment of payment.childrenPayments) {
      this.selectPayment(node, childPayment);
    }
    return;
  }

  setTreeData() {
    if (this.payment) {
      this.dataSource.data = [this.payment];
    }
    this.treeControl.expandAll();
  }

  getTopPayment(payment: Payment): Payment {
    if (payment.originPayment)
      return this.getTopPayment(payment.originPayment);
    return payment;
  }

  private _transformer = (node: Payment, level: number) => {
    return {
      expandable: !!node.childrenPayments && node.childrenPayments.length > 0,
      name: this.getNodeName(node),
      level: level,
    };
  };

  getNodeName(payment: Payment) {
    let label = payment.id + "";
    if (payment.bankTransfert)
      label += " / virement n°" + payment.bankTransfert.id;
    if (payment.invoice)
      label += " / facture n°" + payment.invoice.id;
    if (payment.customerOrder)
      label += " / commande n°" + payment.customerOrder.id;
    if (payment.refund)
      label += " / remboursement n°" + payment.refund.id;
    return label;
  }

  treeControl = new FlatTreeControl<PaymentTreeNode>(
    node => node.level,
    node => node.expandable,
  );

  treeFlattener = new MatTreeFlattener(
    this._transformer,
    node => node.level,
    node => node.expandable,
    node => node.childrenPayments,
  );

  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  hasChild = (_: number, node: PaymentTreeNode) => node.expandable;

}
