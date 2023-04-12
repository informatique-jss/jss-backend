import { Component } from '@angular/core';

import { ActivatedRoute } from '@angular/router';
import { PhoneService } from 'src/app/modules/miscellaneous/services/tiers.phone.service';
import { PhoneTeams as PhoneSearch } from '../../model/PhoneSearch';

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
    private route: ActivatedRoute) { }

  onSubmit() {
    const phoneNumber = this.route.snapshot.paramMap.get('phoneNumber');
    if (phoneNumber) {
      this.phoneService.getPhones(phoneNumber).subscribe(result => {
        if (result)
          for (let phone of result)
            console.log(phone);
      });
    }
  }
}
