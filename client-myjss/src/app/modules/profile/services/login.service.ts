import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppService } from '../../main/services/app.service';
import { AppRestService } from '../../main/services/appRest.service';
import { Responsable } from '../model/Responsable';

export const ADMINISTRATEURS: string = 'ROLE_OSIRIS_ADMINISTRATEURS';
export const ACCOUNTING: string = 'ROLE_OSIRIS_COMPTABILITÉ';
export const ACCOUNTING_RESPONSIBLE: string = 'ROLE_OSIRIS_RESPONSABLE_COMPTABILITÉ';

@Injectable({
  providedIn: 'root'
})
export class LoginService extends AppRestService<Responsable> {

  constructor(http: HttpClient, private appService: AppService) {
    super(http, "profile");
  }

  currentUser: Responsable | undefined;
  private currentUserChange = new BehaviorSubject<boolean>(true);
  currentUserChangeMessage = this.currentUserChange.asObservable();

  logUser(userId: number, aToken: string) {
    return new Observable<Boolean>(observer => {
      this.get(new HttpParams().set("userId", userId).set("aToken", aToken), "login").subscribe(response => {
        this.getUserRoles().subscribe(response => {
          let roles = [];
          for (let role of response as any) {
            roles.push(role["authority"]);
          }
          localStorage.setItem('roles', JSON.stringify(roles));
          this.currentUserChange.next(true);
          observer.next(true);
          observer.complete();
        })
      })
    })
  }

  sendConnectionLink(mail: string) {
    return this.get(new HttpParams().set("mail", mail), 'login/token/send', "Le lien de connexion vous a été envoyé", "Erreur lors de l'envoi du lien, veuillez vérifier votre saisie");
  }

  signOut() {
    this.currentUser = undefined;
    return new Observable<Boolean>(observer => {
      this.get(new HttpParams(), 'login/signout', "Vous avez été déconnecté").subscribe(response => {
        this.currentUserChange.next(false);
        observer.next(true);
        observer.complete();
      })
    })
  }

  getUserRoles() {
    return this.getList(new HttpParams(), "login/roles");
  }

  getCurrentUser(forceFetch: boolean = false, getFromCache: boolean = false): Observable<Responsable> {
    return new Observable<Responsable>(observer => {
      if (!forceFetch && (getFromCache || this.currentUser)) {
        observer.next(this.currentUser!);
        observer.complete();
      } else {
        this.get(new HttpParams(), "user", "", "", true).subscribe(response => {
          this.currentUser = response;
          this.currentUserChange.next(true);
          observer.next(this.currentUser);
          observer.complete();
        })
      }
    });
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
            if (role == searchRole || role == ADMINISTRATEURS)
              return true;
          }
        }
      }
    }
    return false;
  }
}
