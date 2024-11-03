import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { AddOrderComponent } from '../add-order/add-order.component';
import { CheckoutComponent } from '../checkout/checkout.component';
import { ChooseServiceComponent } from '../choose-service/choose-service.component';
import { ChooseTypeComponent } from '../choose-type/choose-type.component';
import { QuotationComponent } from './quotation.component';

const routes: Routes = [
  { path: 'order/new', component: AddOrderComponent },
  { path: 'order/new/subscribe', component: AddOrderComponent },
  { path: 'order/new/formality', component: AddOrderComponent },
  { path: 'order/new/announcement', component: AddOrderComponent },
  { path: 'order/new/subscribe', component: AddOrderComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule,
    FormsModule,
    ReactiveFormsModule,
    CKEditorModule
  ],
  declarations: [QuotationComponent,
    AddOrderComponent,
    ChooseTypeComponent,
    ChooseServiceComponent,
    CheckoutComponent,
  ]
})
export class QuotationModule { }

