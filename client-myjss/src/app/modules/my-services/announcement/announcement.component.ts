import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../libs/app.service';

@Component({
  selector: 'announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit {

  title: string = 'Publiez votre annonce en 3 étapes';
  firstStepTitle: string = 'Sélectionnez votre modèle';
  secondStepTitle: string = 'Remplissez, visualisez et validez votre annonce';
  thirdStepTitle: string = 'Complétez vos coordonnées et payez';
  firstStepDescription: string = 'Accédez à une bibliothèque complète de formulaires adaptés à vos besoins en annonces légales.';
  secondStepDescription: string = 'Profitez d’une saisie libre ou guidée grâce à nos modèles intuitifs. Obtenez le prix instantanément.';
  thirdStepDescription: string = 'Bénéficiez d’une interface sécurisée et fiable pour le traitement de vos informations personnelles.';

  constructor(private appService: AppService) {
  }

  ngOnInit() {

  }
  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
  openProduct(event: any) {
    this.appService.openRoute(event, "product/" + "", undefined);
  }
  openServices(event: any) {
    this.appService.openRoute(event, "my-services/" + "", undefined);
  }
}
