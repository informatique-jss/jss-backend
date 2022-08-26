import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { JssSubscription } from '../../tiers/model/JssSubscription';

@Injectable({
  providedIn: 'root'
})
export class JssSubscriptionService extends AppRestService<JssSubscription>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

}
