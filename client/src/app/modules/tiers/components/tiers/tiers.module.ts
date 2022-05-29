import { TiersAttachmentsComponent } from './../tiers-attachments/tiers-attachments.component';
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
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterModule, Routes } from '@angular/router';
import { PrincipalComponent } from '../tiers-main/tiers-main.component';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';
import { DocumentManagementComponent } from './../document-management/document-management.component';
import { SpecialOffersDialogComponent } from './../special-offers-dialog/special-offers-dialog.component';
import { TiersComponent } from './tiers.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { UploadTiersAttachementDialogComponent } from '../upload-tiers-attachement-dialog/upload-tiers-attachement-dialog.component';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSortModule } from '@angular/material/sort';
import { HistoryComponent } from 'src/app/modules/miscellaneous/components/history/history.component';
import { TiersFollowupComponent } from '../tiers-followup/tiers-followup.component';
import { AddressingComponent } from '../addressing/addressing.component';
import { ResponsableMainComponent } from '../responsable-main/responsable-main.component';

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
    MatSortModule
  ],
  declarations: [TiersComponent,
    PrincipalComponent,
    SpecialOffersDialogComponent,
    DocumentManagementComponent,
    SettlementBillingComponent,
    TiersAttachmentsComponent,
    UploadTiersAttachementDialogComponent,
    HistoryComponent,
    TiersFollowupComponent,
    AddressingComponent,
    ResponsableMainComponent
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ]
})
export class TiersModule { }
