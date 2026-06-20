import { Link, NavLink, Route, Routes } from 'react-router-dom';

import { AccountPage } from './features/account/AccountPage';
import { ConfirmEmailPage } from './features/account/ConfirmEmailPage';
import { LoginPage } from './features/account/LoginPage';
import { RegisterPage } from './features/account/RegisterPage';
import { EventCatalogPage } from './features/events/EventCatalogPage';
import { EventDetailsPage } from './features/events/EventDetailsPage';
import { FavoriteEventsPage } from './features/events/FavoriteEventsPage';
import { useAuth } from './state/authStore';

export default function App() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="app">
      <header className="app-header">
        <Link className="brand" to="/">
          Wydarzenia Wrocław
        </Link>
        <nav className="main-nav" aria-label="Główna nawigacja">
          <NavLink to="/">Wydarzenia</NavLink>
          {isAuthenticated ? <NavLink to="/favorites">Ulubione</NavLink> : null}
          {isAuthenticated ? <NavLink to="/account">Konto</NavLink> : <NavLink to="/login">Zaloguj</NavLink>}
        </nav>
      </header>

      <Routes>
        <Route element={<EventCatalogPage />} path="/" />
        <Route element={<EventCatalogPage />} path="/events" />
        <Route element={<EventDetailsPage />} path="/events/:eventId" />
        <Route element={<FavoriteEventsPage />} path="/favorites" />
        <Route element={<LoginPage />} path="/login" />
        <Route element={<RegisterPage />} path="/register" />
        <Route element={<ConfirmEmailPage />} path="/confirm-email" />
        <Route element={<AccountPage />} path="/account" />
      </Routes>
    </div>
  );
}
