import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes, provideRouter } from '@angular/router';
import { HttpErrorInterceptor } from '../httpErrorInterceptor.service';
import { AppComponent } from './app.component';
import { ToastComponent } from './libs/toast/toast.component';
import { FooterComponent } from './main/components/footer/footer.component';
import { MainModule } from './main/components/main/main.module';
import { NewsletterComponent } from './main/components/newsletter/newsletter.component';
registerLocaleData(localeFr, 'fr');

const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
];

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    ToastComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    RouterModule,
    MainModule,
    NewsletterComponent
  ],
  exports: [
  ],
  providers: [provideRouter(routes),
  provideClientHydration(),
  {
    provide: HTTP_INTERCEPTORS,
    useClass: HttpErrorInterceptor,
    multi: true
  },
  provideHttpClient(withFetch()),
  { provide: LOCALE_ID, useValue: 'fr' },
  provideHttpClient(withInterceptorsFromDi())],
  bootstrap: [AppComponent]
})
export class AppModule { }

