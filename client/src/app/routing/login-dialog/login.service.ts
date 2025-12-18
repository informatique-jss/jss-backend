import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { AppService } from '../../services/app.service';
import { User } from './User';

export const ADMINISTRATEURS: string = 'ROLE_OSIRIS_ADMINISTRATEURS';
export const BETA_TESTEURS: string = 'ROLE_OSIRIS_BETA_TESTEURS';
export const ACCOUNTING: string = 'ROLE_OSIRIS_COMPTABILITÉ';
export const ACCOUNTING_RESPONSIBLE: string = 'ROLE_OSIRIS_RESPONSABLE_COMPTABILITÉ';
export const TEAM_RESPONSIBLE: string = 'ROLE_OSIRIS_RESPONSABLES_EQUIPE';

@Injectable({
  providedIn: 'root'
})
export class LoginService extends AppRestService<User> {

  constructor(http: HttpClient, private appService: AppService) {
    super(http, "profile");
  }

  currentUsername: string | undefined;

  private loggedState: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  loggedStateObservable = this.loggedState.asObservable();

  setLoggedIn(loggedIn: boolean) {
    this.loggedState.next(loggedIn);
  }

  logUser(user: User) {
    this.currentUsername = user.username.toUpperCase();
    return this.loginUser(new HttpParams(), "login", user);
  }

  setUserRoleAndRefresh() {
    /* this.getUserRoles().subscribe(response => {
       let roles = [];
       let reload = !localStorage.getItem('roles');
       for (let role of response as any) {
         roles.push(role["authority"]);
       }
       localStorage.setItem('roles', JSON.stringify(roles));
       if (reload) {
         window.location.reload();
         this.appService.openRoute(null, '/', null);
       }
     })*/
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
