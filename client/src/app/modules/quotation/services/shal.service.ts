import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Shal } from '../../quotation/model/Shal';

@Injectable({
  providedIn: 'root'
})
export class ShalService extends AppRestService<Shal>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

}
