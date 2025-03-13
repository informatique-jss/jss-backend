import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';

@Component({
  selector: 'about-us',
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.css']
})
export class AboutUsComponent implements OnInit {

  modalImage: HTMLImageElement | null = null;

  constructor() { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });

    this.initImageModal();

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
