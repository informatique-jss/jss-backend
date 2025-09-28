import { Component, Input } from '@angular/core';
import { NgIcon } from "@ng-icons/core";
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';

@Component({
  selector: 'app-page-title',
  templateUrl: './page-title.component.html',
  imports: [NgIcon, ...SHARED_IMPORTS],
  standalone: true,
})
export class PageTitleComponent {
  @Input() title: string = 'Welcome!';
  @Input() subTitle: string | null = null;
}
