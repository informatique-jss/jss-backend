import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, Subscriber } from 'rxjs';
import { AppService } from '../../main/services/app.service';
import { AppRestService } from '../../main/services/appRest.service';
import { PlatformService } from '../../main/services/platform.service';
import { CustomerOrderService } from '../../my-account/services/customer.order.service';
import { QuotationService } from '../../my-account/services/quotation.service';
import { Responsable } from '../model/Responsable';
import { ResponsableService } from './responsable.service';

export const ADMINISTRATEURS: string = 'ROLE_OSIRIS_ADMINISTRATEURS';
export const ACCOUNTING: string = 'ROLE_OSIRIS_COMPTABILITÉ';
export const ACCOUNTING_RESPONSIBLE: string = 'ROLE_OSIRIS_RESPONSABLE_COMPTABILITÉ';

@Injectable({
  providedIn: 'root'
})
export class LoginService extends AppRestService<Responsable> {

  constructor(http: HttpClient, private appService: AppService,
    private quotationService: QuotationService,
    private customerOrderService: CustomerOrderService,
    private plateformService: PlatformService,
    private responsableService: ResponsableService,
  ) {
    super(http, "profile");
  }

  currentUser: Responsable | undefined;
  private currentUserChange = new BehaviorSubject<boolean>(true);
  currentUserChangeMessage = this.currentUserChange.asObservable();

  logUser(userId: number, aToken: string, isFromQuotation: boolean) {
    return new Observable<Boolean>(observer => {
      this.get(new HttpParams().set("userId", userId).set("aToken", aToken), "login").subscribe(response => {
        this.refreshUserRoles().subscribe(response => {
          if (!isFromQuotation) {
            this.quotationService.cleanStorageData();
            this.completeObserver(observer);
          } else {
            this.responsableService.getResponsable(userId).subscribe(responsable => {
              // if the user where in a quotation draft then we save it in the db to retrieve it later in quotation steps
              let quotation = this.quotationService.getCurrentDraftQuotation();
              if (quotation) {
                quotation.responsable = responsable;
                this.quotationService.saveQuotationForAnonymousUser(quotation!, false).subscribe(response => {
                  this.quotationService.setCurrentDraftQuotationId(response.id)
                  this.completeObserver(observer);
                });
              } else { // if no quotation then its a customer order
                let customerOrder = this.customerOrderService.getCurrentDraftOrder();
                customerOrder!.responsable = responsable;
                this.customerOrderService.saveOrderForAnonymousUser(customerOrder!, false).subscribe(response => {
                  this.customerOrderService.setCurrentDraftOrderId(response.id)
                  this.completeObserver(observer);
                });
              }
            });
          }
        })
      })
    })
  }

  private completeObserver(observer: Subscriber<Boolean>) {
    observer.next(true);
    observer.complete();
  }

  switchUser(newUserId: number) {
    return new Observable<Boolean>(observer => {
      this.get(new HttpParams().set("newUserId", newUserId), "switch").subscribe(response => {
        this.currentUser = undefined;
        this.refreshUserRoles().subscribe(response => {
          observer.next(true);
          observer.complete();
        })
      })
    })
  }

  refreshUserRoles() {
    return new Observable<Boolean>(observer => {
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
  }

  sendConnectionLink(mail: string, isFromQuotation: boolean) {
    return this.get(new HttpParams().set("mail", mail).set("isFromQuotation", isFromQuotation), 'login/token/send', "Le lien de connexion vous a été envoyé", "Erreur lors de l'envoi du lien, veuillez vérifier votre saisie");
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
    if (this.plateformService.isServer())
      return of(undefined) as any as Observable<Responsable>;
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
