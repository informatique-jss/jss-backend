import { Component } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { PhoneService } from 'src/app/modules/miscellaneous/services/tiers.phone.service';
import { PhoneTeams as PhoneSearch } from '../../model/PhoneSearch';
import { AppService } from 'src/app/services/app.service';
import { RESPONSABLE_ENTITY_TYPE, TIERS_ENTITY_TYPE, PROVIDER_ENTITY_TYPE } from 'src/app/routing/search/search.component';

@Component({
  selector: 'app-number-form',
  templateUrl: './search-phone.component.html',
  styleUrls: ['./search-phone.component.css']
})
export class SearchPhoneComponent {
  number: string = '';
  phoneSearchs: PhoneSearch[] | undefined;
  constructor(
    private phoneService: PhoneService,
    private appService: AppService,
    private route: ActivatedRoute,
    private router: Router) { }


  onSubmit() {
    const phoneNumber = this.route.snapshot.paramMap.get('phoneNumber');
    if (phoneNumber) {
      this.phoneService.getPhones(phoneNumber).subscribe(result => {
        if (result){
          this.phoneSearchs = result;
        }
      });
    }
  }

  openRoute(event: any, link: string) {
    if (link.includes(RESPONSABLE_ENTITY_TYPE.entityType.toLowerCase())) {
      link = TIERS_ENTITY_TYPE.entityType.toLowerCase() + link;
    }else if(link.includes(PROVIDER_ENTITY_TYPE.entityType.toLowerCase())){
      const firstSlashIndex = PROVIDER_ENTITY_TYPE.entryPoint.indexOf('/');
      link = PROVIDER_ENTITY_TYPE.entryPoint.substring(0, firstSlashIndex) + link;

 }
    this.appService.openRoute(event, link, null);
  }
}
