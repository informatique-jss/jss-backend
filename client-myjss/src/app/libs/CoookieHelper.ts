export function getGaClientId(): string | null {
  const match = document.cookie.match('(?:^|;)\\s*_ga=([^;]*)');
  if (match && match[1]) {
    // The format is often GA1.x.UID. We want to retrieve the UID part (from the 3rd segment)
    // Or more simply, we take everything after "GA1.x."
    const parts = match[1].split('.');
    if (parts.length >= 3) {
      return parts.slice(2).join('.'); // returns something like "123456789.987654321"
    }
  }
  return null; //  Fallback or error handling
}