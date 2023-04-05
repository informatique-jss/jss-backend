import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
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
import { EditorModule } from '@tinymce/tinymce-angular';
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
import { ReferentialAnnouncementNoticeTemplateComponent } from '../referentials/referential-announcement-notice-template/referential-announcement-notice-template.component';
import { ReferentialAttachmentTypeComponent } from '../referentials/referential-attachment-type/referential-attachment-type.component';
import { ReferentialBillingClosureRecipientTypeComponent } from '../referentials/referential-billing-closure-recipient-type/referential-billing-closure-recipient-type.component';
import { ReferentialBillingClosureTypeComponent } from '../referentials/referential-billing-closure-type/referential-billing-closure-type.component';
import { ReferentialBillingItemComponent } from '../referentials/referential-billing-item/referential-billing-item.component';
import { ReferentialBillingLabelTypeComponent } from '../referentials/referential-billing-label-type/referential-billing-label-type.component';
import { ReferentialBillingTypeComponent } from '../referentials/referential-billing-type/referential-billing-type.component';
import { ReferentialBodaccPublicationTypeComponent } from '../referentials/referential-bodacc-publication-type/referential-bodacc-publication-type.component';
import { ReferentialBuildingDomiciliationComponent } from '../referentials/referential-building-domiciliation/referential-building-domiciliation.componentt';
import { ReferentialCharacterPriceComponent } from '../referentials/referential-character-price/referential-character-price.component';
import { ReferentialCityComponent } from '../referentials/referential-city/referential-city.component';
import { ReferentialCivilityComponent } from '../referentials/referential-civility/referential-civility.component';
import { ReferentialCompetentAuthorityTypeComponent } from '../referentials/referential-competent-authority-type/referential-competent-authority-type.component';
import { ReferentialCompetitorComponent } from '../referentials/referential-competitor/referential-competitor.component';
import { ReferentialCountryComponent } from '../referentials/referential-country/referential-country.component';
import { ReferentialDeliveryServiceComponent } from '../referentials/referential-delivery-service/referential-delivery-service.component';
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
import { ReferentialPaymentDeadlineTypeComponent } from '../referentials/referential-payment-deadline-type/referential-payment-deadline-type.component';
import { ReferentialPaymentTypeComponent } from '../referentials/referential-payment-type/referential-payment-type.component';
import { ReferentialPaymentWayComponent } from '../referentials/referential-payment-way/referential-payment-way.component';
import { ReferentialPrincipalAccountingAccountComponent } from '../referentials/referential-principal-accounting-account/referential-principal-accounting-account.component';
import { ReferentialProvisionTypeComponent } from '../referentials/referential-provision-type/referential-provision-type.component';
import { ReferentialRefundTypeComponent } from '../referentials/referential-refund-type/referential-refund-type.component';
import { ReferentialRegieComponent } from '../referentials/referential-regie/referential-regie.component';
import { ReferentialRegionComponent } from '../referentials/referential-region/referential-region.component';
import { ReferentialSpecialOfferComponent } from '../referentials/referential-special-offer/referential-special-offer.component';
import { ReferentialSubscriptionPeriodTypeComponent } from '../referentials/referential-subscription-period-type/referential-subscription-period-type.component';
import { ReferentialTiersCategoryComponent } from '../referentials/referential-tiers-category/referential-tiers-category.component';
import { ReferentialTiersFollowupTypeComponent } from '../referentials/referential-tiers-followup-type/referential-tiers-followup-type.component';
import { ReferentialTiersTypeComponent } from '../referentials/referential-tiers-type/referential-tiers-type.component';
import { ReferentialTransfertFundsTypeComponent } from '../referentials/referential-transfert-fund-type/referential-transfert-fund-type.component';
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
    EditorModule,
    ReactiveFormsModule,
    MatFormFieldModule,
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
  ],
  declarations: [AdministrationComponent,
    ReferentialActTypeComponent,
    ReferentialBodaccPublicationTypeComponent,
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
    ReferentialPaymentWayComponent,
    ReferentialPrincipalAccountingAccountComponent,
    LogComponent,
    ViewLogComponent,
    CompetentAuthorityComponent,
    ProviderComponent,
  ], exports: [LogComponent]
})
export class AdministrationModule { }
