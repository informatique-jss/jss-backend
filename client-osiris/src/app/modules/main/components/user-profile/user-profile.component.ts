import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgbCollapseModule, NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { LoginService } from '../../../../../../../client-myjss/src/app/modules/profile/services/login.service';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeService } from '../../../profile/services/employee.service';
import { UserDropdownItemType } from '../../model/UserDropDownItemType';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [
    RouterLink,
    SHARED_IMPORTS,
    NgbCollapseModule,
    NgbDropdown,
    NgbDropdownToggle,
    NgbDropdownMenu,
    NgIcon
  ],
  standalone: true
})
export class UserProfileComponent implements OnInit {
  menuItems: UserDropdownItemType[] = [];
  currentUser: Employee | undefined;

  constructor(private employeeService: EmployeeService, private loginService: LoginService) { }

  ngOnInit(): void {
    this.employeeService.getCurrentEmployee().subscribe(res => {
      if (res)
        this.currentUser = res;
    })
  }

}
