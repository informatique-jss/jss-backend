import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TutelleCuratelle } from 'src/app/modules/quotation/model/guichet-unique/referentials/TutelleCuratelle';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TutelleCuratelleService extends AppRestService<TutelleCuratelle>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTutelleCuratelle() {
    return this.getListCached(new HttpParams(), 'tutelle-curatelle');
  }

}
