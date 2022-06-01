import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { SpecialOffersDialogComponent } from 'src/app/modules/miscellaneous/components/special-offers-dialog/special-offers-dialog.component';
import { ResponsableMainComponent } from '../responsable-main/responsable-main.component';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';
import { TiersFollowupComponent } from '../tiers-followup/tiers-followup.component';
import { PrincipalComponent } from '../tiers-main/tiers-main.component';
import { DocumentManagementComponent } from './../document-management/document-management.component';
import { TiersComponent } from './tiers.component';

const routes: Routes = [
  { path: 'tiers', component: TiersComponent },
  { path: 'tiers/:id', component: TiersComponent },
  { path: 'tiers/responsable/:id', component: TiersComponent }
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
    MatDialogModule,
    MatTableModule,
    MatIconModule,
    MatChipsModule,
    MatExpansionModule,
    MatCheckboxModule,
    MatTooltipModule,
    MatListModule,
    MatProgressBarModule,
    MatSortModule,
    MiscellaneousModule
  ],
  declarations: [TiersComponent,
    PrincipalComponent,
    SpecialOffersDialogComponent,
    DocumentManagementComponent,
    SettlementBillingComponent,
    TiersFollowupComponent,
    ResponsableMainComponent,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ]
})
export class TiersModule { }
