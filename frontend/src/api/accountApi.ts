import {
  ChangePasswordRequest,
  LoginRequest,
  LoginResponseDto,
  RegisterRequest,
  RegisterResponseDto,
  UserDto,
} from './backendTypes';
import { apiRequest } from './httpClient';

export const accountApi = {
  register(request: RegisterRequest) {
    return apiRequest<RegisterResponseDto>('/auth/register', {
      method: 'POST',
      body: request,
    });
  },

  confirmEmail(token: string) {
    return apiRequest<null>('/auth/confirm', {
      query: { token },
    });
  },

  login(request: LoginRequest) {
    return apiRequest<LoginResponseDto>('/auth/login', {
      method: 'POST',
      body: request,
    });
  },

  logout(token: string) {
    return apiRequest<null>('/auth/logout', {
      method: 'POST',
      token,
    });
  },

  refresh(token: string) {
    return apiRequest<LoginResponseDto>('/auth/refresh', {
      method: 'POST',
      token,
    });
  },

  me(token: string) {
    return apiRequest<UserDto>('/account/me', {
      token,
    });
  },

  changePassword(token: string, request: ChangePasswordRequest) {
    return apiRequest<null>('/account/password', {
      method: 'PUT',
      token,
      body: request,
    });
  },

  deleteMe(token: string) {
    return apiRequest<null>('/account/me', {
      method: 'DELETE',
      token,
    });
  },
};
