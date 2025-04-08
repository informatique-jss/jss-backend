import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../libs/app.service';

@Component({
  selector: 'apostille',
  templateUrl: './apostille.component.html',
  styleUrls: ['./apostille.component.css'],
  standalone: false
})
export class ApostilleComponent implements OnInit {

  constructor(private appService: AppService) { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  openAnnouncements(event: any) {
    this.appService.openRoute(event, "/services/announcement", undefined);
  }

  openFormality(event: any) {
    this.appService.openRoute(event, "/services/formality", undefined);
  }

  openDomiciliation(event: any) {
    this.appService.openRoute(event, "/services/domiciliation", undefined);
  }

  openDocument(event: any) {
    this.appService.openRoute(event, "/services/document", undefined);
  }
}
