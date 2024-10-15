import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { AccountMenuComponent } from '../account-menu/account-menu.component';
import { EditAddressComponent } from '../edit-address/edit-address.component';
import { EditAffaireComponent } from '../edit-affaire/edit-affaire.component';
import { OrderDetailsComponent } from '../order-details/order-details.component';
import { OrdersComponent } from '../orders/orders.component';
import { OverviewComponent } from '../overview/overview.component';
import { QuotationsComponent } from '../quotations/quotations.component';
import { ScopeComponent } from '../scope/scope.component';
import { SignInComponent } from '../sign-in/sign-in.component';
import { SignOutComponent } from '../sign-out/sign-out.component';
import { MyAccountComponent } from './my-account.component';

const routes: Routes = [
  {
    path: 'account', component: MyAccountComponent,
    children: [
      { path: 'overview', component: OverviewComponent },
      { path: 'scope', component: ScopeComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'orders/details/:id', component: OrderDetailsComponent },
      { path: 'quotations', component: QuotationsComponent },
      { path: 'affaire/edit/:id/:idOrder', component: EditAffaireComponent },
      { path: 'address/edit/:idOrder', component: EditAddressComponent }
    ]
  },
  { path: 'account/signin', component: SignInComponent },
  { path: 'account/signout', component: SignOutComponent },
  { path: 'profile/login', component: OverviewComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  declarations: [MyAccountComponent,
    AccountMenuComponent,
    OverviewComponent,
    SignInComponent,
    SignOutComponent,
    ScopeComponent,
    OrdersComponent,
    QuotationsComponent,
    OrderDetailsComponent,
    EditAffaireComponent,
    EditAddressComponent,
  ]
})
export class MyAccountModule { }
