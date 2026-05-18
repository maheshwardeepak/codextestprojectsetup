export interface User {
  id: number;
  email: string;
  authProvider: 'LOCAL' | 'GOOGLE';
  role: 'USER';
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Project {
  id: number;
  name: string;
  createdAt: string;
}

export interface Task {
  id: number;
  projectId: number;
  title: string;
  completed: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardSummary {
  projectCount: number;
  taskCount: number;
  completedTaskCount: number;
}
