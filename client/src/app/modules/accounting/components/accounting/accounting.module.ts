import { DragDropModule } from "@angular/cdk/drag-drop";
import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DATE_LOCALE } from "@angular/material/core";
import { MatDialogModule } from "@angular/material/dialog";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatFormFieldModule } from "@angular/material/form-field";
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
import { AddAccountingRecordComponent } from "../add-accounting-record/add-accounting-record.component";
import { DeleteAccountingRecordDialogComponent } from "../delete-accounting-record-dialog/delete-accounting-record-dialog.component";
import { AccountingBalanceGeneraleComponent } from "./../accounting-balance-generale/accounting-balance-generale.component";
import { AccountingBalanceComponent } from "./../accounting-balance/accounting-balance.component";
import { AccountingBilanComponent } from "./../accounting-bilan/accounting-bilan.component";
import { AccountingProfitLostComponent } from "./../accounting-profit-lost/accounting-profit-lost.component";
import { AccountingRecordComponent } from "./../accounting-record/accounting-record.component";
import { AccountingComponent } from "./accounting.component";

const routes: Routes = [
  { path: 'accounting', component: AccountingComponent },
  { path: 'accounting/add', component: AddAccountingRecordComponent },
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
    AccountingBilanComponent,
    AccountingBalanceComponent,
    AccountingProfitLostComponent,
    AccountingBalanceGeneraleComponent,
    DeleteAccountingRecordDialogComponent,
    AddAccountingRecordComponent,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ],
  exports: [
    AccountingRecordComponent,
  ]
})
export class AccountingModule { }
