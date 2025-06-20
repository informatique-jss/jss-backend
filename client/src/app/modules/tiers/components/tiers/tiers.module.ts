import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { CrmModule } from 'src/app/modules/crm/components/crm/crm.module';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { SpecialOffersDialogComponent } from 'src/app/modules/miscellaneous/components/special-offers-dialog/special-offers-dialog.component';
import { AccountingModule } from '../../../accounting/components/accounting/accounting.module';
import { InvoicingModule } from '../../../invoicing/components/invoicing/invoicing.module';
import { QuotationModule } from '../../../quotation/components/quotation/quotation.module';
import { ReportingModule } from '../../../reporting/components/reporting/reporting.module';
import { ConfrereComponent } from '../confrere/confrere.component';
import { SearchPhoneComponent } from '../phone-teams/search-phone.component';
import { ResponsableMainComponent } from '../responsable-main/responsable-main.component';
import { RffListComponent } from '../rff-list/rff-list.component';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';
import { TiersListComponent } from '../tiers-list/tiers-list.component';
import { PrincipalComponent } from '../tiers-main/tiers-main.component';
import { VisitPrepaCustomerOrdersResponsibleComponent } from '../visit-prepa-customer-orders-responsible/visit-prepa-customer-orders-responsible.component';
import { VisitPrepaTiersResponsibleInfoComponent } from '../visit-prepa-tiers-responsible-info/visit-prepa-tiers-responsible-info.component';
import { VouchersListComponent } from '../vouchers-list/vouchers-list.component';
import { TiersComponent } from './tiers.component';

const routes: Routes = [
  { path: 'tiers', component: TiersComponent },
  { path: 'tiers/:id', component: TiersComponent },
  { path: 'tiers/responsable/:id', component: TiersComponent },
  { path: 'confrere/:id', component: ConfrereComponent },
  { path: 'tiers/phone/:phoneNumber', component: SearchPhoneComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MatTabsModule,
    MatSelectModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatTableModule,
    MatIconModule,
    MatExpansionModule,
    MatListModule,
    MatRadioModule,
    MatSortModule,
    MiscellaneousModule,
    AccountingModule,
    MatTooltipModule,
    DragDropModule,
    QuotationModule,
    InvoicingModule,
    MatBadgeModule,
    ReportingModule,
    CrmModule,
    QuotationModule,
  ],
  declarations: [TiersComponent,
    PrincipalComponent,
    SpecialOffersDialogComponent,
    SettlementBillingComponent,
    ResponsableMainComponent,
    ConfrereComponent,
    SearchPhoneComponent,
    TiersListComponent,
    RffListComponent,
    VisitPrepaTiersResponsibleInfoComponent,
    VisitPrepaCustomerOrdersResponsibleComponent,
    VouchersListComponent
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ],
  exports: [
    SettlementBillingComponent,
    TiersComponent,
    ConfrereComponent
  ]
})
export class TiersModule { }
