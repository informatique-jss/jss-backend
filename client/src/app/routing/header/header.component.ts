import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(protected appService: AppService,
    private location: Location,
    private router: Router,) { }

  title: string = "titre";
  titleSubscription: Subscription = new Subscription;

  sidenavOpenState: boolean = true;
  sidenavOpenStateSubscription: Subscription = new Subscription;

  ngOnInit() {
    this.titleSubscription = this.appService.titleObservable.subscribe(item => this.title = item);
    this.sidenavOpenStateSubscription = this.appService.sidenavOpenStateObservable.subscribe(item => this.sidenavOpenState = item)
  }

  ngOnDestroy() {
    this.titleSubscription.unsubscribe();
    this.sidenavOpenStateSubscription.unsubscribe();
  }

  public onToggleSidenav = () => {
    this.appService.changeSidenavOpenState(!this.sidenavOpenState);
  }

  goBack() {
    this.location.back();
  }
}
