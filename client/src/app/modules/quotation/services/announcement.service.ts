import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Announcement } from '../model/Announcement';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService extends AppRestService<Announcement>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

}
