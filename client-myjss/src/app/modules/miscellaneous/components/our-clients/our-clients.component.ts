import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, CUSTOM_ELEMENTS_SCHEMA, ElementRef, OnInit, ViewChild } from '@angular/core';
import { SwiperContainer } from 'swiper/element';

@Component({
  selector: 'our-clients',
  templateUrl: './our-clients.component.html',
  styleUrls: ['./our-clients.component.css'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [CommonModule],
  standalone: true
})
export class OurClientsComponent implements OnInit, AfterViewInit {

  @ViewChild('ourClientsSwiper1') ourClientsSwiper1!: ElementRef<SwiperContainer>;
  @ViewChild('ourClientsSwiper2') ourClientsSwiper2!: ElementRef<SwiperContainer>;

  constructor() { }

  ngOnInit() { }

  ngAfterViewInit() {

    // Initialize main Swiper
    const params1 = {
      autoHeight: true,
      spaceBetween: "15",
      speed: "4000",
      slidesPerView: "9",
      loop: "true",
      freeMode: true,
      autoplay: {
        delay: 0,
        disableOnInteraction: false,
      },
      injectStyles: [
        '.swiper-wrapper { transition-timing-function: linear !important }',
      ],
    }
    const params2 = {
      autoHeight: true,
      spaceBetween: "15",
      speed: "4000",
      slidesPerView: "9",
      loop: "true",
      freeMode: true,
      autoplay: {
        delay: 0,
        disableOnInteraction: false,
        reverseDirection: true,
      },
      injectStyles: [
        '.swiper-wrapper { transition-timing-function: linear !important }',
      ],
    }

    Object.assign(this.ourClientsSwiper1.nativeElement, params1);
    this.ourClientsSwiper1.nativeElement.initialize();
    Object.assign(this.ourClientsSwiper2.nativeElement, params2);
    this.ourClientsSwiper2.nativeElement.initialize();
  }
}
