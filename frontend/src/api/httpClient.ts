import { ApiResponse } from './backendTypes';

const API_PREFIX = '/api';

type QueryValue = string | number | boolean | null | undefined;

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: unknown;
  token?: string | null;
  query?: Record<string, QueryValue>;
};

const createUrl = (path: string, query?: Record<string, QueryValue>) => {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  const params = new URLSearchParams();

  Object.entries(query ?? {}).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      params.set(key, String(value));
    }
  });

  const suffix = params.toString();
  return `${API_PREFIX}${normalizedPath}${suffix ? `?${suffix}` : ''}`;
};

export async function apiRequest<T>(
  path: string,
  { method = 'GET', body, token, query }: RequestOptions = {},
): Promise<T> {
  const headers = new Headers();
  headers.set('Accept', 'application/json');

  if (body !== undefined) {
    headers.set('Content-Type', 'application/json');
  }

  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(createUrl(path, query), {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  let payload: ApiResponse<T> | undefined;

  try {
    payload = (await response.json()) as ApiResponse<T>;
  } catch {
    payload = undefined;
  }

  if (!response.ok || payload?.success === false) {
    throw new Error(payload?.message || 'Nie udało się połączyć z backendem.');
  }

  if (!payload) {
    throw new Error('Backend zwrócił pustą odpowiedź.');
  }

  return payload.data;
};
