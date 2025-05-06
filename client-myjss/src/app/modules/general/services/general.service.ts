import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../../libs/appRest.service";

@Injectable({
  providedIn: 'root'
})
export class GeneralService extends AppRestService<Boolean> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  receiveDemoByMail(mail: string) {
    return this.getList(new HttpParams().set("mail", mail), "demo/send");
  }

}
