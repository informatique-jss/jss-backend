import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
  selector: 'domiciliation',
  templateUrl: './domiciliation.component.html',
  styleUrls: ['./domiciliation.component.css']
})
export class DomiciliationComponent implements OnInit {
  constructor() { }

  ngOnInit() {
  }
  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
}
