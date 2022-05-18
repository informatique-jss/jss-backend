import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private title: BehaviorSubject<string> = new BehaviorSubject<string>("title");
  titleObservable = this.title.asObservable();

  private sidenavOpenState: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  sidenavOpenStateObservable = this.sidenavOpenState.asObservable();


  constructor() { }

  changeHeaderTitle(title: string) {
    this.title.next(title);
  }

  changeSidenavOpenState(sidenavOpenState: boolean) {
    this.sidenavOpenState.next(sidenavOpenState);
  }

}
