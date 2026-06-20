import { FormEvent, useState } from 'react';

import { AppButton } from '../ui/AppButton';
import { AppTextInput } from '../ui/AppTextInput';

type ChangePasswordModalProps = {
  open: boolean;
  error: string;
  onCancel: () => void;
  onSubmit: (oldPassword: string, newPassword: string) => Promise<void>;
};

export function ChangePasswordModal({
  open,
  error,
  onCancel,
  onSubmit,
}: ChangePasswordModalProps) {
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!open) {
    return null;
  }

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setIsSubmitting(true);

    try {
      await onSubmit(oldPassword, newPassword);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-backdrop" role="presentation">
      <form className="modal-panel" onSubmit={submit}>
        <h2>Zmień hasło</h2>
        <p>Nowe hasło musi mieć co najmniej 8 znaków oraz zawierać literę i cyfrę.</p>
        <AppTextInput
          label="Stare hasło"
          onChange={(event) => setOldPassword(event.target.value)}
          required
          type="password"
          value={oldPassword}
        />
        <AppTextInput
          label="Nowe hasło"
          onChange={(event) => setNewPassword(event.target.value)}
          required
          type="password"
          value={newPassword}
        />
        {error ? <p className="form-error">{error}</p> : null}
        <div className="button-row">
          <AppButton disabled={isSubmitting} onClick={onCancel} variant="secondary">
            Anuluj
          </AppButton>
          <AppButton disabled={isSubmitting} type="submit">
            Zmień hasło
          </AppButton>
        </div>
      </form>
    </div>
  );
}
