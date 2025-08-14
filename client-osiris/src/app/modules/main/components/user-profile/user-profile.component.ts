import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgbCollapseModule, NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { UserDropdownItemType } from '../../model/UserDropDownItemType';

@Component({
  selector: 'app-user-profile',
  imports: [
    RouterLink,
    SHARED_IMPORTS,
    NgbCollapseModule,
    NgbDropdown,
    NgbDropdownToggle,
    NgbDropdownMenu,
    NgIcon
  ],
  templateUrl: './user-profile.component.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  standalone: true
})
export class UserProfileComponent {
  menuItems: UserDropdownItemType[] = [];
}
