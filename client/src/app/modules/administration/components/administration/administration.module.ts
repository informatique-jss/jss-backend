import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { InvoicingModule } from 'src/app/modules/invoicing/components/invoicing/invoicing.module';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { AccountingModule } from '../../../accounting/components/accounting/accounting.module';
import { QuotationModule } from '../../../quotation/components/quotation/quotation.module';
import { TiersModule } from '../../../tiers/components/tiers/tiers.module';
import { CompetentAuthorityComponent } from '../competent-authority/competent-authority.component';
import { ConstantComponent } from '../constant/constant.component';
import { LogComponent } from '../log/log.component';
import { ProviderComponent } from '../provider/provider.component';
import { ReferentialAccountingAccountClassComponent } from '../referentials/referential-accounting-account-class/referential-accounting-account-class.component';
import { ReferentialAccountingAccountComponent } from '../referentials/referential-accounting-account/referential-accounting-account.component';
import { ReferentialAccountingJournalComponent } from '../referentials/referential-accounting-journal/referential-accounting-journal.component';
import { ReferentialActTypeComponent } from '../referentials/referential-act-type/referential-act-type.component';
import { ReferentialActiveDirectoryGroupComponent } from '../referentials/referential-active-directory-group/referential-active-directory-group.component';
import { ReferentialAnnouncementNoticeTemplateComponent } from '../referentials/referential-announcement-notice-template/referential-announcement-notice-template.component';
import { ReferentialAttachmentTypeComponent } from '../referentials/referential-attachment-type/referential-attachment-type.component';
import { ReferentialBillingClosureRecipientTypeComponent } from '../referentials/referential-billing-closure-recipient-type/referential-billing-closure-recipient-type.component';
import { ReferentialBillingClosureTypeComponent } from '../referentials/referential-billing-closure-type/referential-billing-closure-type.component';
import { ReferentialBillingItemComponent } from '../referentials/referential-billing-item/referential-billing-item.component';
import { ReferentialBillingLabelTypeComponent } from '../referentials/referential-billing-label-type/referential-billing-label-type.component';
import { ReferentialBillingTypeComponent } from '../referentials/referential-billing-type/referential-billing-type.component';
import { ReferentialBuildingDomiciliationComponent } from '../referentials/referential-building-domiciliation/referential-building-domiciliation.componentt';
import { ReferentialCharacterPriceComponent } from '../referentials/referential-character-price/referential-character-price.component';
import { ReferentialCityComponent } from '../referentials/referential-city/referential-city.component';
import { ReferentialCivilityComponent } from '../referentials/referential-civility/referential-civility.component';
import { ReferentialCompetentAuthorityTypeComponent } from '../referentials/referential-competent-authority-type/referential-competent-authority-type.component';
import { ReferentialCompetitorComponent } from '../referentials/referential-competitor/referential-competitor.component';
import { ReferentialComplainCauseComponent } from '../referentials/referential-complain-cause/referential-complain-cause.component';
import { ReferentialComplainOriginComponent } from '../referentials/referential-complain-origin/referential-complain-origin.component';
import { ReferentialComplainProblemComponent } from '../referentials/referential-complain-problem/referential-complain-problem.component';
import { ReferentialCountryComponent } from '../referentials/referential-country/referential-country.component';
import { ReferentialCustomerOrderFrequencyComponent } from '../referentials/referential-customer-order-frequency/referential-customer-order-frequency.component';
import { ReferentialCustomerOrderOriginComponent } from '../referentials/referential-customer-order-origin/referential-customer-order-origin.component';
import { ReferentialDeliveryServiceComponent } from '../referentials/referential-delivery-service/referential-delivery-service.component';
import { ReferentialDepartmentVatSettingComponent } from '../referentials/referential-department-vat-setting/referential-department-vat-setting.component';
import { ReferentialDepartmentComponent } from '../referentials/referential-department/referential-department.component';
import { ReferentialDocumentTypeComponent } from '../referentials/referential-document-type/referential-document-type.component';
import { ReferentialDomiciliationContractTypeComponent } from '../referentials/referential-domiciliation-contract-type/referential-domiciliation-contract-type.componentt';
import { ReferentialFundTypeComponent } from '../referentials/referential-fund-type/referential-fund-type.component';
import { ReferentialGiftComponent } from '../referentials/referential-gift/referential-gift.component';
import { ReferentialInvoiceStatusComponent } from '../referentials/referential-invoice-status/referential-invoice-status.component';
import { ReferentialJournalTypeComponent } from '../referentials/referential-journal-type/referential-journal-type.component';
import { ReferentialLanguageComponent } from '../referentials/referential-language/referential-language.component';
import { ReferentialLegalFormComponent } from '../referentials/referential-legal-form/referential-legal-form.component';
import { ReferentialMailRedirectionTypeComponent } from '../referentials/referential-mail-redirection-type/referential-mail-redirection-type.component';
import { ReferentialNoticeTypeFamilyComponent } from '../referentials/referential-notice-type-family/referential-notice-type-family.component';
import { ReferentialNoticeTypeComponent } from '../referentials/referential-notice-type/referential-notice-type.component';
import { ReferentialPaperSetTypeComponent } from '../referentials/referential-paper-set/referential-paper-set.component';
import { ReferentialPaymentDeadlineTypeComponent } from '../referentials/referential-payment-deadline-type/referential-payment-deadline-type.component';
import { ReferentialPaymentTypeComponent } from '../referentials/referential-payment-type/referential-payment-type.component';
import { ReferentialPrincipalAccountingAccountComponent } from '../referentials/referential-principal-accounting-account/referential-principal-accounting-account.component';
import { ReferentialProvisionTypeComponent } from '../referentials/referential-provision-type/referential-provision-type.component';
import { ReferentialQuotationAbandonReasonComponent } from '../referentials/referential-quotation-abandon-reason/referential-quotation-abandon-reason';
import { ReferentialRefundTypeComponent } from '../referentials/referential-refund-type/referential-refund-type.component';
import { ReferentialRegieComponent } from '../referentials/referential-regie/referential-regie.component';
import { ReferentialRegionComponent } from '../referentials/referential-region/referential-region.component';
import { ReferentialRffFrequencyComponent } from '../referentials/referential-rff-frequency/referential-rff-frequency.component';
import { ReferentialServiceFamilyGroupComponent } from '../referentials/referential-service-family-group/referential-service-family-group.component';
import { ReferentialServiceFamilyComponent } from '../referentials/referential-service-family/referential-service-family.component';
import { ReferentialServiceFieldTypeComponent } from '../referentials/referential-service-field-type/referential-service-field-type.component';
import { ReferentialServiceTypeComponent } from '../referentials/referential-service/referential-service-type.component';
import { ReferentialSpecialOfferComponent } from '../referentials/referential-special-offer/referential-special-offer.component';
import { ReferentialSubscriptionPeriodTypeComponent } from '../referentials/referential-subscription-period-type/referential-subscription-period-type.component';
import { ReferentialTiersCategoryComponent } from '../referentials/referential-tiers-category/referential-tiers-category.component';
import { ReferentialTiersFollowupTypeComponent } from '../referentials/referential-tiers-followup-type/referential-tiers-followup-type.component';
import { ReferentialTiersTypeComponent } from '../referentials/referential-tiers-type/referential-tiers-type.component';
import { ReferentialTransfertFundsTypeComponent } from '../referentials/referential-transfert-fund-type/referential-transfert-fund-type.component';
import { ReferentialTypeDocumentComponent } from '../referentials/referential-type-document/referential-type-document.component';
import { ReferentialVatCollectionTypeComponent } from '../referentials/referential-vat-collection-type/referential-vat-collection-type.component';
import { ReferentialVatComponent } from '../referentials/referential-vat/referential-vat.component';
import { ReferentialWeekDayComponent } from '../referentials/referential-weekday/referential-weekday.component';
import { ReferentialProvisionFamilyTypeComponent } from '../referentials/referentiel-provision-familiy-type/referential-provision-family-type.component';
import { ReferentialRecordTypeComponent } from '../referentials/referentiel-record-type/referential-record-type.component';
import { ViewLogComponent } from '../view-log/view-log.component';
import { AdministrationComponent } from './administration.component';

