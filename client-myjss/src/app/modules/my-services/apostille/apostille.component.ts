import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
  selector: 'apostille',
  templateUrl: './apostille.component.html',
  styleUrls: ['./apostille.component.css']
})
export class ApostilleComponent implements OnInit {

  title: string = ' Authentifiez vos documents pour l’étranger en 3 étapes';
  firstStepTitle: string = 'Remplissez le bon de commande';
  secondStepTitle: string = 'Envoyez les originaux';
  thirdStepTitle: string = 'Récupérez vos documents authentifiés';
  firstStepDescription: string = 'Indiquez le type de document, le pays de destination et les éventuelles traductions.';
  secondStepDescription: string = 'Transmettez les documents originaux à cette adresse : JSS - Service Apostilles Légalisations, 10 Boulevard Haussmann, 75009 Paris';
  thirdStepDescription: string = 'Recevez vos documents selon les modalités que vous aurez précisées.';

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
}
