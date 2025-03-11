import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  annoncesLegalesPicto: string = "assets/pictos/Annonces.svg"
  formalitesPicto: string = "assets/pictos/Formalités.svg"
  apostilleLegaPicto: string = "assets/pictos/Apostille&Légalisation.svg"
  domiciliationPicto: string = "assets/pictos/Domiciliation.svg"
  fournitureDocumentsPicto: string = "assets/pictos/Fourniture-documents.svg"
  logoJss: string = '/assets/images/white-logo.svg';
  videoParis: string = 'assets/videos/paris-home-video.webm'

  constructor() { }

  ngOnInit() {
  }

}
