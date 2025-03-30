import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { jarallax } from 'jarallax';
import { Subscription } from 'rxjs';
import { AppService } from '../../../../libs/app.service';
import { DocumentService } from '../../../my-account/services/document.service';

@Component({
  selector: 'mandatory-documents',
  templateUrl: './mandatory-documents.component.html',
  styleUrls: ['./mandatory-documents.component.css']
})
export class MandatoryDocumentsComponent implements OnInit {
  debounce: any;
  searchObservableRef: Subscription | undefined;
  searchText: string = "";
  searchResults: string[] = [];

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private documentService: DocumentService
  ) { }

  ngOnInit() {
  }
  practicalSheetsForm = this.formBuilder.group({});

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  searchForDocuments() {
    clearTimeout(this.debounce);
    this.searchResults = [];
    this.debounce = setTimeout(() => {
      this.searchDocuments();
    }, 500);
  }

  searchDocuments() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    // if (this.searchText && this.searchText.length > 2)
    // this.searchObservableRef = this.documentService.

  }

  clearSearch() {
    this.searchText = '';
    this.searchResults = [];
  }
}
