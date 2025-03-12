import { Component, OnInit } from '@angular/core';
import { register } from 'swiper/element/bundle';
register();

@Component({
  selector: 'app-society',
  templateUrl: './society.component.html',
  styleUrls: ['./society.component.css']
})
export class SocietyComponent implements OnInit {

  constructor() { }
  selectedTabContent: any;
  ngOnInit() {
    this.selectedTabContent = this.tabs[0];
  }
  tabs = [
    { id: 'aboutUs', name: 'A propos', active: false },
    { id: 'ourStory', name: 'Notre histoire', active: true },
    { id: 'ourTeams', name: 'Nos équipes', active: false },
    { id: 'joinUs', name: 'Nous rejoindre', active: false },
  ];

  breadcrumbItems = ['La société'];

  onTabClick(selectedIndex: number): void {
    this.tabs.forEach((tab, index) => {
      tab.active = index === selectedIndex;
    });

    this.updateBreadcrumb(this.tabs[selectedIndex]);
  }

  updateBreadcrumb(tab: any): void {
    this.breadcrumbItems = ['La société', tab.name];
  }
}
