import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserPreference } from '../model/UserPreference';
import { AppRestService } from './appRest.service';

@Injectable({
  providedIn: 'root'
})
export class RestUserPreferenceService extends AppRestService<UserPreference> {

  constructor(http: HttpClient) {
    super(http, "profile");
  }

  getUserPreferenceValue(code: string): Observable<string> {
    // see UserPreferenceService to see how to parse Json values
    return this.get(new HttpParams().set("code", code), "user-preference/code", "", "Erreur lors de la récupération des préférences utilisateur") as any as Observable<string>;
  }

  setUserPreference(json: any, code: string) {
    return this.postItem(new HttpParams().set("code", code), "user-preference/set", JSON.stringify(json), "", "Erreur lors de l'enregistrement des préférences utilisateur");
  }

  deleteUserPreferences() {
    return this.get(new HttpParams(), "user-preferences/delete", "", "Erreur lors de l'effacement des préférences utilisateur");
  }

  deleteUserPreference(codeToDelete: string) {
    return this.get(new HttpParams().set("codeToDelete", codeToDelete), "user-preference/delete", "", "Erreur lors de l'effacement de la préférence utilisateur");
  }
}



