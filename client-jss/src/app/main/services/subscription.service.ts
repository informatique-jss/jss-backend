import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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

  // if response == null --> the currentUser is either not connected or have no rights to share a post
  getNumberOfRemainingPostsToShareForCurrentMonth() {
    return this.get(new HttpParams(), "subscription/share-post-left", "", "") as any as Observable<number>;
  }
}
