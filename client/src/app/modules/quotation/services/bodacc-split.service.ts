import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { BodaccSplit } from '../../quotation/model/BodaccSplit';

@Injectable({
  providedIn: 'root'
})
export class BodaccSplitService extends AppRestService<BodaccSplit>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

}
