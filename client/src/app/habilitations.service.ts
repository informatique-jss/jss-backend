import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HabilitationsService {

  constructor(http: HttpClient
  ) {
  }

  canViewTiersModule() {
    return true;
  }

}



