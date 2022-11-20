import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OptionJQPA } from 'src/app/modules/quotation/model/guichet-unique/referentials/OptionJQPA';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class OptionJQPAService extends AppRestService<OptionJQPA>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getOptionJQPA() {
    return this.getListCached(new HttpParams(), 'option-jqpa');
  }

}
