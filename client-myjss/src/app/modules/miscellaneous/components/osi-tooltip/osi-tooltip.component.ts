import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PlatformService } from '../../../main/services/platform.service';

@Component({
  selector: 'osi-tooltip',
  templateUrl: './osi-tooltip.component.html',
  styleUrls: ['./osi-tooltip.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class OsiTooltipComponent implements OnInit {

  @ViewChild('tooltip') tooltipRef!: ElementRef<HTMLElement>;
  @ViewChild('trigger') triggerRef!: ElementRef<HTMLElement>;

  isTooltipAppeared = false;

  tooltipText: string | undefined;
  leftPosition: number = 0;
  topPosition: number = 0;

  constructor(private platefomService: PlatformService
  ) { }

  ngOnInit() {
    // TODO
    this.tooltipText = "Besoin d'aide ? Contactez-nous !  ";
  }

  show() {
    this.isTooltipAppeared = true;
    if (this.platefomService.isBrowser())
      setTimeout(() => this.updateTooltipPosition());
  }

  hide() {
    this.isTooltipAppeared = false;
  }

  toggle() {
    this.isTooltipAppeared = !this.isTooltipAppeared;
    if (this.isTooltipAppeared && this.platefomService.isBrowser()) {
      setTimeout(() => this.updateTooltipPosition());
    }
  }

  @HostListener('window:resize')
  onResize() {
    this.leftPosition = 0;
    this.topPosition = 0;
    if (this.isTooltipAppeared && this.platefomService.isBrowser()) {
      setTimeout(() => this.updateTooltipPosition());
    }
  }

  private updateTooltipPosition() {
    const trigger = this.triggerRef.nativeElement;
    const tooltip = this.tooltipRef.nativeElement;

    // get logo positioning
    const rectTrigger = trigger.getBoundingClientRect();

    // get tooltip size
    const tooltipWidth = tooltip.clientWidth;

    const pxOverflowRight = rectTrigger.right + tooltipWidth - window.innerWidth;
    if (pxOverflowRight > 0) {
      this.leftPosition = -pxOverflowRight;
    }
  }
}