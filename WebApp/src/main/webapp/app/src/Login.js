import React, { useState } from 'react';
import duckAnimation from './media/duckSunflower.gif';
import { useNavigate } from 'react-router-dom';

/**
 * Login component that handles user login functionality.
 * This component includes form input for username and password, password visibility toggle,
 * and form submission to the server. It displays a popup message for success or error feedback.
 * @param {function} onLogin - Function to handle login success, receiving user data as a parameter.
 */
function Login({ onLogin }) {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });

  /**
   * Handle input changes for form fields.
   * @param {Object} e - The event object.
   */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  // State to toggle password visibility
  const [passwordShown, setPasswordShown] = useState(false);

  // Toggle password visibility
  const togglePasswordVisibility = () => {
    setPasswordShown(!passwordShown);
  };

  // State for managing popup visibility and message
  const [popupSuccessfullyLogIn, setPopup] = useState({ show: false, message: '' });

  /**
   * Handle form submission.
   * Sends form data to the server for authentication.
   * @param {Object} e - The event object.
   */
  const handleSubmit = async (e) => {
    e.preventDefault();  // Prevent default form submission behavior
    const formDataObj = new FormData();
    formDataObj.append('username', formData.username);
    formDataObj.append('password', formData.password);

    try {
      const response = await fetch('http://localhost:8080/api/login', {
        method: 'POST',
        body: formDataObj
      });
      if (response.ok) {
        const data = await response.json(); // Assuming server sends back JSON with user data
        console.log('Login successful');
        setPopup({ show: true, message: 'Successful login' });
        // Set a timeout to hide the popup and redirect to user profile page after a few seconds
        setTimeout(() => {
          onLogin(data);
          navigate('/userProfile');
          setPopup({ show: false, message: '' });
        }, 1000);
      } else {
        const errorMsg = await response.text(); // Use response.text() for plain text response
        console.log('Login failed:', errorMsg);
        setPopup({ show: true, message: errorMsg });
      }
    } catch (error) {
      console.error('Network or other error', error);
      setPopup({ show: true, message: 'Failed to login due to a network or system error.' });
    } finally {
      // Automatically hide the popup after 2 seconds
      setTimeout(() => {
        setPopup({ show: false, message: '' });
      }, 2000);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Login</h2>
      <label>
        Username:
        <input type="text" name="username" value={formData.username} onChange={handleChange} required />
      </label>
      <br />
      <label>
        Password:
        <input type={passwordShown ? "text" : "password"} name="password" value={formData.password} onChange={handleChange} required />
        <button type="button" onClick={togglePasswordVisibility}>
          {passwordShown ? "Hide" : "Show"}
        </button>
      </label>
      <br />
      {popupSuccessfullyLogIn.show && (
        <div style={{
          position: 'absolute',
          top: '20%', // Adjust positioning as needed
          left: '50%',
          transform: 'translate(-50%, -50%)',
          backgroundColor: '#f8f9fa',
          padding: '20px',
          borderRadius: '5px',
          boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'
        }}>
          {popupSuccessfullyLogIn.message}
        </div>
      )}
      <button type="submit">Login</button>
      <p style={{ marginTop: '30px', color: 'blue' }}>Don't have a user yet? You can <a href="register">register</a> right here.</p>
      <img src={duckAnimation} alt="Happy duck" tabIndex="1" style={{ width: '200px', height: '200px', marginRight: 'auto'}} />
    </form>
  );
}

export default Login;
