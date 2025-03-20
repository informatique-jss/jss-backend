import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
  selector: 'formality',
  templateUrl: './formality.component.html',
  styleUrls: ['./formality.component.css'],
})
export class FormalityComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }
  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

}
