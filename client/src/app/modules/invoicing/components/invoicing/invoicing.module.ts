
import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from "@angular/material/dialog";
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from "@angular/material/expansion";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatRadioModule } from '@angular/material/radio';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTabsModule } from "@angular/material/tabs";
import { MatTooltipModule } from "@angular/material/tooltip";
import { RouterModule, Routes } from "@angular/router";
import { PaymentListComponent } from "src/app/modules/quotation/components/payment-list/payment-list.component";
import { QuotationModule } from "src/app/modules/quotation/components/quotation/quotation.module";
import { MiscellaneousModule } from "../../../miscellaneous/components/miscellaneous/miscellaneous.module";
import { AddInvoiceComponent } from "../add-invoice/add-invoice.component";
import { AddPaymentComponent } from "../add-payment/add-payment.component";
import { AmountDialogComponent } from "../amount-dialog/amount-dialog.component";
import { AssociatePaymentDialogComponent } from "../associate-payment-dialog/associate-payment-dialog.component";
import { BankTransfertListComponent } from '../bank-transfert-list/bank-transfert-list.component';
import { DirectDebitTransfertListComponent } from '../direct-debit-transfert-list/direct-debit-transfert-list.component';
import { InvoiceDetailsComponent } from "../invoice-details/invoice-details.component";
import { InvoiceListComponent } from "../invoice-list/invoice-list.component";
import { InvoicePaymentTableComponent } from "../invoice-payment-table/invoice-payment-table.component";
import { InvoicePaymentComponent } from "../invoice-payment/invoice-payment.component";
import { ReceiptReconciliationEditDialogComponent } from "../receipt-reconciliation-edit-dialog/receipt-reconciliation-edit-dialog.component";
import { ReceiptReconciliationComponent } from "../receipt-reconciliation/receipt-reconciliation.component";
import { RefundListComponent } from '../refund-list/refund-list.component';
import { InvoiceComponent } from "./invoicing.component";

const routes: Routes = [
  { path: 'invoicing', component: InvoiceComponent },
  { path: 'invoicing/view/:id', component: InvoiceDetailsComponent },
  { path: 'invoicing/payment/:id', component: PaymentListComponent },
  { path: 'invoicing/refund/:id', component: RefundListComponent },
  { path: 'invoicing/bankTransfert/:id', component: BankTransfertListComponent },
  { path: 'invoicing/directDebit/:id', component: DirectDebitTransfertListComponent },
  { path: 'invoicing/add/:id/:idProvision/:idCustomerOrder', component: AddInvoiceComponent },
  { path: 'invoicing/add/:id', component: AddInvoiceComponent },
  { path: 'invoicing/azure/add/:id/:idProvision', component: AddInvoiceComponent },
  { path: 'invoicing/credit-note/:idInvoice', component: AddInvoiceComponent },
  { path: 'invoicing/payment/add', component: AddPaymentComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MatTabsModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    MatExpansionModule,
    MatCardModule,
    MatTooltipModule,
    MatStepperModule,
    MatDividerModule,
    MiscellaneousModule,
    QuotationModule,
    MatRadioModule,
  ],
  declarations: [InvoiceComponent,
    InvoiceListComponent,
    InvoiceDetailsComponent,
    InvoicePaymentTableComponent,
    InvoicePaymentComponent,
    RefundListComponent,
    AssociatePaymentDialogComponent,
    AddInvoiceComponent,
    AmountDialogComponent,
    BankTransfertListComponent,
    AddPaymentComponent,
    ReceiptReconciliationComponent,
    ReceiptReconciliationEditDialogComponent,
  ], exports: [
    InvoiceListComponent,
    InvoicePaymentTableComponent,
    RefundListComponent,
    ReceiptReconciliationComponent,
    InvoiceDetailsComponent,
  ]
})
export class InvoicingModule { }
