import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { SpecialOffersDialogComponent } from 'src/app/modules/miscellaneous/components/special-offers-dialog/special-offers-dialog.component';
import { ResponsableMainComponent } from '../responsable-main/responsable-main.component';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';
import { TiersFollowupComponent } from '../tiers-followup/tiers-followup.component';
import { PrincipalComponent } from '../tiers-main/tiers-main.component';
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
    MatDialogModule,
    MatTableModule,
    MatIconModule,
    MatExpansionModule,
    MatListModule,
    MatSortModule,
    MiscellaneousModule,
    DragDropModule
  ],
  declarations: [TiersComponent,
    PrincipalComponent,
    SpecialOffersDialogComponent,
    SettlementBillingComponent,
    TiersFollowupComponent,
    ResponsableMainComponent,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ]
})
export class TiersModule { }
