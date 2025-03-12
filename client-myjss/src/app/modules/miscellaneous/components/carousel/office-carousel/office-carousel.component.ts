import { Component, OnInit } from '@angular/core';
import { CarouselItem } from '../../../model/CarouselItem';
import { GenericCarouselComponent } from '../generic-carousel/generic-carousel.component';

@Component({
  selector: 'office-carousel',
  templateUrl: '../generic-carousel/generic-carousel.component.html',
  styleUrls: ['../generic-carousel/generic-carousel.component.css']
})
export class OfficeCarouselComponent extends GenericCarouselComponent implements OnInit {

  override title = "Un espace de travail ouvert et inspirant";
  override carouselItems: CarouselItem[] = [
    { title: '', description: '', image: 'assets/img/carousel/office-carousel-1.png' },
    { title: '', description: '', image: 'assets/img/carousel/office-carousel-2.jpg' },
    { title: '', description: '', image: 'assets/img/carousel/office-carousel-3.png' },
    { title: '', description: '', image: 'assets/img/carousel/office-carousel-1.png' },
    { title: '', description: '', image: 'assets/img/carousel/office-carousel-2.jpg' },
    { title: '', description: '', image: 'assets/img/carousel/office-carousel-3.png' },

  ];

}
