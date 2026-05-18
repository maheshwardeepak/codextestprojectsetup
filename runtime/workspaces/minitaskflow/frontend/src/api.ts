import type { AuthResponse, DashboardSummary, Project, Task, User } from './types';

declare global {
  interface Window {
    __MINITASKFLOW_CONFIG__?: {
      apiBaseUrl?: string;
      googleClientId?: string;
    };
  }
}

const runtimeApiBaseUrl = window.__MINITASKFLOW_CONFIG__?.apiBaseUrl;
const buildApiBaseUrl = import.meta.env.VITE_API_BASE_URL as string | undefined;
const runtimeGoogleClientId = window.__MINITASKFLOW_CONFIG__?.googleClientId;
const buildGoogleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID as string | undefined;

export const apiBaseUrl = (runtimeApiBaseUrl || buildApiBaseUrl || 'http://localhost:8080').replace(/\/$/, '');
export const googleClientId = (runtimeGoogleClientId || buildGoogleClientId || '').trim();
export const googleLoginConfigured = googleClientId.length > 0;

class ApiClientError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'ApiClientError';
  }
}

async function request<T>(path: string, token: string | null, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);
  headers.set('Accept', 'application/json');
  if (options.body) {
    headers.set('Content-Type', 'application/json');
  }
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(`${apiBaseUrl}${path}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;
    try {
      const body = (await response.json()) as { message?: string };
      if (body.message) {
        message = body.message;
      }
    } catch {
      message = response.statusText || message;
    }
    throw new ApiClientError(message);
  }

  return (await response.json()) as T;
}

export const api = {
  register(email: string, password: string) {
    return request<AuthResponse>('/api/auth/register', null, {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
  },
  login(email: string, password: string) {
    return request<AuthResponse>('/api/auth/login', null, {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
  },
  googleLogin(credential: string, csrfToken: string) {
    return request<AuthResponse>('/api/auth/google', null, {
      method: 'POST',
      body: JSON.stringify({ credential, csrfToken })
    });
  },
  me(token: string) {
    return request<User>('/api/users/me', token);
  },
  dashboard(token: string) {
    return request<DashboardSummary>('/api/dashboard/summary', token);
  },
  projects(token: string) {
    return request<Project[]>('/api/projects', token);
  },
  createProject(token: string, name: string) {
    return request<Project>('/api/projects', token, {
      method: 'POST',
      body: JSON.stringify({ name })
    });
  },
  tasks(token: string, projectId: number) {
    return request<Task[]>(`/api/projects/${projectId}/tasks`, token);
  },
  createTask(token: string, projectId: number, title: string) {
    return request<Task>(`/api/projects/${projectId}/tasks`, token, {
      method: 'POST',
      body: JSON.stringify({ title })
    });
  },
  completeTask(token: string, taskId: number) {
    return request<Task>(`/api/tasks/${taskId}/complete`, token, {
      method: 'PATCH'
    });
  }
};
