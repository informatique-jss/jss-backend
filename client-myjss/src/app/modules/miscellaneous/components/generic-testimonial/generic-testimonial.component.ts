import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'generic-testimonial',
  templateUrl: './generic-testimonial.component.html',
  styleUrls: ['./generic-testimonial.component.css'],
  imports: [CommonModule],
  standalone: true
})
export class GenericTestimonialComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
