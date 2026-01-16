import { Component, Input } from '@angular/core';
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap';
import { NgIconComponent } from "@ng-icons/core";
import { environment } from '../../../../../environments/environment';
import { OSIRIS_TIERS_ROUTE } from '../../../../libs/Constants';
import { callNumber } from '../../../../libs/MailHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { TiersDto } from '../../model/TiersDto';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'tiers-header',
  templateUrl: './tiers-header.component.html',
  imports: [
    ...SHARED_IMPORTS,
    NgIconComponent,
    AvatarComponent,
    NgbDropdown,
    NgbDropdownToggle,
    NgbDropdownMenu,
    NgbDropdownItem,
  ],
  standalone: true,
})
export class TiersHeaderComponent {

  @Input() tiers: TiersDto | undefined;

  OSIRIS_TIERS_ROUTE = OSIRIS_TIERS_ROUTE;

  frontendOsirisUrl = environment.frontendUrl;

  constructor(private tiersService: TiersService) { }


  callPhoneNumber(phone: string) {
    callNumber(phone);
  }

  deleteTiers() {
    //TODO
  }

  printLabel() {
    if (this.tiers && this.tiers.id)
      this.tiersService.printTiersLabel(this.tiers.id, undefined).subscribe();
  }
}

