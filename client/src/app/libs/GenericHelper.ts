export function copyObject<T>(object: T): T {
  var objectCopy = <T>{};

  for (var key in object) {
    if ((object as any).hasOwnProperty(key) && key != 'id') {
      objectCopy[key] = object[key];
    }
  }
  return objectCopy;
}
