import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { AppService } from './src/app/libs/app.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private appService: AppService,
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    request = request.clone({
      withCredentials: true
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
            if (doNotRedirectOnNonAuthenticated != "true")
              this.appService.openRoute(undefined, "/", undefined);
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

}