import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbCollapse, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { NgIconComponent } from '@ng-icons/core';

@Component({
  selector: 'ui-card',
  templateUrl: './ui-card.component.html',
  styleUrls: ['./ui-card.component.css'],
  standalone: true,
  imports: [
    NgbCollapse,
    NgIconComponent,
    NgbTooltip,
  ]
})
export class UiCardComponent {

  @Input() title!: string
  @Input() isTogglable?: boolean
  @Input() isReloadable?: boolean
  @Input() isCloseable?: boolean
  @Input() bodyClass?: string
  @Input() className?: string
  @Input() customButtonIcon?: string
  @Input() customButtonTooltip?: string

  @Output() isCustomButtonSelected: EventEmitter<boolean> = new EventEmitter<boolean>(false);

  isCollapsed = false
  isReloading = false;
  isVisible = true;
  isCustomButtonSelectedValue = false;

  reload() {
    this.isReloading = true;
    setTimeout(() => (this.isReloading = false), 1500); // fake reload
  }

  close() {
    this.isVisible = false;
  }

  changeCustomButtonSelected() {
    this.isCustomButtonSelectedValue = !this.isCustomButtonSelectedValue;
    this.isCustomButtonSelected.next(this.isCustomButtonSelectedValue);
  }
}
