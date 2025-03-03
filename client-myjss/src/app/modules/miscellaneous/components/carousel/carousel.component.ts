import { Component, OnInit, ViewChild } from '@angular/core';
import { DragScrollComponent } from 'ngx-drag-scroll';

@Component({
  selector: 'app-carousel',
  templateUrl: './carousel.component.html',
  styleUrls: ['./carousel.component.css']
})
export class CarouselComponent implements OnInit {
  @ViewChild('nav') dragScroll: DragScrollComponent | undefined; // Référence à drag-scroll
  ngOnInit() {
  }
  items = [
    { image: 'assets/img/avatar/01.jpg', title: 'Card 1', description: 'Description for Card 1' },
    { image: 'assets/img/avatar/02.jpg', title: 'Card 2', description: 'Description for Card 2' },
    { image: 'assets/img/avatar/03.jpg', title: 'Card 3', description: 'Description for Card 3' },
    { image: 'assets/img/avatar/04.jpg', title: 'Card 4', description: 'Description for Card 4' },
    { image: 'assets/img/avatar/05.jpg', title: 'Card 5', description: 'Description for Card 5' },
    // Ajoutez plus d'éléments si nécessaire
  ];

  ngAfterViewInit() {
    // Vous pouvez appeler moveLeft() ou moveRight() ici si nécessaire
  }

  moveLeft() {
    if (this.dragScroll) {
      this.dragScroll.moveLeft(); // Déplacer le carousel vers la gauche
    }
  }

  moveRight() {
    if (this.dragScroll) {
      this.dragScroll.moveRight(); // Déplacer le carousel vers la droite
    }
  }

}
