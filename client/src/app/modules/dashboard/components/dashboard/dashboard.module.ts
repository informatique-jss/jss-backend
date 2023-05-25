import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
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
