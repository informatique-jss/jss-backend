import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Rna } from '../model/Rna';

@Injectable({
  providedIn: 'root'
})
export class RnaService extends AppRestService<Rna>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getRna(rna: string) {
    return this.getList(new HttpParams().set("rna", rna), "rna");
  }
}
