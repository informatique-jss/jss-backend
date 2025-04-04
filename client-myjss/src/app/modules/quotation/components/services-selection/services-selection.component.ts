import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-services-selection',
  templateUrl: './services-selection.component.html',
  styleUrls: ['./services-selection.component.css'],
  standalone: false
})
export class ServicesSelectionComponent implements OnInit {

  affaires = [
    {
      id: 1,
      title: '10000000000000 - EXEMPLE BIS - 18 BOULEVARD DE LA TRINITÉ',
      services: [
        { id: 1, label: 'Transfert de Diège Hors Ressort', icon: 'ai-cross' },
        { id: 2, label: 'Changement de dirigeant - SC / SCP', icon: 'ai-cross' },
        { id: 3, label: 'Transfert de Diège Hors Ressort', icon: 'ai-cross' },
        { id: 4, label: 'Transfert', icon: 'ai-cross' }
      ]
    },
    {
      id: 2,
      title: '10000000000000 - EXEMPLE BIS - 18 BOULEVARD DE LA TRINITÉ',
      services: [
        { id: 1, label: 'Transfert de Diège Hors Ressort', icon: 'ai-cross' },
        { id: 4, label: 'Transfert', icon: 'ai-cross' }
      ]
    },
    {
      id: 3,
      title: '10000000000000 - EXEMPLE BIS - 18 BOULEVARD DE LA TRINITÉ',
      services: [
        { id: 1, label: 'Transfert de Diège Hors Ressort', icon: 'ai-cross' },
        { id: 4, label: 'Transfert', icon: 'ai-cross' }
      ]
    }
  ];

  selectedCardId: number | null = null;

  servicesSearched: string[] | null = null;

  constructor(
    private formBuilder: FormBuilder,
  ) { }

  servicesForm = this.formBuilder.group({});


  ngOnInit() {
  }

  searchServices() {
    throw new Error('Method not implemented.');
  }

  // Méthode pour sélectionner une carte
  selectCard(affaireId: number, event: Event): void {
    // Empêcher la propagation du clic si c'est un clic sur un pill
    if ((event.target as HTMLElement).closest('.pill')) {
      return;
    }

    // Toggle de la sélection
    this.selectedCardId = this.selectedCardId === affaireId ? null : affaireId;
  }

  // Méthode pour gérer le clic sur les pills
  deleteService(event: Event, serviceIdToDelete: number): void {
    event.stopPropagation();
  }
}
