import {
  HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EMPTY, Observable } from 'rxjs';
import { catchError, retry, tap } from 'rxjs/operators';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private snackBar: MatSnackBar) { }

  errorMessage: string = "";
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(request).pipe(
      retry(3),
      tap(data => {
        if (data.type > 0) {
          let successfulMessage = "";
          for (let k of request.context.keys()) {
            successfulMessage = request.context.get(k) + "";
            break;
          }
          if (successfulMessage != undefined && successfulMessage != null && successfulMessage != "") {
            let sb = this.snackBar.open('👍 ' + successfulMessage, 'Fermer', {
              duration: 5 * 1000,
              panelClass: ["blue-snackbar"]
            });
            sb.onAction().subscribe(() => {
              sb.dismiss();
            });
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
          console.error('A client-side error occurred:', error.error.message);
          this.errorMessage = 'A client-side error occurred: ' + error.error.message;
        } else {
          // The backend returned an unsuccessful response code.
          // The response body may contain clues as to what went wrong,
          console.error(`Backend returned code ${error.status}, body was: ${error.error}`);
          this.errorMessage = 'A server-side error occurred: ' + `HTTP ${error.status}, body : ${error.error}`;
        }
        if (this.errorMessage != "") {
          if (errorMessage != undefined && errorMessage != null && errorMessage != "") {
            this.errorMessage = errorMessage;
          } else {
            this.errorMessage = '😢 Dommage, une erreur est apparue : ' + this.errorMessage;
          }
          let sb = this.snackBar.open(this.errorMessage, 'Fermer', {
            duration: 50 * 1000, panelClass: ["red-snackbar"]
          });
          sb.onAction().subscribe(() => {
            sb.dismiss();
          });
        }

        return EMPTY;
      })
    );
  }

}
