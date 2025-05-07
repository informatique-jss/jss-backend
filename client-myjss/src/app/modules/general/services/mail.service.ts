import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../../libs/appRest.service";

@Injectable({
  providedIn: 'root'
})
export class MailService extends AppRestService<Boolean> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  subscribeDemo(mail: string, firstName: string, lastName: string, phone: string) {
    let httpParams = new HttpParams().set("mail", mail).set("firstName", firstName).set("lastName", lastName);
    if (phone && phone.length > 0)
      httpParams = httpParams.set("phoneNumber", phone);
    return this.get(httpParams, "subscribe/demo");
  }

  subscribeContactForm(mail: string, firstName: string, lastName: string, message: string) {
    return this.get(new HttpParams().set("mail", mail).set("firstName", firstName).set("lastName", lastName).set("message", message), "subscribe/demo");
  }
}
