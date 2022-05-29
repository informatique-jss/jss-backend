import { Tiers } from '../modules/tiers/model/Tiers';
import { TiersType } from './../modules/tiers/model/TiersType';
import { PROSPECT_TYPE_CODE } from './Constants';
export function compareWithId(o1: any, o2: any) {
  if (o1 == null && o2 != null || o1 != null && o2 == null)
    return false;
  return o1.id == o2.id;
}

export function isTiersTypeProspect(tiers: Tiers) {
  return tiers != null && tiers != undefined && tiers.tiersType != null && tiers.tiersType != undefined && tiers.tiersType.code == PROSPECT_TYPE_CODE;
}
