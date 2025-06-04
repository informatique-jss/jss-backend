import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Subscription } from '../model/Subscription';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService extends AppRestService<Subscription> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  givePost(postId: number, recipientMailString: string) {
    return this.get(new HttpParams().set("postId", postId).set("recipientMailString", recipientMailString), "subscription/give-post", "", "");
  }
}
