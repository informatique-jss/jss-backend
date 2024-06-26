import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PaperSetResult } from '../model/PaperSetResult';

@Injectable({
  providedIn: 'root'
})
export class PaperSetResultService extends AppRestService<PaperSetResult> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  searchPaperSets() {
    return this.getList(new HttpParams(), "paper-sets/search");
  }

}
