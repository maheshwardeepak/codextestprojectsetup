import { FormEvent, useEffect, useMemo, useState } from 'react';
import { api, apiBaseUrl, googleLoginConfigured } from './api';
import type { DashboardSummary, Project, Task, User } from './types';

const tokenStorageKey = 'minitaskflow.token';

const emptySummary: DashboardSummary = {
  projectCount: 0,
  taskCount: 0,
  completedTaskCount: 0
};

function App() {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(tokenStorageKey));
  const [user, setUser] = useState<User | null>(null);
  const [projects, setProjects] = useState<Project[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [summary, setSummary] = useState<DashboardSummary>(emptySummary);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [authMode, setAuthMode] = useState<'register' | 'login'>('register');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [projectName, setProjectName] = useState('');
  const [taskTitle, setTaskTitle] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const selectedProject = useMemo(
    () => projects.find((project) => project.id === selectedProjectId) ?? null,
    [projects, selectedProjectId]
  );

  useEffect(() => {
    if (!token) {
      return;
    }
    void loadWorkspace(token);
  }, [token]);

  async function loadWorkspace(currentToken: string) {
    setLoading(true);
    setError('');
    try {
      const [currentUser, dashboard, projectList] = await Promise.all([
        api.me(currentToken),
        api.dashboard(currentToken),
        api.projects(currentToken)
      ]);
      setUser(currentUser);
      setSummary(dashboard);
      setProjects(projectList);
      const firstProjectId = projectList[0]?.id ?? null;
      setSelectedProjectId(firstProjectId);
      if (firstProjectId) {
        setTasks(await api.tasks(currentToken, firstProjectId));
      } else {
        setTasks([]);
      }
    } catch (caught) {
      signOut();
      setError(messageFrom(caught));
    } finally {
      setLoading(false);
    }
  }

  async function refreshSummary(currentToken = token) {
    if (!currentToken) {
      return;
    }
    setSummary(await api.dashboard(currentToken));
  }

  async function handleAuth(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      const response = authMode === 'register'
        ? await api.register(email, password)
        : await api.login(email, password);
      localStorage.setItem(tokenStorageKey, response.token);
      setToken(response.token);
      setUser(response.user);
      setEmail('');
      setPassword('');
      await loadWorkspace(response.token);
    } catch (caught) {
      setError(messageFrom(caught));
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateProject(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!token || !projectName.trim()) {
      return;
    }
    setLoading(true);
    setError('');
    try {
      const project = await api.createProject(token, projectName);
      setProjects((current) => [project, ...current]);
      setSelectedProjectId(project.id);
      setTasks([]);
      setProjectName('');
      await refreshSummary();
    } catch (caught) {
      setError(messageFrom(caught));
    } finally {
      setLoading(false);
    }
  }

  async function handleSelectProject(projectId: number) {
    if (!token) {
      return;
    }
    setSelectedProjectId(projectId);
    setLoading(true);
    setError('');
    try {
      setTasks(await api.tasks(token, projectId));
    } catch (caught) {
      setError(messageFrom(caught));
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateTask(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!token || !selectedProjectId || !taskTitle.trim()) {
      return;
    }
    setLoading(true);
    setError('');
    try {
      const task = await api.createTask(token, selectedProjectId, taskTitle);
      setTasks((current) => [task, ...current]);
      setTaskTitle('');
      await refreshSummary();
    } catch (caught) {
      setError(messageFrom(caught));
    } finally {
      setLoading(false);
    }
  }

  async function handleCompleteTask(taskId: number) {
    if (!token) {
      return;
    }
    setLoading(true);
    setError('');
    try {
      const completedTask = await api.completeTask(token, taskId);
      setTasks((current) => current.map((task) => (task.id === taskId ? completedTask : task)));
      await refreshSummary();
    } catch (caught) {
      setError(messageFrom(caught));
    } finally {
      setLoading(false);
    }
  }

  function signOut() {
    localStorage.removeItem(tokenStorageKey);
    setToken(null);
    setUser(null);
    setProjects([]);
    setTasks([]);
    setSummary(emptySummary);
    setSelectedProjectId(null);
  }

  if (!token || !user) {
    return (
      <main className="auth-shell" data-testid="auth-screen">
        <section className="auth-panel">
          <div>
            <p className="eyebrow">MiniTaskFlow</p>
            <h1>{authMode === 'register' ? 'Create your workspace' : 'Welcome back'}</h1>
          </div>

          <form onSubmit={handleAuth} className="stack">
            <label>
              Email
              <input
                data-testid="auth-email"
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                autoComplete="email"
                required
              />
            </label>
            <label>
              Password
              <input
                data-testid="auth-password"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                autoComplete={authMode === 'register' ? 'new-password' : 'current-password'}
                minLength={8}
                required
              />
            </label>
            {error && <p className="error" role="alert">{error}</p>}
            <button data-testid="auth-submit" type="submit" disabled={loading}>
              {authMode === 'register' ? 'Register' : 'Log in'}
            </button>
          </form>

          <div className="auth-separator" aria-hidden="true">
            <span />
            <strong>or</strong>
            <span />
          </div>

          <button data-testid="google-login" type="button" className="google-button" disabled={!googleLoginConfigured}>
            Google login
          </button>
          {!googleLoginConfigured && (
            <p className="config-message" data-testid="google-config-message">
              Google login requires configuration.
            </p>
          )}

          <button
            type="button"
            className="text-button"
            onClick={() => {
              setAuthMode(authMode === 'register' ? 'login' : 'register');
              setError('');
            }}
          >
            {authMode === 'register' ? 'Use existing account' : 'Create account'}
          </button>

          <p className="runtime-note">API: {apiBaseUrl}</p>
        </section>
      </main>
    );
  }

  return (
    <main className="app-shell" data-testid="app-screen">
      <header className="topbar">
        <div>
          <p className="eyebrow">MiniTaskFlow</p>
          <h1>Task operations</h1>
        </div>
        <div className="account">
          <span>{user.email}</span>
          <button type="button" className="secondary" data-testid="sign-out" onClick={signOut}>Sign out</button>
        </div>
      </header>

      {error && <p className="error surface" role="alert">{error}</p>}

      <section className="metrics" aria-label="Dashboard counts">
        <div className="metric">
          <span>Projects</span>
          <strong data-testid="project-count">{summary.projectCount}</strong>
        </div>
        <div className="metric">
          <span>Tasks</span>
          <strong data-testid="task-count">{summary.taskCount}</strong>
        </div>
        <div className="metric">
          <span>Completed</span>
          <strong data-testid="completed-count">{summary.completedTaskCount}</strong>
        </div>
      </section>

      <section className="workspace-grid">
        <div className="panel">
          <div className="panel-heading">
            <h2>Projects</h2>
            {loading && <span className="loading">Syncing</span>}
          </div>
          <form onSubmit={handleCreateProject} className="inline-form">
            <label className="sr-only" htmlFor="project-name">Project name</label>
            <input
              id="project-name"
              data-testid="project-name"
              value={projectName}
              onChange={(event) => setProjectName(event.target.value)}
              placeholder="Project name"
              maxLength={120}
              required
            />
            <button data-testid="create-project" type="submit" disabled={loading}>Create</button>
          </form>

          <div className="list" data-testid="project-list">
            {projects.length === 0 ? (
              <p className="empty">No projects yet.</p>
            ) : (
              projects.map((project) => (
                <button
                  key={project.id}
                  type="button"
                  className={project.id === selectedProjectId ? 'row selected' : 'row'}
                  data-testid={`project-${project.id}`}
                  onClick={() => void handleSelectProject(project.id)}
                >
                  <span>{project.name}</span>
                </button>
              ))
            )}
          </div>
        </div>

        <div className="panel">
          <div className="panel-heading">
            <h2>{selectedProject ? selectedProject.name : 'Tasks'}</h2>
          </div>
          {selectedProject ? (
            <>
              <form onSubmit={handleCreateTask} className="inline-form">
                <label className="sr-only" htmlFor="task-title">Task title</label>
                <input
                  id="task-title"
                  data-testid="task-title"
                  value={taskTitle}
                  onChange={(event) => setTaskTitle(event.target.value)}
                  placeholder="Task title"
                  maxLength={180}
                  required
                />
                <button data-testid="create-task" type="submit" disabled={loading}>Add</button>
              </form>

              <div className="list" data-testid="task-list">
                {tasks.length === 0 ? (
                  <p className="empty">No tasks in this project.</p>
                ) : (
                  tasks.map((task) => (
                    <div key={task.id} className={task.completed ? 'task completed' : 'task'}>
                      <span>{task.title}</span>
                      {task.completed ? (
                        <span className="status" data-testid={`task-completed-${task.id}`}>Completed</span>
                      ) : (
                        <button
                          type="button"
                          className="secondary"
                          data-testid={`complete-task-${task.id}`}
                          onClick={() => void handleCompleteTask(task.id)}
                          disabled={loading}
                        >
                          Complete
                        </button>
                      )}
                    </div>
                  ))
                )}
              </div>
            </>
          ) : (
            <p className="empty">Create or select a project.</p>
          )}
        </div>
      </section>
    </main>
  );
}

function messageFrom(caught: unknown) {
  return caught instanceof Error ? caught.message : 'Something went wrong.';
}

export default App;
