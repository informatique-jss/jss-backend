import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Page } from '../model/Page';

@Injectable({
  providedIn: 'root'
})
export class PageService extends AppRestService<Page>{

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAllPages() {
    return this.getListCached(new HttpParams(), "pages");
  }

}
