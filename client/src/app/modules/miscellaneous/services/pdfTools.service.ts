import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PdfToolsService extends AppRestService<any> {

  constructor(private http: HttpClient) {
    super(http, "pdf-tools");
  }

  sendPdf(pdf: any): Observable<any> {
    return this.postItem(new HttpParams(), pdf);
  }
}

