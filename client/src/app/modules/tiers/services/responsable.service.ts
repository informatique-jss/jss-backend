import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Responsable } from '../model/Responsable';

@Injectable({
  providedIn: 'root'
})
export class ResponsableService extends AppRestService<Responsable>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getResponsable(id: number) {
    return this.getById("responsable", id);
  }
}
