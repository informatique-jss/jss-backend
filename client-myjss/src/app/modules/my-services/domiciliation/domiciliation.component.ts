import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
  selector: 'domiciliation',
  templateUrl: './domiciliation.component.html',
  styleUrls: ['./domiciliation.component.css']
})
export class DomiciliationComponent implements OnInit {


  title: string = ' Domiciliez votre entreprise en 3 étapes';
  firstStepTitle: string = 'Choisissez votre formule';
  secondStepTitle: string = 'Téléversez les pièces justificatives';
  thirdStepTitle: string = 'Recevez votre contrat par email';
  firstStepDescription: string = 'Sélectionnez le type de renvoi de courrier (postal, digital ou les deux).';
  secondStepDescription: string = "Déposez les pièces nécessaire à l'établissement de votre contrat de domiciliation directement sur le site. Ces pièces varient si c'est une société ou une succursale à domicilier.";
  thirdStepDescription: string = "Dès la signature de votre contrat, il vous est envoyé par mail et votre société est domiciliée.";

  constructor() { }

  ngOnInit() {
  }
  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
}
