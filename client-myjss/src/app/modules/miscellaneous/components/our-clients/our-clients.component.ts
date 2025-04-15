import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, CUSTOM_ELEMENTS_SCHEMA, ElementRef, OnInit, ViewChild } from '@angular/core';
import { fromEvent, Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
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

  smallViewport: number = 576;
  mediumViewport: number = 768;
  largeViewport: number = 992;
  extraLargeViewport: number = 1200;

  private destroy$ = new Subject<void>();

  constructor() { }

  ngOnInit() { }

  ngAfterViewInit() {

    // Initialize main Swiper
    const params1 = {
      autoHeight: true,
      spaceBetween: "15",
      speed: "4000",
      slidesPerView: this.getSlidesPerView(),
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
      slidesPerView: this.getSlidesPerView(),
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

    fromEvent(window, 'resize')
      .pipe(debounceTime(200), takeUntil(this.destroy$))
      .subscribe(() => {
        this.updateSwiperSlidesPerView();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private getSlidesPerView(): number {
    const width = window.innerWidth;

    if (width < this.smallViewport) {
      return 3;
    } else if (width >= this.smallViewport && width < this.mediumViewport) {
      return 4;
    } else if (width >= this.mediumViewport && width < this.largeViewport) {
      return 6;
    } else if (width >= this.largeViewport && width < this.extraLargeViewport) {
      return 7;
    } else {
      return 9;
    }
  }

  private updateSwiperSlidesPerView(): void {
    const swiperEl1 = this.ourClientsSwiper1.nativeElement;
    const swiperEl2 = this.ourClientsSwiper2.nativeElement;

    swiperEl1.slidesPerView = this.getSlidesPerView();
    swiperEl2.slidesPerView = this.getSlidesPerView();
  }
}
