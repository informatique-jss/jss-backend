import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { RouterModule, Routes } from '@angular/router';
import { HistoryComponent } from 'src/app/modules/miscellaneous/components/history/history.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { QuotationComponent } from './quotation.component';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';
import { AffaireComponent } from '../affaire/affaire.component';
import { ProvisionComponent } from 'src/app/modules/quotation/components/provision/provision.component';

const routes: Routes = [
  { path: 'quotation', component: QuotationComponent },
  { path: 'quotation/:id', component: QuotationComponent },
  { path: 'order', component: QuotationComponent },
  { path: 'order/:id', component: QuotationComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatRadioModule,
    MatAutocompleteModule,
    MatDialogModule,
    MatIconModule,
    MatChipsModule,
    MatExpansionModule,
    MatCheckboxModule,
    MatTooltipModule,
    MatTabsModule,
    MiscellaneousModule,
    MatTableModule,
    MatSortModule
  ],
  declarations: [QuotationComponent,
    OrderingCustomerComponent,
    QuotationManagementComponent,
    AffaireComponent,
    ProvisionComponent],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ]
})
export class QuotationModule { }
