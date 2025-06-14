import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../services/appRest.service";

@Injectable({
  providedIn: 'root'
})
export class ContributeService extends AppRestService<Boolean> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  contributeContactForm(mail: string, firstName: string, lastName: string, phone: string, message: string) {
    let httpParams = new HttpParams().set("mail", mail).set("firstName", firstName).set("lastName", lastName).set("message", message);
    if (phone && phone.length > 0)
      httpParams = httpParams.set("phoneNumber", phone);
    return this.get(httpParams, "subscribe/contact");
  }
}
