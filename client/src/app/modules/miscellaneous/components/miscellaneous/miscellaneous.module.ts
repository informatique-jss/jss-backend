import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { MatTableExporterModule } from 'mat-table-exporter';
import { TrustHtmlPipe } from 'src/app/libs/TrustHtmlPipe';
import { AddressingComponent } from '../addressing/addressing.component';
import { AttachmentsComponent } from '../attachments/attachments.component';
import { ChipsStatusComponent } from '../chips-status/chips-status.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { ConfrereDialogComponent } from '../confreres-dialog/confreres-dialog.component';
import { AutocompleteAccountingAccountComponent } from '../forms/autocomplete-accounting-account/autocomplete-accounting-account.component';
import { AutocompleteAffaireComponent } from '../forms/autocomplete-affaire/autocomplete-affaire.component';
import { AutocompleteBillingItemComponent } from '../forms/autocomplete-billing-item/autocomplete-billing-item.component';
import { AutocompleteCityComponent } from '../forms/autocomplete-city/autocomplete-city.component';
import { AutocompleteCompetentAuthorityComponent } from '../forms/autocomplete-competent-authority/autocomplete-competent-authority.component';
import { AutocompleteConfrereComponent } from '../forms/autocomplete-confrere/autocomplete-confrere.component';
import { AutocompleteCountryComponent } from '../forms/autocomplete-country/autocomplete-country.component';
import { AutocompleteDepartmentComponent } from '../forms/autocomplete-department/autocomplete-department.component';
import { AutocompleteEmployeeComponent } from '../forms/autocomplete-employee/autocomplete-employee.component';
import { AutocompleteFormalisteEmployeeComponent } from '../forms/autocomplete-formaliste-employee/autocomplete-formaliste-employee.component';
import { AutocompleteInsertionEmployeeComponent } from '../forms/autocomplete-insertion-employee/autocomplete-insertion-employee.component';
import { AutocompleteLegalFormComponent } from '../forms/autocomplete-legal-form/autocomplete-legal-form.component';
import { AutocompletePostalCodeComponent } from '../forms/autocomplete-postal-code/autocomplete-postal-code.component';
import { AutocompleteRegieComponent } from '../forms/autocomplete-regie/autocomplete-regie.component';
import { AutocompleteRegionComponent } from '../forms/autocomplete-region/autocomplete-region.component';
import { AutocompleteResponsableComponent } from '../forms/autocomplete-responsable/autocomplete-responsable.component';
import { AutocompleteRnaComponent } from '../forms/autocomplete-rna/autocomplete-rna.component';
import { AutocompleteSalesEmployeeComponent } from '../forms/autocomplete-sales-employee/autocomplete-sales-employee.component';
import { AutocompleteSirenComponent } from '../forms/autocomplete-siren/autocomplete-siren.component';
import { AutocompleteSiretComponent } from '../forms/autocomplete-siret/autocomplete-siret.component';
import { AutocompleteTiersIndividualComponent } from '../forms/autocomplete-tiers-individual/autocomplete-tiers-individual.component';
import { ChipsCityComponent } from '../forms/chips-city/chips-city.component';
import { ChipsCompetitorComponent } from '../forms/chips-competitor/chips-competitor.component';
import { ChipsConfrereComponent } from '../forms/chips-confrere/chips-confrere.component';
import { ChipsMailComponent } from '../forms/chips-mail/chips-mail.component';
import { ChipsPhoneComponent } from '../forms/chips-phone/chips-phone.component';
import { ChipsProvisionFamilyTypeComponent } from '../forms/chips-provision-family/chips-provision-family.component';
import { ChipsSpecialOffersComponent } from '../forms/chips-special-offers/chips-special-offers.component';
import { GenericCheckboxComponent } from '../forms/generic-checkbox/generic-checkbox.component';
import { GenericDateRangePickerComponent } from '../forms/generic-date-range-picker/generic-date-range-picker.component';
import { GenericDatepickerComponent } from '../forms/generic-datepicker/generic-datepicker.component';
import { GenericInputComponent } from '../forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../forms/generic-textarea/generic-textarea.component';
import { GenericToggleComponent } from '../forms/generic-toggle/generic-toggle.component';
import { RadioGroupActTypeComponent } from '../forms/radio-group-act-type/radio-group-act-type.component';
import { RadioGroupBillingClosureRecipientComponent } from '../forms/radio-group-billing-closure-recipient/radio-group-billing-closure-recipient.component';
import { RadioGroupBillingClosureComponent } from '../forms/radio-group-billing-closure/radio-group-billing-closure.component';
import { RadioGroupBillingLabelComponent } from '../forms/radio-group-billing-label/radio-group-billing-label.component';
import { RadioGroupCivilityComponent } from '../forms/radio-group-civility/radio-group-civility.component';
import { RadioGroupJournalTypeComponent } from '../forms/radio-group-journal-type/radio-group-journal-type.component';
import { RadioGroupLanguageComponent } from '../forms/radio-group-language/radio-group-language.component';
import { RadioGroupQuotationLabelTypeComponent } from '../forms/radio-group-quotation-label-type/radio-group-quotation-label-type.component';
import { RadioGroupRecordTypeComponent } from '../forms/radio-group-record-type/radio-group-record-type.component';
import { RadioGroupTransfertFundsComponent } from '../forms/radio-group-transfert-funds/radio-group-transfert-funds.component';
import { SelectAccountingAccountClassComponent } from '../forms/select-accounting-account-class/select-accounting-account-class.component';
import { SelectAccountingJournalComponent } from '../forms/select-accounting-journal/select-accounting-journal.component';
import { SelectAffaireStatusComponent } from '../forms/select-affaire-status/select-affaire-status.component';
import { SelectAssignationTypeComponent } from '../forms/select-assignation-type/select-assignation-type.component';
import { SelectBillingItemsComponent } from '../forms/select-billing-items/select-billing-items.component';
import { SelectBillingTypeComponent } from '../forms/select-billing-type/select-billing-type.component';
import { SelectBodaccPublicationTypeComponent } from '../forms/select-bodacc-publication-type/select-bodacc-publication-type.component';
import { SelectBuildingDomicilationComponent } from '../forms/select-building-domicilation/select-building-domicilation.component';
import { SelectCompetentAuthorityTypeComponent } from '../forms/select-competent-authority-type/select-competent-authority-type.component';
import { SelectContractTypeComponent } from '../forms/select-contract-type/select-contract-type.component';
import { SelectDayComponent } from '../forms/select-day/select-day.component';
import { SelectDeliveryServiceComponent } from '../forms/select-delivery-service/select-delivery-service.component';
import { SelectDepartmentsComponent } from '../forms/select-departments/select-departments.component';
import { SelectFollowupComponent } from '../forms/select-followup/select-followup.component';
import { SelectFundTypeComponent } from '../forms/select-fund-type/select-fund-type.component';
import { SelectGiftComponent } from '../forms/select-gift/select-gift.component';
import { SelectInvoiceStatusComponent } from '../forms/select-invoice-status/select-invoice-status.component';
import { SelectJournalTypeComponent } from '../forms/select-journal-type/select-journal-type.component';
import { SelectMailRedirectionComponent } from '../forms/select-mail-redirection/select-mail-redirection.component';
import { SelectNoticeFamilyComponent } from '../forms/select-notice-family/select-notice-family.component';
import { SelectPaymentDeadlineComponent } from '../forms/select-payment-deadline/select-payment-deadline.component';
import { SelectPaymentTypesComponent } from '../forms/select-payment-types/select-payment-types.component';
import { SelectPaymentWayComponent } from '../forms/select-payment-way/select-payment-way.component';
import { SelectProvisionFamilyComponent } from '../forms/select-provision-family/select-provision-familiy.component';
import { SelectProvisionScreenTypeComponent } from '../forms/select-provision-screen-type/select-provision-screen-type.component';
import { SelectProvisionTypeComponent } from '../forms/select-provision-type/select-provision-type.component';
import { SelectOrderingStatusComponent as SelectQuotationStatusComponent } from '../forms/select-quotation-status/select-quotation-status.component';
import { SelectRefundTypeComponent } from '../forms/select-refund-type/select-refund-type.component';
import { SelectRegionsComponent } from '../forms/select-regions/select-regions.component';
import { SelectResponsableComponent } from '../forms/select-responsable/select-responsable.component';
import { SelectSubscriptionPeriodComponent } from '../forms/select-subscription-period/select-subscription-period.component';
import { SelectTiersCategoryComponent } from '../forms/select-tiers-category/select-tiers-category.component';
import { SelectTiersTypeComponent } from '../forms/select-tiers-type/select-tiers-type.component';
import { SelectVatCollectionTypeComponent } from '../forms/select-vat-collection-type/select-vat-collection-type.component';
import { SelectVatComponent } from '../forms/select-vat/select-vat.component';
import { GradeComponent } from '../grade/grade.component';
import { HistoryComponent } from '../history/history.component';
import { SingleAttachmentComponent } from '../single-attachment/single-attachment.component';
import { SortTableComponent } from '../sort-table/sort-table.component';
import { UploadAttachementDialogComponent } from '../upload-attachement-dialog/upload-attachement-dialog.component';
import { MiscellaneousComponent } from './miscellaneous.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatTableModule,
    MatSortModule,
    MatIconModule,
    MatListModule,
    MatSelectModule,
    MatProgressBarModule,
    MatButtonModule,
    MatChipsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatTooltipModule,
    MatMenuModule,
    MatTableExporterModule,
    MatRadioModule,
    DragDropModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatCheckboxModule,
    MatAutocompleteModule,
    RouterModule,
  ],
  declarations: [MiscellaneousComponent,
    HistoryComponent,
    UploadAttachementDialogComponent,
    AttachmentsComponent,
    AddressingComponent,
    ConfirmDialogComponent,
    SelectTiersTypeComponent,
    GenericDatepickerComponent,
    GenericInputComponent,
    AutocompleteSalesEmployeeComponent,
    AutocompleteFormalisteEmployeeComponent,
    GenericToggleComponent,
    AutocompletePostalCodeComponent,
    RadioGroupCivilityComponent,
    ChipsSpecialOffersComponent,
    GenericTextareaComponent,
    SelectDeliveryServiceComponent,
    AutocompleteCountryComponent,
    ChipsPhoneComponent,
    SelectFollowupComponent,
    RadioGroupLanguageComponent,
    ChipsMailComponent,
    GenericCheckboxComponent,
    AutocompleteInsertionEmployeeComponent,
    RadioGroupBillingLabelComponent,
    RadioGroupBillingClosureRecipientComponent,
    SelectRefundTypeComponent,
    SelectResponsableComponent,
    SelectGiftComponent,
    SelectTiersCategoryComponent,
    SelectPaymentTypesComponent,
    AutocompleteSirenComponent,
    SelectMailRedirectionComponent,
    AutocompleteAffaireComponent,
    SelectBuildingDomicilationComponent,
    SelectPaymentDeadlineComponent,
    RadioGroupBillingClosureComponent,
    SelectContractTypeComponent,
    AutocompleteLegalFormComponent,
    AutocompleteSiretComponent,
    ConfrereDialogComponent,
    SelectSubscriptionPeriodComponent,
    SelectProvisionFamilyComponent,
    AutocompleteRnaComponent,
    SelectProvisionTypeComponent,
    AutocompleteResponsableComponent,
    AutocompleteTiersIndividualComponent,
    SelectNoticeFamilyComponent,
    SelectBodaccPublicationTypeComponent,
    AutocompleteCompetentAuthorityComponent,
    RadioGroupTransfertFundsComponent,
    SelectDepartmentsComponent,
    RadioGroupActTypeComponent,
    SelectAssignationTypeComponent,
    RadioGroupRecordTypeComponent,
    AutocompleteCityComponent,
    SelectBillingTypeComponent,
    SelectVatComponent,
    SelectRegionsComponent,
    AutocompleteRegionComponent,
    AutocompleteRegieComponent,
    SelectFundTypeComponent,
    ChipsCityComponent,
    AutocompleteDepartmentComponent,
    SelectAccountingAccountClassComponent,
    SelectCompetentAuthorityTypeComponent,
    ChipsCompetitorComponent,
    SelectBillingItemsComponent,
    ChipsConfrereComponent,
    SelectJournalTypeComponent,
    RadioGroupQuotationLabelTypeComponent,
    TrustHtmlPipe,
    SelectVatCollectionTypeComponent,
    AutocompleteEmployeeComponent,
    AutocompleteConfrereComponent,
    RadioGroupJournalTypeComponent,
    SelectDayComponent,
    GradeComponent,
    AutocompleteAccountingAccountComponent,
    SelectAccountingJournalComponent,
    SortTableComponent,
    ChipsProvisionFamilyTypeComponent,
    SelectProvisionScreenTypeComponent,
    AutocompleteBillingItemComponent,
    GenericDateRangePickerComponent,
    SelectInvoiceStatusComponent,
    SelectAffaireStatusComponent,
    ChipsStatusComponent,
    SelectQuotationStatusComponent,
    SelectPaymentWayComponent,
    SingleAttachmentComponent],
  exports: [
    HistoryComponent,
    AttachmentsComponent,
    UploadAttachementDialogComponent,
    AddressingComponent,
    ChipsCityComponent,
    ConfirmDialogComponent,
    SelectDayComponent,
    GenericInputComponent,
    SelectProvisionFamilyComponent,
    AutocompleteDepartmentComponent,
    GenericDatepickerComponent,
    SelectAccountingAccountClassComponent,
    RadioGroupRecordTypeComponent,
    AutocompleteFormalisteEmployeeComponent,
    AutocompleteSalesEmployeeComponent,
    SelectNoticeFamilyComponent,
    AutocompleteCompetentAuthorityComponent,
    RadioGroupJournalTypeComponent,
    SelectJournalTypeComponent,
    AutocompleteSirenComponent,
    RadioGroupQuotationLabelTypeComponent,
    AutocompleteAccountingAccountComponent,
    AutocompleteEmployeeComponent,
    ConfrereDialogComponent,
    AutocompleteTiersIndividualComponent,
    RadioGroupLanguageComponent,
    AutocompleteRegionComponent,
    GenericDateRangePickerComponent,
    SelectContractTypeComponent,
    SelectBuildingDomicilationComponent,
    AutocompleteResponsableComponent,
    RadioGroupTransfertFundsComponent,
    ChipsSpecialOffersComponent,
    RadioGroupActTypeComponent,
    AutocompleteLegalFormComponent,
    ChipsConfrereComponent,
    SelectMailRedirectionComponent,
    SelectVatComponent,
    GradeComponent,
    ChipsCompetitorComponent,
    RadioGroupBillingClosureComponent,
    SelectFundTypeComponent,
    SelectProvisionTypeComponent,
    GenericCheckboxComponent,
    SelectDepartmentsComponent,
    SelectCompetentAuthorityTypeComponent,
    SelectRegionsComponent,
    SelectBillingTypeComponent,
    AutocompleteAffaireComponent,
    AutocompleteCountryComponent,
    SelectGiftComponent,
    AutocompleteInsertionEmployeeComponent,
    SelectFollowupComponent,
    SelectTiersCategoryComponent,
    AutocompleteSiretComponent,
    SelectBillingItemsComponent,
    SelectQuotationStatusComponent,
    RadioGroupBillingLabelComponent,
    SelectProvisionScreenTypeComponent,
    SelectResponsableComponent,
    SelectSubscriptionPeriodComponent,
    RadioGroupBillingClosureRecipientComponent,
    SelectPaymentTypesComponent,
    SelectRefundTypeComponent,
    AutocompleteConfrereComponent,
    SelectDeliveryServiceComponent,
    ChipsMailComponent,
    TrustHtmlPipe,
    SelectBodaccPublicationTypeComponent,
    SelectPaymentDeadlineComponent,
    GenericTextareaComponent,
    AutocompletePostalCodeComponent,
    ChipsStatusComponent,
    ChipsPhoneComponent,
    RadioGroupCivilityComponent,
    AutocompleteRnaComponent,
    GenericToggleComponent,
    SelectVatCollectionTypeComponent,
    SelectTiersTypeComponent,
    AutocompleteCityComponent,
    SelectAccountingJournalComponent,
    SingleAttachmentComponent,
    SortTableComponent,
    SelectInvoiceStatusComponent,
    AutocompleteRegieComponent,
    SelectAssignationTypeComponent,
    ChipsProvisionFamilyTypeComponent,
    SelectPaymentWayComponent,
    SelectAffaireStatusComponent,
    AutocompleteBillingItemComponent,
  ]
})
export class MiscellaneousModule { }
