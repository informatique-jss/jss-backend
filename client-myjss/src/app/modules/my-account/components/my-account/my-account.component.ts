import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AccountMenuComponent } from '../account-menu/account-menu.component';

@Component({
  selector: '.my-account',
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AccountMenuComponent]
})
export class MyAccountComponent implements OnInit {

  constructor(
  ) { }

  ngOnInit() {
  }
}
