import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BodaccFusion } from '../../quotation/model/BodaccFusion';

@Injectable({
  providedIn: 'root'
})
export class BodaccFusionService extends AppRestService<BodaccFusion>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

}
