import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { CopyClipboardDirective } from '../../../../libs/CopyClipboard.directive';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { AccountMenuComponent } from '../account-menu/account-menu.component';
import { AffairesComponent } from '../affaires/affaires.component';
import { BillingClosureComponent } from '../billing-closure/billing-closure.component';
import { EditAddressComponent } from '../edit-address/edit-address.component';
import { EditAffaireComponent } from '../edit-affaire/edit-affaire.component';
import { OrderDetailsComponent } from '../order-details/order-details.component';
import { OrdersComponent } from '../orders/orders.component';
import { OverviewComponent } from '../overview/overview.component';
import { PayOrderComponent } from '../pay-order/pay-order.component';
import { PayQuotationComponent } from '../pay-quotation/pay-quotation.component';
import { QuotationDetailsComponent } from '../quotation-details/quotation-details.component';
import { QuotationsComponent } from '../quotations/quotations.component';
import { ScopeComponent } from '../scope/scope.component';
import { SignInComponent } from '../sign-in/sign-in.component';
import { SignOutComponent } from '../sign-out/sign-out.component';
import { UserSettingsComponent } from '../user-settings/user-settings.component';
import { MyAccountComponent } from './my-account.component';

const routes: Routes = [
  {
    path: 'account', component: MyAccountComponent,
    children: [
      { path: 'overview', component: OverviewComponent },
      { path: 'scope', component: ScopeComponent },
      { path: 'closure', component: BillingClosureComponent },
      { path: 'quotations', component: QuotationsComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'orders/details/:id', component: OrderDetailsComponent },
      { path: 'order/address/edit/:idOrder', component: EditAddressComponent },
      { path: 'order/pay/:idOrder', component: PayOrderComponent },
      { path: 'quotations/details/:id', component: QuotationDetailsComponent },
      { path: 'quotation/address/edit/:idQuotation', component: EditAddressComponent },
      { path: 'quotation/pay/:idQuotation', component: PayQuotationComponent },
      { path: 'affaires', component: AffairesComponent },
      { path: 'affaires/:idAffaire', component: AffairesComponent },
      { path: 'affaire/edit/:id/:idOrder', component: EditAffaireComponent },
      { path: 'settings', component: UserSettingsComponent },
      { path: 'settings/:idResponsable', component: UserSettingsComponent },
      { path: 'settings/address/edit/:idResponsable', component: EditAddressComponent },
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
    PayOrderComponent,
    CopyClipboardDirective,
    AffairesComponent,
    QuotationDetailsComponent,
    PayQuotationComponent,
    BillingClosureComponent,
    UserSettingsComponent,
  ]
})
export class MyAccountModule { }