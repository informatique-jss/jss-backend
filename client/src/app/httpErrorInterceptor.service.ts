import {
  HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { LoginService } from './routing/login-dialog/login.service';
import { AppService } from './services/app.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private appService: AppService,
    private loginService: LoginService,
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
            this.appService.displaySnackBar('👍 ' + successfulMessage, false, 5);
          }
        }
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = "";
        for (let k of request.context.keys()) {
          errorMessage = request.context.get(k) + "";
        }
        if (error.error instanceof Error) {
          // A client-side or network error occurred. Handle it accordingly.
          console.error('A client-side error occurred:', error.message);
          errorMessage = 'Erreur côté navigateur, vous êtes peut-être déconnecté : ' + error.message;
        } else {
          // The backend returned an unsuccessful response code.
          // The response body may contain clues as to what went wrong,

          // If HTTP 403, user not logged in
          if (error.status == 403) {
            this.loginService.setLoggedIn(false);
            return EMPTY;
          } else {
            console.error(`Backend returned code ${error.status}, body was: ${error.message}`);
            errorMessage = 'Erreur côté serveur : ' + `HTTP ${error.status}, body : ${error.message}`;
          }
        }
        if (errorMessage != "") {
          if (errorMessage != undefined && errorMessage != null && errorMessage != "") {
            errorMessage = errorMessage;
          } else {
            errorMessage = '😢 Dommage, une erreur est apparue : ' + errorMessage;
          }
          this.appService.displaySnackBar(errorMessage, false, 50);
        }

        return EMPTY;
      })
    );
  }

}
