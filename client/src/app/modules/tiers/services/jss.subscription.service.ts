import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { JssSubscription } from '../../tiers/model/JssSubscription';

@Injectable({
  providedIn: 'root'
})
export class JssSubscriptionService extends AppRestService<JssSubscription>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

}
