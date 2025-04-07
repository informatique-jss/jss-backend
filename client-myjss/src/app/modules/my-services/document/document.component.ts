import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
    selector: 'document',
    templateUrl: './document.component.html',
    styleUrls: ['./document.component.css'],
    standalone: false
})
export class DocumentComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
}
