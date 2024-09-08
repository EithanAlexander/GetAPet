/**
 * Entry point of the React application.
 * It initializes the root component and renders the application.
 */

import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

// Create a root and render the App component inside React.StrictMode for highlighting potential problems
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
