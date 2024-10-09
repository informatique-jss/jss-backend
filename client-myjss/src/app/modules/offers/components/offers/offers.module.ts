import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OffersComponent } from './offers.component';

const routes: Routes = [
  { path: 'offers', component: OffersComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule
  ],
  declarations: [OffersComponent],
})
export class OffersModule { }
