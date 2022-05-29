import { SearchComponent } from './search/search.component';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { TiersComponent } from '../modules/tiers/components/tiers/tiers.component';

const routes: Routes = [
  { path: '', redirectTo: '/tiers', pathMatch: 'full' }
];

@NgModule({
  declarations: [SearchComponent],
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatTabsModule,
    MatIconModule,
    MatCardModule,
    MatButtonModule
  ],
  exports: [
    RouterModule
  ]
})
export class RoutingModule { }
