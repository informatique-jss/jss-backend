import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { QuillModule } from 'ngx-quill';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';
import { AffaireComponent } from '../affaire/affaire.component';
import { BodaccFusionComponent } from '../bodacc-fusion/bodacc-fusion.component';
import { BodaccMainComponent } from '../bodacc-main/bodacc-main.component';
import { BodaccSaleComponent } from '../bodacc-sale/bodacc-sale.component';
import { BodaccSplitComponent } from '../bodacc-split/bodacc-split.component';
import { DomiciliationComponent } from '../domiciliation/domiciliation.component';
import { InvoiceManagementComponent } from '../invoice-management/invoice-management.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';
import { ShalComponent } from '../shal/shal.component';
import { QuotationComponent } from './quotation.component';

const routes: Routes = [
  { path: 'quotation', component: QuotationComponent },
  { path: 'quotation/:id', component: QuotationComponent },
  { path: 'order', component: QuotationComponent },
  { path: 'order/:id', component: QuotationComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    QuillModule.forRoot({
      modules: {
        syntax: false,
        toolbar: {
          container: [
            ['bold', 'italic'],
            [{ 'font': [] }],
            [{ 'color': [] }],
            [{ 'background': [] }],
            [{ align: '' }, { align: 'center' }],
            [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
            [{ 'list': 'ordered' }, { 'list': 'bullet' }],
            [{ 'size': [] }],
            ['clean'],
            //['link'],

          ],
        }
      },
      placeholder: '',
    }),
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
    MatSidenavModule,
    MatTableModule,
    MatSortModule,
    MatMenuModule,
    MatDatepickerModule
  ],
  declarations: [QuotationComponent,
    OrderingCustomerComponent,
    QuotationManagementComponent,
    AffaireComponent,
    DomiciliationComponent,
    BodaccMainComponent,
    AddAffaireDialogComponent,
    ProvisionItemComponent,
    BodaccSaleComponent,
    InvoiceManagementComponent,
    BodaccFusionComponent,
    BodaccSplitComponent,
    ShalComponent],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ]
})
export class QuotationModule { }
