import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { AppButton } from '../../components/ui/AppButton';
import { AppTextInput } from '../../components/ui/AppTextInput';
import { useAuth } from '../../state/authStore';

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setIsSubmitting(true);
    setError('');

    try {
      await login(email, password);
      navigate('/account');
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się zalogować.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="auth-page">
      <form className="auth-panel" onSubmit={submit}>
        <h1>Zaloguj użytkownika</h1>
        <p>Logowanie jest możliwe tylko dla konta aktywnego.</p>
        <AppTextInput
          autoComplete="email"
          label="Adres e-mail"
          onChange={(event) => setEmail(event.target.value)}
          required
          type="email"
          value={email}
        />
        <AppTextInput
          autoComplete="current-password"
          label="Hasło"
          onChange={(event) => setPassword(event.target.value)}
          required
          type="password"
          value={password}
        />
        {error ? <p className="form-error">{error}</p> : null}
        <div className="button-row">
          <AppButton disabled={isSubmitting} type="submit">
            Zaloguj
          </AppButton>
          <Link to="/register">
            <AppButton disabled={isSubmitting} variant="secondary">
              Zarejestruj konto
            </AppButton>
          </Link>
        </div>
      </form>
    </main>
  );
}
