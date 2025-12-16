import { Component, Input, OnInit } from '@angular/core';
import { NgIcon } from "@ng-icons/core";
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';

@Component({
  selector: 'app-page-title',
  templateUrl: './page-title.component.html',
  imports: [NgIcon,
    ...SHARED_IMPORTS
  ],
  standalone: true,
})
export class PageTitleComponent implements OnInit {
  @Input() breadcrumbPaths: { label: string, route: string }[] = [];
  @Input() title: string = 'Welcome!';
  @Input() subTitle: string | null = null;
  @Input() isMarginBottom: boolean = true;

  ngOnInit(): void {
    this.breadcrumbPaths.unshift({ label: "Journal Spécial des Sociétés", route: "/" });
  }
}
