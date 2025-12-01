import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { DirectDebitTransfertListComponent } from 'src/app/modules/invoicing/components/direct-debit-transfert-list/direct-debit-transfert-list.component';
import { InvoiceListComponent } from 'src/app/modules/invoicing/components/invoice-list/invoice-list.component';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { ActeDepositComponent } from '../acte-deposit/acte-deposit.component';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';
import { AddAffaireComponent } from '../add-affaire/add-affaire.component';
import { AddIncidentReportComponent } from '../add-incident-report/add-incident-report.component';
import { AffaireCorrectionComponent } from '../affaire-correction/affaire-correction.component';
import { AffaireListComponent } from '../affaire-list/affaire-list.component';
import { AffaireComponent } from '../affaire/affaire.component';
import { AnnouncementListComponent } from '../announcement-list/announcement-list.component';
import { AnnouncementComponent } from '../announcement/announcement.component';
import { BaloListComponent } from '../balo-list/balo-list.component';
import { BodaccListComponent } from '../bodacc-list/bodacc-list.component';
import { ChooseAssignedUserDialogComponent } from '../choose-assigned-user-dialog/choose-assigned-user-dialog.component';
import { ChooseCompetentAuthorityDialogComponent } from '../choose-competent-authority-dialog/choose-competent-authority-dialog.component';
import { CustomerOrderCommentComponent } from '../customer-order-comment/customer-order-comment.component';
import { CustomerOrderPaymentComponent } from '../customer-order-payment/customer-order-payment.component';
import { DebourComponent } from '../debour/debour.component';
import { DomiciliationFeesComponent } from '../domiciliation-fees/domiciliation-fees.component';
import { DomiciliationComponent } from '../domiciliation/domiciliation.component';
import { FormaliteAssociateDialog } from '../formalite-associate-dialog/formalite-associate-dialog';
import { FormaliteComponent } from '../formalite/formalite.component';
import { GuichetUniqueStatusComponent } from '../guichet-unique-status/guichet-unique-status.component';
import { IncidentReportListComponent } from '../incident-report-list/incident-report-list.component';
import { InfogreffeStatusComponent } from '../infogreffe-status/infogreffe-status.component';
import { InvoiceManagementComponent } from '../invoice-management/invoice-management.component';
import { JoListComponent } from '../jo-list/jo-list.component';
import { KanbanOrderComponent } from '../kanban-order/kanban-order.component';
import { MissingAttachmentQueriesComponent } from '../missing-attachment-queries/missing-attachment-queries.component';
import { OrderSidePanelDetailsComponent } from '../order-side-panel-details/order-side-panel-details.component';
import { OrderSimilaritiesDialogComponent } from '../order-similarities-dialog/order-similarities-dialog.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { OrderingListTaggedComponent } from '../ordering-list-tagged/ordering-list-tagged.component';
import { OrderingListComponent } from '../ordering-list/ordering-list.component';
import { PaperSetListComponent } from '../paper-set-list/paper-set-list.component';
import { PaperSetComponent } from '../paper-set/paper-set.component';
import { PaymentListComponent } from '../payment-list/payment-list.component';
import { PrintLabelDialogComponent } from '../print-label-dialog/print-label-dialog.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { ProvisionListComponent } from '../provision-list/provision-list.component';
import { ProvisionOptionsComponent } from '../provision-options/provision-options.component';
import { ProvisionPaymentComponent } from '../provision-payment/provision-payment.component';
import { ProvisionSidePanelDetailsComponent } from '../provision-side-panel-details/provision-side-panel-details.component';
import { ProvisionComponent } from '../provision/provision.component';
import { QuotationAbandonReasonDialog } from '../quotation-abandon-reason-dialog/quotation-abandon-reason-dialog';
import { QuotationListComponent } from '../quotation-list/quotation-list.component';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';
import { QuotationSidePanelDetailsComponent } from '../quotation-side-panel-details/quotation-side-panel-details.component';
import { RecurringChildOrderingListComponent } from '../recurring-child-ordering-list/recurring-child-ordering-list.component';
import { RecurringOrderingListComponent } from '../recurring-ordering-list/recurring-ordering-list.component';
import { RecurringParentOrderingListComponent } from '../recurring-parent-ordering-list/recurring-parent-ordering-list.component';
import { RefundPaymentDialogComponent } from '../refund-payment-dialog/refund-payment-dialog.component';
import { SelectAccountingAccountDialogComponent } from '../select-accounting-account-dialog/select-accounting-account-dialog.component';
import { MissingAttachmentMailDialogComponent } from '../select-attachment-type-dialog/missing-attachment-mail-dialog.component';
import { SelectAttachmentsDialogComponent } from '../select-attachments-dialog/select-attachment-dialog.component';
import { SelectDocumentTypeDialogComponent } from '../select-document-type-dialog/select-document-type-dialog.component';
import { SelectMultiServiceTypeDialogComponent } from '../select-multi-service-type-dialog/select-multi-service-type-dialog.component';
import { SelectPaperSetTypeDialogComponent } from '../select-paper-set-type-dialog/select-paper-set-type-dialog.component';
import { SelectServiceDialogComponent } from '../select-service-dialog/select-service-dialog.component';
import { ServiceSidePanelDetailsComponent } from '../service-side-panel-details/service-side-panel-details.component';
import { ServiceComponent } from '../service/service.component';
import { SimpleProvisionComponent } from '../simple-provision/simple-provision.component';
import { SirenDialogComponent } from '../siren-dialog/siren-dialog.component';
import { SuggestedQuotationsDialogComponent } from '../suggested-quotations-dialog/suggested-quotations-dialog.component';
import { QuotationComponent } from './quotation.component';

