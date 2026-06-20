export const formatDateTime = (value: string) =>
  new Intl.DateTimeFormat('pl-PL', {
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    month: 'long',
    year: 'numeric',
  }).format(new Date(value));

export const toStartOfDayIso = (date: string) =>
  date ? new Date(`${date}T00:00:00.000Z`).toISOString() : undefined;

export const toEndOfDayIso = (date: string) =>
  date ? new Date(`${date}T23:59:59.999Z`).toISOString() : undefined;

export const isDateInputValid = (value: string) =>
  value.trim().length === 0 || /^\d{4}-\d{2}-\d{2}$/.test(value.trim());
