export function compareWithId(o1: any, o2: any) {
  if (o1 == null && o2 != null || o1 != null && o2 == null)
    return false;
  return o1.id == o2.id;
}
