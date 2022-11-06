import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Genre } from 'src/app/modules/quotation/model/guichet-unique/referentials/Genre';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class GenreService extends AppRestService<Genre>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getGenre() {
    return this.getList(new HttpParams(), 'genre');
  }

}                        
