import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  alPictogram: string = "assets/pictograms/AL.svg";
  formalityPictogram: string = "assets/pictograms/Formalities.svg";
  apostilePictogram: string = "assets/pictograms/Apostile.svg";
  domiciliationPictogram: string = "assets/pictograms/Domiciliation.svg";
  documentsProvidingPictogram: string = "assets/pictograms/Documents-providing.svg";
  logoJss: string = 'assets/images/white-logo.svg';
  videoParis: string = 'assets/videos/paris-home-video.webm';

  constructor() { }

  ngOnInit() {
  }

}
