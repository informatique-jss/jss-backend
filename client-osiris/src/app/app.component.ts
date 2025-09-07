import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { provideIcons } from '@ng-icons/core';
import * as tablerIcons from '@ng-icons/tabler-icons';
import * as tablerIconsFill from '@ng-icons/tabler-icons/fill';
import { Subscription } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { SHARED_IMPORTS } from './libs/SharedImports';
import { ConstantService } from './modules/main/services/constant.service';
import { Responsable } from './modules/profile/model/Responsable';
import { LoginService } from './modules/profile/services/login.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    SHARED_IMPORTS
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  viewProviders: [provideIcons({ ...tablerIcons, ...tablerIconsFill })],
  standalone: true
})
export class AppComponent implements OnInit {

  currentUser: Responsable | undefined;
  loggedStateSubscription: Subscription = new Subscription;
  loggedIn = true;

  constructor(private titleService: Title, private meta: Meta,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private constantService: ConstantService,
    private loginService: LoginService
  ) {
  }

  ngOnInit(): void {
    this.constantService.initConstant();

    this.loggedStateSubscription = this.loginService.loggedStateObservable.subscribe(item => {
      this.loggedIn = item
      if (!this.loggedIn) {
        // TODO : redirect to login page
      }

      if (environment.production == false && this.loginService.hasGroup(['toto']) == false)
        this.loginService.setUserRoleAndRefresh();
    });

    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        map(() => {
          let route = this.activatedRoute;
          while (route.firstChild) {
            route = route.firstChild;
          }
          return route;
        }),
        mergeMap(route => route.data)
      )
      .subscribe(data => {
        if (data['title']) {
          this.titleService.setTitle(data['title'] +
            ' | INSPINIA - Angular Responsive Bootstrap 5 Admin Dashboard');
        }
      });
  }

}
