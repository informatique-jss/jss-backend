import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { HttpErrorInterceptor } from '../../httpErrorInterceptor.service';
import { AppComponent } from './app.component';
import { ToastComponent } from './libs/toast/toast.component';
import { MiscellaneousModule } from './modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { MyAccountModule } from './modules/my-account/components/my-account/my-account.module';
import { OffersModule } from './modules/offers/components/offers/offers.module';
import { FooterComponent } from './modules/profile/components/footer/footer.component';
import { LoginComponent } from './modules/profile/components/login/login.component';
import { TopBarComponent } from './modules/profile/components/top-bar/top-bar.component';
registerLocaleData(localeFr, 'fr');

const routes: Routes = [
  { path: '', redirectTo: 'offers', pathMatch: 'full' },
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    TopBarComponent,
    FooterComponent,
    ToastComponent,
  ],
  exports: [],
  bootstrap: [AppComponent],
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MyAccountModule,
    OffersModule,
    MiscellaneousModule
  ], providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: HttpErrorInterceptor,
    multi: true
  },
  { provide: LOCALE_ID, useValue: 'fr' },
  provideHttpClient(withInterceptorsFromDi(), withFetch())
  ]
})
export class AppModule { }


