import { Component, OnInit, ViewChild } from '@angular/core';
import { DragScrollComponent } from 'ngx-drag-scroll';
import { CarouselItem } from '../../../model/CarouselItem';

@Component({
  selector: 'generic-carousel',
  templateUrl: './generic-carousel.component.html',
  styleUrls: ['./generic-carousel.component.css']
})
export class GenericCarouselComponent implements OnInit {

  @ViewChild('nav') dragScroll: DragScrollComponent | undefined;
  carouselItems: CarouselItem[] = [] as Array<CarouselItem>;
  title: string = "";

  ngOnInit() {
  }
  itemsPerPage = 3;
  pages: number[] = [];
  currentPage = 0;

  ngAfterViewInit() {
    this.calculatePages();
    if (this.dragScroll)
      this.dragScroll.dsInitialized.subscribe(() => {
        this.updateActivePage();
      });
  }

  calculatePages() {
    const totalItems = this.carouselItems.length;
    const totalPages = Math.ceil(totalItems / this.itemsPerPage);
    this.pages = Array.from({ length: totalPages }, (_, i) => i);
  }

  updateActivePage() {
    if (this.dragScroll) {
      const activeGroupIndex = Math.floor(this.dragScroll.currIndex / this.itemsPerPage);
      this.currentPage = activeGroupIndex;
    }
  }

  goToPage(index: number) {
    if (this.dragScroll) {
      this.dragScroll.moveTo(index * this.itemsPerPage);
      this.currentPage = index;
    }
  }

  moveLeft() {
    if (this.dragScroll) {
      this.dragScroll.moveLeft();
    }
  }

  moveRight() {
    if (this.dragScroll) {
      this.dragScroll.moveRight();
    }
  }
  sanitizeText(html: string): string {
    let doc = new DOMParser().parseFromString(html, 'text/html');
    return doc.body.textContent || "";
  }
}
