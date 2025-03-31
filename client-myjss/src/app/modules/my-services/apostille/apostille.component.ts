import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
    selector: 'apostille',
    templateUrl: './apostille.component.html',
    styleUrls: ['./apostille.component.css'],
    standalone: false
})
export class ApostilleComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
}
