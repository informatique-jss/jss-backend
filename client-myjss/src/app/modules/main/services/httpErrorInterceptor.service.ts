import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { AppService } from './app.service';
import { PlatformService } from './platform.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private appService: AppService,
    private platformService: PlatformService
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    request = request.clone({
      withCredentials: true,
      headers: request.headers.set("domain", "myjss" + (environment.production ? '_PROD' : '_REC')).set("gaClientId", this.getGaClientId())
    });


    return next.handle(request).pipe(
      tap(data => {
        if (data.type > 0) {
          let successfulMessage = "";
          for (let k of request.context.keys()) {
            successfulMessage = request.context.get(k) + "";
            break;
          }
          if (successfulMessage != undefined && successfulMessage != null && successfulMessage != "") {
            this.appService.displayToast('👍 ' + successfulMessage, false, "Succès", 5000);
          }
        }
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = "";
        let doNotRedirectOnNonAuthenticated = "";
        let i = 0;
        for (let k of request.context.keys()) {
          if (i == 1)
            errorMessage = request.context.get(k) + "";
          if (i == 2)
            doNotRedirectOnNonAuthenticated = request.context.get(k) + "";

          i++;
        }
        if (error.error instanceof Error) {
          // A client-side or network error occurred. Handle it accordingly.
          errorMessage = 'Erreur côté navigateur, vous êtes peut-être déconnecté : ' + error.message;
        } else {
          // The backend returned an unsuccessful response code.
          // The response body may contain clues as to what went wrong,

          // If HTTP 403, user not logged in
          if (error.status == 403) {
            if (doNotRedirectOnNonAuthenticated != "true" && this.platformService.isBrowser() && window.location.href.indexOf('/account') >= 0 && window.location.href.indexOf('/account/signin') < 0)
              this.appService.openRoute(undefined, "/account/signin", undefined);
            return EMPTY;
          } else if (error.status == 0) {
            // Server unavailable or user not connected to network => ignore error
          } else if (error.status == 400) {
            if (error.headers.get("incorrectField"))
              errorMessage = 'La valeur du champ ' + error.headers.get("incorrectField") + ' est incorrecte';
            else if (error.headers.get("errorMessageToDisplay"))
              errorMessage = "" + error.headers.get("errorMessageToDisplay");
            else if (error.headers.get("duplicateIds"))
              errorMessage = "Eléments déjà trouvés avec les identifiants suivants : " + error.headers.get("duplicateIds");
          } else if (error.status == 500) {
            errorMessage = 'Erreur côté serveur. \nMerci de contacter l\'administrateur ou bien de déclarer un bug à l\'aide du bouton en haut à gauche et de founir l\'indication technique suivante : ' + error.headers.get("error");
          } else {
            errorMessage = 'Erreur côté serveur : ' + `HTTP ${error.status}, body : ${error.message}`;
          }
        }
        if (errorMessage != "") {
          if (errorMessage != undefined && errorMessage != null && errorMessage != "") {
            errorMessage = errorMessage;
          } else {
            errorMessage = '😢 Dommage, une erreur est apparue : ' + errorMessage;
          }
          this.appService.displayToast(errorMessage, true, "Erreur !", 5000);
        }

        return EMPTY;
      })
    );
  }

  // Even if consent is not given by the user, a cookie (anonyme) is created and will be found
  getGaClientId(): string {
    if (this.platformService.getNativeDocument() != undefined) {
      const match = this.platformService.getNativeDocument()!.cookie.match('(?:^|;)\\s*_ga=([^;]*)');
      // The format is often GA1.x.UID. We want to retrieve the UID part (from the 3rd segment)
      // Or more simply, we take everything after "GA1.x."
      if (match && match[1]) {
        const parts = match[1].split('.');
        if (parts.length >= 3) {
          return parts.slice(2).join('.'); // returns something like "123456789.987654321"
        }
      }
    }
    return ""; //  Fallback or error handling
  }
}