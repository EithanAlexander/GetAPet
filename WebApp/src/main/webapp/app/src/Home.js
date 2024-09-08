import React from 'react';
import { Link } from 'react-router-dom';
import petImage from './media/happy.jpg';

/**
 * Home component that serves as the landing page for the web app.
 * This component provides a welcome message and an introduction to the purpose of the app,
 * encouraging users to explore ads for pets seeking homes and to learn more about the app.
 */
function Home() {
  return (
    <div>
      <h2>Welcome to our web app - Get A Pet! 😊</h2>
      <p>We are glad to have you here! 🥳</p>
      <p>Our web application was created to help pets find a loving home. 🐾</p>
      <p>Are you seeking a furry friend for life? 🐶🐱 Do you need to find a new home for a loved pet? ❤️</p>
      <p>You are at the right place! Please check out our <Link to="/ads">ads</Link> page. 📢</p>
      <p>For more info, please check our <Link to="/about us">about us</Link> page. ℹ️</p>
      <p style={{ marginTop: '40px' }}>So let's find out - is it going to be you soon?</p>
      <img src={petImage} alt="A cute pet" style={{ width: '300px', height: 'auto', marginTop: '0px' }} />
    </div>
  );
}

export default Home;
