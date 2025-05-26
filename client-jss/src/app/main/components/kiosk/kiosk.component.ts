import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';

@Component({
  selector: 'kiosk',
  templateUrl: './kiosk.component.html',
  styleUrls: ['./kiosk.component.css'],
  imports: [SHARED_IMPORTS],
  standalone: true
})
export class KioskComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
