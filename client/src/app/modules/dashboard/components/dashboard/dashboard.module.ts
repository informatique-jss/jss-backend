import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { AdministrationModule } from 'src/app/modules/administration/components/administration/administration.module';
import { InvoicingModule } from 'src/app/modules/invoicing/components/invoicing/invoicing.module';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { QuotationModule } from 'src/app/modules/quotation/components/quotation/quotation.module';
import { AssignNewOrderDialogComponent } from '../assign-new-order-dialog/assign-new-order-dialog.component';
import { OrderKabanComponent } from '../order-kaban/order-kaban.component';
import { ProvisionAffectationKanbanComponent } from '../provision-affectation-kanban/provision-affectation-kanban.component';
import { ProvisionBoardComponent } from '../provision-board/provision-board.component';
import { ProvisionKanbanComponent } from '../provision-kanban/provision-kanban.component';
import { QuotationKanbanComponent } from '../quotation-kanban/quotation-kanban.component';
import { SaveCurrentViewDialog } from '../save-current-view-dialog/save-current-view-dialog.component';
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
    MatExpansionModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MiscellaneousModule,
    MatBadgeModule,
    MatTooltipModule,
    DragDropModule,
    MatMenuModule,
    MatStepperModule,
    MatProgressSpinnerModule,
    MatCheckboxModule,
    InvoicingModule,
    QuotationModule,
    AdministrationModule,
    MatDialogModule,
  ],
  declarations: [
    DashboardComponent,
    ProvisionBoardComponent,
    OrderKabanComponent,
    QuotationKanbanComponent,
    AssignNewOrderDialogComponent,
    ProvisionKanbanComponent,
    ProvisionAffectationKanbanComponent,
    SaveCurrentViewDialog,
  ],
  exports: [
  ],
  providers: [
  ]
})
export class DashboardModule { }
