import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'about-us',
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.css'],
  standalone: false
})
export class AboutUsComponent implements OnInit {

  @ViewChild('modalImage') modalImageRef!: ElementRef<HTMLImageElement>;

  constructor(private appService: AppService) { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  openImageModal(imageUrl: string): void {
    if (this.modalImageRef) {
      this.modalImageRef.nativeElement.src = imageUrl;
    }
  }
}
