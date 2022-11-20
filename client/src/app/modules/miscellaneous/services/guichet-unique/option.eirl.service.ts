import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OptionEirl } from 'src/app/modules/quotation/model/guichet-unique/referentials/OptionEirl';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class OptionEirlService extends AppRestService<OptionEirl>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getOptionEirl() {
    return this.getListCached(new HttpParams(), 'option-eirl');
  }

}
