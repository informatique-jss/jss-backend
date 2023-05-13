import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { MatLegacyMenuModule as MatMenuModule } from '@angular/material/legacy-menu';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { RouterModule, Routes } from '@angular/router';
import { AdministrationModule } from 'src/app/modules/administration/components/administration/administration.module';
import { InvoicingModule } from 'src/app/modules/invoicing/components/invoicing/invoicing.module';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { QuotationModule } from 'src/app/modules/quotation/components/quotation/quotation.module';
import { ProvisionBoardComponent } from '../provision-board/provision-board.component';
import { DashboardComponent } from './dashboard.component';

const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MatDividerModule,
    MatTabsModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MiscellaneousModule,
    MatTooltipModule,
    DragDropModule,
    MatMenuModule,
    MatCheckboxModule,
    InvoicingModule,
    QuotationModule,
    AdministrationModule,
  ],
  declarations: [
    DashboardComponent,
    ProvisionBoardComponent
  ],
  providers: [
  ]
})
export class DashboardModule { }