const routes: Routes = [
  { path: 'quotation', component: QuotationComponent },
  { path: 'quotation/:id', component: QuotationComponent },
  { path: 'order', component: QuotationComponent },
  { path: 'recurring-order', component: RecurringOrderingListComponent },
  { path: 'order/:id', component: QuotationComponent },
  { path: 'provision', component: ProvisionListComponent },
  { path: 'provisions/:employeeId', component: ProvisionListComponent },
  { path: 'provision/:id', component: ProvisionComponent },
  { path: 'service/:idService', component: ProvisionComponent },
  { path: 'provision/:id/:idProvision', component: ProvisionComponent },
  { path: 'affaire', component: AffaireListComponent },
  { path: 'affaire-correction', component: AffaireCorrectionComponent },
  { path: 'affaire/:id', component: AffaireComponent },
  { path: 'paper-set', component: PaperSetListComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatStepperModule,
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
    MatDividerModule,
    MiscellaneousModule,
    MatBadgeModule,
    MatSidenavModule,
    MatTableModule,
    MatSortModule,
    MatMenuModule,
    MatDatepickerModule,
    MatCardModule,
    CKEditorModule,
    DragDropModule
  ],
  declarations: [QuotationComponent,
    QuotationAbandonReasonDialog,
    OrderingCustomerComponent,
    QuotationManagementComponent,
    DomiciliationComponent,
    OrderSimilaritiesDialogComponent,
    AddAffaireDialogComponent,
    ProvisionItemComponent,
    InvoiceManagementComponent,
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
    MissingAttachmentMailDialogComponent,
    AddAffaireComponent,
    CustomerOrderPaymentComponent,
    PaymentListComponent,
    AnnouncementListComponent,
    SelectAttachmentsDialogComponent,
    PrintLabelDialogComponent,
    DirectDebitTransfertListComponent,
    ChooseCompetentAuthorityDialogComponent,
    SelectAccountingAccountDialogComponent,
    RefundPaymentDialogComponent,
    ProvisionPaymentComponent,
    // Guichet unique
    GuichetUniqueStatusComponent,
    DebourComponent,
    ActeDepositComponent,
    ServiceComponent,
    SelectServiceDialogComponent,
    SelectDocumentTypeDialogComponent,
    MissingAttachmentQueriesComponent,
    RecurringOrderingListComponent,
    RecurringParentOrderingListComponent,
    RecurringChildOrderingListComponent,
    DomiciliationFeesComponent,
    CustomerOrderCommentComponent,
    PaperSetListComponent,
    PaperSetComponent,
    SelectPaperSetTypeDialogComponent,
    OrderingListTaggedComponent,
    FormaliteAssociateDialog,
    InfogreffeStatusComponent,
    SelectMultiServiceTypeDialogComponent,
    QuotationSidePanelDetailsComponent,
    ProvisionSidePanelDetailsComponent,
    OrderSidePanelDetailsComponent,
    ServiceSidePanelDetailsComponent,
    IncidentReportListComponent,
    AddIncidentReportComponent,
    InvoiceListComponent,
    SuggestedQuotationsDialogComponent,
    AffaireCorrectionComponent,
    BodaccListComponent,
    BaloListComponent,
    JoListComponent,
    KanbanOrderComponent,
    SirenDialogComponent
  ],
  exports: [
    OrderingListComponent,
    ProvisionListComponent,
    QuotationListComponent,
    OrderingListComponent,
    PaymentListComponent,
    AnnouncementListComponent,
    DirectDebitTransfertListComponent,
    QuotationComponent,
    OrderingListTaggedComponent,
    InfogreffeStatusComponent,
    PaperSetComponent,
    CustomerOrderCommentComponent,
    MissingAttachmentQueriesComponent,
    AddAffaireComponent,
    QuotationSidePanelDetailsComponent,
    ProvisionSidePanelDetailsComponent,
    OrderSidePanelDetailsComponent,
    ServiceSidePanelDetailsComponent,
    AddIncidentReportComponent,
    IncidentReportListComponent,
    InvoiceListComponent,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ]
})
export class QuotationModule { }
