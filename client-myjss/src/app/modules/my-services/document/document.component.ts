import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
  selector: 'document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.css']
})
export class DocumentComponent implements OnInit {
  title: string = 'Récupérez les documents nécessaires à vos transactions ou formalités';
  firstStepTitle: string = 'Choisissez les documents dont vous avez besoin';
  secondStepTitle: string = 'Validez votre commande';
  firstStepDescription: string = 'Nous vous fournissons tous types de documents disponibles auprès des autorités compétentes : Kbis, registres, Actes, CNO, Etats complets, RBE.';
  secondStepDescription: string = 'Dès validation nos équipes se mobilisent pour obtenir vos pièces dans les meilleurs délais.';

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
}
