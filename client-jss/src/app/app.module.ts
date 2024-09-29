import { registerLocaleData } from '@angular/common';
import { provideHttpClient, withFetch, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, provideRouter } from '@angular/router';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { MainModule } from './main/components/main/main.module';
registerLocaleData(localeFr, 'fr');

@NgModule({ declarations: [
        AppComponent,
    ],
    exports: [],
    bootstrap: [AppComponent], imports: [BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        RouterModule,
        MainModule], providers: [provideRouter(routes),
        provideClientHydration(),
        provideHttpClient(withFetch()),
        { provide: LOCALE_ID, useValue: 'fr' }, provideHttpClient(withInterceptorsFromDi())] })
export class AppModule { }

