import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-our-clients',
  templateUrl: './our-clients.component.html',
  styleUrls: ['./our-clients.component.css']
})
export class OurClientsComponent implements OnInit {

  engieLogo: string = "assets/images/engie-logo.png";

  @Input()
  scrollDirection: 'normal' | 'reverse' = 'normal';

  constructor() { }

  ngOnInit() {
  }

}
