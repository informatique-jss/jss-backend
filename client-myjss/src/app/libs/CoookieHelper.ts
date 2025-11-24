// Even if consent is not given by the user, a cookie (anonyme) is created and will be found
export function getGaClientId(): string {
  const match = document.cookie.match('(?:^|;)\\s*_ga=([^;]*)');
  // The format is often GA1.x.UID. We want to retrieve the UID part (from the 3rd segment)
  // Or more simply, we take everything after "GA1.x."
  const parts = match![1].split('.');
  return parts.slice(2).join('.'); // returns something like "123456789.987654321"
}