import { Component, Input } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from "@ng-icons/core";

@Component({
  selector: 'app-ui-card',
  templateUrl: './ui-card.component.html',
  imports: [
    NgIcon,
    NgbCollapse
  ],
  standalone: true
})
export class UiCardComponent {

  @Input() title!: string
  @Input() isTogglable?: boolean
  @Input() isReloadable?: boolean
  @Input() isCloseable?: boolean
  @Input() bodyClass?: string
  @Input() className?: string

  isCollapsed = false
  isReloading = false;
  isVisible = true;

  reload() {
    this.isReloading = true;
    setTimeout(() => (this.isReloading = false), 1500); // fake reload
  }

  close() {
    this.isVisible = false;
  }
}