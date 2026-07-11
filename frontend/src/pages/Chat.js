import { useState } from 'react';
import api from '../api/axios';

function Chat({ document, onBack }) {
  const [question, setQuestion] = useState('');
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleAsk = async (e) => {
    e.preventDefault();
    if (!question.trim()) return;

    const userQuestion = question;
    setQuestion('');
    setLoading(true);

    // Add user message
    setMessages((prev) => [
      ...prev,
      { type: 'user', text: userQuestion },
    ]);

    try {
      const response = await api.post('/api/rag/ask', {
        documentId: document.id,
        question: userQuestion,
      });

      // Add AI answer
      setMessages((prev) => [
        ...prev,
        { type: 'ai', text: response.data.answer },
      ]);
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        { type: 'ai', text: '❌ Something went wrong. Please try again.' },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <button style={styles.backBtn} onClick={onBack}>
          ← Back
        </button>
        <div style={styles.headerInfo}>
          <h2 style={styles.headerTitle}>📄 {document.originalName}</h2>
          <span style={styles.headerMeta}>
            {document.chunkCount} chunks
          </span>
        </div>
      </div>

      {/* Chat Messages */}
      <div style={styles.chatContainer}>
        {messages.length === 0 ? (
          <div style={styles.emptyChat}>
            <p style={styles.emptyChatText}>
              🤖 Ask me anything about this document!
            </p>
            <p style={styles.emptyChatHint}>
              Try: "What is the main topic?" or "Explain the first concept"
            </p>
          </div>
        ) : (
          <div style={styles.messages}>
            {messages.map((msg, index) => (
              <div
                key={index}
                style={{
                  ...styles.message,
                  ...(msg.type === 'user'
                    ? styles.userMessage
                    : styles.aiMessage),
                }}
              >
                <span style={styles.messageIcon}>
                  {msg.type === 'user' ? '👤' : '🤖'}
                </span>
                <p style={styles.messageText}>{msg.text}</p>
              </div>
            ))}
            {loading && (
              <div style={{ ...styles.message, ...styles.aiMessage }}>
                <span style={styles.messageIcon}>🤖</span>
                <p style={styles.messageText}>⏳ Thinking...</p>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Input */}
      <form style={styles.inputContainer} onSubmit={handleAsk}>
        <input
          style={styles.input}
          type="text"
          placeholder="Ask a question about the document..."
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          disabled={loading}
        />
        <button
          style={styles.sendBtn}
          type="submit"
          disabled={loading || !question.trim()}
        >
          Send →
        </button>
      </form>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    backgroundColor: '#f0f2f5',
    display: 'flex',
    flexDirection: 'column',
  },
  header: {
    backgroundColor: '#1a1a2e',
    padding: '16px 32px',
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
  },
  backBtn: {
    padding: '8px 16px',
    backgroundColor: 'transparent',
    color: 'white',
    border: '1px solid white',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  headerInfo: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
  },
  headerTitle: {
    color: 'white',
    margin: 0,
    fontSize: '18px',
  },
  headerMeta: {
    color: '#aaa',
    fontSize: '14px',
  },
  chatContainer: {
    flex: 1,
    padding: '24px',
    maxWidth: '800px',
    width: '100%',
    margin: '0 auto',
    overflowY: 'auto',
  },
  emptyChat: {
    textAlign: 'center',
    marginTop: '100px',
  },
  emptyChatText: {
    fontSize: '20px',
    color: '#1a1a2e',
  },
  emptyChatHint: {
    color: '#666',
    fontSize: '14px',
  },
  messages: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  message: {
    display: 'flex',
    gap: '12px',
    padding: '16px',
    borderRadius: '12px',
    maxWidth: '80%',
  },
  userMessage: {
    backgroundColor: '#4f46e5',
    color: 'white',
    alignSelf: 'flex-end',
    flexDirection: 'row-reverse',
  },
  aiMessage: {
    backgroundColor: 'white',
    color: '#1a1a2e',
    alignSelf: 'flex-start',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
  },
  messageIcon: {
    fontSize: '20px',
    flexShrink: 0,
  },
  messageText: {
    margin: 0,
    lineHeight: '1.6',
    fontSize: '15px',
  },
  inputContainer: {
    padding: '16px 24px',
    backgroundColor: 'white',
    borderTop: '1px solid #eee',
    display: 'flex',
    gap: '12px',
    maxWidth: '800px',
    width: '100%',
    margin: '0 auto',
    boxSizing: 'border-box',
  },
  input: {
    flex: 1,
    padding: '12px 16px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '16px',
    outline: 'none',
  },
  sendBtn: {
    padding: '12px 24px',
    backgroundColor: '#4f46e5',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '16px',
  },
};

export default Chat;