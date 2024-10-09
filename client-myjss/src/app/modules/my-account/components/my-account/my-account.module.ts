import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { AccountMenuComponent } from '../account-menu/account-menu.component';
import { OverviewComponent } from '../overview/overview.component';
import { SignInComponent } from '../sign-in/sign-in.component';
import { SignOutComponent } from '../sign-out/sign-out.component';
import { MyAccountComponent } from './my-account.component';

const routes: Routes = [
  {
    path: 'account', component: MyAccountComponent,
    children: [
      { path: 'overview', component: OverviewComponent }
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
    MiscellaneousModule
  ],
  declarations: [MyAccountComponent,
    AccountMenuComponent,
    OverviewComponent,
    SignInComponent,
    SignOutComponent,
  ]
})
export class MyAccountModule { }
