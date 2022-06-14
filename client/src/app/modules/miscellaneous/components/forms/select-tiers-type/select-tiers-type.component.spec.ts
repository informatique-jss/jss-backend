/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { SelectTiersTypeComponent } from './select-tiers-type.component';


describe('AutocompleteCityComponent', () => {
  let component: SelectTiersTypeComponent;
  let fixture: ComponentFixture<SelectTiersTypeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelectTiersTypeComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectTiersTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
