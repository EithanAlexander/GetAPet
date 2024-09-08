import React, { useState } from 'react';
import horseAnimation from './media/runningHorse.gif';
import logo from './media/logoAslan.png';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link
} from 'react-router-dom';
import Home from './Home';
import About from './About';
import Ads from './Ads';
import Guides from './Guides';
import Register from './Register';
import Login from './Login';
import SeekingHome from './SeekingHome';
import CreateAd from './CreateAd';
import UserProfile from './UserProfile';
import MyAds from './MyAds';
import MyFavorites from './MyFavorites';
import './App.css';

/**
 * Main application component that sets up the routes and navigation for the app.
 * Handles user authentication state and displays appropriate navigation links and options.
 */
function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  /**
   * Handles user login by updating the authentication state and user data.
   *
   * @param {Object} userData - The data of the logged-in user.
   */
  const handleLogin = (userData) => {
    setIsAuthenticated(true);
    setUser(userData);
  };

  /**
   * Handles user logout by resetting the authentication state and user data.
   * Displays a farewell message upon logout.
   */
  const handleLogout = () => {
    setIsAuthenticated(false);
    setUser(null);
    alert('Bye bye!');
  };

  return (
    <Router>
      <div>
        <nav style={{ marginTop: '20px' }}>
          <ul>
            <img src={logo} alt="Logo" style={{ width: '50px', marginRight: '5px' }} />
            {!isAuthenticated && <li style={{marginLeft: '15px', marginTop: '10px' }}><Link to="/register"><button className="button" type="button">Register</button></Link></li>}
            {!isAuthenticated && <li style={{marginTop: '10px' }}><Link to="/login"><button className="button" type="button">Login</button></Link></li>}
            {isAuthenticated && (
              <>
                <li style={{marginTop: '10px', marginLeft: '15px', color: 'blue', fontWeight: 'bold' }}>Welcome, {user.username}!</li>
                <li style={{marginTop: '10px' }}><button className="button" onClick={handleLogout}>Logout</button></li>
              </>
            )}
          </ul>
          <ul>
            <li><Link to="/">Home</Link></li>
            <li><Link to="/about us">About Us</Link></li>
            <li><Link to="/ads">Ads</Link></li>
            <li><Link to="/guides">Guides</Link></li>
            {isAuthenticated && (<li><Link to="/userProfile">Profile</Link></li>)}
            <img src={horseAnimation} alt="Running Horse" tabIndex="1" style={{ width: '50px', height: '50px', marginRight: 'auto', marginTop: '-20px'}} />
          </ul>
        </nav>

        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/about us" element={<About />} />
          <Route path="/ads" element={<Ads />}>
            <Route path="seekingAHome" element={<SeekingHome user={user} isAuthenticated={isAuthenticated} />} />
            <Route path="newAd" element={<CreateAd user={user} />} />
          </Route>
          <Route path="/guides" element={<Guides />} />
          {!isAuthenticated && <Route path="/register" element={<Register />} />}
          {!isAuthenticated && <Route path="/login" element={<Login onLogin={handleLogin} />} />}
          {isAuthenticated && (
            <Route path="/userProfile" element={<UserProfile user={user} />}>
              <Route path="myAds" element={<MyAds user={user} />} />
              <Route path="myFavorites" element={<MyFavorites user={user} />} />
            </Route>
          )}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
