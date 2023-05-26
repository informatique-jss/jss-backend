import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SortTableEditDialogComponent } from './sort-table-edit-dialog-component.component';

describe('SortTableEditDialogComponent', () => {
  let component: SortTableEditDialogComponent;
  let fixture: ComponentFixture<SortTableEditDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SortTableEditDialogComponent]
    });
    fixture = TestBed.createComponent(SortTableEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
