import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'grade',
  templateUrl: './grade.component.html',
  styleUrls: ['./grade.component.css']
})
export class GradeComponent implements OnInit {

  constructor() { }

  /**
   * The model of grade property
   * Mandatory
   */
  @Input() grade: string = "";

  ngOnInit() {
  }

  splitString(string: string): string[] {
    if (!string)
      return [] as Array<string>;
    return string.split("");
  }
}
