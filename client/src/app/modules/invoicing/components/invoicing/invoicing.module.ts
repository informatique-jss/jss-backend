
import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule } from "@angular/material/dialog";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatRadioModule } from '@angular/material/radio';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTabsModule } from "@angular/material/tabs";
import { MatTooltipModule } from "@angular/material/tooltip";
import { RouterModule, Routes } from "@angular/router";
import { QuotationModule } from "src/app/modules/quotation/components/quotation/quotation.module";
import { MiscellaneousModule } from "../../../miscellaneous/components/miscellaneous/miscellaneous.module";
import { AddInvoiceComponent } from "../add-invoice/add-invoice.component";
import { AmountDialogComponent } from "../amount-dialog/amount-dialog.component";
import { AssociateDepositDialogComponent } from '../associate-deposit-dialog/associate-deposit-dialog.component';
import { AssociatePaymentDialogComponent } from "../associate-payment-dialog/associate-payment-dialog.component";
import { BankTransfertListComponent } from '../bank-transfert-list/bank-transfert-list.component';
import { DeboursAmountInvoicedDialogComponent } from '../debours-amount-invoiced-dialog/debours-amount-invoiced-dialog.component';
import { DeboursAmountTaxableDialogComponent } from "../debours-amount-taxable-dialog/debours-amount-taxable-dialog.component";
import { InvoiceDetailsComponent } from "../invoice-details/invoice-details.component";
import { InvoiceListComponent } from "../invoice-list/invoice-list.component";
import { InvoicePaymentTableComponent } from "../invoice-payment-table/invoice-payment-table.component";
import { InvoicePaymentComponent } from "../invoice-payment/invoice-payment.component";
import { RefundListComponent } from '../refund-list/refund-list.component';
import { InvoiceComponent } from "./invoicing.component";

const routes: Routes = [
  { path: 'invoicing', component: InvoiceComponent },
  { path: 'invoicing/view/:id', component: InvoiceDetailsComponent },
  { path: 'invoicing/add/:id', component: AddInvoiceComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MatTabsModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    MatExpansionModule,
    MatTooltipModule,
    MatStepperModule,
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
    AssociateDepositDialogComponent,
    AddInvoiceComponent,
    AmountDialogComponent,
    BankTransfertListComponent,
    DeboursAmountTaxableDialogComponent,
    DeboursAmountInvoicedDialogComponent,
  ], exports: [
    InvoiceListComponent,
    InvoicePaymentTableComponent,
    RefundListComponent,
  ]
})
export class InvoicingModule { }
