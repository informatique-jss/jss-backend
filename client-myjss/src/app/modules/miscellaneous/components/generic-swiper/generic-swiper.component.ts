import {
  Component,
  ContentChild,
  CUSTOM_ELEMENTS_SCHEMA,
  ElementRef,
  Input,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import { fromEvent, Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PlatformService } from '../../../main/services/platform.service';

@Component({
  selector: 'generic-swiper',
  templateUrl: './generic-swiper.component.html',
  styleUrls: ['./generic-swiper.component.css'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [SHARED_IMPORTS],
  standalone: true
})
export class GenericSwiperComponent implements OnInit {

  mediumViewport: number = 768;
  largeViewport: number = 992;
  maxHeight: number = 0;
  slideWidth: number = 0;

  @ViewChild('genericSwiper') genericSwiper!: ElementRef<any>;
  @Input() items: any[] = [];
  @Input() subtitle: string = '';
  @Input() title: string = '';
  @Input() slidesPerView: number = 3; // If using firstFixedImg, please keep slides per view to 3
  @Input() firstFixedImg: string | undefined;
  @ContentChild(TemplateRef) templateRefFirstItem!: TemplateRef<any>; // Take the content of the personalised HTML
  @ContentChild(TemplateRef) templateRef!: TemplateRef<any>; // Take the content of the personalised HTML

  private destroy$ = new Subject<void>();

  constructor(private platformService: PlatformService) { }

  ngOnInit() { }

  async ngAfterViewInit() {
    if (this.platformService.isServer())
      return;

    // Initialize main Swiper
    await customElements.whenDefined('swiper-container');
    const params = {
      autoHeight: true,
      spaceBetween: "10",
      speed: "500",
      slidesPerView: this.getSlidesPerView(),
      loop: "true",
      injectStyles: this.firstFixedImg && window.innerWidth > this.largeViewport ? [`
          .swiper-pagination-bullet {
            text-align: start;
            line-height: 20px;
            font-size: 12px;
            opacity: 1;
            background: rgba(255, 255, 255, 0);
            border: white solid 1px;
          }

          .swiper-pagination-bullet-active {
            color: #fff;
            background:rgb(255, 255, 255);
          }

          .swiper-pagination-horizontal {
            margin-left: -16.8%;
          }
          `] : [`
          .swiper-pagination-bullet {
            text-align: start;
            line-height: 20px;
            font-size: 12px;
            opacity: 1;
            background: rgba(255, 255, 255, 0);
            border: white solid 1px;
          }

          .swiper-pagination-bullet-active {
            color: #fff;
            background:rgb(255, 255, 255);
          }`],
      pagination: {
        clickable: true,
      },
    }

    Object.assign(this.genericSwiper.nativeElement, params);

    this.genericSwiper.nativeElement.initialize();

    // We set a timeout to be sure that everything is loaded
    setTimeout(() => {
      this.setMaxSlideHeight();
    }, 0);

    fromEvent(window, 'resize')
      .pipe(debounceTime(200), takeUntil(this.destroy$))
      .subscribe(() => {
        this.setMaxSlideHeight();
        this.updateSwiperSlidesPerView();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  isBrowser() {
    return this.platformService.isBrowser();
  }

  private getSlidesPerView(): number {
    if (this.platformService.isServer())
      return this.slidesPerView;

    const width = window.innerWidth;

    if (width < this.mediumViewport) {
      return 1;
    } else if (width >= this.mediumViewport && width < this.largeViewport) {
      return 2;
    } else {
      return this.slidesPerView;
    }
  }

  private updateSwiperSlidesPerView(): void {
    const swiperEl = this.genericSwiper.nativeElement;

    swiperEl.slidesPerView = this.getSlidesPerView();
  }


  private setMaxSlideHeight(): void {
    const swiperEl = this.genericSwiper?.nativeElement as HTMLElement;
    if (!swiperEl) return;

    const slides = swiperEl.querySelectorAll('swiper-slide');

    slides.forEach((slide: any) => {
      // Reset the height to properly calculate the new value
      slide.style.height = 'auto';
    });

    this.maxHeight = 0;
    // We search for the slide with the maxHeight
    slides.forEach((slide: any) => {
      const slideHeight = slide.scrollHeight;
      if (slideHeight > this.maxHeight) {
        this.maxHeight = slideHeight;
      }
    });

    // Set slide width (same width for all slides)
    this.slideWidth = slides[0].scrollWidth;

    swiperEl.style.height = `${this.maxHeight}px`;

    slides.forEach((slide: any) => {
      slide.style.height = `${this.maxHeight}px`;
    });
  }

  slideNext(): void {
    if (this.genericSwiper && this.genericSwiper.nativeElement.swiper) {
      this.genericSwiper.nativeElement.swiper.slideNext();
    }
  }

  slidePrev(): void {
    if (this.genericSwiper && this.genericSwiper.nativeElement.swiper) {
      this.genericSwiper.nativeElement.swiper.slidePrev();
    }
  }
}
