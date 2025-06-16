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
import { DateAdapter, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { MatTableExporterModule } from 'mat-table-exporter';
import { NgxEchartsModule } from 'ngx-echarts';
import { TrustHtmlPipe } from 'src/app/libs/TrustHtmlPipe';
import { AdministrationComponent } from '../../../administration/components/administration/administration.component';
import { AddNotificationDialogComponent } from '../add-notification-dialog/add-notification-dialog.component';
import { AddressingComponent } from '../addressing/addressing.component';
import { AttachmentsComponent } from '../attachments/attachments.component';
import { AutomaticMailListComponent } from '../automatic-mail-list/automatic-mail-list.component';
import { AvatarChipComponent } from '../avatar-chip/avatar-chip.component';
import { AvatarComponent } from '../avatar/avatar.component';
import { BarChartComponent } from '../bar-chart/bar-chart.component';
import { ChipsStatusComponent } from '../chips-status/chips-status.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { ConfrereDialogComponent } from '../confreres-dialog/confreres-dialog.component';
import { EditCommentDialogComponent } from '../edit-comment-dialog.component/edit-comment-dialog-component.component';
import { EmployeeDialogComponent } from '../employee-dialog/employee-dialog.component';
import { AutocompleteAccountingAccountComponent } from '../forms/autocomplete-accounting-account/autocomplete-accounting-account.component';
import { AutocompleteAffaireComponent } from '../forms/autocomplete-affaire/autocomplete-affaire.component';
import { AutocompleteAzureInvoiceComponent } from '../forms/autocomplete-azure-invoice/autocomplete-azure-invoice';
import { AutocompleteBillingItemComponent } from '../forms/autocomplete-billing-item/autocomplete-billing-item.component';
import { AutocompleteBillingTypeComponent } from '../forms/autocomplete-billing-type/autocomplete-billing-type.component';
import { AutocompleteCityComponent } from '../forms/autocomplete-city/autocomplete-city.component';
import { AutocompleteCompetentAuthorityComponent } from '../forms/autocomplete-competent-authority/autocomplete-competent-authority.component';
import { AutocompleteConfrereComponent } from '../forms/autocomplete-confrere/autocomplete-confrere.component';
import { AutocompleteCountryComponent } from '../forms/autocomplete-country/autocomplete-country.component';
import { AutocompleteCustomerOrderComponent } from '../forms/autocomplete-customer-order/autocomplete-customer-order.component';
import { AutocompleteDepartmentComponent } from '../forms/autocomplete-department/autocomplete-department.component';
import { AutocompleteEmployeeComponent } from '../forms/autocomplete-employee/autocomplete-employee.component';
import { AutocompleteGuichetUniqueFormaliteComponent } from '../forms/autocomplete-formalite-guichet-unique/autocomplete-formalite-guichet-unique.component';
import { AutocompleteInfogreffeFormaliteComponent } from '../forms/autocomplete-formalite-infogreffe/autocomplete-formalite-infogreffe.component';
import { AutocompleteLegalFormComponent } from '../forms/autocomplete-legal-form/autocomplete-legal-form.component';
import { AutocompletePaymentComponent } from '../forms/autocomplete-payment/autocomplete-payment.component';
import { AutocompletePostComponent } from '../forms/autocomplete-post/autocomplete-post.component';
import { AutocompletePostalCodeComponent } from '../forms/autocomplete-postal-code/autocomplete-postal-code.component';
import { AutocompletePrincipalAccountingAccountComponent } from '../forms/autocomplete-principal-accounting-account/autocomplete-principal-accounting-account.component';
import { AutocompleteProviderComponent } from '../forms/autocomplete-provider/autocomplete-provider.component';
import { AutocompleteProvisionTypeComponent } from '../forms/autocomplete-provision-type/autocomplete-provision-type.component';
import { AutocompleteRegionComponent } from '../forms/autocomplete-region/autocomplete-region.component';
import { AutocompleteResponsableComponent } from '../forms/autocomplete-responsable/autocomplete-responsable.component';
import { AutocompleteRnaComponent } from '../forms/autocomplete-rna/autocomplete-rna.component';
import { AutocompleteServiceTypeComponent } from '../forms/autocomplete-service-type/autocomplete-service-type.component';
import { AutocompleteSirenComponent } from '../forms/autocomplete-siren/autocomplete-siren.component';
import { AutocompleteSiretComponent } from '../forms/autocomplete-siret/autocomplete-siret.component';
import { AutocompleteTiersIndividualComponent } from '../forms/autocomplete-tiers-individual/autocomplete-tiers-individual.component';
import { ChipsAttachmentTypeComponent } from '../forms/chips-attachment-type/chips-attachment-type.component';
import { ChipsCityComponent } from '../forms/chips-city/chips-city.component';
import { ChipsCompetitorComponent } from '../forms/chips-competitor/chips-competitor.component';
import { ChipsEmployeeComponent } from '../forms/chips-employee/chips-employee.component';
import { ChipsFormaliteGuichetUniqueComponent } from '../forms/chips-formalite-guichet-unique/chips-formalite-guichet-unique.component';
import { ChipsFormeJuridiqueComponent } from '../forms/chips-forme-juridique/chips-forme-juridique.component';
import { ChipsMailComponent } from '../forms/chips-mail/chips-mail.component';
import { ChipsPaymentTypeComponent } from '../forms/chips-payment-type/chips-payment-type.component';
import { ChipsPhoneComponent } from '../forms/chips-phone/chips-phone.component';
import { ChipsPrincipalAccountingAccountComponent } from '../forms/chips-principal-accounting-account/chips-principal-accounting-account.component';
import { ChipsProvisionFamilyTypeComponent } from '../forms/chips-provision-family/chips-provision-family.component';
import { ChipsServiceTypeComponent } from '../forms/chips-service-type/chips-service-type.component';
import { ChipsSpecialOffersComponent } from '../forms/chips-special-offers/chips-special-offers.component';
import { ChipsTypeDocumentComponent } from '../forms/chips-type-document/chips-type-document.component';
import { GenericCheckboxComponent } from '../forms/generic-checkbox/generic-checkbox.component';
import { GenericChipsInputComponent } from '../forms/generic-chips-input/generic-chips-input.component';
import { GenericDateRangePickerComponent } from '../forms/generic-date-range-picker/generic-date-range-picker.component';
import { CustomDateAdapter } from '../forms/generic-datepicker/CustomDateAdapter';
import { GenericDatepickerComponent } from '../forms/generic-datepicker/generic-datepicker.component';
import { GenericDatetimePickerComponent } from '../forms/generic-datetime-picker/generic-datetime-picker.component';
import { GenericInputComponent } from '../forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../forms/generic-textarea/generic-textarea.component';
import { GenericToggleChoiceComponent } from '../forms/generic-toggle-choice/generic-toggle-choice.component';
import { GenericToggleComponent } from '../forms/generic-toggle/generic-toggle.component';
import { AutocompleteCodeEEEPaysComponent } from '../forms/guichet-unique/autocomplete-code-eee-pays/autocomplete-code-eee-pays.component';
import { AutocompleteCodeInseePaysNaissanceComponent } from '../forms/guichet-unique/autocomplete-code-insee-pays-naissance/autocomplete-code-insee-pays-naissance.component';
import { AutocompleteCodeInseePaysComponent } from '../forms/guichet-unique/autocomplete-code-insee-pays/autocomplete-code-insee-pays.component';
import { AutocompleteCodeNationaliteComponent } from '../forms/guichet-unique/autocomplete-code-nationalite/autocomplete-code-nationalite.component';
import { AutocompleteCodePaysComponent } from '../forms/guichet-unique/autocomplete-code-pays/autocomplete-code-pays.component';
import { AutocompleteCodifNormeComponent } from '../forms/guichet-unique/autocomplete-codif-norme/autocomplete-codif-norme.component';
import { AutocompleteDeviseCapitalComponent } from '../forms/guichet-unique/autocomplete-devise-capital/autocomplete-devise-capital.component';
import { AutocompleteEventsComponent } from '../forms/guichet-unique/autocomplete-events/autocomplete-events.component';
import { AutocompleteFormeJuridiqueComponent } from '../forms/guichet-unique/autocomplete-forme-juridique/autocomplete-forme-juridique.component';
import { AutocompleteMineurNationaliteComponent } from '../forms/guichet-unique/autocomplete-mineur-nationalite/autocomplete-mineur-nationalite.component';
import { AutocompleteModeExerciceComponent } from '../forms/guichet-unique/autocomplete-mode-exercice/autocomplete-mode-exercice.component';
import { AutocompleteRoleEntrepriseComponent } from '../forms/guichet-unique/autocomplete-role-entreprise/autocomplete-role-entreprise.component';
import { AutocompleteRoleComponent } from '../forms/guichet-unique/autocomplete-role/autocomplete-role.component';
import { AutocompleteSecondRoleEntrepriseComponent } from '../forms/guichet-unique/autocomplete-second-role-entreprise/autocomplete-second-role-entreprise.component';
import { AutocompleteTypeDocumentComponent } from '../forms/guichet-unique/autocomplete-type-document/autocomplete-type-document.component';
import { AutocompleteTypeVoieComponent } from '../forms/guichet-unique/autocomplete-type-voie/autocomplete-type-voie.component';
import { RadioGroupActiviteReguliereComponent } from '../forms/guichet-unique/radio-group-activite-reguliere/radio-group-activite-reguliere.component';
import { RadioGroupCapaciteEngagementComponent } from '../forms/guichet-unique/radio-group-capacite-engagement/radio-group-capacite-engagement.component';
import { RadioGroupDestinationLocationGeranceMandComponent } from '../forms/guichet-unique/radio-group-destination-location-gerance-mand/radio-group-destination-location-gerance-mand.component';
import { RadioGroupDiffusionINSEEComponent } from '../forms/guichet-unique/radio-group-diffusion-insee/radio-group-diffusion-insee.component';
import { RadioGroupDocumentExtensionComponent } from '../forms/guichet-unique/radio-group-document-extension/radio-group-document-extension.component';
import { RadioGroupExerciceActiviteComponent } from '../forms/guichet-unique/radio-group-exercice-activite/radio-group-exercice-activite.component';
import { RadioGroupGenreComponent } from '../forms/guichet-unique/radio-group-genre/radio-group-genre.component';
import { RadioGroupJeuneAgriculteurComponent } from '../forms/guichet-unique/radio-group-jeune-agriculteur/radio-group-jeune-agriculteur.component';
import { RadioGroupLieuDeLiquidationComponent } from '../forms/guichet-unique/radio-group-lieu-de-liquidation/radio-group-lieu-de-liquidation.component';
import { RadioGroupMineurSexeComponent } from '../forms/guichet-unique/radio-group-mineur-sexe/radio-group-mineur-sexe.component';
import { RadioGroupMotifDisparitionComponent } from '../forms/guichet-unique/radio-group-motif-disparition/radio-group-motif-disparition.component';
import { RadioGroupMotifRejetCmaComponent } from '../forms/guichet-unique/radio-group-motif-rejet-cma/radio-group-motif-rejet-cma.component';
import { RadioGroupMotifRejetMsaComponent } from '../forms/guichet-unique/radio-group-motif-rejet-msa/radio-group-motif-rejet-msa.component';
import { RadioGroupMotifTrasnfertComponent } from '../forms/guichet-unique/radio-group-motif-trasnfert/radio-group-motif-trasnfert.component';
import { RadioGroupNatureCessationComponent } from '../forms/guichet-unique/radio-group-nature-cessation/radio-group-nature-cessation.component';
import { RadioGroupNatureDesActiviteComponent } from '../forms/guichet-unique/radio-group-nature-des-activite/radio-group-nature-des-activite.component';
import { RadioGroupNatureDomaineComponent } from '../forms/guichet-unique/radio-group-nature-domaine/radio-group-nature-domaine.component';
import { RadioGroupNatureVoletSocialComponent } from '../forms/guichet-unique/radio-group-nature-volet-social/radio-group-nature-volet-social.component';
import { RadioGroupOptionJQPAComponent } from '../forms/guichet-unique/radio-group-option-jqpa/radio-group-option-jqpa.component';
import { RadioGroupOptionParticuliereRegimeBenefiComponent } from '../forms/guichet-unique/radio-group-option-particuliere-regime-benefi/radio-group-option-particuliere-regime-benefi.component';
import { RadioGroupOrigineFusionScissionComponent } from '../forms/guichet-unique/radio-group-origine-fusion-scission/radio-group-origine-fusion-scission.component';
import { RadioGroupPerimetreComponent } from '../forms/guichet-unique/radio-group-perimetre/radio-group-perimetre.component';
import { RadioGroupPeriodiciteVersementComponent } from '../forms/guichet-unique/radio-group-periodicite-versement/radio-group-periodicite-versement.component';
import { RadioGroupQualiteDeNonSedentariteComponent } from '../forms/guichet-unique/radio-group-qualite-de-non-sedentarite/radio-group-qualite-de-non-sedentarite.component';
import { RadioGroupQualiteNonSedentaireComponent } from '../forms/guichet-unique/radio-group-qualite-non-sedentaire/radio-group-qualite-non-sedentaire.component';
import { RadioGroupRoleContratComponent } from '../forms/guichet-unique/radio-group-role-contrat/radio-group-role-contrat.component';
import { RadioGroupSituationVisAVisMsaComponent } from '../forms/guichet-unique/radio-group-situation-visavis-msa/radio-group-situation-visavis-msa.component';
import { RadioGroupStatutDomaineComponent } from '../forms/guichet-unique/radio-group-statut-domaine/radio-group-statut-domaine.component';
import { RadioGroupStatutPourLaFormaliteBlocReComponent } from '../forms/guichet-unique/radio-group-statut-pour-la-formalite-bloc-re/radio-group-statut-pour-la-formalite-bloc-re.component';
import { RadioGroupStatutPraticienComponent } from '../forms/guichet-unique/radio-group-statut-praticien/radio-group-statut-praticien.component';
import { RadioGroupTaciteReconductionComponent } from '../forms/guichet-unique/radio-group-tacite-reconduction/radio-group-tacite-reconduction.component';
import { RadioGroupTotalitePartieComponent } from '../forms/guichet-unique/radio-group-totalite-partie/radio-group-totalite-partie.component';
import { RadioGroupTypeDeStatutsComponent } from '../forms/guichet-unique/radio-group-type-de-statuts/radio-group-type-de-statuts.component';
import { RadioGroupTypeDissolutionComponent } from '../forms/guichet-unique/radio-group-type-dissolution/radio-group-type-dissolution.component';
import { RadioGroupTypeLocataireGerantMandataireComponent } from '../forms/guichet-unique/radio-group-type-locataire-gerant-mandataire/radio-group-type-locataire-gerant-mandataire.component';
import { RadioGroupTypePersonneAncienExploitantComponent } from '../forms/guichet-unique/radio-group-type-personne-ancien-exploitant/radio-group-type-personne-ancien-exploitant.component';
import { RadioGroupTypePersonneBlocPreneurBailComponent } from '../forms/guichet-unique/radio-group-type-personne-bloc-preneur-bail/radio-group-type-personne-bloc-preneur-bail.component';
import { RadioGroupTypePersonneComponent } from '../forms/guichet-unique/radio-group-type-personne/radio-group-type-personne.component';
import { RadioGroupTypeRepresentantComponent } from '../forms/guichet-unique/radio-group-type-representant/radio-group-type-representant.component';
import { SelectCodeRolePersonneQualifieeComponent } from '../forms/guichet-unique/select-code-role-personne-qualifiee/select-code-role-personne-qualifiee.component';
import { SelectConditionVersementTVAComponent } from '../forms/guichet-unique/select-condition-versement-tva/select-condition-versement-tva.component';
import { SelectDestinationEtablissementComponent } from '../forms/guichet-unique/select-destination-etablissement/select-destination-etablissement.component';
import { SelectDestinationComponent } from '../forms/guichet-unique/select-destination/select-destination.component';
import { SelectFormeExerciceActivitePrincipalComponent } from '../forms/guichet-unique/select-forme-exercice-activite-principal/select-forme-exercice-activite-principal.component';
import { SelectFormeExerciceComponent } from '../forms/guichet-unique/select-forme-exercice/select-forme-exercice.component';
import { SelectFormeSocialeComponent } from '../forms/guichet-unique/select-forme-sociale/select-forme-sociale.component';
import { SelectMineurLienParenteComponent } from '../forms/guichet-unique/select-mineur-lien-parente/select-mineur-lien-parente.component';
import { SelectModalitesDeControleComponent } from '../forms/guichet-unique/select-modalites-de-controle/select-modalites-de-controle.component';
import { SelectMotifCessationComponent } from '../forms/guichet-unique/select-motif-cessation/select-motif-cessation.component';
import { SelectMotifFinEirlComponent } from '../forms/guichet-unique/select-motif-fin-eirl/select-motif-fin-eirl.component';
import { SelectMotifRejetGreffeComponent } from '../forms/guichet-unique/select-motif-rejet-greffe/select-motif-rejet-greffe.component';
import { SelectNatureGeranceComponent } from '../forms/guichet-unique/select-nature-gerance/select-nature-gerance.component';
import { SelectOptionEirlComponent } from '../forms/guichet-unique/select-option-eirl/select-option-eirl.component';
import { SelectOrganismeAssuranceMaladieActueComponent } from '../forms/guichet-unique/select-organisme-assurance-maladie-actue/select-organisme-assurance-maladie-actue.component';
import { SelectPeriodiciteEtOptionsParticulieComponent } from '../forms/guichet-unique/select-periodicite-et-options-particulie/select-periodicite-et-options-particulie.component';
import { SelectPrecisionActiviteComponent } from '../forms/guichet-unique/select-precision-activite/select-precision-activite.component';
import { SelectRegimeImpositionBeneficesComponent } from '../forms/guichet-unique/select-regime-imposition-benefices/select-regime-imposition-benefices.component';
import { SelectRegimeImpositionBenefices2Component } from '../forms/guichet-unique/select-regime-imposition-benefices2/select-regime-imposition-benefices2.component';
import { SelectRegimeImpositionTVAComponent } from '../forms/guichet-unique/select-regime-imposition-tva/select-regime-imposition-tva.component';
import { SelectRegistreEirlDeLancienneEirlComponent } from '../forms/guichet-unique/select-registre-eirl-de-lancienne-eirl/select-registre-eirl-de-lancienne-eirl.component';
import { SelectRegistreEirlComponent } from '../forms/guichet-unique/select-registre-eirl/select-registre-eirl.component';
import { SelectRoleConjointComponent } from '../forms/guichet-unique/select-role-conjoint/select-role-conjoint.component';
import { SelectRolePourEntrepriseComponent } from '../forms/guichet-unique/select-role-pour-entreprise/select-role-pour-entreprise.component';
import { SelectSituationMatrimonialeComponent } from '../forms/guichet-unique/select-situation-matrimoniale/select-situation-matrimoniale.component';
import { SelectStatusComponent } from '../forms/guichet-unique/select-status/select-status.component';
import { SelectStatutContratComponent } from '../forms/guichet-unique/select-statut-contrat/select-statut-contrat.component';
import { SelectStatutExerciceActiviteSimultanComponent } from '../forms/guichet-unique/select-statut-exercice-activite-simultan/select-statut-exercice-activite-simultan.component';
import { SelectStatutFormaliteComponent } from '../forms/guichet-unique/select-statut-formalite/select-statut-formalite.component';
import { SelectStatutPourFormaliteComponent } from '../forms/guichet-unique/select-statut-pour-formalite/select-statut-pour-formalite.component';
import { SelectStatutPourLaFormaliteComponent } from '../forms/guichet-unique/select-statut-pour-la-formalite/select-statut-pour-la-formalite.component';
import { SelectStatutVisAVisFormaliteComponent } from '../forms/guichet-unique/select-statut-visavis-formalite/select-statut-visavis-formalite.component';
import { SelectSuccursaleOuFilialeComponent } from '../forms/guichet-unique/select-succursale-ou-filiale/select-succursale-ou-filiale.component';
import { SelectTutelleCuratelleComponent } from '../forms/guichet-unique/select-tutelle-curatelle/select-tutelle-curatelle.component';
import { SelectTypeDePersonneComponent } from '../forms/guichet-unique/select-type-de-personne/select-type-de-personne.component';
import { SelectTypeExploitationComponent } from '../forms/guichet-unique/select-type-exploitation/select-type-exploitation.component';
import { SelectTypeFormaliteComponent } from '../forms/guichet-unique/select-type-formalite/select-type-formalite.component';
import { SelectTypeLiasseComponent } from '../forms/guichet-unique/select-type-liasse/select-type-liasse.component';
import { SelectTypeOrigineComponent } from '../forms/guichet-unique/select-type-origine/select-type-origine.component';
import { SelectTypePersonneContractanteComponent } from '../forms/guichet-unique/select-type-personne-contractante/select-type-personne-contractante.component';
import { RadioGroupActTypeComponent } from '../forms/radio-group-act-type/radio-group-act-type.component';
import { RadioGroupBillingClosureRecipientComponent } from '../forms/radio-group-billing-closure-recipient/radio-group-billing-closure-recipient.component';
import { RadioGroupBillingClosureComponent } from '../forms/radio-group-billing-closure/radio-group-billing-closure.component';
import { RadioGroupBillingLabelComponent } from '../forms/radio-group-billing-label/radio-group-billing-label.component';
import { RadioGroupCivilityComponent } from '../forms/radio-group-civility/radio-group-civility.component';
import { RadioGroupJournalTypeComponent } from '../forms/radio-group-journal-type/radio-group-journal-type.component';
import { RadioGroupLanguageComponent } from '../forms/radio-group-language/radio-group-language.component';
import { RadioGroupRecordTypeComponent } from '../forms/radio-group-record-type/radio-group-record-type.component';
import { RadioGroupTransfertFundsComponent } from '../forms/radio-group-transfert-funds/radio-group-transfert-funds.component';
import { SelectAccountingAccountClassComponent } from '../forms/select-accounting-account-class/select-accounting-account-class.component';
import { SelectAccountingJournalComponent } from '../forms/select-accounting-journal/select-accounting-journal.component';
import { SelectActiveDirectoryGroupComponent } from '../forms/select-active-directory-group/select-active-directory-group.component';
import { SelectAssignationTypeComponent } from '../forms/select-assignation-type/select-assignation-type.component';
import { SelectAttachmentTypeComponent } from '../forms/select-attachment-type/select-attachment-type.component';
import { SelectBatchCategoryComponent } from '../forms/select-batch-category/select-batch-category.component';
import { SelectBatchSettingsComponent } from '../forms/select-batch-settings/select-batch-settings.component';
import { SelectBatchStatusComponent } from '../forms/select-batch-status/select-batch-status.component';
import { SelectBillingItemsComponent } from '../forms/select-billing-items/select-billing-items.component';
import { SelectBillingTypeComponent } from '../forms/select-billing-type/select-billing-type.component';
import { SelectBuildingDomicilationComponent } from '../forms/select-building-domicilation/select-building-domicilation.component';
import { SelectCategoryComponent } from '../forms/select-category/select-category.component';
import { SelectCompetentAuthorityTypeComponent } from '../forms/select-competent-authority-type/select-competent-authority-type.component';
import { SelectContractTypeComponent } from '../forms/select-contract-type/select-contract-type.component';
import { SelectCustomerOrderFrequencyComponent } from '../forms/select-customer-order-frequency/select-customer-order-frequency.component';
import { SelectCustomerOrderOriginComponent } from '../forms/select-customer-order-origin/select-customer-order-origin.component';
import { SelectCustomerOrderStatusComponent } from '../forms/select-customer-order-status/select-customer-order-status.component';
import { SelectDayComponent } from '../forms/select-day/select-day.component';
import { SelectDeliveryServiceComponent } from '../forms/select-delivery-service/select-delivery-service.component';
import { SelectDepartmentComponent } from '../forms/select-department/select-department.component';
import { SelectDepartmentsComponent } from '../forms/select-departments/select-departments.component';
import { SelectDocumentTypeComponent } from '../forms/select-document-type/select-document-type.component';
import { SelectEmployeeComponent } from '../forms/select-employee/select-employee.component';
import { SelectFollowupComponent } from '../forms/select-followup/select-followup.component';
import { SelectFormaliteInfogreffeStatusComponent } from '../forms/select-formalite-infogreffe-status/select-formalite-infogreffe-status.component';
import { SelectFundTypeComponent } from '../forms/select-fund-type/select-fund-type.component';
import { SelectGiftComponent } from '../forms/select-gift/select-gift.component';
import { SelectIncidentCauseComponent } from '../forms/select-incident-cause/select-incident-cause.component';
import { SelectIncidentReportStatusComponent } from '../forms/select-incident-report-status/select-incident-report-status.component';
import { SelectIncidentResponsibilityComponent } from '../forms/select-incident-responsibility/select-incident-responsibility.component';
import { SelectIncidentTypeComponent } from '../forms/select-incident-type/select-incident-type.component';
import { SelectIndicatorGroupComponent } from '../forms/select-indicator-group/select-indicator-group.component';
import { SelectIndicatorComponent } from '../forms/select-indicator/select-indicator.component';
import { SelectInvoiceStatusOneComponent } from '../forms/select-invoice-status-one/select-invoice-status-one.component';
import { SelectInvoiceStatusComponent } from '../forms/select-invoice-status/select-invoice-status.component';
import { SelectInvoicingBlockageComponent } from '../forms/select-invoicing-blockage/select-invoicing-blockage.component';
import { SelectJournalTypeOneComponent } from '../forms/select-journal-type-one/select-journal-type-one.component';
import { SelectJournalTypeComponent } from '../forms/select-journal-type/select-journal-type.component';
import { SelectJssCategoryComponent } from '../forms/select-jss-category/select-jss-category.component';
import { SelectMailRedirectionComponent } from '../forms/select-mail-redirection/select-mail-redirection.component';
import { SelectMyJssCategoryComponent } from '../forms/select-myjss-category/select-myjss-category.component';
import { SelectNodeComponent } from '../forms/select-node/select-node.component';
import { SelectNoticeFamilyComponent } from '../forms/select-notice-family/select-notice-family.component';
import { SelectNoticeTemplateComponent } from '../forms/select-notice-template/select-notice-template.component';
import { SelectNoticeTypeComponent } from '../forms/select-notice-type/select-notice-type.component';
import { SelectNotificationTypesComponent } from '../forms/select-notification-types/select-notification-types.component';
import { SelectPaperSetTypeComponent } from '../forms/select-paper-set-type/select-paper-set-type.component';
import { SelectPaymentDeadlineComponent } from '../forms/select-payment-deadline/select-payment-deadline.component';
import { SelectPaymentTypesComponent } from '../forms/select-payment-types/select-payment-types.component';
import { SelectPrintLabelRecipientComponent } from '../forms/select-print-label-recipient/select-print-label-recipient.component';
import { SelectProvisionFamilyComponent } from '../forms/select-provision-family/select-provision-familiy.component';
import { SelectProvisionScreenTypeComponent } from '../forms/select-provision-screen-type/select-provision-screen-type.component';
import { SelectProvisionStautsComponent } from '../forms/select-provision-stauts/select-provision-stauts.component';
import { SelectProvisionTypeComponent } from '../forms/select-provision-type/select-provision-type.component';
import { SelectProvisionComponent } from '../forms/select-provision/select-provision.component';
import { SelectPublishingDepartmentComponent } from '../forms/select-publishing-department/select-publishing-department.component';
import { SelectQuotationAbandonReasonComponent } from '../forms/select-quotation-abandon-reason/select-quotation-abandon-reason.component';
import { SelectQuotationStatusComponent } from '../forms/select-quotation-status/select-quotation-status.component';
import { SelectRefundTypeComponent } from '../forms/select-refund-type/select-refund-type.component';
import { SelectRegionsComponent } from '../forms/select-regions/select-regions.component';
import { SelectReportingDatasetComponent } from '../forms/select-reporting-dataset/select-reporting-dataset.component';
import { SelectResponsableComponent } from '../forms/select-responsable/select-responsable.component';
import { SelectRffFrequencyComponent } from '../forms/select-rff-frequency/select-rff-frequency.component';
import { SelectServiceFamilyGroupComponent } from '../forms/select-service-family-group/select-service-family-group.component';
import { SelectServiceFamilyComponent } from '../forms/select-service-family/select-service-family.component';
import { SelectServiceFieldDataTypeComponent } from '../forms/select-service-field-data-type/select-service-field-data-type.component';
import { SelectServiceFieldTypeComponent } from '../forms/select-service-field-type/select-service-field-type.component';
import { SelectServiceTypeComponent } from '../forms/select-service-type/select-service-type.component';
import { SelectSpecialOfferComponent } from '../forms/select-special-offer/select-special-offer.component';
import { SelectSubscriptionPeriodComponent } from '../forms/select-subscription-period/select-subscription-period.component';
import { SelectTiersCategoryComponent } from '../forms/select-tiers-category/select-tiers-category.component';
import { SelectTiersTypeComponent } from '../forms/select-tiers-type/select-tiers-type.component';
import { SelectTypeDocumentComponent } from '../forms/select-type-document/select-type-document.component';
import { SelectValueServiceFieldTypeComponent } from '../forms/select-value-service-field-type/select-value-service-field-type.component';
import { SelectVatCollectionTypeComponent } from '../forms/select-vat-collection-type/select-vat-collection-type.component';
import { SelectVatComponent } from '../forms/select-vat/select-vat.component';
import { SelectWebinarComponent } from '../forms/select-webinar/select-webinar.component';
import { SingleChipsMailComponent } from '../forms/single-chips-mail/single-chips-mail.component';
import { GaugeChartComponent } from '../gauge-chart/gauge-chart.component';
import { GradeComponent } from '../grade/grade.component';
import { HistoryComponent } from '../history/history.component';
import { MultipleUploadComponent } from '../multiple-upload/multiple-upload.component';
import { NotificationDialogComponent } from '../notification-dialog/notification-dialog.component';
import { NotificationListComponent } from '../notification-list/notification-list.component';
import { SingleAttachmentComponent } from '../single-attachment/single-attachment.component';
import { SortTableComponent } from '../sort-table/sort-table.component';
import { TiersFollowupComponent } from '../tiers-followup/tiers-followup.component';
import { UploadAttachementDialogComponent } from '../upload-attachement-dialog/upload-attachement-dialog.component';
import { WorkflowDialogComponent } from '../workflow-dialog/workflow-dialog.component';
import { MiscellaneousComponent } from './miscellaneous.component';

const routes: Routes = [
  { path: 'referential/affaire/:id', component: AdministrationComponent },
  { path: 'notifications', component: NotificationListComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
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
    MatNativeDateModule,
    MatFormFieldModule,
    MatTooltipModule,
    MatMenuModule,
    MatTableExporterModule,
    MatRadioModule,
    MatCardModule,
    MatBadgeModule,
    DragDropModule,
    MatTabsModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatCheckboxModule,
    MatAutocompleteModule,
    MatProgressSpinnerModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts'),
    }),
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
    GenericToggleComponent,
    AutocompletePostalCodeComponent,
    RadioGroupCivilityComponent,
    ChipsSpecialOffersComponent,
    GenericTextareaComponent,
    SelectDeliveryServiceComponent,
    AutocompleteCountryComponent,
    ChipsPhoneComponent,
    SelectFollowupComponent,
    EmployeeDialogComponent,
    RadioGroupLanguageComponent,
    ChipsMailComponent,
    GenericCheckboxComponent,
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
    SelectProvisionStautsComponent,
    AutocompleteResponsableComponent,
    AutocompleteTiersIndividualComponent,
    SelectNoticeFamilyComponent,
    AutocompleteCompetentAuthorityComponent,
    RadioGroupTransfertFundsComponent,
    SelectDepartmentsComponent,
    RadioGroupActTypeComponent,
    SelectAssignationTypeComponent,
    RadioGroupRecordTypeComponent,
    AutocompleteCityComponent,
    SelectVatComponent,
    SelectRegionsComponent,
    AutocompleteRegionComponent,
    SelectFundTypeComponent,
    ChipsCityComponent,
    AutocompleteDepartmentComponent,
    SelectAccountingAccountClassComponent,
    SelectCompetentAuthorityTypeComponent,
    ChipsCompetitorComponent,
    SelectBillingItemsComponent,
    SelectJournalTypeComponent,
    TrustHtmlPipe,
    SelectVatCollectionTypeComponent,
    AutocompleteEmployeeComponent,
    AutocompleteConfrereComponent,
    RadioGroupJournalTypeComponent,
    SelectDayComponent,
    GradeComponent,
    SelectDocumentTypeComponent,
    AutocompleteAccountingAccountComponent,
    SelectAccountingJournalComponent,
    SortTableComponent,
    AutocompleteProviderComponent,
    ChipsProvisionFamilyTypeComponent,
    SelectProvisionScreenTypeComponent,
    SelectAttachmentTypeComponent,
    AutocompleteBillingItemComponent,
    GenericDateRangePickerComponent,
    SelectInvoiceStatusComponent,
    AutocompleteBillingTypeComponent,
    ChipsStatusComponent,
    SelectJournalTypeOneComponent,
    SelectQuotationStatusComponent,
    SelectInvoiceStatusOneComponent,
    SelectCustomerOrderStatusComponent,
    AutocompletePaymentComponent,
    AvatarComponent,
    AvatarChipComponent,
    SelectDepartmentsComponent,
    NotificationDialogComponent,
    SelectDepartmentComponent,
    WorkflowDialogComponent,
    AutocompleteCustomerOrderComponent,
    ChipsEmployeeComponent,
    SingleAttachmentComponent,
    ChipsAttachmentTypeComponent,
    AddNotificationDialogComponent,
    AutocompletePrincipalAccountingAccountComponent,
    GenericDatetimePickerComponent,
    TiersFollowupComponent,
    AutomaticMailListComponent,
    GenericChipsInputComponent,
    SelectReportingDatasetComponent,
    ChipsPaymentTypeComponent,
    AutocompleteAzureInvoiceComponent,
    SelectCustomerOrderOriginComponent,
    EditCommentDialogComponent,
    SelectProvisionComponent,
    AutocompletePostComponent,
    //Guichet Unique
    AutocompleteTypeDocumentComponent,
    ChipsTypeDocumentComponent,
    RadioGroupMotifRejetCmaComponent,
    SelectMotifRejetGreffeComponent,
    RadioGroupMotifRejetMsaComponent,
    RadioGroupMotifTrasnfertComponent,
    RadioGroupActiviteReguliereComponent,
    RadioGroupCapaciteEngagementComponent,
    AutocompleteCodeEEEPaysComponent,
    AutocompleteCodeInseePaysComponent,
    AutocompleteCodeInseePaysNaissanceComponent,
    AutocompleteCodeNationaliteComponent,
    AutocompleteCodePaysComponent,
    SelectCodeRolePersonneQualifieeComponent,
    AutocompleteCodifNormeComponent,
    SelectConditionVersementTVAComponent,
    SelectDestinationComponent,
    SelectDestinationEtablissementComponent,
    RadioGroupDestinationLocationGeranceMandComponent,
    AutocompleteDeviseCapitalComponent,
    RadioGroupDiffusionINSEEComponent,
    RadioGroupDocumentExtensionComponent,
    AutocompleteEventsComponent,
    RadioGroupExerciceActiviteComponent,
    SelectFormeExerciceComponent,
    SelectFormeExerciceActivitePrincipalComponent,
    AutocompleteFormeJuridiqueComponent,
    SelectFormeSocialeComponent,
    RadioGroupGenreComponent,
    RadioGroupJeuneAgriculteurComponent,
    RadioGroupLieuDeLiquidationComponent,
    SelectMineurLienParenteComponent,
    AutocompleteMineurNationaliteComponent,
    RadioGroupMineurSexeComponent,
    SelectModalitesDeControleComponent,
    AutocompleteModeExerciceComponent,
    SelectMotifCessationComponent,
    RadioGroupMotifDisparitionComponent,
    SelectMotifFinEirlComponent,
    RadioGroupNatureCessationComponent,
    RadioGroupNatureDesActiviteComponent,
    RadioGroupNatureDomaineComponent,
    SelectNatureGeranceComponent,
    RadioGroupNatureVoletSocialComponent,
    SelectOptionEirlComponent,
    RadioGroupOptionJQPAComponent,
    RadioGroupOptionParticuliereRegimeBenefiComponent,
    SelectOrganismeAssuranceMaladieActueComponent,
    RadioGroupOrigineFusionScissionComponent,
    RadioGroupPerimetreComponent,
    SelectPeriodiciteEtOptionsParticulieComponent,
    RadioGroupPeriodiciteVersementComponent,
    SelectPrecisionActiviteComponent,
    RadioGroupQualiteDeNonSedentariteComponent,
    RadioGroupQualiteNonSedentaireComponent,
    SelectRegimeImpositionBeneficesComponent,
    SelectRegimeImpositionBenefices2Component,
    SelectRegimeImpositionTVAComponent,
    SelectRegistreEirlComponent,
    SelectRegistreEirlDeLancienneEirlComponent,
    AutocompleteRoleComponent,
    SelectRoleConjointComponent,
    RadioGroupRoleContratComponent,
    AutocompleteRoleEntrepriseComponent,
    SelectRolePourEntrepriseComponent,
    AutocompleteSecondRoleEntrepriseComponent,
    SelectSituationMatrimonialeComponent,
    RadioGroupSituationVisAVisMsaComponent,
    SelectStatusComponent,
    SelectStatutContratComponent,
    RadioGroupStatutDomaineComponent,
    SelectStatutExerciceActiviteSimultanComponent,
    SelectStatutFormaliteComponent,
    SelectStatutPourFormaliteComponent,
    SelectStatutPourLaFormaliteComponent,
    RadioGroupStatutPourLaFormaliteBlocReComponent,
    RadioGroupStatutPraticienComponent,
    SelectStatutVisAVisFormaliteComponent,
    SelectSuccursaleOuFilialeComponent,
    RadioGroupTaciteReconductionComponent,
    RadioGroupTotalitePartieComponent,
    SelectTutelleCuratelleComponent,
    SelectTypeDePersonneComponent,
    RadioGroupTypeDeStatutsComponent,
    RadioGroupTypeDissolutionComponent,
    SelectTypeExploitationComponent,
    SelectTypeFormaliteComponent,
    SelectTypeLiasseComponent,
    RadioGroupTypeLocataireGerantMandataireComponent,
    SelectTypeOrigineComponent,
    RadioGroupTypePersonneComponent,
    RadioGroupTypePersonneBlocPreneurBailComponent,
    RadioGroupTypePersonneAncienExploitantComponent,
    SelectTypePersonneContractanteComponent,
    RadioGroupTypeRepresentantComponent,
    AutocompleteTypeVoieComponent,
    AutocompleteGuichetUniqueFormaliteComponent,
    ChipsFormaliteGuichetUniqueComponent,
    SelectRffFrequencyComponent,
    SelectBatchStatusComponent,
    SelectBatchSettingsComponent,
    SelectNodeComponent,
    BarChartComponent,
    GaugeChartComponent,
    SelectBatchCategoryComponent,
    SelectQuotationAbandonReasonComponent,
    SelectServiceFamilyGroupComponent,
    SelectServiceFamilyComponent,
    ChipsFormeJuridiqueComponent,
    SelectTypeDocumentComponent,
    AutocompleteTypeDocumentComponent,
    SelectServiceTypeComponent,
    SelectSpecialOfferComponent,
    AutocompleteServiceTypeComponent,
    MultipleUploadComponent,
    AutocompleteProvisionTypeComponent,
    SelectCustomerOrderFrequencyComponent,
    SelectBillingTypeComponent,
    SelectPaperSetTypeComponent,
    ChipsPrincipalAccountingAccountComponent,
    SelectServiceFieldTypeComponent,
    SelectServiceFieldTypeComponent,
    SelectValueServiceFieldTypeComponent,
    SelectServiceFieldDataTypeComponent,
    SelectActiveDirectoryGroupComponent,
    AutocompleteInfogreffeFormaliteComponent,
    SelectFormaliteInfogreffeStatusComponent,
    ChipsServiceTypeComponent,
    SingleChipsMailComponent,
    SelectCategoryComponent,
    SelectPrintLabelRecipientComponent,
    SelectMyJssCategoryComponent,
    SelectJssCategoryComponent,
    SelectEmployeeComponent,
    SelectNotificationTypesComponent,
    NotificationListComponent,
    SelectWebinarComponent,
    SelectIndicatorComponent,
    SelectIndicatorGroupComponent,
    GenericToggleChoiceComponent,
    SelectInvoicingBlockageComponent,
    SelectIncidentResponsibilityComponent,
    SelectIncidentReportStatusComponent,
    SelectPublishingDepartmentComponent,
    SelectIncidentCauseComponent,
    SelectIncidentTypeComponent,
    SelectNoticeTypeComponent,
    SelectNoticeTemplateComponent
  ],
  exports: [
    HistoryComponent,
    AttachmentsComponent,
    UploadAttachementDialogComponent,
    AddressingComponent,
    ChipsCityComponent,
    SelectJournalTypeOneComponent,
    SelectInvoiceStatusOneComponent,
    SelectDepartmentComponent,
    ConfirmDialogComponent,
    SelectDayComponent,
    GenericInputComponent,
    SelectDepartmentsComponent,
    SelectProvisionFamilyComponent,
    AutocompleteDepartmentComponent,
    GenericDatepickerComponent,
    SelectAccountingAccountClassComponent,
    RadioGroupRecordTypeComponent,
    SelectNoticeFamilyComponent,
    AutocompleteCompetentAuthorityComponent,
    RadioGroupJournalTypeComponent,
    SelectJournalTypeComponent,
    AutocompleteSirenComponent,
    AutocompleteAccountingAccountComponent,
    AutocompleteEmployeeComponent,
    ConfrereDialogComponent,
    AutocompleteTiersIndividualComponent,
    RadioGroupLanguageComponent,
    AutocompleteBillingTypeComponent,
    AutocompleteRegionComponent,
    GenericDateRangePickerComponent,
    SelectContractTypeComponent,
    SelectBuildingDomicilationComponent,
    AutocompleteResponsableComponent,
    RadioGroupTransfertFundsComponent,
    ChipsSpecialOffersComponent,
    RadioGroupActTypeComponent,
    AutocompleteLegalFormComponent,
    AvatarComponent,
    AvatarChipComponent,
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
    AutocompleteAffaireComponent,
    SelectDocumentTypeComponent,
    SelectAttachmentTypeComponent,
    AutocompleteCountryComponent,
    SelectGiftComponent,
    SelectFollowupComponent,
    SelectTiersCategoryComponent,
    AutocompleteSiretComponent,
    SelectBillingItemsComponent,
    SelectQuotationStatusComponent,
    RadioGroupBillingLabelComponent,
    AutocompleteProviderComponent,
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
    ChipsEmployeeComponent,
    SelectPaymentDeadlineComponent,
    GenericTextareaComponent,
    AutocompletePostalCodeComponent,
    ChipsStatusComponent,
    ChipsPhoneComponent,
    SelectCustomerOrderStatusComponent,
    RadioGroupCivilityComponent,
    AutocompleteRnaComponent,
    ChipsAttachmentTypeComponent,
    SelectProvisionStautsComponent,
    GenericToggleComponent,
    SelectVatCollectionTypeComponent,
    SelectTiersTypeComponent,
    AutocompleteCityComponent,
    SelectAccountingJournalComponent,
    SingleAttachmentComponent,
    SortTableComponent,
    EditCommentDialogComponent,
    SelectInvoiceStatusComponent,
    SelectAssignationTypeComponent,
    ChipsProvisionFamilyTypeComponent,
    AutocompleteBillingItemComponent,
    AutocompleteCustomerOrderComponent,
    AutocompletePaymentComponent,
    WorkflowDialogComponent,
    TiersFollowupComponent,
    AutomaticMailListComponent,
    AutocompletePrincipalAccountingAccountComponent,
    GenericChipsInputComponent,
    SelectReportingDatasetComponent,
    ChipsPaymentTypeComponent,
    AutocompleteAzureInvoiceComponent,
    SelectProvisionComponent,
    AutocompletePostComponent,
    //Guichet Unique
    AutocompleteTypeDocumentComponent,
    RadioGroupMotifRejetCmaComponent,
    SelectMotifRejetGreffeComponent,
    ChipsAttachmentTypeComponent,
    RadioGroupMotifRejetMsaComponent,
    RadioGroupMotifTrasnfertComponent,
    RadioGroupActiviteReguliereComponent,
    RadioGroupCapaciteEngagementComponent,
    AutocompleteCodeEEEPaysComponent,
    AutocompleteCodeInseePaysComponent,
    AutocompleteCodeInseePaysNaissanceComponent,
    AutocompleteCodeNationaliteComponent,
    AutocompleteCodePaysComponent,
    SelectCodeRolePersonneQualifieeComponent,
    AutocompleteCodifNormeComponent,
    SelectConditionVersementTVAComponent,
    SelectDestinationComponent,
    SelectDestinationEtablissementComponent,
    RadioGroupDestinationLocationGeranceMandComponent,
    AutocompleteDeviseCapitalComponent,
    RadioGroupDiffusionINSEEComponent,
    RadioGroupDocumentExtensionComponent,
    AutocompleteEventsComponent,
    RadioGroupExerciceActiviteComponent,
    SelectFormeExerciceComponent,
    SelectFormeExerciceActivitePrincipalComponent,
    AutocompleteFormeJuridiqueComponent,
    SelectFormeSocialeComponent,
    RadioGroupGenreComponent,
    RadioGroupJeuneAgriculteurComponent,
    RadioGroupLieuDeLiquidationComponent,
    SelectMineurLienParenteComponent,
    AutocompleteMineurNationaliteComponent,
    RadioGroupMineurSexeComponent,
    SelectModalitesDeControleComponent,
    AutocompleteModeExerciceComponent,
    SelectMotifCessationComponent,
    RadioGroupMotifDisparitionComponent,
    SelectMotifFinEirlComponent,
    RadioGroupNatureCessationComponent,
    RadioGroupNatureDesActiviteComponent,
    RadioGroupNatureDomaineComponent,
    SelectNatureGeranceComponent,
    RadioGroupNatureVoletSocialComponent,
    SelectOptionEirlComponent,
    RadioGroupOptionJQPAComponent,
    RadioGroupOptionParticuliereRegimeBenefiComponent,
    SelectOrganismeAssuranceMaladieActueComponent,
    RadioGroupOrigineFusionScissionComponent,
    RadioGroupPerimetreComponent,
    SelectPeriodiciteEtOptionsParticulieComponent,
    RadioGroupPeriodiciteVersementComponent,
    SelectPrecisionActiviteComponent,
    RadioGroupQualiteDeNonSedentariteComponent,
    RadioGroupQualiteNonSedentaireComponent,
    SelectRegimeImpositionBeneficesComponent,
    SelectRegimeImpositionBenefices2Component,
    SelectRegimeImpositionTVAComponent,
    SelectRegistreEirlComponent,
    SelectRegistreEirlDeLancienneEirlComponent,
    AutocompleteRoleComponent,
    SelectRoleConjointComponent,
    RadioGroupRoleContratComponent,
    AutocompleteRoleEntrepriseComponent,
    SelectRolePourEntrepriseComponent,
    AutocompleteSecondRoleEntrepriseComponent,
    SelectSituationMatrimonialeComponent,
    RadioGroupSituationVisAVisMsaComponent,
    SelectStatusComponent,
    SelectStatutContratComponent,
    RadioGroupStatutDomaineComponent,
    SelectStatutExerciceActiviteSimultanComponent,
    SelectStatutFormaliteComponent,
    SelectStatutPourFormaliteComponent,
    SelectStatutPourLaFormaliteComponent,
    RadioGroupStatutPourLaFormaliteBlocReComponent,
    RadioGroupStatutPraticienComponent,
    SelectStatutVisAVisFormaliteComponent,
    SelectSuccursaleOuFilialeComponent,
    RadioGroupTaciteReconductionComponent,
    ChipsAttachmentTypeComponent,
    RadioGroupTotalitePartieComponent,
    SelectTutelleCuratelleComponent,
    SelectTypeDePersonneComponent,
    RadioGroupTypeDeStatutsComponent,
    RadioGroupTypeDissolutionComponent,
    SelectTypeExploitationComponent,
    SelectTypeFormaliteComponent,
    SelectTypeLiasseComponent,
    RadioGroupTypeLocataireGerantMandataireComponent,
    SelectTypeOrigineComponent,
    RadioGroupTypePersonneComponent,
    RadioGroupTypePersonneBlocPreneurBailComponent,
    RadioGroupTypePersonneAncienExploitantComponent,
    SelectTypePersonneContractanteComponent,
    RadioGroupTypeRepresentantComponent,
    ChipsTypeDocumentComponent,
    AutocompleteTypeVoieComponent,
    GenericDatetimePickerComponent,
    AutocompleteGuichetUniqueFormaliteComponent,
    ChipsFormaliteGuichetUniqueComponent,
    SelectCustomerOrderOriginComponent,
    SelectRffFrequencyComponent,
    SelectBatchStatusComponent,
    SelectBatchSettingsComponent,
    SelectNodeComponent,
    BarChartComponent,
    GaugeChartComponent,
    SelectBatchCategoryComponent,
    SelectQuotationAbandonReasonComponent,
    SelectServiceFamilyGroupComponent,
    SelectServiceFamilyComponent,
    ChipsFormeJuridiqueComponent,
    SelectTypeDocumentComponent,
    AutocompleteTypeDocumentComponent,
    SelectServiceTypeComponent,
    SelectSpecialOfferComponent,
    AutocompleteServiceTypeComponent,
    MultipleUploadComponent,
    AutocompleteProvisionTypeComponent,
    SelectCustomerOrderFrequencyComponent,
    SelectBillingTypeComponent,
    SelectPaperSetTypeComponent,
    ChipsPrincipalAccountingAccountComponent,
    SelectServiceFieldTypeComponent,
    SelectServiceFieldTypeComponent,
    SelectValueServiceFieldTypeComponent,
    SelectServiceFieldDataTypeComponent,
    SelectActiveDirectoryGroupComponent,
    AutocompleteInfogreffeFormaliteComponent,
    SelectFormaliteInfogreffeStatusComponent,
    ChipsServiceTypeComponent,
    SingleChipsMailComponent,
    SelectCategoryComponent,
    SelectPrintLabelRecipientComponent,
    SelectMyJssCategoryComponent,
    SelectJssCategoryComponent,
    SelectEmployeeComponent,
    SelectNotificationTypesComponent,
    SelectWebinarComponent,
    SelectIndicatorComponent,
    SelectIndicatorGroupComponent,
    GenericToggleChoiceComponent,
    SelectInvoicingBlockageComponent,
    SelectIncidentResponsibilityComponent,
    SelectIncidentReportStatusComponent,
    SelectPublishingDepartmentComponent,
    SelectIncidentCauseComponent,
    SelectIncidentTypeComponent,
    SelectNoticeTypeComponent,
    SelectNoticeTemplateComponent
  ], providers: [
    { provide: DateAdapter, useClass: CustomDateAdapter }
    , SortTableComponent
  ]
})
export class MiscellaneousModule { }
