import { Component, OnInit } from '@angular/core';

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
  isError: boolean = false;

  uploadedDocuments: string[] = ["MBE", "Copie du diplôme, du titre ou de l’autorisation provisoire ou définitive permettant à la société d’exercer l’activité réglementée"];

  constructor() { }

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
}
