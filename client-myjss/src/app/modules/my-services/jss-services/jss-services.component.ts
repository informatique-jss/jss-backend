import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jss-services',
  templateUrl: './jss-services.component.html',
  styleUrls: ['./jss-services.component.css']
})
export class JssServicesComponent implements OnInit {
  constructor() { }
  selectedTabContent: any;
  ngOnInit() {
    this.selectedTabContent = this.tabs[0];
  }
  tabs = [
    { id: 'announcement', name: 'Annonces légales', active: true },
    { id: 'formality', name: 'Formalités légales', active: false },
    { id: 'apostille', name: 'Apostilles-Légalisation', active: false },
    { id: 'domiciliation', name: 'Domiciliation', active: false },
    { id: 'document', name: 'Fourniture de documents', active: false }
  ];

  // Initialisation du breadcrumb avec "Services"
  breadcrumbItems = ['Services'];

  // Gestion du clic sur un onglet
  onTabClick(selectedIndex: number): void {
    // Réinitialisation de l'état des onglets
    this.tabs.forEach((tab, index) => {
      tab.active = index === selectedIndex;
    });

    // Mettre à jour dynamiquement le breadcrumb avec le nom de l'onglet sélectionné
    this.updateBreadcrumb(this.tabs[selectedIndex]);
  }

  // Mettre à jour le breadcrumb
  updateBreadcrumb(tab: any): void {
    this.breadcrumbItems = ['Services', tab.name];
  }
}
