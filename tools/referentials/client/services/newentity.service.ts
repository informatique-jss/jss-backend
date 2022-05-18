import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { NewEntity } from '../../targetPackage/model/NewEntity';

@Injectable({
  providedIn: 'root'
})
export class NewEntityService extends AppRestService<NewEntity>{

  constructor(http: HttpClient) {
    super(http, "controllerName");
  }

  getNewEntities() {
    return this.getList(new HttpParams(), "entryPointName");
  }

}
