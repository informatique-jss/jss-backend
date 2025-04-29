export function compareWithId(o1: any, o2: any) {
  if (o1 == null && o2 != null || o1 != null && o2 == null)
    return false;
  if (o1.id && o2.id)
    return o1.id == o2.id;
  if (o1.code && o2.code)
    return o1.code == o2.code;
  return o1 == o2;
}