const routes: Routes = [
  { path: 'administration', component: AdministrationComponent },
  { path: 'administration/competent/authority', component: CompetentAuthorityComponent },
  { path: 'administration/competent/authority/:id', component: CompetentAuthorityComponent },
  { path: 'administration/provider', component: ProviderComponent },
  { path: 'administration/provider/:id', component: ProviderComponent },
  { path: 'administration/log/:id', component: ViewLogComponent },
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
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
    MatRadioModule,
    MatAutocompleteModule,
    MatIconModule,
    MatChipsModule,
    MatCheckboxModule,
    MatTooltipModule,
    MiscellaneousModule,
    MatSortModule,
    MatExpansionModule,
    MatTableModule,
    TiersModule,
    QuotationModule,
    AccountingModule,
    MatDividerModule,
    CKEditorModule,
    InvoicingModule,
  ],
  declarations: [AdministrationComponent,
    ReferentialActTypeComponent,
    ReferentialBuildingDomiciliationComponent,
    ReferentialDomiciliationContractTypeComponent,
    ReferentialFundTypeComponent,
    ReferentialJournalTypeComponent,
    ReferentialMailRedirectionTypeComponent,
    ReferentialNoticeTypeFamilyComponent,
    ReferentialProvisionFamilyTypeComponent,
    ReferentialRecordTypeComponent,
    ReferentialTransfertFundsTypeComponent,
    ReferentialBillingClosureRecipientTypeComponent,
    ReferentialBillingClosureTypeComponent,
    ReferentialBillingLabelTypeComponent,
    ReferentialPaymentDeadlineTypeComponent,
    ReferentialRefundTypeComponent,
    ReferentialSubscriptionPeriodTypeComponent,
    ReferentialTiersCategoryComponent,
    ReferentialTiersFollowupTypeComponent,
    ReferentialTiersTypeComponent,
    ReferentialAttachmentTypeComponent,
    ReferentialCivilityComponent,
    ReferentialCompetentAuthorityTypeComponent,
    ReferentialCountryComponent,
    ReferentialDeliveryServiceComponent,
    ReferentialDocumentTypeComponent,
    ReferentialLanguageComponent,
    ReferentialGiftComponent,
    ReferentialQuotationAbandonReasonComponent,
    ReferentialLegalFormComponent,
    ReferentialPaymentTypeComponent,
    ReferentialAccountingAccountClassComponent,
    ReferentialAccountingAccountComponent,
    ReferentialRegionComponent,
    ReferentialVatComponent,
    ReferentialWeekDayComponent,
    ReferentialCharacterPriceComponent,
    ReferentialNoticeTypeComponent,
    ReferentialProvisionTypeComponent,
    ReferentialSpecialOfferComponent,
    ReferentialCompetitorComponent,
    ReferentialCityComponent,
    ReferentialAnnouncementNoticeTemplateComponent,
    ReferentialVatCollectionTypeComponent,
    ReferentialBillingItemComponent,
    ReferentialDepartmentComponent,
    ConstantComponent,
    ReferentialBillingTypeComponent,
    ReferentialRegieComponent,
    ReferentialAccountingJournalComponent,
    ReferentialInvoiceStatusComponent,
    ReferentialPrincipalAccountingAccountComponent,
    LogComponent,
    ViewLogComponent,
    CompetentAuthorityComponent,
    ProviderComponent,
    ReferentialCustomerOrderOriginComponent,
    ReferentialDepartmentVatSettingComponent,
    ReferentialTypeDocumentComponent,
    ReferentialRffFrequencyComponent,
    ReferentialComplainProblemComponent,
    ReferentialComplainCauseComponent,
    ReferentialComplainOriginComponent,
    ReferentialServiceTypeComponent,
    ReferentialServiceFamilyComponent,
    ReferentialServiceFamilyGroupComponent,
    ReferentialServiceTypeComponent,
    ReferentialCustomerOrderFrequencyComponent,
    ReferentialActiveDirectoryGroupComponent,
    ReferentialPaperSetTypeComponent,
    ReferentialServiceFieldTypeComponent,
  ], exports: [LogComponent,
    CompetentAuthorityComponent,
    ProviderComponent
  ]
})
export class AdministrationModule { }
