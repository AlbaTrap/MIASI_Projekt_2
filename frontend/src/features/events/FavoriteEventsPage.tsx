import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import { eventsApi } from '../../api/eventsApi';
import { favoritesApi } from '../../api/favoritesApi';
import { EventCard } from '../../components/events/EventCard';
import { AppButton } from '../../components/ui/AppButton';
import { EmptyState } from '../../components/ui/EmptyState';
import { LoadingState } from '../../components/ui/LoadingState';
import { mapEventDto, mapEventToSearchResult } from '../../mappers/eventMapper';
import { useAuth } from '../../state/authStore';
import { CatalogEvent } from '../../types/events';
import { isVisibleInSavedEvents } from './eventLifecycle';

export function FavoriteEventsPage() {
  const { isAuthenticated, session } = useAuth();
  const [events, setEvents] = useState<CatalogEvent[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    const loadFavorites = async () => {
      if (!session?.accessToken) {
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError('');
        const favorites = await favoritesApi.getFavorites(session.accessToken);
        const favoriteEvents = await Promise.all(
          favorites.map((favorite) => eventsApi.getEventDetails(favorite.eventId)),
        );

        if (active) {
          setEvents(favoriteEvents.map(mapEventDto));
        }
      } catch (caughtError) {
        if (active) {
          setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się pobrać ulubionych wydarzeń.');
        }
      } finally {
        if (active) {
          setIsLoading(false);
        }
      }
    };

    void loadFavorites();

    return () => {
      active = false;
    };
  }, [session?.accessToken]);

  const visibleEvents = useMemo(
    () => events.filter(isVisibleInSavedEvents).map(mapEventToSearchResult),
    [events],
  );

  if (!isAuthenticated) {
    return (
      <main className="page-shell narrow-page">
        <section className="panel">
          <h1>Ulubione wydarzenia</h1>
          <p>Zaloguj się, żeby zobaczyć wydarzenia dodane do ulubionych.</p>
          <Link to="/login">
            <AppButton>Zaloguj się</AppButton>
          </Link>
        </section>
      </main>
    );
  }

  return (
    <main className="page-shell favorites-page">
      <section className="hero-panel favorites-hero">
        <div className="hero-copy">
          <p className="eyebrow">Twoje miejsca</p>
          <h1>Ulubione wydarzenia</h1>
          <p>
            Tu zobaczysz wydarzenia zapisane jako ulubione, również wtedy, gdy zostaną anulowane albo
            zarchiwizowane.
          </p>
        </div>
      </section>

      {isLoading ? <LoadingState /> : null}
      {error ? <div className="notice notice-error">{error}</div> : null}

      {!isLoading && !error && visibleEvents.length === 0 ? (
        <EmptyState title="Brak ulubionych">
          Nie masz jeszcze wydarzeń dodanych do ulubionych.
        </EmptyState>
      ) : null}

      {!isLoading && !error && visibleEvents.length > 0 ? (
        <section className="event-list" aria-label="Ulubione wydarzenia">
          {visibleEvents.map((event) => (
            <EventCard context="saved" event={event} key={event.eventId} />
          ))}
        </section>
      ) : null}
    </main>
  );
}
