import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
  selector: 'join-us',
  templateUrl: './join-us.component.html',
  styleUrls: ['./join-us.component.css'],
})
export class JoinUsComponent implements OnInit {

  images: string[] = [
    "assets/img/societe/join_us_caroussel1.png",
    "assets/img/societe/join_us_caroussel2.png",
    "assets/img/societe/join_us_caroussel3.png",
    "assets/img/societe/join_us_caroussel1.png",
    "assets/img/societe/join_us_caroussel2.png",
    "assets/img/societe/join_us_caroussel3.png",
  ];

  @ViewChild('modalImage') modalImageRef!: ElementRef<HTMLImageElement>;

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.6
    });
  }

  openImageModal(imageUrl: string): void {
    if (this.modalImageRef) {
      this.modalImageRef.nativeElement.src = imageUrl;
    }
  }
}
