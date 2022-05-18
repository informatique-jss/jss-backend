import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TiersComponent } from '../modules/tiers/components/tiers/tiers.component';

const routes: Routes = [
  { path: 'tiers', component: TiersComponent },
  { path: '', redirectTo: '/tiers', pathMatch: 'full' }
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class RoutingModule { }
