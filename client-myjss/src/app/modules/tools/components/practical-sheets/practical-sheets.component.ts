import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { PracticalSheetsService } from '../../services/practical-sheets.service';

@Component({
  selector: 'app-practical-sheets',
  templateUrl: './practical-sheets.component.html',
  styleUrls: ['./practical-sheets.component.css']
})
export class PracticalSheetsComponent implements OnInit {

  practicalSheetsSearched: string = ""; //debug
  debounce: any;
  loadingPracticalSheetsSearch: boolean = false;

  practicalSheets: Post[] | undefined;
  filteredPracticalSheets: Post[] | undefined;

  searchService: string = "";


  constructor(
    private formBuilder: FormBuilder,
    private practicalSheetsService: PracticalSheetsService,
  ) { }

  practicalSheetsForm = this.formBuilder.group({});

  ngOnInit() {
  }

  searchPracticalSheet() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveSearchSiret();
    }, 500);
  }

  effectiveSearchSiret() {
    if (this.practicalSheetsSearched) {
      this.loadingPracticalSheetsSearch = true;
      this.filteredPracticalSheets = this.practicalSheets.filter(sheet => JSON.stringify(sheet).toLowerCase().indexOf(this.searchService) >= 0);
      this.practicalSheetsService.getPracticalSheetsBySearch(this.practicalSheetsSearched).subscribe(response => {
        this.loadingPracticalSheetsSearch = false;
        if (response && response.length == 1)
          this.affaire = response[0];
      })
    }
  }
}
