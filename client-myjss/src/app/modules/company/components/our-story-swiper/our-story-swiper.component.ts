import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, CUSTOM_ELEMENTS_SCHEMA, ElementRef, OnInit, ViewChild } from '@angular/core';
import { SwiperContainer } from 'swiper/element';

@Component({
    selector: 'our-story-swiper',
    templateUrl: './our-story-swiper.component.html',
    styleUrls: ['./our-story-swiper.component.css'],
    imports: [CommonModule],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OurStorySwiperComponent implements OnInit, AfterViewInit {


  history = [
    { year: 1898, category: "JOURNAL SPÉCIAL DES SOCIÉTÉS FRANÇAISES PAR ACTIONS", text: 'Création du supplément de l’annuaire général des sociétés françaises par actions', image: "assets/images/image-story-1898.png" },
    { year: 1900, category: "ANNONCES LÉGALES", text: 'Première habitation dans le département de la Seine', image: "assets/images/image-story-1898.png" },
    { year: 1985, category: "FORMALITÉS", text: 'Création du service de formalité légales', image: "assets/images/image-story-1898.png" },
    { year: 2002, category: "SITE WEB", text: 'Lancement du site internet jss.fr', image: "assets/images/image-story-1898.png" },
    { year: 2003, category: "FORMATIONS", text: 'Création du service des formations juridiques', image: "assets/images/image-story-1898.png" },
    { year: 2004, category: "", text: 'Création de la revue juridique, “le Journal des Sociétés”, mensuel du juriste et de l’entreprise', image: "assets/images/image-story-2004.png" },
    { year: 2014, category: "TRAITEMENT DES FORMALITÉS", text: 'Création du service de formalités dématérialisée', image: "assets/images/image-story-1898.png" },
    { year: 2015, category: "CROISSANCE EXTERNE", text: 'Rachat du Journal “les Annonces de la Seine"', image: "assets/images/image-story-2015.png" },
    { year: 2016, category: "ANNONCES LÉGALES", text: 'Nouvelles habilitations dans les départements 78, 91 et 95', image: "assets/images/image-story-1898.png" },
    { year: 2019, category: "DOMICILIATION", text: 'Création du Service Domiciliation', image: "assets/images/image-story-1898.png" },
    { year: 2020, category: "ANNONCES LÉGALES", text: 'Ouverture du Service de Presse de En Ligne (SPEL)', image: "assets/images/image-story-2020.png" },
    { year: 2022, category: "JOURNAL SPÉCIAL DES SOCIÉTÉS", text: 'Le journal devient hebdomadaire', image: "assets/images/image-story-1898.png" },
    { year: 2023, category: "JOURNAL SPÉCIAL DES SOCIÉTÉS", text: 'Digitalisation du journal', image: "assets/images/image-story-1898.png" }
  ];

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
