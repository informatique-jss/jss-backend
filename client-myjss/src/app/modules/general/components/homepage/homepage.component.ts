import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],
  standalone: false
})
export class HomepageComponent implements OnInit {

  annoncesLegalesPicto: string = "assets/pictos/Annonces.svg"
  formalitesPicto: string = "assets/pictos/Formalités.svg"
  apostilleLegaPicto: string = "assets/pictos/Apostille&Légalisation.svg"
  domiciliationPicto: string = "assets/pictos/Domiciliation.svg"
  fournitureDocumentsPicto: string = "assets/pictos/Fourniture-documents.svg"
  logoJss: string = '/assets/images/white-logo.svg';
  videoParis: string = 'assets/videos/paris-home-video.webm'

  constructor(private appService: AppService) { }

  ngOnInit() {
  }

  openAnnouncements(event: any) {
    this.appService.openRoute(event, "/services/announcement", undefined);
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
