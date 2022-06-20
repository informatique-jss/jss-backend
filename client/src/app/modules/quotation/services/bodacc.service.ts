import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Bodacc } from '../../quotation/model/Bodacc';

@Injectable({
  providedIn: 'root'
})
export class BodaccService extends AppRestService<Bodacc>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

}
