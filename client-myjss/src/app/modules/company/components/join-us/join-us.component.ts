import { CommonModule } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, ElementRef, OnInit, ViewChild } from '@angular/core';
import { jarallax } from 'jarallax';
import { SwiperContainer } from 'swiper/element';

@Component({
  selector: 'join-us',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './join-us.component.html',
  styleUrls: ['./join-us.component.css'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JoinUsComponent implements OnInit {

  @ViewChild('imagesSwiper') imagesSwiper!: ElementRef<SwiperContainer>;

  images: string[] = [
    "assets/img/societe/join_us_caroussel1.png",
    "assets/img/societe/join_us_caroussel2.png",
    "assets/img/societe/join_us_caroussel3.png",
    "assets/img/societe/join_us_caroussel1.png",
    "assets/img/societe/join_us_caroussel2.png",
    "assets/img/societe/join_us_caroussel3.png",
  ];

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });

    // Initialize main Swiper
    const params = {
      autoHeight: true,
      spaceBetween: "10",
      speed: "500",
      slidesPerView: "3",
      loop: "true",
      injectStyles: [`
      .swiper-pagination-bullet {
        text-align: center;
        line-height: 20px;
        font-size: 12px;
        color: red;
        opacity: 1;
        background: rgba(255, 255, 255, 0);
        border: white solid 1px;
      }

      .swiper-pagination-bullet-active {
        color: #fff;
        background:rgb(255, 255, 255);
      }
      `],
      pagination: {
        clickable: true,
      },
    }

    Object.assign(this.imagesSwiper.nativeElement, params);
    this.imagesSwiper.nativeElement.initialize();
  }

  slideNext(): void {
    if (this.imagesSwiper && this.imagesSwiper.nativeElement.swiper) {
      this.imagesSwiper.nativeElement.swiper.slideNext();
    }
  }

  slidePrev(): void {
    if (this.imagesSwiper && this.imagesSwiper.nativeElement.swiper) {
      this.imagesSwiper.nativeElement.swiper.slidePrev();
    }
  }

}
