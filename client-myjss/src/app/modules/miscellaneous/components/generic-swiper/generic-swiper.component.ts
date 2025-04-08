import { CommonModule } from '@angular/common';
import { Component, ContentChild, CUSTOM_ELEMENTS_SCHEMA, ElementRef, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
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

  @Input() isFirstItem: any | undefined;

  constructor() { }

  ngOnInit() { }

  ngAfterViewInit() {
    // Initialize main Swiper
    const params = {
      autoHeight: true,
      spaceBetween: "10",
      speed: "500",
      slidesPerView: this.slidesPerView,
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
