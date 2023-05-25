import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSortModule } from '@angular/material/sort';
import { RouterModule, Routes } from '@angular/router';
import { EditorModule } from '@tinymce/tinymce-angular';
import { DirectDebitTransfertListComponent } from 'src/app/modules/invoicing/components/direct-debit-transfert-list/direct-debit-transfert-list.component';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';
import { AddAffaireComponent } from '../add-affaire/add-affaire.component';
import { AddDebourComponent } from '../add-debour/add-debour.component';
import { AffaireListComponent } from '../affaire-list/affaire-list.component';
import { AffaireComponent } from '../affaire/affaire.component';
import { AnnouncementListComponent } from '../announcement-list/announcement-list.component';
import { AnnouncementComponent } from '../announcement/announcement.component';
import { BodaccFusionComponent } from '../bodacc-fusion/bodacc-fusion.component';
import { BodaccMainComponent } from '../bodacc-main/bodacc-main.component';
import { BodaccSaleComponent } from '../bodacc-sale/bodacc-sale.component';
import { BodaccSplitComponent } from '../bodacc-split/bodacc-split.component';
import { ChooseAssignedUserDialogComponent } from '../choose-assigned-user-dialog/choose-assigned-user-dialog.component';
import { ChooseCompetentAuthorityDialogComponent } from '../choose-competent-authority-dialog/choose-competent-authority-dialog.component';
import { CustomerOrderPaymentComponent } from '../customer-order-payment/customer-order-payment.component';
import { DebourListComponent } from '../debour-list/debour-list.component';
import { DomiciliationComponent } from '../domiciliation/domiciliation.component';
import { FormaliteComponent } from '../formalite/formalite.component';
import { ContentComponent } from '../guichet-unique/content/content.component';
import { EntrepriseComponent } from '../guichet-unique/entreprise/entreprise.component';
import { IdentiteComponent } from '../guichet-unique/identite/identite.component';
import { NatureCreationComponent } from '../guichet-unique/nature-creation/nature-creation.component';
import { PersonnePhysiqueComponent } from '../guichet-unique/personne-physique/personne-physique.component';
import { InvoiceManagementComponent } from '../invoice-management/invoice-management.component';
import { OrderSimilaritiesDialogComponent } from '../order-similarities-dialog/order-similarities-dialog.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { OrderingListComponent } from '../ordering-list/ordering-list.component';
import { PaymentListComponent } from '../payment-list/payment-list.component';
import { PrintLabelDialogComponent } from '../print-label-dialog/print-label-dialog.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { ProvisionListComponent } from '../provision-list/provision-list.component';
import { ProvisionOptionsComponent } from '../provision-options/provision-options.component';
import { ProvisionComponent } from '../provision/provision.component';
import { QuotationListComponent } from '../quotation-list/quotation-list.component';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';
import { SelectAccountingAccountDialogComponent } from '../select-accounting-account-dialog/select-accounting-account-dialog.component';
import { SelectAttachmentTypeDialogComponent } from '../select-attachment-type-dialog/select-attachment-type-dialog.component';
import { SelectAttachmentsDialogComponent } from '../select-attachments-dialog/select-attachment-dialog.component';
import { SimpleProvisionComponent } from '../simple-provision/simple-provision.component';
import { QuotationComponent } from './quotation.component';

const routes: Routes = [
  { path: 'quotation', component: QuotationComponent },
  { path: 'quotation/:id', component: QuotationComponent },
  { path: 'order', component: QuotationComponent },
  { path: 'order/:id', component: QuotationComponent },
  { path: 'provision', component: ProvisionListComponent },
  { path: 'provisions/:employeeId', component: ProvisionListComponent },
  { path: 'provision/:id', component: ProvisionComponent },
  { path: 'provision/:id/:idProvision', component: ProvisionComponent },
  { path: 'affaire', component: AffaireListComponent },
  { path: 'affaire/:id', component: AffaireComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    EditorModule,
    FormsModule,
    ReactiveFormsModule,
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
    MatDatepickerModule,
  ],
  declarations: [QuotationComponent,
    OrderingCustomerComponent,
    QuotationManagementComponent,
    DomiciliationComponent,
    OrderSimilaritiesDialogComponent,
    BodaccMainComponent,
    AddAffaireDialogComponent,
    ProvisionItemComponent,
    BodaccSaleComponent,
    InvoiceManagementComponent,
    BodaccFusionComponent,
    BodaccSplitComponent,
    QuotationListComponent,
    ChooseAssignedUserDialogComponent,
    ProvisionListComponent,
    ProvisionComponent,
    OrderingListComponent,
    ProvisionOptionsComponent,
    FormaliteComponent,
    AnnouncementComponent,
    SimpleProvisionComponent,
    AffaireListComponent,
    AffaireComponent,
    SelectAttachmentTypeDialogComponent,
    AddAffaireComponent,
    CustomerOrderPaymentComponent,
    PaymentListComponent,
    AnnouncementListComponent,
    SelectAttachmentsDialogComponent,
    PrintLabelDialogComponent,
    AddDebourComponent,
    DirectDebitTransfertListComponent,
    ChooseCompetentAuthorityDialogComponent,
    DebourListComponent,
    SelectAccountingAccountDialogComponent,
    // Guichet unique
    ContentComponent,
    NatureCreationComponent,
    PersonnePhysiqueComponent,
    IdentiteComponent,
    EntrepriseComponent,
  ],
  exports: [
    OrderingListComponent,
    ProvisionListComponent,
    QuotationListComponent,
    OrderingListComponent,
    PaymentListComponent,
    AnnouncementListComponent,
    DirectDebitTransfertListComponent,
    DebourListComponent,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ]
})
export class QuotationModule { }
