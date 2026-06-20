import { InputHTMLAttributes } from 'react';

type AppTextInputProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  error?: string;
};

export function AppTextInput({ id, label, error, className = '', ...props }: AppTextInputProps) {
  const inputId = id ?? props.name ?? label.toLowerCase().replace(/\s+/g, '-');

  return (
    <label className={`text-field ${className}`.trim()} htmlFor={inputId}>
      <span>{label}</span>
      <input id={inputId} {...props} />
      {error ? <small className="form-error">{error}</small> : null}
    </label>
  );
}
