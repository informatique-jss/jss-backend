import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'services-selection',
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

  selectCard(affaireId: number, event: Event): void {
    // Do not propagate clic if it is on pill
    if ((event.target as HTMLElement).closest('.pill')) {
      return;
    }

    this.selectedCardId = this.selectedCardId === affaireId ? null : affaireId;
  }

  deleteService(event: Event, serviceIdToDelete: number): void {
    event.stopPropagation();
  }
}
