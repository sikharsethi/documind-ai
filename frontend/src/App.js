import { useState } from 'react';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Chat from './pages/Chat';

function App() {
  const [user, setUser] = useState(null);
  const [selectedDocument, setSelectedDocument] = useState(null);

  const handleLogin = (userData) => {
    setUser(userData);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setSelectedDocument(null);
  };

  const handleSelectDocument = (doc) => {
    setSelectedDocument(doc);
  };

  const handleBack = () => {
    setSelectedDocument(null);
  };

  // Show login if not logged in
  if (!user) {
    return <Login onLogin={handleLogin} />;
  }

  // Show chat if document selected
  if (selectedDocument) {
    return (
      <Chat
        document={selectedDocument}
        onBack={handleBack}
      />
    );
  }

  // Show dashboard
  return (
    <Dashboard
      user={user}
      onLogout={handleLogout}
      onSelectDocument={handleSelectDocument}
    />
  );
}

export default App;