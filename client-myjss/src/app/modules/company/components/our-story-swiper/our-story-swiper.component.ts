import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, CUSTOM_ELEMENTS_SCHEMA, ElementRef, OnInit, ViewChild } from '@angular/core';
import { SwiperContainer } from 'swiper/element';

@Component({
  selector: 'our-story-swiper',
  templateUrl: './our-story-swiper.component.html',
  styleUrls: ['./our-story-swiper.component.css'],
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  standalone: true
})
export class OurStorySwiperComponent implements OnInit, AfterViewInit {

  @ViewChild('mainSwiper') mainSwiper!: ElementRef<SwiperContainer>;
  @ViewChild('datesSwiper') datesSwiper!: ElementRef<SwiperContainer>;

  constructor() { }

  ngOnInit() { }

  ngAfterViewInit() {
    // Initialize main Swiper
    const mainSwiperParams = {
      thumbs: {
        swiper: this.datesSwiper.nativeElement
      },
      controller: {
        control: this.datesSwiper.nativeElement
      },
      centeredSlides: true, // Center the slides
      autoHeight: true
    };
    Object.assign(this.mainSwiper.nativeElement, mainSwiperParams);
    this.mainSwiper.nativeElement.initialize();

    // Initialize dates Swiper
    const datesSwiperParams = {
      slidesPerView: 9,
      centeredSlides: true,
      controller: {
        control: this.mainSwiper.nativeElement
      },
    };
    Object.assign(this.datesSwiper.nativeElement, datesSwiperParams);
    this.datesSwiper.nativeElement.initialize();

    // Center the active slide in dates Swiper
    if (this.mainSwiper.nativeElement && this.mainSwiper.nativeElement.swiper) {
      this.mainSwiper.nativeElement.swiper.on('slideChange', () => {
        const activeIndex = this.mainSwiper.nativeElement.swiper.activeIndex;
        this.datesSwiper.nativeElement.swiper.slideTo(activeIndex);
        const slides = this.datesSwiper.nativeElement.querySelectorAll('swiper-slide');
        slides.forEach((slide, index) => {
          if (index === activeIndex) {
            slide.classList.add('zoom');
          } else {
            slide.classList.remove('zoom');
          }
        });
      });
    }

    // Hide/Show slides on hover
    const updateVisibleSlides = () => {
      const activeIndex = this.datesSwiper.nativeElement.swiper.activeIndex;
      const slides = this.datesSwiper.nativeElement.querySelectorAll('swiper-slide');
      slides.forEach((slide, index) => {
        if (index >= activeIndex - 1 && index <= activeIndex + 1) {
          slide.classList.remove('hidden');
        } else {
          slide.classList.add('hidden');
        }
      });
    };

    const showAll = () => {
      const slides = this.datesSwiper.nativeElement.querySelectorAll('swiper-slide');
      slides.forEach((slide) => {
        slide.classList.remove('hidden');
      });
    };

    // Update visibility on mousewheel
    const handleMouseWheel = () => {
      updateVisibleSlides();
    };

    this.datesSwiper.nativeElement.addEventListener('mouseenter', showAll);
    this.datesSwiper.nativeElement.addEventListener('mouseleave', updateVisibleSlides);


    this.mainSwiper.nativeElement.addEventListener('mousewheel', handleMouseWheel);

    // Initial hide extremes
    updateVisibleSlides();
  }
}
