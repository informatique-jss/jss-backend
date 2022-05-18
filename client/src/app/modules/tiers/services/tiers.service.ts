import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Tiers } from '../model/Tiers';
import { TiersType } from '../model/TiersType';

@Injectable({
  providedIn: 'root'
})
export class TiersService extends AppRestService<Tiers>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiers(id: number) {
    return this.getById("tiers", id);
  }

}
