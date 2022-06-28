import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Team } from 'src/app/modules/profile/model/Team';
import { TeamService } from 'src/app/modules/profile/services/team.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-profile',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTeamComponent extends GenericReferentialComponent<Team> implements OnInit {
  constructor(private teamService: TeamService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<Team> {
    return this.teamService.addOrUpdateTeam(this.selectedEntity!);
  }
  getGetObservable(): Observable<Team[]> {
    return this.teamService.getTeams();
  }
}
