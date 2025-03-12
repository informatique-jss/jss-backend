import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { jarallax } from 'jarallax';
import { SwiperContainer } from 'swiper/element';

@Component({
  selector: 'our-story',
  templateUrl: './our-story.component.html',
  styleUrls: ['./our-story.component.css']
})
export class OurStoryComponent implements OnInit {

  constructor() { }
  @ViewChild('swiper') swiper!: ElementRef<SwiperContainer>;
  history = [
    { year: 1980, text: 'Lancement de la marque Herta en France' },
    { year: 1990, text: 'Expansion à l’international' },
    { year: 2000, text: 'Introduction de nouveaux produits' },
    { year: 2010, text: 'Engagements en faveur du bio et du bien-être animal' },
    { year: 2020, text: 'Innovation et durabilité' }
  ];

  ngOnInit(): void {

  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
}

