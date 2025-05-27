import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';

@Component({
  selector: 'generic-testimonial',
  templateUrl: './generic-testimonial.component.html',
  styleUrls: ['./generic-testimonial.component.css'],
  imports: [SHARED_IMPORTS],
  standalone: true
})
export class GenericTestimonialComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
