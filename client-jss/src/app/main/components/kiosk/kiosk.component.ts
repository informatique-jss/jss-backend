import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { NewsletterComponent } from "../newsletter/newsletter.component";

@Component({
  selector: 'kiosk',
  templateUrl: './kiosk.component.html',
  styleUrls: ['./kiosk.component.css'],
  imports: [SHARED_IMPORTS, NewsletterComponent],
  standalone: true
})
export class KioskComponent implements OnInit {

  yearOpened: number = 2023;
  jssEditionNumberHovered: number = 0;

  constructor() { }

  ngOnInit() {
  }

  openTab(event: any, year: number) {
    event.preventDefault();
    this.yearOpened = year;
  }

  openReadExtractButton(jssEditionNumber: number) {
    this.jssEditionNumberHovered = jssEditionNumber;
  }

  closeReadExtractButton() {
    this.jssEditionNumberHovered = 0;
  }

  openExtract(jssEditionToOpen: number) {
    console.log("opening extract : " + jssEditionToOpen);
    // TODO : Open extract of magazine
  }


}
