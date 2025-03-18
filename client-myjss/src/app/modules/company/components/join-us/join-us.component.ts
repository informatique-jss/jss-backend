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

  modalImage: HTMLImageElement | null = null;

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.6
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

    this.initImageModal();
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

  initImageModal(): void {
    // Select modal image
    this.modalImage = document.getElementById('modal-image') as HTMLImageElement;

    // Select clickable links for modal
    const imageLinks = document.querySelectorAll('[data-bs-toggle="modal"]');

    // Add click event for each link
    imageLinks.forEach(link => {
      link.addEventListener('click', (event) => {
        const target = event.currentTarget as HTMLElement;
        const imageUrl = target.getAttribute('data-bs-img');
        if (this.modalImage && imageUrl) {
          this.modalImage.src = imageUrl;
        }
      });
    });
  }


}
