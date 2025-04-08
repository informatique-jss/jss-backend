import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../libs/app.service';

@Component({
  selector: 'document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.css'],
  standalone: false
})
export class DocumentComponent implements OnInit {

  constructor(private appService: AppService) { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  openFormality(event: any) {
    this.appService.openRoute(event, "/services/formality", undefined);
  }

  openApostille(event: any) {
    this.appService.openRoute(event, "/services/apostille", undefined);
  }

  openDomiciliation(event: any) {
    this.appService.openRoute(event, "/services/domiciliation", undefined);
  }

  openDocument(event: any) {
    this.appService.openRoute(event, "/services/document", undefined);
  }
}
