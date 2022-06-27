import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
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
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { ReferentialActTypeComponent } from '../referentials/referential-act-type/referential-act-type.component';
import { ReferentialBillingClosureRecipientTypeComponent } from '../referentials/referential-billing-closure-recipient-type/referential-billing-closure-recipient-type.component';
import { ReferentialBillingClosureTypeComponent } from '../referentials/referential-billing-closure-type/referential-billing-closure-type.component';
import { ReferentialBodaccPublicationTypeComponent } from '../referentials/referential-bodacc-publication-type/referential-bodacc-publication-type.component';
import { ReferentialBuildingDomiciliationComponent } from '../referentials/referential-building-domiciliation/referential-building-domiciliation.componentt';
import { ReferentialDomiciliationContractTypeComponent } from '../referentials/referential-domiciliation-contract-type/referential-domiciliation-contract-type.componentt';
import { ReferentialFundTypeComponent } from '../referentials/referential-fund-type/referential-fund-type.component';
import { ReferentialJournalTypeComponent } from '../referentials/referential-journal-type/referential-journal-type.component';
import { ReferentialMailRedirectionTypeComponent } from '../referentials/referential-mail-redirection-type/referential-mail-redirection-type.component';
import { ReferentialNoticeTypeFamilyComponent } from '../referentials/referential-notice-type-family/referential-notice-type-family.component';
import { ReferentialQuotationLabelTypeComponent } from '../referentials/referential-quotation-label-type/referential-notice-type-family.component';
import { ReferentialQuotationStatusComponent } from '../referentials/referential-quotation-status/referential-quotation-status.component';
import { ReferentialTransfertFundsTypeComponent } from '../referentials/referential-transfert-fund-type/referential-transfert-fund-type.component';
import { ReferentialProvisionFamilyTypeComponent } from '../referentials/referentiel-provision-familiy-type/referential-provision-family-type.component';
import { ReferentialRecordTypeComponent } from '../referentials/referentiel-record-type/referential-record-type.component';
import { AdministrationComponent } from './administration.component';

const routes: Routes = [
  { path: 'administration', component: AdministrationComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MatTabsModule,
    FormsModule,
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
    MatTableModule,
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
    ReferentialQuotationLabelTypeComponent,
    ReferentialQuotationStatusComponent,
    ReferentialRecordTypeComponent,
    ReferentialTransfertFundsTypeComponent,
    ReferentialBillingClosureRecipientTypeComponent,
    ReferentialBillingClosureTypeComponent,
  ],
})
export class AdministrationModule { }
