import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
  selector: 'formality',
  templateUrl: './formality.component.html',
  styleUrls: ['./formality.component.css'],
})
export class FormalityComponent implements OnInit {

  title: string = 'Déposez votre formalité en 3 étapes';
  firstStepTitle: string = 'Sélectionnez votre formalité';
  secondStepTitle: string = 'Identifiez la structure concernée';
  thirdStepTitle: string = 'Remplissez le bon de commande et téléversez les pièces justificatives ';
  firstStepDescription: string = 'Choisissez les évènements ou mises à jour à formaliser.';
  secondStepDescription: string = 'Saisissez les informations de la structure à créer ou le SIRET de l’entité existante.';
  thirdStepDescription: string = 'Naviguez sur une interface sécurisée et optimisée pour un traitement rapide et fiable de vos informations.';

  constructor() { }

  ngOnInit() {
  }
  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

}
