import { DecimalPipe } from '@angular/common';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';
import { ApplicationConfig, LOCALE_ID, provideZoneChangeDetection } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter, withInMemoryScrolling, withViewTransitions } from '@angular/router';
import { provideDaterangepickerLocale } from 'ngx-daterangepicker-bootstrap';
import { routes } from './app.routes';
import { HttpErrorInterceptor } from './modules/main/services/httpErrorInterceptor.service';
export const appConfig: ApplicationConfig = {
  providers: [
    DecimalPipe,
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes,
      withViewTransitions(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'top'
      })),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    },
    { provide: LOCALE_ID, useValue: 'fr' },
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi(), withFetch()),
    provideDaterangepickerLocale({
      format: 'DD/MM/YYYY',
      displayFormat: 'DD/MM/YYYY',
      direction: 'ltr',
      separator: ' au ',
      applyLabel: 'Valider',
      cancelLabel: 'Annuler',
      weekLabel: 'S',
      customRangeLabel: 'Plage personnalisée',
      daysOfWeek: ['Di', 'Lu', 'Ma', 'Me', 'Je', 'Ve', 'Sa'],
      monthNames: [
        'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
        'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'
      ],
      firstDay: 1
    })
  ],
};

