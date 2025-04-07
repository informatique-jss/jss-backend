import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
    selector: 'our-team',
    templateUrl: './our-team.component.html',
    styleUrls: ['./our-team.component.css'],
    standalone: false
})
export class OurTeamComponent implements OnInit {

  departments: string[] = ["Direction", "Commercial", "Annonces", "Formalités", "Marketing", "Rédaction", "Comptabilité", "Informatique"];
  selectedTab: string = "Direction";

  constructor() { }

  ngOnInit() {
  }


  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  onTabClick(department: string) {
    this.selectedTab = department;
  }

}
