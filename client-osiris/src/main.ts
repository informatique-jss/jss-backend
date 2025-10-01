import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { bootstrapApplication } from '@angular/platform-browser';
import * as echarts from 'echarts/core';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { langFR } from './app/libs/echart/langFR';

registerLocaleData(localeFr, 'fr');

echarts.registerLocale("FR", langFR);

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));

