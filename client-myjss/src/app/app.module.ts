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
import { CompanyModule } from './modules/company/components/company/company.module';
import { GeneralModule } from './modules/general/components/general/general.module';
import { MiscellaneousModule } from './modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { MyAccountModule } from './modules/my-account/components/my-account/my-account.module';
import { MyJssServicesModule } from './modules/my-services/myjss-services/myjss-services.module';
import { FooterComponent } from './modules/profile/components/footer/footer.component';
import { SearchComponent } from './modules/profile/components/search/search.component';
import { TopBarComponent } from './modules/profile/components/top-bar/top-bar.component';
import { QuotationModule } from './modules/quotation/components/quotation/quotation.module';
import { ToolsModule } from './modules/tools/components/tools/tools.module';

registerLocaleData(localeFr, 'fr');


const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
];

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    TopBarComponent,
    ToastComponent,
    SearchComponent,

  ],
  exports: [],
  bootstrap: [AppComponent],
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', scrollPositionRestoration: 'enabled' }),
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MyAccountModule,
    MiscellaneousModule,
    QuotationModule,
    MyJssServicesModule,
    GeneralModule,
    CompanyModule,
    ToolsModule
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
