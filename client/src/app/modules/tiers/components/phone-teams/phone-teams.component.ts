import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { PhoneService } from 'src/app/modules/miscellaneous/services/tiers.phone.service';
import { ActivatedRoute } from '@angular/router';
import { PhoneTeams } from '../../model/PhoneTeams';

@Component({
  selector: 'app-number-form',
  templateUrl: './phone-teams.component.html',
  styleUrls: ['./phone-teams.component.css']
})
export class PhoneTeamsComponent {
  number: string = '';
  phoneTeams: PhoneTeams[] | undefined;
  constructor(private http: HttpClient, private phoneService: PhoneService, private route: ActivatedRoute) {}

  onSubmit() {
    const phoneNumber = this.route.snapshot.paramMap.get('phoneNumber');
    if (phoneNumber) {
      this.phoneService.getPhones(phoneNumber).subscribe(result => {
        //phoneTeams.ENTITY_TYPE = result.ENTITY_TYPE;
        /*this.phoneTeams = result.map(item => {
          const phoneTeam: PhoneTeams = {
            entity_type: item?.ENTITY_TYPE || null,
            entity_id: item?.ENTITY_ID || null
          };
          return phoneTeam;
        });*/
      });
    }
  }
}
