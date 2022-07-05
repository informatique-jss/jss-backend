/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { SpecialOffersDialogComponent } from '../special-offers-dialog/special-offers-dialog.component';


describe('SpecialOffersDialogComponent', () => {
  let component: SpecialOffersDialogComponent;
  let fixture: ComponentFixture<SpecialOffersDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SpecialOffersDialogComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SpecialOffersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
