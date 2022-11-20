import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { AppService } from '../../services/app.service';
import { User } from './User';

@Injectable({
  providedIn: 'root'
})
export class LoginService extends AppRestService<User>{

  constructor(http: HttpClient, private appService: AppService) {
    super(http, "profile");
  }

  currentUsername: string | undefined;

  private loggedState: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  loggedStateObservable = this.loggedState.asObservable();

  public ADMINISTRATEURS: string = 'ROLE_OSIRIS_ADMINISTRATEURS';

  setLoggedIn(loggedIn: boolean) {
    this.loggedState.next(loggedIn);
  }

  logUser(user: User) {
    this.currentUsername = user.username.toUpperCase();
    return this.loginUser(new HttpParams(), "login", user);
  }

  getCurrentUserName() {
    return this.currentUsername;
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

  public hasGroup(searchRoles: string[]): boolean {
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
            if (role == searchRole || role == this.ADMINISTRATEURS)
              return true;
          }
        }
      }
    }
    return false;
  }
}
