import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';

@Component({
  selector: 'appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class AppointmentComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
