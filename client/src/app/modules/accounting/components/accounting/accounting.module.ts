import { DragDropModule } from "@angular/cdk/drag-drop";
import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DATE_LOCALE } from "@angular/material/core";
import { MatDialogModule } from "@angular/material/dialog";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatListModule } from "@angular/material/list";
import { MatMenuModule } from '@angular/material/menu';
import { MatSortModule } from "@angular/material/sort";
import { MatTableModule } from "@angular/material/table";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTooltipModule } from "@angular/material/tooltip";
import { RouterModule, Routes } from "@angular/router";
import { MiscellaneousModule } from "../../../miscellaneous/components/miscellaneous/miscellaneous.module";
import { AccountingBilanComponent } from "../accounting-bilan/accounting-bilan.component";
import { AccountingProfitLostComponent } from "../accounting-profit-lost/accounting-profit-lost.component";
import { AddAccountingRecordComponent } from "../add-accounting-record/add-accounting-record.component";
import { CounterPartDialogComponent } from "../counter-part-dialog/counter-part-dialog.component";
import { FaeComponent } from "../fae/fae.component";
import { FnpComponent } from "../fnp/fnp.component";
import { SuspiciousInvoiceComponent } from '../suspicious-invoice/suspicious-invoice.component';
import { TreasureComponent } from "../treasure/treasure.component";
import { AccountingBalanceGeneraleComponent } from "./../accounting-balance-generale/accounting-balance-generale.component";
import { AccountingBalanceComponent } from "./../accounting-balance/accounting-balance.component";
import { AccountingRecordComponent } from "./../accounting-record/accounting-record.component";
import { AccountingComponent } from "./accounting.component";

const routes: Routes = [
  { path: 'accounting', component: AccountingComponent },
  { path: 'accounting/view/:accountingAccountId', component: AccountingComponent },
  { path: 'accounting/add', component: AddAccountingRecordComponent },
  { path: 'accounting/edit/:temporaryOperationId', component: AddAccountingRecordComponent },
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
    MatTableModule,
    MatMenuModule,
    MatIconModule,
    MatExpansionModule,
    MatTooltipModule,
    MatListModule,
    MatSortModule,
    MiscellaneousModule,
    DragDropModule
  ],
  declarations: [AccountingComponent,
    AccountingRecordComponent,
    AccountingBalanceComponent,
    AccountingBalanceGeneraleComponent,
    AddAccountingRecordComponent,
    AccountingBilanComponent,
    AccountingProfitLostComponent,
    FaeComponent,
    FnpComponent,
    TreasureComponent,
    SuspiciousInvoiceComponent,
    CounterPartDialogComponent
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ],
  exports: [
    AccountingRecordComponent,
  ]
})
export class AccountingModule { }
