const categoryLabels: Record<string, string> = {
  KONCERT: 'Koncert',
  TEATR: 'Teatr',
  SPORT: 'Sport',
  EDUKACJA: 'Edukacja',
  KULTURA: 'Kultura',
  INNE: 'Inne',
};

const sourceTypeLabels: Record<string, string> = {
  ADMINISTRATOR: 'Administrator',
  SCRAPER: 'Import',
  EXTERNAL: 'Źródło zewnętrzne',
  EXTERNAL_API: 'Źródło zewnętrzne',
};

export const formatCategoryLabel = (category: string) =>
  categoryLabels[category?.trim().toUpperCase()] || category || 'Inne';

export const formatSourceTypeLabel = (sourceType: string) =>
  sourceTypeLabels[sourceType?.trim().toUpperCase()] || sourceType || 'Brak danych';
