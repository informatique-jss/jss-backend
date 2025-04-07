import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'required-information',
  templateUrl: './required-information.component.html',
  styleUrls: ['./required-information.component.css'],
  standalone: false
})
export class RequiredInformationComponent implements OnInit {


  downloadingPercentage: number = 100;
  totalFileWeight: number = 120;
  isDownloading: boolean = this.downloadingPercentage != 100 ? true : false;
  isError: boolean = true;

  uploadedDocuments: string[] = ["MBE", "Copie du diplôme, du titre ou de l’autorisation provisoire ou définitive permettant à la société d’exercer l’activité réglementée"];

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

  constructor(private formBuilder: FormBuilder) { }

  servicesForm = this.formBuilder.group({});
  docInformationForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onCardClick(): void {
    const fileInput = document.getElementById('fileDropRef') as HTMLInputElement;
    fileInput?.click();
  }

  onFileSelected(event: any): void {
    const files = event.target.files;
    if (files.length > 0) {
      // TODO
      console.log('Fichiers sélectionnés :', files);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    const card = event.target as HTMLElement;
    card.classList.add('dragover');
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    const card = event.target as HTMLElement;
    card.classList.remove('dragover');
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    const card = event.target as HTMLElement;
    card.classList.remove('dragover');

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      // TODO
      console.log('Fichiers déposés :', files);
    }
  }

  deleteFile() {
    //TODO
    throw new Error('Method not implemented.');
  }

  selectCard(affaireId: number, event: Event): void {
    // Do not propagate clic if it is on pill
    if ((event.target as HTMLElement).closest('.pill')) {
      return;
    }

    this.selectedCardId = this.selectedCardId === affaireId ? null : affaireId;
  }

  searchServices() {
    //TODO
    throw new Error('Method not implemented.');
  }
}
