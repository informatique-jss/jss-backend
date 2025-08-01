import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppService } from '../../main/services/app.service';
import { AppRestService } from '../../main/services/appRest.service';
import { User } from '../model/User';

export const ADMINISTRATEURS: string = 'ROLE_OSIRIS_ADMINISTRATEURS';
export const ACCOUNTING: string = 'ROLE_OSIRIS_COMPTABILITÉ';
export const ACCOUNTING_RESPONSIBLE: string = 'ROLE_OSIRIS_RESPONSABLE_COMPTABILITÉ';

@Injectable({
  providedIn: 'root'
})
export class LoginService extends AppRestService<User> {

  private loggedState: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  loggedStateObservable = this.loggedState.asObservable();

  setLoggedIn(loggedIn: boolean) {
    this.loggedState.next(loggedIn);
  }

  constructor(http: HttpClient, private appService: AppService) {
    super(http, "profile");
  }

  currentUsername: string | undefined;

  logUser(user: User) {
    this.currentUsername = user.username.toUpperCase();
    return this.loginUser(new HttpParams(), "login", user);
  }

  setUserRoleAndRefresh() {
    this.getUserRoles().subscribe(response => {
      let roles = [];
      for (let role of response as any) {
        roles.push(role["authority"]);
      }
      localStorage.setItem('roles', JSON.stringify(roles));
      window.location.reload();
      this.appService.openRoute(null, '/', null);
    })
  }

  getUserRoles() {
    return this.getList(new HttpParams(), "login/roles");
  }

  public hasGroup(searchRoles: string[], includeAdmin: boolean = true): boolean {
    let roleJson = localStorage.getItem('roles');
    let roles = null;
    if (roleJson != null) {
      try {
        roles = JSON.parse(roleJson);
      } catch {
        return false;
      }
      if (roles) {
        for (let role of roles) {
          for (let searchRole of searchRoles) {
            if (role == searchRole || includeAdmin && role == ADMINISTRATEURS)
              return true;
          }
        }
      }
    }
    return false;
  }
}
