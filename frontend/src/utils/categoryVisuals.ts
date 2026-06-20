export type CategoryVisual = {
  label: string;
  mark: string;
  background: string;
  accent: string;
  tone: 'music' | 'education' | 'culture' | 'sport' | 'food' | 'other';
};

const normalizeCategory = (category: string) => category.trim().toLowerCase();

const createFallbackMark = (category: string) => {
  const normalized = category.trim();
  return (normalized.length > 0 ? normalized.slice(0, 2) : 'EV').toUpperCase();
};

export const getCategoryVisual = (category: string): CategoryVisual => {
  const normalized = normalizeCategory(category);

  if (normalized.includes('muzyk') || normalized.includes('koncert')) {
    return {
      label: category || 'Muzyka',
      mark: 'MU',
      background: '#4B233B',
      accent: '#FF5A3D',
      tone: 'music',
    };
  }

  if (
    normalized.includes('eduk') ||
    normalized.includes('warsztat') ||
    normalized.includes('nauka') ||
    normalized.includes('java')
  ) {
    return {
      label: category || 'Edukacja',
      mark: 'ED',
      background: '#82946C',
      accent: '#F2B84B',
      tone: 'education',
    };
  }

  if (
    normalized.includes('kultur') ||
    normalized.includes('sztuk') ||
    normalized.includes('teatr') ||
    normalized.includes('wystaw')
  ) {
    return {
      label: category || 'Kultura',
      mark: 'KU',
      background: '#B65A3C',
      accent: '#B9D7E6',
      tone: 'culture',
    };
  }

  if (normalized.includes('sport') || normalized.includes('bieg') || normalized.includes('fitness')) {
    return {
      label: category || 'Sport',
      mark: 'SP',
      background: '#007C7A',
      accent: '#D7FF3F',
      tone: 'sport',
    };
  }

  if (
    normalized.includes('food') ||
    normalized.includes('jedz') ||
    normalized.includes('kulin') ||
    normalized.includes('gastr')
  ) {
    return {
      label: category || 'Jedzenie',
      mark: 'FO',
      background: '#F2B84B',
      accent: '#4B233B',
      tone: 'food',
    };
  }

  return {
    label: category || 'Wydarzenie',
    mark: createFallbackMark(category),
    background: '#B9D7E6',
    accent: '#007C7A',
    tone: 'other',
  };
};
