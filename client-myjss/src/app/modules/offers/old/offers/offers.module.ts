import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductComponent } from '../product/product.component';
import { OffersComponent } from './offers.component';

const routes: Routes = [
  { path: 'offers', component: OffersComponent },
  { path: 'product', component: ProductComponent },

];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule
  ],
  declarations: [OffersComponent,
    ProductComponent,
  ],
})
export class OffersModule { }
