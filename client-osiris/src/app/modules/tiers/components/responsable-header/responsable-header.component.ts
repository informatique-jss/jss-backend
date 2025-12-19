import { Component, Input } from '@angular/core';
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap';
import { NgIconComponent } from "@ng-icons/core";
import { callNumber } from '../../../../libs/MailHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { ResponsableDto } from '../../model/ResponsableDto';
import { ResponsableService } from '../../services/responsable.service';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'responsable-header',
  templateUrl: './responsable-header.component.html',
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
export class ResponsableHeaderComponent {

  @Input() responsable: ResponsableDto | undefined;

  constructor(
    private responsableService: ResponsableService,
    private tiersService: TiersService,
  ) { }


  callPhoneNumber(phone: string) {
    callNumber(phone);
  }

  deleteResponsable() {
    //TODO
  }

  printLabel() {
    if (this.responsable && this.responsable.tiersId)
      this.tiersService.printTiersLabel(this.responsable.tiersId, this.responsable.id).subscribe();
  }
}

