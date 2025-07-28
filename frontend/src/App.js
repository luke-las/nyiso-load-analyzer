import React from 'react';
import LoadDataFetcher from './components/LoadDataFetcher';
import './App.css';

function App() {
  return (
    <div className="app-container">
      <h1 className="app-title">NYISO Load Data Viewer</h1>
      <LoadDataFetcher />
    </div>
  );
}

export default App;
