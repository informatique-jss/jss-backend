import { Component, OnInit } from '@angular/core';
import Swiper from 'swiper';

@Component({
  selector: 'carousel',
  templateUrl: './carousel.component.html',
  styleUrls: ['./carousel.component.css']
})
export class CarouselComponent implements OnInit {

  constructor() { }

  swiper: Swiper | undefined;


  ngOnInit(): void {
    this.swiper = new Swiper('.swiper-container', {
      loop: true,
      slidesPerView: 3,
      spaceBetween: 20,
      navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
      },
      pagination: {
        el: '.swiper-pagination',
        clickable: true,
      },
      breakpoints: {
        // Si la taille de l'écran est plus petite que 768px, afficher 1 élément
        768: {
          slidesPerView: 1,
        },
        // Si la taille de l'écran est plus grande ou égale à 768px, afficher 2 éléments
        1024: {
          slidesPerView: 2,
        },
      },
    });
  }

}

