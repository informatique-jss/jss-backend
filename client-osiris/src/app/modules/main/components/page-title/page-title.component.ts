import { Component, Input } from '@angular/core';
import { NgIcon } from "@ng-icons/core";

@Component({
  selector: 'app-page-title',
  templateUrl: './page-title.component.html',
  imports: [NgIcon],
  standalone: true,
})
export class PageTitleComponent {
  @Input() title: string = 'Welcome!';
  @Input() subTitle: string | null = null;
}
