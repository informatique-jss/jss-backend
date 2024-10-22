import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { AddOrderComponent } from '../add-order/add-order.component';
import { CheckoutComponent } from '../checkout/checkout.component';
import { ChooseServiceComponent } from '../choose-service/choose-service.component';
import { ChooseTypeComponent } from '../choose-type/choose-type.component';
import { QuotationComponent } from './quotation.component';

const routes: Routes = [
  { path: 'order/new', component: AddOrderComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule
  ],
  declarations: [QuotationComponent,
    AddOrderComponent,
    ChooseTypeComponent,
    ChooseServiceComponent,
    CheckoutComponent,
  ]
})
export class QuotationModule { }

