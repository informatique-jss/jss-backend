import { CommonModule } from '@angular/common';
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
import { SwiperContainer } from 'swiper/element';

@Component({
  selector: 'generic-swiper',
  templateUrl: './generic-swiper.component.html',
  styleUrls: ['./generic-swiper.component.css'],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [CommonModule],
  standalone: true
})
export class GenericSwiperComponent implements OnInit {

  @ViewChild('genericSwiper') genericSwiper!: ElementRef<SwiperContainer>;
  @Input() items: any[] = [];
  @Input() subtitle: string = '';
  @Input() title: string = '';
  @Input() slidesPerView: number = 3;
  @ContentChild(TemplateRef) templateRefFirstItem!: TemplateRef<any>; // Take the content of the personalised HTML
  @ContentChild(TemplateRef) templateRef!: TemplateRef<any>; // Take the content of the personalised HTML

  @Input() firstItemImage: any | undefined;

  private destroy$ = new Subject<void>();

  constructor() { }

  ngOnInit() { }

  ngAfterViewInit() {
    // Initialize main Swiper
    const params = {
      autoHeight: true,
      spaceBetween: "10",
      speed: "500",
      slidesPerView: this.getSlidesPerView(),
      loop: "true",
      injectStyles: [`
          .swiper-pagination-bullet {
            text-align: center;
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
          `],
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
      });
  }


  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setMaxSlideHeight(): void {
    const swiperEl = this.genericSwiper?.nativeElement as HTMLElement;
    if (!swiperEl) return;

    const slides = swiperEl.querySelectorAll('swiper-slide');
    let maxHeight = 0;

    slides.forEach((slide: HTMLElement) => {
      // Reset the height to properly calculate the new value
      slide.style.height = 'auto';
    });

    slides.forEach((slide: HTMLElement) => {
      const slideHeight = slide.scrollHeight;
      if (slideHeight > maxHeight) {
        maxHeight = slideHeight;
      }
    });

    swiperEl.style.height = `${maxHeight}px`;

    slides.forEach((slide: HTMLElement) => {
      slide.style.height = `${maxHeight}px`;
    });

  }


  getSlidesPerView(): number {
    if (this.firstItemImage && this.firstItemImage.length > 0)
      this.slidesPerView = 4;
    return this.slidesPerView;
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
