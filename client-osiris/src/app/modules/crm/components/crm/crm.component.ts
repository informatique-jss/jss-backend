import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';

@Component({
  selector: 'crm',
  templateUrl: './crm.component.html',
  styleUrls: ['./crm.component.scss'],
  imports: [SHARED_IMPORTS],
  standalone: true,
})
export class CrmComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
