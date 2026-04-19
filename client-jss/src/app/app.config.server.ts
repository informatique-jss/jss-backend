import { provideServerRendering, withRoutes } from '@angular/ssr';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ApplicationConfig, mergeApplicationConfig } from '@angular/core';
import { appConfig } from './app.config';
import { serverRoutes } from './app.routes.server';

const serverConfig: ApplicationConfig = {
  providers: [provideHttpClient(withInterceptorsFromDi()), provideServerRendering(withRoutes(serverRoutes))]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
