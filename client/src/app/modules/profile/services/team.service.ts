import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Team } from '../../profile/model/Team';

@Injectable({
  providedIn: 'root'
})
export class TeamService extends AppRestService<Team>{

  constructor(http: HttpClient) {
    super(http, "profile");
  }

  getTeams() {
    return this.getList(new HttpParams(), "teams");
  }
  
   addOrUpdateTeam(team: Team) {
    return this.addOrUpdate(new HttpParams(), "team", team, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
