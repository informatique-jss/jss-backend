export function copyObject<T>(object: T, ignoreId: boolean = true): T {
  var objectCopy = <T>{};

  for (var key in object) {
    if ((object as any).hasOwnProperty(key) && (!ignoreId || key != 'id')) {
      objectCopy[key] = object[key];
    }
  }
  return objectCopy;
}

export function copyObjectList<T>(objects: T[], ignoreId: boolean = true): T[] {
  let outList = new Array<T>;
  if (objects) {
    for (let object of objects) {
      var objectCopy = <T>{};

      for (var key in object) {
        if ((object as any).hasOwnProperty(key) && (!ignoreId || key != 'id')) {
          objectCopy[key] = object[key];
        }
      }
      outList.push(objectCopy);
    }
  }
  return outList;
}
