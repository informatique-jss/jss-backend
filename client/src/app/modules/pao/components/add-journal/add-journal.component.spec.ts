/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { AddJournalComponent } from './add-journal.component';

describe('AddJournalComponent', () => {
  let component: AddJournalComponent;
  let fixture: ComponentFixture<AddJournalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddJournalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddJournalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
